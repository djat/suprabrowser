/**
 * 
 */
package ss.client.event;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ShowSystemListener implements SelectionListener {

	private MessagesPane mp;
	
	private SupraSphereFrame sF;
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ShowSystemListener.class);
	
	public ShowSystemListener(MessagesPane mp) {
		this.mp = mp;
	}
	
	public ShowSystemListener(SupraSphereFrame sF) {
		this.sF = sF;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	
	public void widgetSelected(SelectionEvent se) {
		boolean selection = false;
		
		if(this.mp != null) {
			selection = ((MenuItem)se.widget).getSelection();
			showSystemMessages(this.mp, selection);
		} else {
			MessagesPane mp = this.sF.tabbedPane.getSelectedMessagesPane();
			if(mp != null) {
				selection = ((MenuItem)se.widget).getSelection();
				mp.selectShowSystemButton(selection);
				showSystemMessages(mp, selection);
			}
		}
	}

	private void showSystemMessages(MessagesPane mp, boolean selection) {
		if(selection) {
			mp.showSystemMessages();
		} else {
			mp.hideSystemMessages();
		}
	}
}
