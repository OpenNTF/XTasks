package org.openntf.xtasks.samples;

import java.util.Random;

import lotus.domino.Session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xtasks.xsp.AbstractTask;

public class SampleTask extends AbstractTask {
		
	@Override
	protected void runNotes(Session session, IProgressMonitor monitor) {
		Random generator=new Random();
		int taskId=generator.nextInt(1000);
		int waitingTime=20; //+generator.nextInt(19);
		
		System.out.println("Started SampleTask - " + taskId + " (will take "+waitingTime+" seconds)");
		
		for(int i=0; i<waitingTime; i++) {
			if(i % 5==0) {
				System.out.println("Ticking SampleTask - " + taskId);
			}
			
			if(monitor.isCanceled()) {
				System.out.println("Canceled SampleTask - " + taskId);
				return;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("Finished SampleTask - " + taskId);
	}


}
