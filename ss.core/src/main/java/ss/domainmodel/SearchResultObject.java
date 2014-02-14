/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class SearchResultObject extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "result";
	
	private final ISimpleEntityProperty subject = super
	.createAttributeProperty( "subject/@value" );
	
	private final ISimpleEntityProperty giver = super
	.createAttributeProperty( "giver/@value" );
	
	private final ISimpleEntityProperty type = super
	.createAttributeProperty( "type/@value" );
	
	private final ISimpleEntityProperty role = super
	.createAttributeProperty( "role/@value" );
	
	private final ISimpleEntityProperty content = super
	.createAttributeProperty( "content/@value" );
	
	private final ISimpleEntityProperty comment = super
	.createAttributeProperty( "comment/@value" );
	
	private final ISimpleEntityProperty body = super
	.createAttributeProperty( "body/@value" );
	
	private final ISimpleEntityProperty contact = super
	.createAttributeProperty( "contact/@value" );
	
	private final ISimpleEntityProperty address = super
	.createAttributeProperty( "address/@value" );
	
	private final ISimpleEntityProperty keywords = super
	.createAttributeProperty( "keywords/@value" );
	
	private final IdItemCollection items = super
	.bindListProperty( new IdItemCollection(), "items" );
	
	
	public SearchResultObject() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SearchResultObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SearchResultObject.class);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SearchResultObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SearchResultObject.class);
	}
	
	public void setBody(String value) {
		this.body.setValue(value);
	}
	
	public String getBody() {
		return this.body.getValue();
	}
	
	public void setContact(String value) {
		this.contact.setValue(value);
	}
	
	public String getContact() {
		return this.contact.getValue();
	}
	
	public void setComment(String value) {
		this.comment.setValue(value);
	}
	
	public String getComment() {
		return this.comment.getValue();
	}
	
	public void setContent(String value) {
		this.content.setValue(value);
	}
	
	public String getContent() {
		return this.content.getValue();
	}
	
	public void setKeywords(String value) {
		this.keywords.setValue(value);
	}
	
	public String getKeywords() {
		return this.keywords.getValue();
	}
	
	public void setAddress(String value) {
		this.address.setValue(value);
	}
	
	public String getAddress() {
		return this.address.getValue();
	}
	
	public void setType(String value) {
		this.type.setValue(value);
	}
	
	public String getType() {
		return this.type.getValue();
	}
	
	public void setRole(String value) {
		this.role.setValue(value);
	}
	
	public String getRole() {
		return this.role.getValue();
	}

	
	public void setSubject(String value) {
		this.subject.setValue(value);
	}
	
	public String getSubject() {
		return this.subject.getValue();
	}
	
	public void setGiver(String value) {
		this.giver.setValue(value);
	}
	
	public String getGiver() {
		return this.giver.getValue();
	}
	
	public IdItemCollection getIdCollection() {
		return this.items;
	}

	/**
	 * @return
	 */
	public String getUnhighlitedSubject() {
		return getSubject().replaceAll("<font class=\"search_hightlightbody\">", "").replaceAll("</font>", "");
	}
	
}
