/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.presence;

import java.util.concurrent.atomic.AtomicBoolean;

import ss.common.ThreadUtils;
import ss.common.presence.AbstractPresenceEvent;
import ss.common.presence.StoppedTypingEvent;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
final class TypingQueue  {
    
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TypingQueue.class);
	
    private final ClientPresenceProtocol protocol;
        
    private final String sphereId;
    
    private final String userContactName;
    
    private final AtomicBoolean alive = new AtomicBoolean( true );
    
    private volatile boolean canStopTyping = false;
    
    private volatile boolean alreadyTyped = false;
        
    /**
	 * @param clientPresence
	 * @param userContactName
	 * @param sphereId
	 */
	public TypingQueue(ClientPresenceProtocol protocol, String sphereId, String userContactName) {
		super();
		this.protocol = protocol;
		this.sphereId = sphereId;
		this.userContactName = userContactName;
		ThreadUtils.startDemon( new Runnable() {
			public void run() {
				sendingLoop();
			} 
		}, getClass() );
    }    
    
    private void sendingLoop() {
    	try { 
			while (this.alive.get()) {
				if (getAlready() == true) {
					try {
						setCanStopTyping(true);
						Thread.sleep(4000);
						if (getCanStopTyping() == true) {
							stopTyping();
							setAlreadyTypedFalse();
						}
					} catch (InterruptedException ex) {
						//NOP
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						//NOP
					}
				}
			}
		}
		finally {
			shutdown();
		}
	}
    
    public synchronized void typeKeyAndSetSendFalse() {
        this.canStopTyping = false;
    }
    
    public void sendMessage(final AbstractPresenceEvent event) {
    	if ( logger.isDebugEnabled() ) {
    		logger.debug( "Notify user typing " + event );
    	}
        this.protocol.fireEvent( event );
    }

    private void stopTyping() {
    	sendMessage( new StoppedTypingEvent( this.sphereId, this.userContactName ) );
    }
    /**
     * @return
     */    
    public synchronized boolean getAlready() {
       return this.alreadyTyped;
    }
    
    public synchronized void setAlreadyTypedFalse() {
        this.alreadyTyped = false;        
    }
    
    public synchronized void setAlready() {
        this.alreadyTyped = true;
        this.canStopTyping = true;
    }
    
    public synchronized void setCanStopTyping(boolean can) {
        this.canStopTyping = can;
    }
    
    public synchronized boolean getCanStopTyping() {
        return this.canStopTyping;
    }

	/**
	 * 
	 */
	public void shutdown() {
		this.alive.set( false );
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return this.alive.get();
	}
    
	
}


