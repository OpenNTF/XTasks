package org.openntf.xtasks.xsp;

public interface IBackgroundTask extends Runnable {

	public String getName();

	public void run();
	
	public void cancel();
	
	public boolean isWorking();
	public boolean isStarted();
	
	public String getStatusJSON(); 
}