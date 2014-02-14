/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.enums.MessageType;

/**
 * @author roman
 *
 */
public class CommentObject extends BookmarkObject {

	private int number;
	
	private long commentId;
	
	private MessageType commentThreadType;

	/**
	 * @return the number
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the commentId
	 */
	public long getCommentId() {
		return this.commentId;
	}

	/**
	 * @param commentId the commentId to set
	 */
	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	/**
	 * @return the commentThreadType
	 */
	public MessageType getCommentThreadType() {
		return this.commentThreadType;
	}

	/**
	 * @param commentThreadType the commentThreadType to set
	 */
	public void setCommentThreadType(MessageType commentThreadType) {
		this.commentThreadType = commentThreadType;
	}
	
}
