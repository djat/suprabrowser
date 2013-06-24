/**
 * 
 */
package ss.client.ui.Listeners;

import ss.client.presence.ClientPresence;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.ThreadUtils;

/**
 * @author zobo
 *
 */
public class CommonInputTextPaneListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(CommonInputTextPaneListener.class);
	
    private final SupraSphereFrame supraSphereFrame;

    private volatile ClientPresence clientPresence = null;
    
    protected final MessagesPane messagesPaneOwner;
    /**
     * 
     */
    public CommonInputTextPaneListener(SupraSphereFrame supraSphereFrame, MessagesPane messagesPane) {
        this.supraSphereFrame = supraSphereFrame;
        initClientPresence(messagesPane);
        this.messagesPaneOwner = messagesPane;
        logger.info("Init in inputtextpane");
    }

	private void initClientPresence(final MessagesPane messagesPane) {
		Runnable runnable = new Runnable() {
			public void run() {
				setClientPresence( messagesPane.getClientPresence() );
			}
		};
		ThreadUtils.start(runnable, "ClientPresence setup" );
	}
    
    /**
	 * @param clientPresence
	 */
	protected synchronized void setClientPresence(ClientPresence clientPresence) {
		this.clientPresence = clientPresence;
	}

	/**
	 * @return the sF
	 */
	public final SupraSphereFrame getSupraSphereFrame() {
		return this.supraSphereFrame;
	}

    /**
     * 
     */
    protected synchronized final void notifyUserTyped() {
    	if ( this.clientPresence != null ) {
    		this.clientPresence.notifyUserTyped();
    	}
    }

    /**
     * 
     */
    protected synchronized final void notifyMessageSent() {
    	if ( this.clientPresence != null ) {
    		this.clientPresence.notifyMessageSent();
    	}
	}

}
