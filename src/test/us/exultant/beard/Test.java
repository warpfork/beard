/*
 * Copyright 2012,2013 Eric Myhre <http://exultant.us>
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
import us.exultant.ahs.iob.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.util.*;

public class Test extends BeardApplication {
	public static void main(String... $args) {
		LaunchStandalone.main(Test.class.getName());
	}
	
	public void start(final Beard $beard) {
		$beard.console_log("console log message.");
		
		$beard.eval("$('#main').html('ohai!');");
		$beard.eval("$('#main').append($('<div>').attr('id','ticker'));");
		$beard.eval("$('#ticker').css('font-family','monospace');");
		
		scheduler().schedule(
				new WorkTargetWrapperRunnable(
						new Runnable() { public void run() {
							$beard.eval("$('#ticker').html('the time is now "+new Date()+"');");
						}},
						true,
						false
				),
				ScheduleParams.makeFixedDelay(1)
		);
		
		$beard.eval("$('#main').append('binding test event listeners...');");
		for (DomEvent.Type $type : DomEvent.Type.values())
			SimpleReactor.bind(
					$beard.bus().bind("document", $type),
					new Listener<DomEvent>() { public void hear(DomEvent $evt) {
						$beard.console_log($evt.toString());
					}}
			);
		$beard.eval("$('#main').append('binding test event listeners done.');");
		
		
		$beard.eval("$('#main').append($('<pre>').attr('id','scheduler-report'));");
		scheduler().schedule(
				new WorkTargetWrapperRunnable(
						new Runnable() { public void run() {
							$beard.eval("$('#scheduler-report').html('"+((String)scheduler().describe()).replace("\n", "<br>")+"');");
						}},
						true,
						false
				),
				ScheduleParams.makeFixedDelay(100)
		);
		
		$beard.eval("$('#main').append($('<textarea>').attr('id','le-text'));");
		
		$beard.console_log("startup done.");
		
		

		scheduler().schedule(
				new WorkTargetWrapperRunnable(new Runnable() { public void run() {
						try {
							$beard.eval(IOForge.readResourceAsString("res/beard/testMutationObserverSupport.js"));
						} catch (IOException $e) { throw new Error("go straight to hell.", $e); }
				}}),
				ScheduleParams.makeDelayed(1000)
		);
	}
	
	public void stop() {
		/* meh! */
	}
}
