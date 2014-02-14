package ss.client.ui.viewers.comment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

public class CommentTabItem extends CTabItem {
	
	private Composite internalPane;
	private CommentTabFolder tabFolder;
	
	public CommentTabItem(CommentTabFolder parent) {
		super(parent, SWT.BORDER);
		this.tabFolder = parent;
	}
	
	public CommentTabFolder getTabFolder() {
		return this.tabFolder;
	}
	
	public Composite getInternalPane() {
		return this.internalPane;
	}
	
	public void setContent(Composite comp) {
		this.internalPane = comp;
		this.setControl(comp);
	}
}
