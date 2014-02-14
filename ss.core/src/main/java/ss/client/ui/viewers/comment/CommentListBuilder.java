package ss.client.ui.viewers.comment;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.util.SessionConstants;

public class CommentListBuilder {

	private String selection;
	private String comment_id;
	private MessagesPane mp;
	private Table table;
	private CommentApplicationWindow caw;
	private Label label;
	
	private static final String SUBJECT = "BLOCKCOMMENTSPANE.SUBJECT";
	
	private ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_COMMENT_BLOCKCOMMENTSPANE);
	
	public CommentListBuilder(CommentApplicationWindow caw, Table table, Label label) {
		this.mp = caw.getMP();
		this.selection = caw.getSelectedComment().getSelectedBody();
		this.comment_id = caw.getSelectedComment().getCommentId();
		this.table = table;
		this.caw = caw;
		this.label = label;
	}

	public void buildCommentList() {
		this.label.setText(this.bundle.getString(SUBJECT)+this.selection);

		for(Statement statement : this.mp.getTableStatements()) {
			if(statement.isComment()) {
				CommentStatement comment = CommentStatement.wrap(statement.getBindedDocument());
				if(comment.getCommentId().equals(this.comment_id) 
						&& comment.getSelectedBody().equals(this.selection)) {
					this.addItemInTable(comment);
				}
			}
		}
//		for(Document doc : docs) {
//			String messageId = Statement.wrap(doc).getMessageId();
//			for (Enumeration enumer = ((MessagesMutableTreeNode) this.mp.getMainnode()
//					.getRoot()).preorderEnumeration(); enumer
//					.hasMoreElements();) {
//				MessagesMutableTreeNode temp = (MessagesMutableTreeNode) enumer
//				.nextElement();
//				if(temp.getMessageId().equals(messageId)) {
//					Document tempDoc = temp.returnDoc();
//					Statement tempSt = Statement.wrap(tempDoc);
//
//					if(tempSt.isComment()) {
//						CommentStatement tempComment = CommentStatement.wrap(tempDoc);
//						if(tempComment.getCommentId().equals(this.comment_id) 
//								&& tempComment.getSelectedBody().equals(this.selection)) {
//							this.addItemInTable(tempComment);
//						}
//					}
//				}
//			}
//		}
	}
	
	private void addItemInTable(CommentStatement commSt) {
		String giver = "- "+commSt.getGiver()+":";
		String moment = "("+(new StringTokenizer(commSt.getMoment(), " ")).nextToken()+")";
		String comment = commSt.getComment();
		
		TableItem commItem = new TableItem(this.table, SWT.BORDER);
		commItem.setText(giver+moment+" "+comment);

		if(comment.equals(this.caw.getComment())) {
			//commItem.setBackground(new Color(Display.getDefault(), 200, 200, 200));
			commItem.setFont(new Font(Display.getDefault(), "Helvetica", 10, SWT.BOLD));
		} else {
			//commItem.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			commItem.setFont(new Font(Display.getDefault(), "Lucidasans", 10, SWT.NONE));
		}
		
		if(((String)this.caw.getMP().getRawSession().get(SessionConstants.REAL_NAME)).equals(commSt.getGiver()))
			commItem.setForeground(new Color(Display.getDefault(), 0, 0, 255));
		else 
			commItem.setForeground(new Color(Display.getDefault(), 255, 10, 40));
	}
	
	public void setSelection(String selection) {
		this.selection = selection;
	}
}
