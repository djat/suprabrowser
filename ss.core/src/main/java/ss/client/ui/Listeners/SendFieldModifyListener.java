package ss.client.ui.Listeners;

import java.util.Hashtable;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;


import ss.client.event.SendCreateAction;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;

public class SendFieldModifyListener extends CommonInputTextPaneListener implements ModifyListener , KeyListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendFieldModifyListener.class);
			
	private final Hashtable session;
	
	public SendFieldModifyListener(SupraSphereFrame sF, MessagesPane mp) {
		super(sF, mp); 
		this.session = mp.getRawSession();
	}
	
	public void modifyText(ModifyEvent e) {
		final String text = ((Text)e.getSource()).getText();
		if(text!=null && !text.trim().equals("")) {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "notifyUserTyped " + text );
			}
			notifyUserTyped();
		}
		else {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "skip modify event " + text );
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
        if (e.keyCode == 13) {
        	sendMessage();         
        	e.doit = false;           
        }
	}

	private void sendMessage() {
		SendCreateAction sca = new SendCreateAction( this.messagesPaneOwner );
		sca.doSendCreateAction( super.getSupraSphereFrame(), this.session );
		notifyMessageSent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		//NOOP		
	}
	
	

}
