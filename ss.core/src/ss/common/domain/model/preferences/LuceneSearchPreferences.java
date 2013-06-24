/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public class LuceneSearchPreferences extends DomainObject {

	private int fieldsModifier = 0;
	
	private boolean terseChecked;
	
	private boolean bookmarkChecked;
	
	private boolean keywordChecked;
	
	private boolean emailChecked;
	
	private boolean contactChecked;
	
	private boolean commentChecked;
	
	private boolean messageChecked;
	
	private boolean fileChecked;
	
	private boolean exactPhraseChecked;
	
	private boolean anyWordChecked;
	
	private boolean everyWordChecked;
	
	private boolean subjectChecked;
	
	private boolean bodyChecked;
	
	private boolean contentChecked;
	
	private boolean commentBodyChecked;

	/**
	 * @return the fieldsModifier
	 */
	public int getFieldsModifier() {
		return this.fieldsModifier;
	}

	/**
	 * @param fieldsModifier the fieldsModifier to set
	 */
	public void setFieldsModifier(int fieldsModifier) {
		this.fieldsModifier = fieldsModifier;
	}

	/**
	 * @return the terseChecked
	 */
	public boolean isTerseChecked() {
		return this.terseChecked;
	}

	/**
	 * @param terseChecked the terseChecked to set
	 */
	public void setTerseChecked(boolean terseChecked) {
		this.terseChecked = terseChecked;
	}

	/**
	 * @return the bookmarkChecked
	 */
	public boolean isBookmarkChecked() {
		return this.bookmarkChecked;
	}

	/**
	 * @param bookmarkChecked the bookmarkChecked to set
	 */
	public void setBookmarkChecked(boolean bookmarkChecked) {
		this.bookmarkChecked = bookmarkChecked;
	}

	/**
	 * @return the keywordChecked
	 */
	public boolean isKeywordChecked() {
		return this.keywordChecked;
	}

	/**
	 * @param keywordChecked the keywordChecked to set
	 */
	public void setKeywordChecked(boolean keywordChecked) {
		this.keywordChecked = keywordChecked;
	}

	/**
	 * @return the emailChecked
	 */
	public boolean isEmailChecked() {
		return this.emailChecked;
	}

	/**
	 * @param emailChecked the emailChecked to set
	 */
	public void setEmailChecked(boolean emailChecked) {
		this.emailChecked = emailChecked;
	}

	/**
	 * @return the contactChecked
	 */
	public boolean isContactChecked() {
		return this.contactChecked;
	}

	/**
	 * @param contactChecked the contactChecked to set
	 */
	public void setContactChecked(boolean contactChecked) {
		this.contactChecked = contactChecked;
	}

	/**
	 * @return the commentChecked
	 */
	public boolean isCommentChecked() {
		return this.commentChecked;
	}

	/**
	 * @param commentChecked the commentChecked to set
	 */
	public void setCommentChecked(boolean commentChecked) {
		this.commentChecked = commentChecked;
	}

	/**
	 * @return the messageChecked
	 */
	public boolean isMessageChecked() {
		return this.messageChecked;
	}

	/**
	 * @param messageChecked the messageChecked to set
	 */
	public void setMessageChecked(boolean messageChecked) {
		this.messageChecked = messageChecked;
	}

	/**
	 * @return the fileChecked
	 */
	public boolean isFileChecked() {
		return this.fileChecked;
	}

	/**
	 * @param fileChecked the fileChecked to set
	 */
	public void setFileChecked(boolean fileChecked) {
		this.fileChecked = fileChecked;
	}

	/**
	 * @return the exactPhraseChecked
	 */
	public boolean isExactPhraseChecked() {
		return this.exactPhraseChecked;
	}

	/**
	 * @param exactPhraseChecked the exactPhraseChecked to set
	 */
	public void setExactPhraseChecked(boolean exactPhraseChecked) {
		this.exactPhraseChecked = exactPhraseChecked;
	}

	/**
	 * @return the anyWordChecked
	 */
	public boolean isAnyWordChecked() {
		return this.anyWordChecked;
	}

	/**
	 * @param anyWordChecked the anyWordChecked to set
	 */
	public void setAnyWordChecked(boolean anyWordChecked) {
		this.anyWordChecked = anyWordChecked;
	}

	/**
	 * @return the everyWordChecked
	 */
	public boolean isEveryWordChecked() {
		return this.everyWordChecked;
	}

	/**
	 * @param everyWordChecked the everyWordChecked to set
	 */
	public void setEveryWordChecked(boolean everyWordChecked) {
		this.everyWordChecked = everyWordChecked;
	}

	/**
	 * @return the subjectChecked
	 */
	public boolean isSubjectChecked() {
		return this.subjectChecked;
	}

	/**
	 * @param subjectChecked the subjectChecked to set
	 */
	public void setSubjectChecked(boolean subjectChecked) {
		this.subjectChecked = subjectChecked;
	}

	/**
	 * @return the bodyChecked
	 */
	public boolean isBodyChecked() {
		return this.bodyChecked;
	}

	/**
	 * @param bodyChecked the bodyChecked to set
	 */
	public void setBodyChecked(boolean bodyChecked) {
		this.bodyChecked = bodyChecked;
	}

	/**
	 * @return the contentChecked
	 */
	public boolean isContentChecked() {
		return this.contentChecked;
	}

	/**
	 * @param contentChecked the contentChecked to set
	 */
	public void setContentChecked(boolean contentChecked) {
		this.contentChecked = contentChecked;
	}

	/**
	 * @return the commentBodyChecked
	 */
	public boolean isCommentBodyChecked() {
		return this.commentBodyChecked;
	}

	/**
	 * @param commentBodyChecked the commentBodyChecked to set
	 */
	public void setCommentBodyChecked(boolean commentBodyChecked) {
		this.commentBodyChecked = commentBodyChecked;
	}
}
