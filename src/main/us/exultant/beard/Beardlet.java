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

import us.exultant.ahs.thread.*;
import java.util.concurrent.*;
import org.slf4j.*;

public abstract class Beardlet {
	/**
	 * <p>
	 * Returns the scheduler the application should use for all of its tasks &mdash;
	 * any time you want to do something later or repeatedly, start here.
	 * </p>
	 * 
	 * <p>
	 * This scheduler is responsible for disbatching the {@link #start(Beard)}
	 * and {@link #stop()} lifecycle methods on this Beardlet.
	 * </p>
	 * 
	 * <p>
	 * By default, this scheduler has exactly one thread, so tasks submitted to it can
	 * have shared data structures without worrying about concurrency and locking. For
	 * applications that need multiple threads for performance reasons, constructing a
	 * second scheduler for all non-UI tasks is the recommended approach, but
	 * overriding this method to provide a multithreaded scheduler is also possible if
	 * you know what you're doing.
	 * </p>
	 * 
	 * @return a WorkScheduler
	 */
	public WorkScheduler scheduler() {
		return $scheduler;
	}
	
	/**
	 * <p>
	 * Called at the beginning of the application's life.
	 * </p>
	 * 
	 * <p>
	 * This method will be called exactly once.
	 * </p>
	 * 
	 * <p>
	 * When possible, application windows will not open until after this method
	 * returns; therefore, it makes sense to do bare minimum startup in this method
	 * (set your background colors, etc), then schedule anything at all heavier to
	 * happen in a few moments.
	 * </p>
	 * 
	 * @param $beard
	 */
	public abstract void start(Beard $beard);
	
	/**
	 * <p>
	 * Called to signal that the application is stopping, i.e. the JavaFX window has
	 * been closed or the browser DOM containing the applet unloaded.
	 * </p>
	 * 
	 * <p>
	 * This method will be called exactly once.
	 * </p>
	 * 
	 * <p>
	 * This method will only be called after the {@link #start(Beard)} method. It will
	 * be called from the same thread that called the {@link #start(Beard)} method
	 * unless {@link #scheduler()} has been overriden to run with multiple threads.
	 * </p>
	 */
	public abstract void stop();
	
	
	
	private final WorkScheduler $scheduler = new WorkSchedulerFlexiblePriority(1).start();
	{
		WorkManager.periodicallyFlush($scheduler, 2, TimeUnit.MILLISECONDS);
		$scheduler.completed().setListener(new WorkManager.WorkFailureLogger(LoggerFactory.getLogger(this.getClass())));
	}
	
	
	
	static class WorkTargetStarter extends WorkTargetWrapperRunnable {
		public WorkTargetStarter(final Beardlet $application, final Beard $beard) {
			super(new Runnable() { public void run() {
				$application.scheduler().schedule($beard.bus().getWorkTarget(), ScheduleParams.NOW);
				$application.start($beard);
			}});
		}
	}
	
	
	
	static class WorkTargetStopper extends WorkTargetWrapperRunnable {
		public WorkTargetStopper(final Beardlet $application) {
			super(new Runnable() { public void run() {
				$application.stop();
			}});
		}
	}
}
