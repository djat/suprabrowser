/**
 * 
 */
package ss.client.ui.Listeners.preview;

import java.util.Hashtable;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ss.client.ui.MessagesPane;
import ss.client.ui.email.EmailController;
import ss.domainmodel.ExternalEmailStatement;

/**
 * @author roman
 *
 */
public class PreviewReplyEmailListener extends SelectionAdapter {

	private MessagesPane mp;
	
	private Hashtable session;
	
	public PreviewReplyEmailListener(MessagesPane mp) {
		this.mp = mp;
		this.session = mp.client.session;
	}
	
	public void widgetSelected(SelectionEvent arg0) {
		ExternalEmailStatement email = ExternalEmailStatement.wrap(this.mp.getSelectedStatement().getBindedDocument());
        (new EmailController(this.mp, this.session)).clickedReplyEmail(email);
    }
}
