/**
 * 
 */
package ss.smtp.sender;

import java.util.ArrayList;
import java.util.List;

import ss.smtp.sender.MailerWorker.MailerWorkerListener;

/**
 * @author zobo
 *
 */
public class MailerTask {
	
	public interface MailerTaskListener{
		
		public void solved( SendingElement element );
		
		public void failed( SendingElement element );
		
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MailerTask.class);
	
	private SendingElement originalElement;
	
	private SendingElement element;
	
	private final List<String> mxHosts;
	
	private List<MailerTaskListener> listeners = new ArrayList<MailerTaskListener>();
	
	private MailerWorker currentWorker = null;

	public MailerTask( final SendingElement element, final List<String> mxHosts ){
		this.originalElement = element;
		this.mxHosts = new ArrayList<String>();
		if ( mxHosts != null ) {
			this.mxHosts.addAll( mxHosts );
		}
	}
	
	public void addListener( final MailerTaskListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerTaskListener can not be null");
		}
		this.listeners.add( listener );
	}
	
	public void removeListener( final MailerTaskListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerTaskListener can not be null");
		}
		this.listeners.remove( listener );
	}
	
	public void solve(){
		if ( this.originalElement == null ) {
			logger.error("SendingElement is null");
			failed();
			return;
		}
		if ( (this.mxHosts == null) || (this.mxHosts.isEmpty()) ) {
			logger.error("mxHosts is empty");
			failed();
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Delivering to mxHosts... (size: " + this.mxHosts.size() + "):");
			for ( String h : this.mxHosts ) {
				logger.debug("mxHost : " + h);
			}
		}
		final String host = this.mxHosts.get(0);
		this.mxHosts.remove(0);
		solvePerHost(this.originalElement, host);
	}
	
	public void checkState(){
		if ( this.currentWorker != null ) {
			this.currentWorker.checkState();
		}
	}
	
	private void resolve(){
		if ( this.mxHosts.isEmpty() ) {
			failed();
			return;
		}
		final String host = this.mxHosts.get(0);
		this.mxHosts.remove(0);
		try {
			this.element = SendingElementFactory.recreate(
					((this.element!=null) ? this.element : this.originalElement), host);
		} catch (Exception ex) {
			logger.error( "Cannot recreate sending element",ex);
			failed();
			return;
		}
		solvePerHost( this.element, host );
	}
	
	private void solvePerHost( final SendingElement element, final String host ){
		this.currentWorker = new MailerWorker(element, host);
		this.currentWorker.addListener(new MailerWorkerListener(){

			public void operationFailed() {
				resolve();
			}

			public void operationSuccessfullyFinished() {
				solved();
			}

			public void operationTimeOuted() {
				resolve();
			}
			
		});
		this.currentWorker.start();
	}
	
	private void solved() {
		for ( MailerTaskListener listener : this.listeners ) {
			listener.solved( getElement() );
		}
	}
	
	private void failed() {
		for ( MailerTaskListener listener : this.listeners ) {
			listener.failed( getElement() );
		}
	}

	public SendingElement getElement() {
		return this.originalElement;
	}
}
