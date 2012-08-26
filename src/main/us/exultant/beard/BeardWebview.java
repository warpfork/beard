package us.exultant.beard;

import com.sun.webpane.webkit.*;
import javafx.scene.web.*;

public class BeardWebview extends Beard {
	/**
	 * Constructor to create a Beard around a WebView of your choice.
	 * 
	 * @param $webview
	 */
	public BeardWebview(WebView $webview) {
		super((JSObject)($webview.getEngine().executeScript("window")));
		this.$webview = $webview;
	}
	
	protected final WebView $webview;
}
