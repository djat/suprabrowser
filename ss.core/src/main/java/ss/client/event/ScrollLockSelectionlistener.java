package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.docking.PreviewAreaDocking;

public class ScrollLockSelectionlistener implements SelectionListener {

	private MessagesPane mp;
	
	public ScrollLockSelectionlistener(PreviewAreaDocking docking) {
		this.mp = docking.getMessagesPane();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent e) {
		this.mp.setScrollLock(!this.mp.isScrollLocked());

	}

}
