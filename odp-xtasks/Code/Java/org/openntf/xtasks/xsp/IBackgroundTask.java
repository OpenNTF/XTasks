package org.openntf.xtasks.xsp;

public interface IBackgroundTask extends Runnable {

	public void run();
	public void cancel();
	public boolean isWorking();
	public String getName();
	public boolean isStarted();
	
}