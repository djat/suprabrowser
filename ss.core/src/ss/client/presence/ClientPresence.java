/*
 * Created on Feb 23, 2005
 * 
 */
package ss.client.presence;

/*
 * Created on Feb 16, 2005
 */


import org.dom4j.Document;

import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.common.presence.KeyTypedEvent;
import ss.common.presence.MessageSelectedPresenceEvent;
import ss.common.presence.StoppedTypingEvent;
import ss.common.presence.UserLogginedEvent;

/**
 * Description of the Class
 * 
 * @author david
 * @created September 19, 2003
 */
public final class ClientPresence {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientPresence.class);

	private final ClientPresenceProtocol protocol;
	
	private final MessagesPane messagesPaneOwner; 
	
	private final String userContactName;

	private final String sphereId;
	
	private final TypingQueue typingQueue;
	
	/**
	 * @param protocol
	 * @param messagesPaneOwner
	 */
	public ClientPresence(final ClientPresenceProtocol protocol, final MessagesPane messagesPaneOwner) {
		super();
		this.protocol = protocol;
		this.messagesPaneOwner = messagesPaneOwner;
		this.sphereId = this.messagesPaneOwner.getSphereId();
		this.userContactName = this.messagesPaneOwner.getUserSession().getRealName();
		this.typingQueue = new TypingQueue( this.protocol, this.sphereId, this.userContactName );
		this.protocol.fireEvent( new UserLogginedEvent( this.sphereId ) );
		logger.debug( "ClientPresence created " + this );
	}

	/**
	 * 
	 */
	public void notifyStopTyping() {
		this.typingQueue.sendMessage( new StoppedTypingEvent( this.sphereId, this.userContactName) );
		this.typingQueue.setAlreadyTypedFalse();
	}
	
	/**
	 * 
	 */
	public void notifyMessageSent() {
		checkAlive();
		notifyStopTyping();
	}
	
	/**
	 * 
	 */
	public void notifyMessageSelected() {
		checkAlive();
		this.protocol.fireEvent( new MessageSelectedPresenceEvent( this.sphereId ) );
	}

	/**
	 * 
	 */
	public void notifyUserTyped() {
		checkAlive();
		logger.debug( "notifyUserTyped" );
		if ( this.typingQueue.getAlready() == false ) {
            String replyId = null;
            if ( isReply() ) {
            	Document document = this.messagesPaneOwner.getLastSelectedDoc();
                if ( document != null) {
                    replyId = document.getRootElement().element("message_id").attributeValue(
                                    "value");
                }
            }
            logger.debug( "sending keytyped event" );
            this.typingQueue.sendMessage( new KeyTypedEvent( this.sphereId, this.userContactName, replyId ) );
            this.typingQueue.setAlready();

        } else {
            this.typingQueue.typeKeyAndSetSendFalse();
        }
	}

	private boolean isReply() {
		if ( this.messagesPaneOwner.getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) this.messagesPaneOwner
					.getControlPanel();
			return controlPanel.isReplyChecked();
		}
		else {
			return false;
		}
	}
	
	/**
	 * 
	 */
	public void release() {
		notifyStopTyping();
		this.typingQueue.shutdown();		
	}
	
	public boolean isAlive() {
		return this.typingQueue.isAlive();
	}
	
	private void checkAlive() {
		if ( !isAlive()) {
			logger.error( "Presence is dead " + this );
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Presence[SphereId:" + this.sphereId+"]";
	}
		
}
