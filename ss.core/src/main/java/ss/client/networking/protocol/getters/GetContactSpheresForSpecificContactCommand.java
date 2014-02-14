/**
 * 
 */
package ss.client.networking.protocol.getters;

/**
 * @author zobo
 *
 */
public class GetContactSpheresForSpecificContactCommand extends
		AbstractGetterCommand {

	private static final long serialVersionUID = -1973121378470679778L;
	
	private static final String CONTACT_NAME = "contactName";

	public void setContactName( final String contactName ){
		putArg(CONTACT_NAME, contactName);
	}
	
	public String getContactName(){
		return getStringArg(CONTACT_NAME);
	}
}
