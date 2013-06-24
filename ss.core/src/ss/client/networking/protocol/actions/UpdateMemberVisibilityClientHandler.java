/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.List;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.spheremanagement.memberaccess.SphereMemberBundle;
import ss.common.SSProtocolConstants;
import ss.common.protocolobjects.MemberVisibilityProtocolObject;

/**
 *
 */
public class UpdateMemberVisibilityClientHandler extends AbstractDocumentClientHandler {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateMemberVisibilityClientHandler.class);
	
	/**
	 * @param cli
	 */
	public UpdateMemberVisibilityClientHandler(DialogsMainCli cli) {
		super(cli);
	}

	/* (non-Javadoc)
	 * @see ss.client.networking.protocol.AbstractClientHandler#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return SSProtocolConstants.UPDATE_MEMBER_VISIBILITY;
	}
	
	public void saveSphereMemberVisibility(List<SphereMemberBundle> added, List<SphereMemberBundle> removed) {
		MemberVisibilityProtocolObject protocolObject = new MemberVisibilityProtocolObject();
		for( SphereMemberBundle bundle : added) {
			protocolObject.getAdded().add( bundle.toSphereMember() );
		}
		for( SphereMemberBundle bundle : removed) {
			protocolObject.getRemoved().add( bundle.toSphereMember() );
		}
		logger.debug( "sending update member visibiliy" );
		super.sendUpdate(protocolObject);
	}
	

}
