/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tree.MessagesTreeActionDispatcher;

/**
 * @author roman
 *
 */
public class MessagesTreeMouseListenerSWT implements MouseListener {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeMouseListenerSWT.class);

	private MessagesTreeActionDispatcher listener;
	
	public MessagesTreeMouseListenerSWT(MessagesPane mp) {
		this.listener = new MessagesTreeActionDispatcher(mp);
	}
	
	public void mouseDoubleClick(MouseEvent me) {
		if (logger.isDebugEnabled()) {
			logger.debug("Double click in messages tree performed");
		}
		this.listener.processDoubleClickOnSelectedNode();
	}

	public void mouseDown(MouseEvent me) {
		if(me.button==1 && me.count==1) {
			this.listener.singleLeftMouseClicked();
		} else if(me.button==3 ) {
			if (((me.stateMask & SWT.ALT) == SWT.ALT) && ((me.stateMask & SWT.CTRL) == SWT.CTRL)) {
				this.listener.showMessagePageDebugConsole();
			}
			else {
				this.listener.singleRightMouseClicked();
			}
		}
		SupraSphereFrame.INSTANCE.getMenuBar().updateAssetMenu();
	}

	public void mouseUp(MouseEvent me) {
	}

	public MessagesTreeActionDispatcher getListener() {
		return this.listener;
	}
}
