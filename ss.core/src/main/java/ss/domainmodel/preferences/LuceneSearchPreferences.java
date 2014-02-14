/**
 * 
 */
package ss.domainmodel.preferences;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author dankosedin
 * 
 */
public class LuceneSearchPreferences extends XmlEntityObject {

	private final ISimpleEntityProperty terse = super
			.createAttributeProperty("types/terse/@value");

	private final ISimpleEntityProperty message = super
			.createAttributeProperty("types/message/@value");

	private final ISimpleEntityProperty comment = super
			.createAttributeProperty("types/comment/@value");

	private final ISimpleEntityProperty bookmark = super
			.createAttributeProperty("types/bookmark/@value");

	private final ISimpleEntityProperty keywords = super
			.createAttributeProperty("types/keywords/@value");

	private final ISimpleEntityProperty contact = super
			.createAttributeProperty("types/contact/@value");

	private final ISimpleEntityProperty file = super
			.createAttributeProperty("types/file/@value");

	private final ISimpleEntityProperty email = super
			.createAttributeProperty("types/email/@value");

	private final ISimpleEntityProperty subject = super
			.createAttributeProperty("fields/subject/@value");

	private final ISimpleEntityProperty body = super
			.createAttributeProperty("fields/body/@value");

	private final ISimpleEntityProperty content = super
			.createAttributeProperty("fields/content/@value");

	private final ISimpleEntityProperty commentBody = super
			.createAttributeProperty("fields/comment/@value");
	
	private final ISimpleEntityProperty role = super
			.createAttributeProperty("fields/role/@value");

	private final ISimpleEntityProperty fieldsModifier = super
			.createAttributeProperty("fields/modifier/@value");

	public LuceneSearchPreferences() {
		super();
	}

	public boolean isBookmarkChecked() {
		return this.bookmark.getBooleanValue(true);
	}

	public void setBookmarkSelected(boolean bookmarkSelected) {
		this.bookmark.setBooleanValue(bookmarkSelected);
	}

	public boolean isCommentChecked() {
		return this.comment.getBooleanValue(true);
	}

	public void setCommentSelected(boolean commentSelected) {
		this.comment.setBooleanValue(commentSelected);
	}

	public boolean isContactChecked() {
		return this.contact.getBooleanValue(true);
	}

	public void setContactSelected(boolean contactSelected) {
		this.contact.setBooleanValue(contactSelected);
	}

	public boolean isFileChecked() {
		return this.file.getBooleanValue(true);
	}

	public void setFileSelected(boolean fileSelected) {
		this.file.setBooleanValue(fileSelected);
	}

	public boolean isEmailChecked() {
		return this.email.getBooleanValue(true);
	}

	public void setEmailSelected(boolean emailSelected) {
		this.email.setBooleanValue(emailSelected);
	}

	public boolean isKeywordsChecked() {
		return this.keywords.getBooleanValue(true);
	}

	public void setKeywordsSelected(boolean keywordsSelected) {
		this.keywords.setBooleanValue(keywordsSelected);
	}

	public boolean isMessageChecked() {
		return this.message.getBooleanValue(true);
	}

	public void setMessageSelected(boolean messageSelected) {
		this.message.setBooleanValue(messageSelected);
	}

	public boolean isTerseChecked() {
		return this.terse.getBooleanValue(true);
	}

	public void setTerseSelected(boolean terseSelected) {
		this.terse.setBooleanValue(terseSelected);
	}

	public boolean isBodySelected() {
		return this.body.getBooleanValue(true);
	}

	public void setBodySelected(boolean bodySelected) {
		this.body.setBooleanValue(bodySelected);
	}
	
	public boolean isRoleSelected(){
		return this.role.getBooleanValue(true);
	}
	
	public void setRoleSelected( boolean roleSelected ){
		this.role.setBooleanValue(roleSelected);
	}

	public boolean isCommentSelected() {
		return this.commentBody.getBooleanValue(true);
	}

	public void setCommentBodySelected(boolean commentBodySelected) {
		this.commentBody.setBooleanValue(commentBodySelected);
	}

	public boolean isContentSelected() {
		return this.content.getBooleanValue(true);
	}

	public void setContentSelected(boolean contentSelected) {
		this.content.setBooleanValue(contentSelected);
	}

	public boolean isSubjectSelected() {
		return this.subject.getBooleanValue(true);
	}

	public void setSubjectSelected(boolean subjectSelected) {
		this.subject.setBooleanValue(subjectSelected);
	}

	public int getFieldsModifier() {
		return Integer.parseInt(this.fieldsModifier.getValueOrDefault("0"));
	}

	public void setFieldsModifier(int fieldsModifier) {
		this.fieldsModifier.setValue("" + fieldsModifier);
	}

}
