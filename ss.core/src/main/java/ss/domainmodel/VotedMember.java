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
public class VotedMember extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "member";
	
	private final ISimpleEntityProperty contactName = super
	.createAttributeProperty( "@value");

	private final ISimpleEntityProperty moment = super
	.createAttributeProperty( "@vote_moment");
	
	public VotedMember() {
		super( ITEM_ROOT_ELEMENT_NAME );
	}
	
	/**
	 * @param contact
	 * @param string
	 */
	public VotedMember(String contact, String moment) {
		this();
		setName(contact);
		setVotedMoment(moment);
	}

	public String getVotedMoment() {
		return this.moment.getValue();
	}
	
	public String getName() {
		return this.contactName.getValue();
	}
	
	public void setVotedMoment(String value) {
		this.moment.setValue(value);
	}
	
	public void setName(String value) {
		this.contactName.setValue(value);
	}
}
