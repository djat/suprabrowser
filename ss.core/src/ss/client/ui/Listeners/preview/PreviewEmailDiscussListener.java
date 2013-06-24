/**
 * 
 */
package ss.client.ui.Listeners.preview;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.MessagesPane;

/**
 * @author roman
 *
 */
public class PreviewEmailDiscussListener extends SelectionAdapter {

	private MessagesPane mp;
	
	public PreviewEmailDiscussListener(MessagesPane mp) {
		this.mp = mp;
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		if(((Button)se.widget).getSelection()) {
        	this.mp.getSmallBrowser().highlightAllCommentedPlaces(this.mp.getSelectedStatement().getMessageId());
        } else {
        	this.mp.getSmallBrowser().unhighlightCommentedPlaces();
        }
	}
}
