package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.ui.ConversationManager;
import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;

public class ThreadViewSelectionListener implements SelectionListener {

	private MessagesPane mp;
	
	public ThreadViewSelectionListener( final MessagesPane mp ) {
		this.mp = mp;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void widgetSelected(SelectionEvent e) {
		boolean selection = ((MenuItem)e.getSource()).getSelection(); 
		this.mp.setThreadView(selection);
		if(selection) {
			if( this.mp.getLastSelectedDoc()!=null && this.mp.isInsertable()) {
				Statement st = Statement.wrap(this.mp.getLastSelectedDoc());
				ConversationManager cm = new ConversationManager(this.mp, st);
				cm.showConversation();
			} 
		}else {
			Statement st = null;
			if(this.mp.getLastSelectedDoc()!=null && this.mp.isInsertable()) {
				st = Statement.wrap(this.mp.getLastSelectedDoc());
				this.mp.loadWindow(st);
			}
		}

	}

}
