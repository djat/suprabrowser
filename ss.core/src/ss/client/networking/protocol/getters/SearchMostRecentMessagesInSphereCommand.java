/**
 * 
 */
package ss.client.networking.protocol.getters;

/**
 * @author zobo
 *
 */
public class SearchMostRecentMessagesInSphereCommand extends
		AbstractGetterCommand {

	private static final long serialVersionUID = -7886476623541881227L;

	private static final String SPHERE_ID_TO_SEARCH = "sphereIdToSearch";

	public void setSphereId( final String sphereId ){
		putArg(SPHERE_ID_TO_SEARCH, sphereId);
	}
	
	public String getSphereId(){
		return getStringArg(SPHERE_ID_TO_SEARCH);
	}
}
