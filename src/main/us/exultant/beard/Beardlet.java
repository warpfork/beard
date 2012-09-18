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

import us.exultant.ahs.thread.*;

public abstract class Beardlet {
	public WorkScheduler scheduler() {
		return DefaultSchedulerSingletonHolder.INSTANCE;
	};
	
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
	
	
	
	private static class DefaultSchedulerSingletonHolder {
		public static final WorkScheduler INSTANCE = new WorkSchedulerFlexiblePriority(1).start();
	}
	
	
	
	static class WorkTargetStarter extends WorkTargetWrapperRunnable {
		public WorkTargetStarter(final Beardlet $application, final Beard $beard) {
			super(new Runnable() { public void run() {
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
