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

import javax.swing.*;
import netscape.javascript.*;

public final class LaunchApplet extends JApplet {
	public void init() {
	 	$beard = new Beard_Direct(JSObject.getWindow(this));
		$beardlet = BeardBootstrap.load(this.getParameter("main"));
	}
	
	private Beardlet	$beardlet;
	private Beard		$beard;
	
	public void start() {
		$beardlet.start($beard);
	}
	
	public void stop() {
		$beardlet.stop();
	}
	
	public void destroy() {
		//$scheduler.stop(true);
	}
}
