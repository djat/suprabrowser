/**
 * 
 */
package ss.client.networking.protocol.getters;

/**
 * @author zobo
 *
 */
public class GetSpheresWithNewAssetsCountCommand extends AbstractGetterCommand {

	private static final long serialVersionUID = 3869140887809292705L;
	
	private static final String CONTACT_NAME = "users_contact_name";

	public void setContactName( final String contactName ){
		if ( contactName == null ) {
			return;
		}
		putArg(CONTACT_NAME, contactName);
	}
	
	public String getContactName(){
		return getStringArg( CONTACT_NAME );
	}
}
