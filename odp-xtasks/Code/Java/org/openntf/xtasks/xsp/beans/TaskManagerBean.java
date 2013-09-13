package org.openntf.xtasks.xsp.beans;

import javax.faces.context.FacesContext;

import org.openntf.xtasks.xsp.AbstractTaskManager;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.events.ApplicationListener;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class TaskManagerBean extends AbstractTaskManager {

	private static final long serialVersionUID = 3369708695166294518L;
	
	private static final String BEAN_NAME="taskManager";
	
	public TaskManagerBean() {
		System.out.println("Welcone to the task manager...");
		
		ApplicationEx app=ApplicationEx.getInstance(FacesContext.getCurrentInstance());
		
		app.addApplicationListener(new ApplicationListener() {

			public void applicationCreated(ApplicationEx app) {}

			public void applicationDestroyed(ApplicationEx app) {
				terminate();
			}
			
		});
		
	}

	public static TaskManagerBean get() {
		return (TaskManagerBean)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), BEAN_NAME);
	}
	
	public void dummy() {}
	
}
