package ss.client.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.Listeners.CommonInputTextPaneListener;

public class InputTextPaneListener extends CommonInputTextPaneListener
		implements KeyListener {

	public InputTextPaneListener(SupraSphereFrame sF, MessagesPane mP) {
		super(sF, mP);
	}

	public void keyTyped(KeyEvent ke) {
		try {
			if (ke.getKeyChar() == '\n') {
				// enterKeyAction();
			} else {
				notifyUserTyped();
			}
		} catch (NullPointerException npe) {
		}
	}

	public void keyPressed(KeyEvent ke) {
		try {
			if (ke.getKeyChar() == '\n') {
				notifyMessageSent();
			}
		} catch (NullPointerException npe) {
		}
	}

	public void keyReleased(KeyEvent event) {
	}

}
