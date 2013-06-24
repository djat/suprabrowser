/**
 * 
 */
package ss.client.ui.viewers.comment;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ss.client.event.CommentedPlacesSelectionListener;
import ss.client.ui.MessagesPane;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class AllCommentedPlacesPane extends Composite {

	private CommentTabFolder parentFolder;
	private Statement parentStatement;
	private Vector<CommentStatement> comments;
	private Vector<String> placeNumbers;
	private MessagesPane mp;
	private Hashtable<Button, CommentStatement> buttonTable;
	Composite comp;
	private static Logger logger = SSLogger.getLogger(AllCommentedPlacesPane.class);
	
	public AllCommentedPlacesPane(CommentTabFolder parentFolder) {
		super(parentFolder, SWT.NONE);
		this.setLayout();
		this.parentFolder = parentFolder;
		this.placeNumbers = new Vector<String>();
		this.comments = new Vector<CommentStatement>();
		initParentNode();
		layoutComponent();
		this.layout();
	}
	
	private void layoutComponent() {
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		this.setLayout(new FillLayout());
		
		this.comp = new Composite(this, SWT.NONE);
		this.comp.setLayout(new GridLayout(1, false));
	
		this.buttonTable = new Hashtable<Button, CommentStatement>();
		
		initPlacesList();
	}

	/**
	 * 
	 */
	private void initPlacesList() {

		try {
			List<Document> subtreeDocs = this.mp.getMessagesTree()
					.getChildrenFor(this.parentStatement.getMessageId());
			if (logger.isDebugEnabled()) {
				logger.debug("parent statement : "+this.parentStatement);
			}
			for (Document doc : subtreeDocs) {
				if (Statement.wrap(doc).isComment()) {
					CommentStatement comment = CommentStatement.wrap(doc);
					
					Button button = new Button(this.comp, SWT.RADIO);
					button.setText(comment.getSelectedBody());

					this.buttonTable.put(button, comment);

					this.placeNumbers.add(comment.getNumber());
				}
			}

			for (Button button : this.buttonTable.keySet()) {
				if (button.getText().equals(
						this.parentFolder.getApplWindow().getSelection())
						&& this.buttonTable.get(button).getNumber().equals(
								this.parentFolder.getApplWindow().getNumber())) {
					button.setSelection(true);
				}
				button
						.addSelectionListener(new CommentedPlacesSelectionListener(
								this.parentFolder));
			}
		} catch (NullPointerException ex) {
			logger.error("empty list", ex);
		}
	}

	private void setLayout() {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.setLayoutData(data);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		
		this.setLayout(layout);
	}
	
	private void initParentNode() {
		this.mp = this.parentFolder.getApplWindow().getMP();
		for(Statement statement : this.mp.getTableStatements()) {
			if(this.parentFolder.getApplWindow().getSelectedComment()!=null) {
				if(statement.getMessageId().equals(
						this.parentFolder.getApplWindow().getSelectedComment().getCommentId())) {
					this.parentStatement = statement;
				}
			} 
		}
	}
	
	public Vector<CommentStatement> getComments() {
		return this.comments;
	}
	
	public Hashtable<Button, CommentStatement> getButtonTable() {
		return this.buttonTable;
	}
}
