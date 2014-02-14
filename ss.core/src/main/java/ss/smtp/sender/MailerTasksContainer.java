/**
 * 
 */
package ss.smtp.sender;

import java.util.ArrayList;
import java.util.List;

import ss.common.ThreadUtils;
import ss.smtp.sender.MailerTask.MailerTaskListener;

/**
 * @author zobo
 *
 */
public class MailerTasksContainer {
	
	public interface MailerTasksContainerListener {
		
		public void elementSended( SendingElement sendingElement );
		
		public void elementFailed( SendingElement sendingElement );
		
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MailerTasksContainer.class);
	
	private List<MailerTask> tasks = new ArrayList<MailerTask>(); 
	
	private List<MailerTasksContainerListener> listeners = new ArrayList<MailerTasksContainerListener>();
	
	private final Thread timeoutChecker; 
	
	public MailerTasksContainer(){
		this.timeoutChecker = new Thread(){

			@Override
			public void run() {
				try {
					sleep( 3000 );
					List<MailerTask> tasks = new ArrayList<MailerTask>();
					tasks.addAll( MailerTasksContainer.this.tasks );
					for ( MailerTask task : tasks ) {
						task.checkState();
					}
				} catch (InterruptedException ex) {
					logger.error( "timeoutChecker interrapted",ex);
				}
			}
			
		};
		ThreadUtils.startDemon(this.timeoutChecker, "timeout for email checker");
	}
	
	public void addListener( final MailerTasksContainerListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerTasksContainerListener can not be null");
		}
		this.listeners.add( listener );
	}
	
	public void removeListener( final MailerTasksContainerListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerTasksContainerListener can not be null");
		}
		this.listeners.remove( listener );
	}
	
	public void addTask( final SendingElement sendingElement ){
		if ( sendingElement == null ) {
			logger.error("sendingElement is null");
			return;
		}
		
		final List<String> mxHosts = sendingElement.getMXHosts();
		final MailerTask task = new MailerTask( sendingElement, mxHosts );
		
		synchronized ( this.tasks ) {
			this.tasks.add( task );
		}
		
		task.addListener(new MailerTaskListener(){

			public void failed(SendingElement element) {
				removeTask(task);
				notifyFailed(element);
			}

			public void solved(SendingElement element) {
				removeTask(task);
				notifySended(element);
			}
			
		});
		
		task.solve();
	}
	
	private void removeTask( final MailerTask task ){
		synchronized ( this.tasks ) {
			this.tasks.remove( task );
		}
	}
	
	private void notifySended( SendingElement sendingElement ) {
		for ( MailerTasksContainerListener listener : this.listeners ) {
			listener.elementSended(sendingElement);
		}
	}
	
	private void notifyFailed( SendingElement sendingElement ) {
		for ( MailerTasksContainerListener listener : this.listeners ) {
			listener.elementFailed(sendingElement);
		}
	}
}
