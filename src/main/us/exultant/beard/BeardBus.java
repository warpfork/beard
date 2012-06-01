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
import us.exultant.ahs.codec.eon.*;
import us.exultant.ahs.codec.json.*;
import us.exultant.ahs.thread.*;
import org.slf4j.*;

public class BeardBus {
	
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
	
	public void hear(String $msg) {
		$incoming.sink().write($msg);
	}
	
	private Pipe<String> $incoming = new DataPipe<String>();
	private Pipe<EonObject> $reception = new DataPipe<EonObject>();
	private Logger $log = LoggerFactory.getLogger(BeardBus.class);
	
	public BeardBus() {
		TranslatingWorkTarget<String,EonObject> $wtt = new TranslatingWorkTarget<String,EonObject>(
				$incoming.source(),
				new Translator<String,EonObject>() {
					public EonObject translate(String $arg0) throws TranslationException {
						return new JsonObject($arg0);
					}
				},
				$reception.sink()
		);
		$wtt.setExceptionHandler(new ExceptionHandler<TranslationException>() {
			public void hear(TranslationException $arg0) {
				$log.warn("failed to process message into JsonObject", $arg0);
			}
		});
		WorkManager.getDefaultScheduler().schedule($wtt, ScheduleParams.NOW);
	}
	
	
	
}
