/**
 * 
 */
package ss.client.event.tagging;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class ShowAllTagsSelectionListener implements SelectionListener {

	private MessagesPane mp;
	
	public ShowAllTagsSelectionListener(MessagesPane mp) {
		super();
		this.mp = mp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		boolean selection = ((MenuItem)e.getSource()).getSelection(); 
		if (selection) {
			showAll();
		} else {
			hideAll();
		}
	}

	private void hideAll() {
		TagManager.INSTANCE.close( this.mp );
	}

	private void showAll() {
		TagManager.INSTANCE.open( this.mp );
	}

}
