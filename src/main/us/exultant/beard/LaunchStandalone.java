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

import javafx.application.*;
import javafx.beans.value.*;
import javafx.concurrent.Worker.State;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.web.*;
import javafx.stage.*;
import netscape.javascript.*;

/**
 * <p>
 * Launch point for Beard applications being run standalone.
 * </p>
 * 
 * <p>
 * Running looks something like this:
 * <pre>java -cp yourapp.jar -jar lib/beard.jar your.beardlet.Class</pre>
 * </p>
 * 
 * <p>
 * Or, alternately, you can put a main method on your Beardlet class that calls to the
 * main method of this (the Standalone) class with YourClass.class.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public final class LaunchStandalone extends Application {
	public static void main(String... $args) {
		if ($args.length != 1) {
			System.err.println("starting a beard application this way requires exactly one argument: the name of the applications Beardlet class.");
			System.exit(1);
		}
		launch($args);
	}
	
	/* JavaFX surprises:
	 * - You have to have a public no-arg constructor to this class.
	 * - The class cannot be nested unless it is static and also public.
	 * - The class must just be public, period, actually.
	 * - The launch() method makes a new instance of this class internally, so it never makes sense to construct an instance of this yourself.
	 * - The launch() method, despite being public and static, can only be called from within a subclass of Application.
	 *    (WHY?!!!?!  So it can reflectively find ITSELF?  Who the fuck designed this thing?!)
	 * So to review: it is never sane to construct this class yourself, but it is also absolutely impossible for me to hide the constructor either.
	 *  Also, I had to make a static method that just calls another static method, just so some stupid reflective bullshit wouldn't bitch about something that's obviously not a problem.
	 * 
	 * Trying to integrate anything with JavaFX is turning out to be more unpleasant than LWJGL.
	 * Which seems a bit ridiculous doesn't it.
	 * 
	 */
	// So how the fuck do I sneak a pointer to anything else into this without using static global fuckups?!
	//   - launch never returns a pointer to the Application instance it makes.
	//   - none of the methods called on the Application by that thread have any other arguments I can control in order to sneak things in.
	//   - what the shit?
	//   - well... I could pass in STRINGS.  That really helps.  Thank god for that.  Stringly typed code is exactly what I want.  That's why I program in java, right?
	// Did seriously no one working on JavaFX ever think that maybe someone someday might want to have some code that comes before the JavaFX thread?
	// Okay, I guess I'll just push shitloads of boilerplate onto library users, cool, great, exactly what I wanted.
	
	/**
	 * Stay away from this constructor. The idiots who designed the JavaFX API made it
	 * completely impossible for me to conceal it, but you REALLY do not want to mess
	 * around here.
	 */
	public LaunchStandalone() {}
	
	private Scene		$scene;
	private Browser		$browserRegion;
	private Beardlet	$beardlet;
	
	public void start(final Stage $stage) {
	 	$beardlet = BeardBootstrap.load(this.getParameters().getRaw().get(0));
		
		$stage.setTitle("Beard Demo");
		$browserRegion = new Browser();
		$browserRegion.$webview.getEngine().loadContent("");
		$scene = new Scene($browserRegion, 750, 500, Color.web("#435678"));
		$stage.setScene($scene);
		
		$browserRegion.$webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			public void changed(ObservableValue<? extends State> $ov, State $oldState, State $newState) {
				if ($newState == State.SUCCEEDED) {
					Beard $beard = new Beard((JSObject)($browserRegion.$webview.getEngine().executeScript("window")), true);
					// note: forcibly disable console in webviews.  i don't know why it's defined by default, because it doesn't seem to go anywhere useful, and it makes calling console_log from a non-javafx thread deadly, which bothers me.
					$beardlet.start($beard);
					$stage.show();
				}
			}
		});
	}
	
	public void stop() {
		$beardlet.stop();
	}
	
	private static class Browser extends Region {
		final WebView	$webview	= new WebView();
		
		public Browser() {
			getChildren().add($webview);
		}
		
		@Override protected void layoutChildren() { layoutInArea($webview, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER); }
		@Override protected double computePrefWidth(double height) { return 750; }
		@Override protected double computePrefHeight(double width) { return 500; }
	}
}
