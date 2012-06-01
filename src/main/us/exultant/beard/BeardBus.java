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

public class BeardBus {
	BeardBus(Beard $beard) {
		this.$beard = $beard;
		$ingress = new Ingress();
	}
	
	private final Beard $beard;
	private final Ingress $ingress;
	
	Ingress getJsExposure() {
		return $ingress;
	}
	
	// so here's what you can do:
	//  call a method to register your interest for click, mouseover, mouseout, mousemove, key, etc.
	//  you do this on a dom selector string of your choice.
	//  you get a ReadHead back.  what message type?  not actually sure.
	// the point in question here is exactly how much js function{} we want the library user to be able to specify.
	//  i tend towards "none".  if you want to do that you are of course always free to do whatever you want with Beard.eval() directly.
	//  if it's any of the other things, there's a finite number of things you could end up getting reported to you (so a JsonObject being exposed outside of the bus might not actually make any sense):
	//   - the JSObject of the thing that the event came from.  unsure if i like to do this; mostly I find JSObject to be destestably inconvenient to actually use.  That's the beef that kicked off this whole piece of work, really.
	//   - the id of the thing the event came from.
	//   - the dom selector string used to set up the event.
	//   - event specific data (mouse button, mouse position, which key, whatever).
	//   are the any other things that could come out of that?
	//    If not, it seems quite reasonable to have java classes for those, and return ReadHead of specific type per each registration method.
	// also potentially in question is how we want to pass things back.
	//  seems like actually we might not need/want JsonObject after all.  might be better/faster/clearer to expose multiple ingress methods to the js realm, once per event kind.
	
	// what is your threading model really going to be.
	//  we can full on jump the shark and demand all the things use WorkTargets in all the places.
	//   this is not a... polite... approach.
	//  we allow the javascript ingress call to push data a pipe in the bus, and the bus has a single thread demuxing disbatching events all into the individual readheads.
	//   unfortunately that's not very pleasant either.  we don't want the "main thread" to be doing vast amounts of polling on a set of readheads that's quite certain to grow large, and i'm not sure i feel safe letting the listener methods do the heavy lifting just because that means something other than the "main thread" doing so much lifting that the synchronization could be terrifying and Bad.
	//  we allow the javascript ingress call to push data into appropriate pipes, directly.
	//   I frankly don't know what this would mean for the threading model of the whole system.  But I don't think I expect it to be good.
	//  we could take option the second there but have the "main thread" itself be the one calling (yeahhh, manually) the bus queue demuxer-dispatcher-thinger.  in that case, having scads of ReadHead and letting those in turn use their Listener's to do work quite directly would actually turn out to be pretty okay.
	//   and only by default of course.  if someone has a situation where they want to use full on WorkTargets throughout their program, so be it, they can schedule the thing instead of calling it from main.
	//   i suspect in most applications i wrote that were heavy duty enough that I wanted to thread a bit, I'd always pretty much end up having a "renderer" thread that, well, does rendery things... and s/mainthread/renderthread/g would do nicely.  And then if individual event handlers from that need to do something serious that feeds back out of the gui area, of course that's their issue and of irreducible complexity.
	
	class Ingress {
		
	}
}
