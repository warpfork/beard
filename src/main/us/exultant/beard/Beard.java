package us.exultant.beard;

import java.applet.*;
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
}
