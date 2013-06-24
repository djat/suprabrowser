/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.Listeners.CommonInputTextPaneListener;

/**
 * @author roman
 *
 */
public class ViewersInputPaneListener extends CommonInputTextPaneListener
		implements KeyListener {

	public ViewersInputPaneListener(SupraSphereFrame sF, MessagesPane mP) {
		super(sF, mP);
	}
	

	public void keyPressed(KeyEvent ke) {	
		try {
			if (ke.character == '\n') {
				notifyMessageSent();
			} else {
				notifyUserTyped();
			}
		} catch (NullPointerException npe) {
		}
	}

	public void keyReleased(KeyEvent arg0) {

	}

}
