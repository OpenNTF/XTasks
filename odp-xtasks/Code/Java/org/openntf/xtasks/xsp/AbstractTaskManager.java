/**
 * Modified with the permission of the original copyright owner Stephan H. Wissel 
 * and available under Apache License 2.0. 
 * 
 * Original Code: http://www.wissel.net/blog/d6plinks/SHWL-99U64Q
 * 
 */

package org.openntf.xtasks.xsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openntf.xtasks.misc.Utils;

public class AbstractTaskManager implements Serializable {

	private static final long serialVersionUID = 6210581748379618339L;

	// # of threads running concurrently
    protected static final int DEFAULT_THREADPOOLSIZE = 2;
    
	private final ExecutorService service;
	protected TreeMap<Calendar, IBackgroundTask> taskList;
    
    public AbstractTaskManager() {
    	this(DEFAULT_THREADPOOLSIZE);
    }
    
    public AbstractTaskManager(int poolSize) {
    	service = Executors.newFixedThreadPool(poolSize);
    	taskList=new TreeMap<Calendar, IBackgroundTask>(Collections.reverseOrder());
    }

    public synchronized void submitService(final IBackgroundTask task) {
        if (task == null) {
            throw new RuntimeException("Null Task is not acceptable");
        }

        if(this.service.isTerminated()) {
        	throw new RuntimeException("Task Manager terminated... No new task can be accepted...");
        }
        
        taskList.put(Calendar.getInstance(), task);
        this.service.execute(task);
    }

    public List<IBackgroundTask> getTasks(String nameTip, boolean onlyCompleted) {
    	List<IBackgroundTask> result=new ArrayList<IBackgroundTask>(taskList.size());
    	
    	for(IBackgroundTask task:taskList.values()) {
    		boolean isThatOK=true;
    		
    		if(onlyCompleted && ! task.isWorking()) {
    			isThatOK=false;
    		} 
    		
    		if(! Utils.isEmptyString(nameTip)) {
    			isThatOK=task.getName().matches(nameTip);
    		}
    		
    		if(isThatOK) result.add(task);
    	}
    	
    	return result;
    }

    /**
     * Stop Tasks and terminates the thread pool.
     * From this point, task manager will be useless. 
     */
    
    public void terminate() {
    	stopTasks();
    	if ((this.service != null) && !this.service.isTerminated()) {
    		this.service.shutdownNow();
    	}
		try {
        	if(!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
        		System.out.println("Waiting for all tasks to be cancelled...");
        		
        		for(int i=0; i<10; i++) {
						if(service.awaitTermination(1, TimeUnit.SECONDS)) {
							break;
						}
        		}
        	}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        				
    	
    }
    
    public void stopTasks() {
       	System.out.println("Stopping Tasks...");
        if ((this.service != null) && !this.service.isTerminated()) {
        	for(IBackgroundTask task:taskList.values()) {
        		if(task.isWorking()) {
        			task.cancel();
        			System.out.println("Canceling '"+task.getName()+"'");
        		} else if(!task.isStarted()) {
        			task.cancel();
        			System.out.println("'"+task.getName()+"' won't work");
        		}
        	}
			
			taskList.clear();
			System.out.println("Stopped all tasks...");
        }
    }
}

