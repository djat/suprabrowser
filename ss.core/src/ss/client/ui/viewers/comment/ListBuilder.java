/**
 * 
 */
package ss.client.ui.viewers.comment;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.MessagesPane;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class ListBuilder {

	private CommentApplicationWindow caw;
	private String selection;
	private String comment_id;
	private MessagesPane mp;
	
	public ListBuilder(CommentApplicationWindow caw) {
		this.caw = caw;
		this.selection = this.caw.getSelection();
		this.comment_id = this.caw.getSelectedComment().getCommentId();
		this.mp = this.caw.getMP();
	}
	
	public List<CommentStatement> returnCommentList() {
		List<CommentStatement> list = new ArrayList<CommentStatement>();
		
		List<Statement> statements = this.mp.getTableStatements();
		
		for(Statement statement : statements) {
			if(statement.isComment()) {
				CommentStatement tempComment = CommentStatement.wrap(statement.getBindedDocument());
				if(tempComment.getCommentId().equals(this.comment_id) 
						&& tempComment.getSelectedBody().equals(this.selection) 
						&& tempComment.getNumber().equals(this.caw.getNumber())) {
					list.add(tempComment);
				}
			}
		}
		return list;
	}
	
	public void setSelection(String selection) {
		this.selection = selection;
	}
}
