package ss.client.debug.deadlock;

import org.eclipse.swt.widgets.Display;

import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;
import ss.common.UiUtils;


public class UiDeadLockGuard extends DeadLockGuard {

	public static final int MESSAGE_SENDER_SLEEP_TIME = 3000;
	
	private final Thread senderThread = new Thread( new MessageSender() );
	
	private Display display = null;
	
	/**
	 * @param deadlockMessagesListener
	 */
	public UiDeadLockGuard(IDeadlockMessagesListener deadlockMessagesListener) {
		super(deadlockMessagesListener);
		this.senderThread.setDaemon( true );
		this.senderThread.setName( IdentityUtils.getNextRuntimeIdForThread( MessageSender.class ) );
	}
	
	public synchronized void start( Display display ) {
		if (display == null) {
			throw new ArgumentNullPointerException("display");
		}
		if ( this.display != null ) {
			throw new IllegalStateException( "Guard already started" );
		}
		this.display = display;
		this.senderThread.start();
		super.start();
	}

	private class MessageSender implements Runnable {

		private Runnable uiMessageDispatcher = new Runnable() {
			public void run() {
				notifyAlive();				
			}
		};
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while( !isDisposed() ) {
				try {
					if ( !UiDeadLockGuard.this.display.isDisposed() ) {
						UiUtils.swtInvoke( this.uiMessageDispatcher );
						Thread.sleep( MESSAGE_SENDER_SLEEP_TIME );
					}
					else {
						dispose();
					}										
				} catch (InterruptedException ex) {
					dispose();
				}				
			}
		}
	}

	@Override
	protected void disposing() {
		super.disposing();
		this.senderThread.interrupt();
	}
	
	

}
 