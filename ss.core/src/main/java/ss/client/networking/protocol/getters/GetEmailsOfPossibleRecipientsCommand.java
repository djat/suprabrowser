/**
 * 
 */
package ss.client.networking.protocol.getters;

/**
 * @author zobo
 *
 */
public class GetEmailsOfPossibleRecipientsCommand extends AbstractGetterCommand {

	private static final long serialVersionUID = 1151227005941329862L;
	
	private static final String SPHERE_ID = "sphere_id";

	public void setLookupSphere(String sphereId){
		putArg(SPHERE_ID, sphereId);
	}
	
	public String getLookupSphere(){
		return getStringArg(SPHERE_ID);
	}
}
