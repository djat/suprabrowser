/**
 * 
 */
package ss.client.networking.protocol.actions;

import org.dom4j.Document;

import ss.domainmodel.ContactStatement;

/**
 * @author zobo
 *
 */
public class ChangeContactAction extends AbstractAction {

	private static final long serialVersionUID = -8724066303843192790L;

	private static final String NEW_CONTACT_NAME_FIRST_NAME = "newContactNameFirstName";
	
	private static final String NEW_CONTACT_NAME_LAST_NAME = "newContactNameLastName";
	
	private static final String OLD_CONTACT_NAME = "oldContactName";
	
	private static final String CHANGED_CONTACT = "newChangedContactStatement";

	/**
	 * Mandatory field
	 */
	public void setOldContactName( final String oldContactName ){
		putArg(OLD_CONTACT_NAME, oldContactName);
	}
	
	public String getOldContactName(){
		return getStringArg(OLD_CONTACT_NAME);
	}
	
	public void setNewContactNameFirstName( final String newContactNameFirstName ){
		putArg(NEW_CONTACT_NAME_FIRST_NAME, newContactNameFirstName);
	}
	
	public String getNewContactNameFirstName(){
		return getStringArg(NEW_CONTACT_NAME_FIRST_NAME);
	}
	
	public void setNewContactNameLastName( final String newContactNameLastName ){
		putArg(NEW_CONTACT_NAME_LAST_NAME, newContactNameLastName);
	}
	
	public String getNewContactNameLastName(){
		return getStringArg(NEW_CONTACT_NAME_LAST_NAME);
	}
	
	public void setNewContact( final ContactStatement contact ) {
		putArg(CHANGED_CONTACT, contact.getBindedDocument());
	}
	
	public ContactStatement getNewContact(){
		final Document doc = getDocumentArg(CHANGED_CONTACT);
		if ( doc == null ) {
			return null;
		}
		return ContactStatement.wrap(doc);
	}
}
