package us.exultant.beard;

import java.applet.*;
import netscape.javascript.*;

public class BeardApplet extends Beard {
	/**
	 * Constructor to create a Beard for an Applet embedded in a proper browser.
	 * 
	 * @param $applet
	 */
	public BeardApplet(Applet $applet) {
		super(JSObject.getWindow($applet));
	}
}
