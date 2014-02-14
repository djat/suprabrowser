package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;

public class ShowAllButtonSelectionListener implements SelectionListener {

	private MessagesPane mp;
	
	public ShowAllButtonSelectionListener(PreviewAreaDocking docking) {
		this.mp = docking.getMessagesPane();
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(SelectionEvent e) {
		Statement statement = null;
		if(this.mp.getMessagesTree().getSelectedDoc()!=null) {
			statement = Statement.wrap(this.mp.getMessagesTree().getSelectedDoc());
		}
		
		this.mp.loadWindow(statement);
		
		this.mp.setInsertable(true);
		this.mp.setNeedOpenComment(false);
		this.mp.deselectThreadViewButton();
		this.mp.setThreadView(false);
		
		this.mp.reorganizePreviewButtons(new TerseStatement());
	}

}
