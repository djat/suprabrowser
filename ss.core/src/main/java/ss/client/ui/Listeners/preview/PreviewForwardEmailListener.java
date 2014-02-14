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
public class PreviewForwardEmailListener extends SelectionAdapter {

	private MessagesPane mp;
	
	Hashtable session = null;
	
	public PreviewForwardEmailListener(MessagesPane mp) {
		this.mp = mp;
		this.session = mp.client.getSession();
	}
	
	public void widgetSelected(SelectionEvent arg0) {
		ExternalEmailStatement email = ExternalEmailStatement.wrap(this.mp.getSelectedStatement().getBindedDocument());
        (new EmailController(this.mp, this.session)).clickedForwardEmail(email);
    }
}
