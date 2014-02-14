/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.protocol.actions.UpdateClubdealVisibilityAction;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.UpdateMemberVisibilityServerHandler;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class UpdateClubdealVisibilityActionHandler extends
		AbstractActionHandler<UpdateClubdealVisibilityAction> {

	private static final Logger logger = SSLogger.getLogger(UpdateClubdealVisibilityActionHandler.class);
	
	public UpdateClubdealVisibilityActionHandler(final DialogsMainPeer peer) {
		super(UpdateClubdealVisibilityAction.class, peer);
	}
	
	@Override
	protected void execute(UpdateClubdealVisibilityAction action) {
		Document sphereDef = action
				.getDocumentArg(SessionConstants.SPHERE_DEFINITION);
		String contactName = action.getStringArg(SessionConstants.CONTACT_NAME);
		boolean isAdding = action.getBooleanArg(SessionConstants.ADDED_REMOVED);
		if (sphereDef == null) {
			logger
					.error("Cannot update clubdeal visibility, sphere definition is null!!!");
			return;
		}
		if (contactName == null) {
			logger
					.error("Cannot update clubdeal visibility for null member!!!");
			return;
		}
		if (this.peer.getVerifyAuth().getLoginForContact(contactName) == null) {
			logger
					.error("Cannot update clubdeal visibility for contact without username!!!");
			return;
		}
		SphereStatement sphere = SphereStatement.wrap(sphereDef);
		String contactLogin = this.peer.getVerifyAuth().getLoginForContact(contactName);
		SphereMember member = new SphereMember(sphere.getCurrentSphere(),
				sphere.getSystemName(), sphere.getDisplayName(), contactLogin,
				contactName);
		List<SphereMember> added = new ArrayList<SphereMember>();
		List<SphereMember> removed = new ArrayList<SphereMember>();
		if(isAdding) {
			added.add(member);
		} else {
			removed.add(member);
		}
		Hashtable update = new Hashtable();
		update.put("added", added);
		update.put("removed", removed);
		new UpdateMemberVisibilityServerHandler(this.peer).handle(update);

		if(this.peer.getVerifyAuth().isAdmin(contactName, contactLogin)) {
			this.peer.getXmldb()
			 	.removeAllAdminContactsFromSphere(sphere.getSystemName());
		}
	}
}
