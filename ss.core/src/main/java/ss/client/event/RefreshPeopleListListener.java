/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import ss.client.ui.MessagesPane;
import ss.common.ArgumentNullPointerException;

/**
 * @author roman
 * 
 */
public class RefreshPeopleListListener implements MouseListener {

	private final MessagesPane messagesPane;

	private final boolean clearSelection;

	public RefreshPeopleListListener(MessagesPane messagesPane, boolean clearSelection) {
		if ( messagesPane == null ) {
			throw new ArgumentNullPointerException( "messagesPane" );
		}
		this.messagesPane = messagesPane;
		this.clearSelection = clearSelection;
	}

	public void mouseDoubleClick(MouseEvent arg0) {
	}

	public void mouseDown(MouseEvent me) {
		this.messagesPane.recheckPeopleListColors(this.clearSelection);
	}

	public void mouseUp(MouseEvent arg0) {
	}

}
