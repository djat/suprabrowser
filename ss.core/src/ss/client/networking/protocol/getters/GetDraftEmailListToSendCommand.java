/**
 * 
 */
package ss.client.networking.protocol.getters;

import ss.smtp.defaultforwarding.ForcedForwardingInfo;

/**
 * @author zobo
 *
 */
public class GetDraftEmailListToSendCommand extends AbstractGetterCommand {

	private static final long serialVersionUID = -2336000208791571890L;

	private static final String FORCED_FORWARDING_INFO = "ForcedForwardingInfo";
	
	private static final String SPHERE_ID = "SphereIdData";
	
	public void putForcedForwardingInfo( final ForcedForwardingInfo info ){
		putArg( FORCED_FORWARDING_INFO, info );
	}
	
	public ForcedForwardingInfo getForcedForwardingInfo(){
		return (ForcedForwardingInfo)getObjectArg( FORCED_FORWARDING_INFO );
	}
	
	public void putSphereId( final String sphereId ) {
		putArg( SPHERE_ID, sphereId );
	}
	
	public String getSphereId() {
		return getStringArg( SPHERE_ID );
	}
}
