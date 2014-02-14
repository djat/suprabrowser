/**
 * 
 */
package ss.client.ui.viewers.comment;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class CommentApplicationWindow extends Dialog {

	private CommentTabFolder folder;
	private String selection;
	private Hashtable session;
	private String comment;
	private CommentStatement selectedComment;
	private MessagesPane mp;
	private String number;
	private Statement parentStatement;
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(CommentApplicationWindow.class);
	
	public CommentApplicationWindow(MessagesPane mp, CommentStatement viewComment) {
		super(mp.sF.getShell());
		this.mp = mp;
		this.selectedComment = viewComment;
		this.number = viewComment.getNumber();
		if(this.selectedComment!=null) {
			this.selection = this.selectedComment.getSelectedBody();
			this.comment = this.selectedComment.getComment();
		}
		this.session = mp.getRawSession();
		this.parentStatement = Statement.wrap(viewComment.getBindedDocument());
	}
	
	public CommentApplicationWindow(MessagesPane mp, String selection, int number) {
		super(SupraSphereFrame.INSTANCE.getShell());
		this.mp = mp;
		this.selection = selection;
		this.session = this.mp.getRawSession();
		this.number = new Integer(number).toString();
		if(mp.getLastSelectedDoc()==null) {
			return;
		}
		this.parentStatement = Statement.wrap(mp.getLastSelectedDoc());
	}
	
	protected Control createContents(Composite parent) {
		getShell().setLayout(new GridLayout());
		getShell().setText("Comment Window");
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new FillLayout());
		
		this.folder = new CommentTabFolder(parent, this);
		if(this.selectedComment==null) {
			this.folder.setSelection(0);
			this.folder.getPostCommentPane().getInputField().setFocus();
		} else {
			this.folder.setSelection(1);
		}
		parent.pack();
	    this.getShell().setSize(480, 400);
	    this.getShell().setVisible(true);
		return parent;
	}
	
	public MessagesPane getMP() {
		return this.mp;
	}
	
	public String getSelection() {
		return this.selection;
	}
	
	public Hashtable getSession() {
		return this.session;
	}
	
	public CommentStatement getSelectedComment() {
		return this.selectedComment;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setContent(String str) {
		this.comment = str;
	}
	
	public CommentTabFolder getTabFolder() {
		return this.folder;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param comment2
	 */
	public void setSelectedComment(CommentStatement comment) {
		this.selectedComment = comment;
	}
	
	public Statement getParentStatement() {
		return this.parentStatement;
	}

	@Override
	protected int getShellStyle() {
		return SWT.RESIZE | SWT.CLOSE | SWT.TITLE;
	}

	@Override
	protected ShellListener getShellListener() {
		return new ShellAdapter() {
			public void shellClosed(ShellEvent se) {
				se.doit = false;
				getTabFolder().getBlockCommentsPane().getTextContainer().dispose();
				se.widget.dispose();
			}
		};
	}

	@Override
	public boolean close() {
		getTabFolder().getBlockCommentsPane().getTextContainer().dispose();
		getShell().dispose();
		return true;
	}
	
	
	
	
}
