/*
 * Copyright 2012 Eric Myhre <http://exultant.us>
 * 
 * This file is part of Beard.
 *
 * Beard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.beard;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.util.*;
import netscape.javascript.*;

/**
 * A message bus for Beard, providing elegant shuttling of events from the DOM to
 * handler logic registered from java.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class BeardBus {
	BeardBus(Beard $beard) {
		this.$beard = $beard;
	}
	
	private final Beard				$beard;
	private final Pipe<DomEvent>			$ingressPipe = new DataPipe<DomEvent>();
	/** Work description for translation and sorting.  ...which actually turns out to be not so much work translating, but it's still good to have a step here because it can separate us from the ingress thread from the js realm. */
	private final Router				$ingressWorker = new Router();
	/** Object that exposes its method to javascript; that method then writes to the {@link #$ingressPipe}.*/
	private final DomEvent.Translator		$ingressGate = new DomEvent.Translator($ingressPipe.sink());
	/** Maps an exposed part of the Route back to the Route itself, so the exposed bits can be used to specify a route for destruction. */
	private final Map<ReadHead<DomEvent>, Route>	$unbindRouter = new IdentityHashMap<ReadHead<DomEvent>, Route>();
	
	/**
	 * <p>
	 * Get an event route set up so that the requested javascript event type will be
	 * caught on elements specified by the given jQuery selector string; these events
	 * will be readable from the ReadHead returned.
	 * </p>
	 * 
	 * @param $type
	 *                the type of DOM event we want to listen for
	 * @param $selector
	 *                a jQuery selection string which describes which elements in the
	 *                current page's DOM should have event listeners attached to them.
	 * @return a ReadHead from which DomEvents will be become readable as soon as
	 *         {@link #getWorkTarget() BeardBus's worker} can route them.
	 */
	public ReadHead<DomEvent> bind(String $selector, DomEvent.Type $type) {
		Route $route = new Route();
		JSObject $fnptr = (JSObject) $beard.$jsb.call(
				"bus_bind",
				new Object[] {
						$route,
						$ingressGate,
						$selector,
						$type.name().toLowerCase(),
				}
		);
		if ($fnptr == null) return null;	// there were no elements in the dom that matched the selector... that's probably a bug on the caller's part.
		$route.$selstr = $selector;
		$route.$type = $type;
		$route.$jsfnptr = $fnptr;
		$route.$pipe = new DataPipe<DomEvent>();
		$unbindRouter.put($route.$pipe.source(), $route);
		return $route.$pipe.source();
	}// there's nothing to stop us from having more than one version of the bind method polymorphically, incidentally, and in particular have one that accepts a pipe as an argument instead of making one and returning a head.  that keeps boilerplate to a min if you don't need that, but also lets you do the advanced stuff joyfully, which fixes your laments and confusion in commit 062f7a73.
	
	/**
	 * <p>
	 * Unbind an event route.
	 * </p>
	 * 
	 * <p>
	 * This both causes BeardBus to forget about the event route, and also attempts to
	 * remove all javascript functions and bindings that were set up.
	 * </p>
	 * 
	 * <p>
	 * The javascript end of this can fail for any number of reasons, because it's
	 * essentially howling into a hurricane. It may fail because the DOM element that
	 * was once bound to has been removed, or because its properties and identity have
	 * changed such that the same selector string no longer identifies the same
	 * element, or because it was already unbound from somewhere else. Previous
	 * iterations of this design had a return of true if we unbound at least once
	 * function somewhere and false otherwise; this has been abandoned as silly and
	 * the return type now speaks only to whether or not BeardBus itself was surprised
	 * by your unbinding request.
	 * </p>
	 * 
	 * @param $bound
	 *                the event stream BeardBus gave you when you did the binding that
	 *                you now want to unbind (or, alternately, the ReadHead of the
	 *                Pipe you gave to BeardBus, depending on which interface you used
	 *                there).
	 * @return true if BeardBus did have some event route to deconstruct; false if
	 *         BeardBus doesn't know what you're talking about (possibly you've
	 *         already unbound it?).
	 */
	public boolean unbind(ReadHead<DomEvent> $bound) {
		Route $route = $unbindRouter.get($bound);
		if ($route == null) return false;
		$unbindRouter.remove($bound);
		$route.$pipe.sink().close();
		$beard.$jsb.call(
				"bus_unbind",
				new Object[] {
						$route.$selstr,
						$route.$type.name(),
						$route.$jsfnptr
				}
		);
		return true;
	}
	
	/**
	 * <p>
	 * Returns the task object that processes raw incoming events from the js realm
	 * and dispatches them to the ReadHead produced by BeardBus. This must be called
	 * periodically for BeardBus to work. The {@link ReadHead#setListener(Listener)
	 * listener} set on ReadHead instances returned by
	 * {@link #bind(String, DomEvent.Type)} will be called by whatever thread runs
	 * this task.
	 * </p>
	 * 
	 * <p>
	 * A simple program may choose to invoke this task directly in its own main thread
	 * and thereby eschew all complex concurrency issues, and use
	 * {@link SimpleReactor#bind(ReadHead, Listener)} to attach handlers directly. The
	 * bound {@code Listener} will then eventually be invoked by the same thread that
	 * called {@code getWorkTarget().call()}, leaving everything happily in one thread
	 * so there is zero concurrency to worry about.
	 * </p>
	 * 
	 * <p>
	 * Alternatively, a program may hand over this WorkTarget to a proper
	 * {@link WorkScheduler}, and make a full WorkTarget of its own to deal with every
	 * event stream. This is more complex to implement, but allows total parallelism.
	 * </p>
	 */
	public WorkTarget<Void> getWorkTarget() {
		return $ingressWorker;
	}
	
	
	
	static class Route {
		/** The event type this route is for.  We use this to do some (extremely minimal!) sanity checking on incoming stuff from the js realm. */
		private DomEvent.Type $type;
		/** The selection string used when this event route was set up.  We need it again for unbinding for obvious reasons. */
		private String $selstr;
		/** The pointer to the javascript function we created and bound for this event route.  This pointer in the ingressRouter is how messages find their way; we also need this pointer to be able to unbind correctly. */
		private JSObject $jsfnptr;
		/** The pipe we push events into; the ReadHead of this is what BeardBus exposes as the return from binding at the end of the day. */
		private Pipe<DomEvent> $pipe;
	}
	
	
	
	/**
	 * Shuttles events from the ingress pipe to the individual pipes of a binding. (In
	 * earlier designs, this was also suposed to handle some processing and
	 * serialization, but it's turned out that we were able to offload all of that
	 * into the js realm and/or LiveConnect itself.)
	 * 
	 * This design makes absolutely certain that no level of error on the side of the
	 * library user could possibly hang up the js thread &mdash; this is of major
	 * importance, since in most browsers, hanging up the js thread hangs up all
	 * rendering of that page, all actions that can generate events on that page (even
	 * scrolling!), and in some browsers even the browser itself (including the
	 * ability to close it!!).
	 */
	private class Router extends WorkTarget.FlowingAdapter<DomEvent,Void> {
		public Router() {
			super($ingressPipe.source(), null, 0);
		}
		
		protected Void run(DomEvent $in) {
			// disbatch that event to an appropriate pipe
			if ($in.routekey == null)
				; /* well that's silly of javascript to have done, disregard that pesky noise. */
			else
				$in.routekey.$pipe.sink().write($in);
			return null;
		}
	}
}
