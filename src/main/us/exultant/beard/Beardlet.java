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
	
	public abstract void start(Beard $beard);
	
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
