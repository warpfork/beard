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

import us.exultant.ahs.iob.*;
import java.io.*;
import java.util.*;
import netscape.javascript.*;

class Beard_Direct implements Beard {
	/**
	 * Constructor to create a Beard around any JSObject. The JSObject should
	 * represent the "window" object of a DOM, or the results will be quite undefined.
	 * 
	 * @param $window
	 */
	public Beard_Direct(JSObject $window) {
		this($window, false);
	}

	Beard_Direct(JSObject $window, boolean $forceNoConsole) {
		// make basic contact, prepare to eval things a lot
		$js_window = $window;
		$precommand = new StringBuilder(1024);
		
		// inaugerate our object in the js world
		$js_beard = (JSObject) eval("window.beard = { injectScript: function(scr){ var dS=document.createElement('script'); dS.type='text/javascript'; dS.innerHTML=scr; document.getElementsByTagName('head')[0].appendChild(dS); return dS; } };");
		try {
			loadScript(IOForge.readResourceAsString("res/beard/beard.js"));
		} catch (IOException $e) { throw new Error("malformed jar: resources missing", $e); }
		$js_beard_internal = (JSObject) $js_beard.getMember("internal");
		
		// load jquery
		try {
			loadScript(IOForge.readResourceAsString("res/beard/jquery-1.7.2.js"));
		} catch (IOException $e) { throw new Error("malformed jar: resources missing", $e); }
		
		// grab a pointer to the "console" object if one's around
		if ($forceNoConsole)
			$console =  null;
		else
			$console = (JSObject) eval("console;");
		
		// initialize other subsystems
		$bus = new BeardBus_Direct(this);
		$assetLoader = new BeardAssetLoader(this);
		
		// normalize the page to have a few standard named elements
		normalizePage();
	}
	
	final JSObject	$js_window;
	final JSObject	$js_beard;
	final JSObject	$js_beard_internal;
	final JSObject	$console;
	final BeardBus	$bus;
	final BeardAssetLoader $assetLoader;
	
	
	
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
		if ($strs.length == 1) return $js_window.eval($strs[0]);
		return $js_window.eval(condense($strs));
	}
	private final StringBuilder $precommand;
	private synchronized String condense(String... $strs) {
		$precommand.setLength(0);
		for (String $s : $strs) $precommand.append($s);
		return $precommand.toString();
	}
	
	
	public BeardBus bus() {
		return $bus;
	}
	
	public BeardAssetLoader assetLoader() {
		return $assetLoader;
	}
	
	
	
	/**
	 * <p>
	 * If a {@code console} object exists in the javascript realm (i.e. the browser
	 * has firebug or something emulating their design), calling this method is the
	 * same as calling {@code console.log}: your message objects will appear in the
	 * firebug console.
	 * </p>
	 * 
	 * <p>
	 * Notes about cross-browser behavior: actual firebug on firefox will tend to call
	 * toString() on any java objects handed to this function that don't have a
	 * primitive translation; chrome on the other hand seems willing to keep the java
	 * object as a reference, but provides approximately zero usable way to examine
	 * it... in other words, you may wish to restrict your use of this function to
	 * only primitive and string arguments. Also worth noting is that in firefox, this
	 * method will begin to perform incredibly slowly once you push a few thousand
	 * lines through it and the firebug console log limit is reached; chrome seems
	 * unphased by this.
	 * </p>
	 * 
	 * @param $msgs
	 */
	public void console_log(Object... $msgs) {
		if ($console != null)
			$console.call("log", $msgs);
		else
			System.err.println("console_log: "+Arrays.toString($msgs));
	}
	
	
	
	/**
	 * <p>
	 * Performs several pieces of groundwork in order to make the browser easier to
	 * deal with and make some shorthand functions globally available:
	 * <ul>
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
	private void normalizePage() {
		eval("document.getElementsByTagName('body')[0].id = 'body';");
		eval("document.getElementsByTagName('head')[0].id = 'head';");
		
		eval("x=$('#main'); if(x.length==0){x=$('<div>').attr('id','main');}           $('#body').append(x);");
		eval("x=$('#dev');  if(x.length==0){x=$('<div>').attr('id','dev'); } x.hide(); $('#body').append(x);");
		eval("$('#main').focus();");
	}
	
	/**
	 * @param $script string to be loaded into a script tag and attached to the head of the web page.  No worries about escaping.
	 */
	public void loadScript(String $script) {
		/* There are many, many ways to go about this.  The most trivial is this:
		 * 
		 * 	eval("dS=document.createElement('script'); dS.type='text/javascript'; dS.innerHTML=\""+$script+"\"; document.getElementsByTagName('head')[0].appendChild(dS);");
		 * 
		 * However that one requires you to worry about escaping -- not good, do not want.
		 * More advanced:
		 * 
		 * 	JSObject $deScript = (JSObject) eval("dS=document.createElement('script'); dS.type='text/javascript'; document.getElementsByTagName('head')[0].appendChild(dS); dS;");
		 * 	$deScript.setMember("innerHTML", $script);
		 * 
		 * This saves you from escaping and works nicely.
		 * 
		 * However, we're going with this mechanism with JSObject.call() because it lets us make the javascript function once, keep it around, and keep using it.
		 * This doesn't make any practical difference, really.  But it seems cleaner and nicer for something that's fairly core.
		 */
		$js_beard.call("injectScript", new Object[]{$script});
	}
}
