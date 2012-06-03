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

import us.exultant.ahs.iob.*;
import java.applet.*;
import java.io.*;
import netscape.javascript.*;

public class Beard {
	public Beard(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuilder(1024);
		$bus = new BeardBus(this);
	}
	private final JSObject	$jso;
	private final BeardBus	$bus;
	
	
	
	/**
	 * <p>
	 * Executes a javascript string in the context of the browser window containing
	 * our applet.
	 * </p>
	 * 
	 * <p>
	 * Note about return types: they're... very ambiguous and not at all convenient.
	 * The author recommends giving up using anything except strings; if you need full
	 * objects or arrays (or even simple things like integers), pass them around as
	 * JSON strings and deal with encoding and decoding on either side.
	 * </p>
	 * 
	 * @see <a
	 *      href="http://docstore.mik.ua/orelly/web/jscript/refp_186.html">Javascript
	 *      - The Definitive Guide</a>
	 * 
	 * @param $strs
	 *                a String or set of Strings which will be concatenated and run as
	 *                javascript.
	 * @return this may return a {@link JSObject}, {@link Boolean}, {@link Double}, or
	 *         {@link String}. If multiple javascript commands were in the strings we
	 *         were given to run, the value returned is from the last of them.
	 */
	public synchronized Object eval(String... $strs) {
		if ($strs.length == 0) return null;
		if ($strs.length == 1) return $jso.eval($strs[0]);
		return $jso.eval(condense($strs));
	}
	private final StringBuilder $precommand;
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $precommand.toString();
	}
	
	
	
	/**
	 * <p>
	 * Performs several pieces of groundwork in order to make the browser easier to
	 * deal with and make some shorthand functions globally available:
	 * <ul>
	 * <li>jQuery is loaded!
	 * <li>the ID of the first Body tag is set to "body"
	 * <li>the ID of the first Head tag is set to "head"
	 * <li>if a div called '#main' doesn't already exist, it is made and added to
	 * '#body'. (All content should be put here; this makes it harder to screw up and
	 * accidentally overwrite the applet itself!)
	 * <li>if a div called '#dev' doesn't already exist, it is made and added to
	 * '#body'. '#dev' is set to be hidden. (This can be used to log into the browser
	 * DOM, which can be a lot less tiresome than the java control panel.)
	 * <li>finally, attempts to set the focus to '#main'. (Otherwise, the browser
	 * often defaults to giving focus to the applet, which screws immensely with
	 * normal keyboard usage.)
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * The element '#main' should be used to contain all the rest of the page &mdash;
	 * modifying any elements of the DOM above that can be dangerous, because results
	 * are unpredictable if the applet itself is unlinked from the DOM. The element
	 * '#dev' is intended for use as a debugging output area.
	 * </p>
	 */
	public void normalizePage() {
		eval("document.getElementsByTagName('body')[0].id = 'body';");
		eval("document.getElementsByTagName('head')[0].id = 'head';");
		
		try {
			String $jquery = IOForge.readResourceAsString("res/beard/jquery-1.7.2.js");
			loadScript($jquery);
		} catch (IOException $e) { throw new Error("malformed jar: resources missing", $e); }
		
		eval("x=$('#main'); if(x.length==0){x=$('<div>').attr('id','main');}           $('#body').append(x);");
		eval("x=$('#dev');  if(x.length==0){x=$('<div>').attr('id','dev'); } x.hide(); $('#body').append(x);");
		eval("$('#main').focus();");
	}
	/**
	 * @param $script watch it, this is loaded by js in double-quotes and isn't escaped automatically!
	 */
	private void loadScript(String $script) {
		eval("dS=document.createElement('script'); dS.type='text/javascript'; dS.innerHTML=\""+$script+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
	}
	
	
	
	/**
	 * Javascript realm uses the object returned by the message to feed in all data
	 * destined for BeardBus.
	 * 
	 * @return the ingress system object for the BeardBus associated with this
	 *         Beard-instance/applet.
	 */
	public BeardBus.Ingress ingress() {
		return $bus.getJsExposure();
	}
}
