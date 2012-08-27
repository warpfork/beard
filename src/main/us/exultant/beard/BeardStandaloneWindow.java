package us.exultant.beard;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.util.concurrent.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.web.*;
import javafx.stage.*;

public class BeardStandaloneWindow extends Application {
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
	 * You may only call this once, ever, or narwhals will eat you.
	 */
	static WebView make() {
		$latch = new CountDownLatch(1);
		WorkScheduler $scheduler = new WorkSchedulerFlexiblePriority(1);
		$scheduler.schedule(new JavafxWorkTarget(), ScheduleParams.NOW).addCompletionListener(new Listener<WorkFuture<?>>() {
			public void hear(WorkFuture<?> $x) {
				if (!$x.isFinishedGracefully())
					try { $x.get(); } catch (Exception $e) { $e.printStackTrace(); System.exit(6); }
			}
		});
		$scheduler.start();
		//launch(new String[0]);
		try {
			$latch.await();
		} catch (InterruptedException $e) { throw new Error($e); }
		return $hack.$browserRegion.$webview;
	}
	
	private static BeardStandaloneWindow $hack;
	private static CountDownLatch $latch;
	
	private static class JavafxWorkTarget extends WorkTargetWrapperCallable<Void> {
		public JavafxWorkTarget() {
			super(new Callable<Void>() {
				public Void call() throws Exception {
					BeardStandaloneWindow.fuckYou();
					return null;
				}
			});
		}
	}
	
	private static void fuckYou() {
		launch();
	}
	
	/**
	 * Stay away from this constructor. The idiots who designed the JavaFX API made it
	 * completely impossible for me to conceal it, but you REALLY do not want to mess
	 * around here.
	 */
	public BeardStandaloneWindow() {}
	
	private Scene	$scene;
	private Browser $browserRegion;
	
	@Override
	public void start(Stage $stage) throws Exception {
		$stage.setTitle("Beard Demo");
		$browserRegion = new Browser();
		$scene = new Scene($browserRegion, 750, 500, Color.web("#435678"));
		$stage.setScene($scene);
		$stage.show();
		$hack = this;
		$latch.countDown();
	}
	
	private static class Browser extends Region {
		final WebView	$webview	= new WebView();
		final WebEngine	$webEngine	= $webview.getEngine();
		
		public Browser() {
			getChildren().add($webview);
		}
		
		@Override protected void layoutChildren() { layoutInArea($webview, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER); }
		@Override protected double computePrefWidth(double height) { return 750; }
		@Override protected double computePrefHeight(double width) { return 500; }
	}
}
