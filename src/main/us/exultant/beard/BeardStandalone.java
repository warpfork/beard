package us.exultant.beard;

import us.exultant.ahs.util.*;
import us.exultant.ahs.thread.*;
import java.util.concurrent.*;
import javafx.scene.web.*;

public class BeardStandalone {
	public static void main(String[] args) {
		BeardStandalone $x = new BeardStandalone();
		new BeardWebview($x.$webview);
	}
	
	public BeardStandalone() {
		$webviewLatch = new LinkedBlockingQueue<WebView>();
		$scheduler = new WorkSchedulerFlexiblePriority(1);
		WorkFuture<Void> $wffx = $scheduler.schedule(new JavafxWorkTarget(), ScheduleParams.NOW);
		$scheduler.start();
		try {
			$webview = $webviewLatch.take();
			X.sayet("got!");
		} catch (InterruptedException $e) { throw new Error($e); }
	}
	
	private final WorkScheduler		$scheduler;
	private final BlockingQueue<WebView>	$webviewLatch;
	private final WebView			$webview;
	
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
}
