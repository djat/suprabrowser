/**
 * 
 */
package ss.client.ui.Listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;

/**
 * @author zobo
 *
 */
public class ControlPanelInputTextPaneListener extends
        CommonInputTextPaneListener implements KeyListener{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ControlPanelInputTextPaneListener.class);
	
	boolean isObservedReply = false;
    /**
     * @param sF
     * @param mP
     */
    public ControlPanelInputTextPaneListener(SupraSphereFrame sF,
            MessagesPane mP) {
        super(sF, mP);
    }

    public void keyPressed(KeyEvent ke) {
    	final ControlPanel cp = (ControlPanel)this.messagesPaneOwner.getControlPanel();
    	if(ke.keyCode==13) {
    		cp.getMP().revertParentMessageColor();
    	}
    }

    public void keyReleased(KeyEvent event) {
        try {
            if (event.keyCode == 13) { // Enter
                notifyMessageSent();
            } 
        } catch (NullPointerException npe) {
        }
    }

}
