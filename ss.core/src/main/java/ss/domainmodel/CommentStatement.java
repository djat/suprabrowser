package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class CommentStatement extends Statement {
	
	private final ISimpleEntityProperty selectedBody = super
	.createTextProperty("body/selected_body");
	
	private final ISimpleEntityProperty commentId = super
	.createAttributeProperty("comment_id/@value");
	
	private final ISimpleEntityProperty commentThread = super
	.createAttributeProperty("comment_thread/@value");
	
	private final ISimpleEntityProperty number = super
	.createAttributeProperty("number/@value");
	
	private final ISimpleEntityProperty selectionPath = super
	.createAttributeProperty("selection_path/@value");
	
	private final ISimpleEntityProperty comment = super
	.createTextProperty("body/comment");
	
	public CommentStatement() {
		super( "email" );
	}

	/**
	  Create file object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static CommentStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, CommentStatement.class);
	}

	/**
	 * Create file object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static CommentStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, CommentStatement.class);
	}
	
	
	
	public void setSelectedBody(String value) {
		this.selectedBody.setValue(value);
	}
	
	public String getSelectedBody() {
		return this.selectedBody.getValue();
	}
	
	
	
	public void setCommentId(String value) {
		this.commentId.setValue(value);
	}
	
	public String getCommentId() {
		return this.commentId.getValue();
	}
	
	public void setCommentThread(String value) {
		this.commentThread.setValue(value);
	}
	
	public String getCommentThread() {
		return this.commentThread.getValue();
	}
	
	public void setNumber(int value) {
		this.number.setValue((new Integer(value)).toString());
	}
	
	public void setNumber(String value) {
		this.number.setValue(value);
	}
	
	public String getNumber() {
		return this.number.getValue();
	}
	
	public void setComment(String value) {
		this.comment.setValue(value);
	}
	
	public String getComment() {
		return this.comment.getValue();
	}
	
	public void setSelectionPath(final String value) {
		this.selectionPath.setValue(value);
	}
	
	public String getSelectionPath() {
		return this.selectionPath.getValue();
	}
}
