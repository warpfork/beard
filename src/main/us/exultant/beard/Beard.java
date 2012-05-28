package us.exultant.beard;

import java.applet.*;
import java.io.*;
import netscape.javascript.*;

public class Beard {
	public Beard(Applet $applet) {
		$jso = JSObject.getWindow($applet);
		$precommand = new StringBuilder(1024);
	}
	private final JSObject	$jso;
	
	
	
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
			String $jquery = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("res/beard/jquery-1.7.2.js"))).readLine();	// this is a hack that works only because I happen to have made that resource a one-line file.  I might pull in IO parts of AHSlib later to deal with this less annoyingly.
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
}
