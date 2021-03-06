package net.celestialgaze.GuraBot.util;

import java.util.Timer;
import java.util.TimerTask;

public class DelayedRunnable {
	Timer timer;
	long endTime = 0;
	Runnable runnable;
	private boolean isDone = false;
	public DelayedRunnable(Runnable runnable) {
		this.runnable = runnable;
		timer = new Timer();
	}
	/**
	* {@summary} Executes the runnable at the given time.
	* @param time  the at which to execute.
	*/
	public DelayedRunnable execute(long time) {
		isDone = false;
		endTime = time;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				runnable.run();
				timer.purge();
				isDone = true;
			}
			
		}, time-System.currentTimeMillis());
		return this;
	}
	public double getTimeRemaining() {
		return (isDone ? 0 : endTime-System.currentTimeMillis());
	}
	public boolean isDone() {
		return isDone;
	}
}