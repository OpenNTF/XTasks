package org.openntf.xtasks.xsp.beans;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.openntf.xtasks.xsp.AbstractTaskManager;
import org.openntf.xtasks.xsp.IBackgroundTask;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.events.ApplicationListener;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;

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

	public static void progressAgent() throws IOException {
		
		try {
			TaskManagerBean taskManager=get();
			
			FacesContext fc=FacesContext.getCurrentInstance();
			XSPContext context=XSPContext.getXSPContext(fc);
			ExternalContext ec=fc.getExternalContext();
			
			ResponseWriter writer=fc.getResponseWriter();
			XspHttpServletResponse response=(XspHttpServletResponse)ec.getResponse();
			
			response.setContentType("application/json");	
			response.setHeader("Cache-Control", "no-cache");

			String taskId=context.getUrlParameter("task");
			boolean first=true;
			
			writer.append("[");
			
			for(IBackgroundTask task:taskManager.getTasks(taskId, false)) {
				if(!first) {
					writer.append(",");
					first=false;
				}
				writer.append(task.getStatusJSON());    		
			}

			writer.append("]");
			writer.endDocument();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	
}
