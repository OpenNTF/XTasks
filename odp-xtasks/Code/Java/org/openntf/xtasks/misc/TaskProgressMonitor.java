package org.openntf.xtasks.misc;

import org.eclipse.core.runtime.IProgressMonitor;

public class TaskProgressMonitor implements IProgressMonitor {

	private boolean canceled;

	private String label;
	private int totalWork = 100;
	private int worked = 0;
	private String taskName;
	private String subTaskName;

	/**
	 * @param label
	 */
	public TaskProgressMonitor(String label) {
		this.label=label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		setTaskName(name);
		this.totalWork = totalWork;
		worked = 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done() {
		worked = totalWork;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double work) {
		if ( work == 0 ){
			return;
		}
		worked += work;
		if ( worked > totalWork ){
			worked = totalWork;
		}

		if ( worked < 0 ){
			worked = 0;
		}       
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean value) {
		this.canceled = value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		taskName = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		subTaskName = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		internalWorked( work );
	}

	public String getStatusJSON() {
		return "{ 	taskLabel : '" + getTaskLabel() + "'," +
				"	isCanceled : '" + isCanceled() + "'," +
				"	isCompleted : '" + (worked==totalWork) + "'," +
				"	worked : " + worked + "," +
				"	totalWork : " + totalWork + "," +
				"	completion : " + (int)((double)worked) / ((double)totalWork) * 100 + "}"; 
	}
	

	/**
	 * @return Task Label in "Label > Task > SubTask" format
	 */
	private String getTaskLabel() {
		String subLabel="";
				
		if(Utils.isEmptyString(taskName)) {
			subLabel=Utils.isEmptyString(subTaskName)?"":subTaskName;
		} else {
			subLabel=taskName+(Utils.isEmptyString(subTaskName)?"":" > "+subTaskName);
		}

		return label+(subLabel.equals("")?"":" > "+subLabel);
	}
	
}
