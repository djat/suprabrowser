/**
 * 
 */
package ss.smtp.sender;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Transport;

/**
 * @author zobo
 *
 */
public class MailerWorker {
	
	public interface MailerWorkerListener {
		
		public void operationSuccessfullyFinished();
		
		public void operationFailed();
		
		public void operationTimeOuted();
		
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MailerWorker.class);
	
	private static final long ALLOWED_TIMEOUT = 3*60*1000;

	private long deliverTimeStarted = Long.MAX_VALUE;
	
	private final SendingElement element;
	
	private final String mxHost;
	
	private Thread thread;
	
	private final List<MailerWorkerListener> listeners = new ArrayList<MailerWorkerListener>();
	
	public MailerWorker( final SendingElement element, final String mxHost ){
		this.element = element;
		this.mxHost = mxHost;
	}
	
	public void addListener( final MailerWorkerListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerWorkerListener can not be null");
		}
		this.listeners.add( listener );
	}
	
	public void removeListener( final MailerWorkerListener listener ){
		if ( listener == null ) {
			throw new NullPointerException("MailerWorkerListener can not be null");
		}
		this.listeners.remove( listener );
	}
	
	public void checkState(){
		if ((System.currentTimeMillis() - this.deliverTimeStarted) > ALLOWED_TIMEOUT){
			this.thread.interrupt();
			this.thread = null;
			for ( MailerWorkerListener listener : this.listeners ) { 
				listener.operationTimeOuted();
			}
		}
	}
	
	public void start(){
		this.thread = new Thread(){

			@Override
			public void run() {
				try {
					deliverImplOneMXHost( MailerWorker.this.element, 
							MailerWorker.this.mxHost);
					for ( MailerWorkerListener listener : MailerWorker.this.listeners ) { 
						listener.operationSuccessfullyFinished();
					}
				} catch (Exception ex) {
					logger.error( "Exception for xmHost: " + MailerWorker.this.mxHost,ex);
					for ( MailerWorkerListener listener : MailerWorker.this.listeners ) { 
						listener.operationFailed();
					}
				}
			}

		};
		this.thread.start();
	}
	
	private void deliverImplOneMXHost( final SendingElement element, final String mxHost ) throws Exception {
		startDelivering();
		if (logger.isDebugEnabled()) {
			logger.debug("Connecting to host : " + mxHost);
		}
		final Transport transport = element.getSendsession().getTransport("smtp");
		transport.connect(mxHost, null, null);
		if (logger.isDebugEnabled()){
			logger.debug("Sending message by " + element.getSendList());
		}
		transport.sendMessage(element.getSmessage(), element.getSendList().getAddresses());
		transport.close();
		if (logger.isDebugEnabled()){
			logger.debug("Sent to " + mxHost);
		}		
	}
	
	private void startDelivering(){
		if (logger.isDebugEnabled()) {
			logger.debug("start Delivering");
		}
		this.deliverTimeStarted = System.currentTimeMillis();
	}
}
