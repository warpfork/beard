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

import us.exultant.ahs.thread.*;
import java.util.*;

public class TestStandalone extends Beardlet {
	public static void main(String... $args) {
		LaunchStandalone.main(TestStandalone.class.getName());
	}
	
	public void start(final Beard $beard) {
		$beard.eval("$('#main').html('ohai!');");
		$beard.eval("$('#main').append($('<div>').attr('id','ticker'));");
		
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
	}
	
	public void stop() {
		/* meh! */
	}
}
