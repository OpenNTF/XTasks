/**
 * Modified and used with permission of Stephan H. Wissel
 * 
 * Original Code: http://www.wissel.net/blog/d6plinks/SHWL-99U64Q
 * 
 */

package org.openntf.xtasks.xsp;

import lotus.domino.NotesException;
import lotus.domino.Session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.xtasks.misc.TaskProgressMonitor;
import org.openntf.xtasks.misc.Utils;

import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.domino.xsp.module.nsf.SessionCloner;

public abstract class AbstractTask implements IBackgroundTask {
    protected final Session notesSession;

    protected static final int STATUS_NEW=0;
    protected static final int STATUS_RUNNING=1;
    protected static final int STATUS_DONE=-1;
    protected static final int STATUS_TERMINATED=-2;
    
    private SessionCloner sessionCloner;
    private NSFComponentModule module;

    private String name="";
    
    private TaskProgressMonitor progressMonitor;

    private int status=STATUS_NEW;
    
    public AbstractTask() {
		this(Utils.getUnique(), null);
	}

    public AbstractTask(String name, final Session optionalSession) {
        // optionalSession MUST be NULL when this should run in a thread, contain a session when
        // the class is running in the same thread as it was constructed
        this.name=name;
    	this.notesSession = optionalSession;
        this.progressMonitor=new TaskProgressMonitor(name);
        this.setDominoContextCloner();
    }

    private void setDominoContextCloner() {
        // Domino stuff to be able to get a cloned session
        if (this.notesSession == null) {
            try {
                this.module = NotesContext.getCurrent().getModule();
                this.sessionCloner = SessionCloner.getSessionCloner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
	public void run() {
		
		if(status==STATUS_TERMINATED) return;
		
		try {
        	Session session;
            if (this.notesSession == null) {
                NotesContext context = new NotesContext(this.module);
                NotesContext.initThread(context);
                session = this.sessionCloner.getSession();
            } else {
                // We run in an established session
                session = this.notesSession;
            }
            
            status=STATUS_RUNNING;
 
            this.runNotes(session, progressMonitor);
            
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            status=STATUS_DONE;
            if (this.notesSession == null) {
                NotesContext.termThread();
                try {
                    this.sessionCloner.recycle();
                } catch (NotesException e1) {
                    e1.printStackTrace();
                }
            }
        }
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isStarted() {
		return (status!=STATUS_NEW);
	}

	public boolean isWorking() {
		return (status==STATUS_RUNNING);
	}

	public void cancel() {
		if(isWorking()) {
			progressMonitor.setCanceled(true);
		}
		status=STATUS_TERMINATED;
	}

	public String getStatusJSON() {
		return progressMonitor.getStatusJSON();
	}

	
    protected abstract void runNotes(Session session, IProgressMonitor monitor);

}