/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.SphereRoleRenameAction;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class SphereRoleRenameHandler extends AbstractActionHandler<SphereRoleRenameAction> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SphereRoleRenameHandler(final DialogsMainPeer peer) {
		super(SphereRoleRenameAction.class, peer);
	}

	@Override
	protected void execute(SphereRoleRenameAction action) {
		if(action==null) {
			return;
		}
		if(!SphereRoleObject.isValid(action.getSphereRole())) {
			return;
		}
		String roleToRemove = action.getSphereRole();
		String replacement = action.getReplacement();
		this.peer.getXmldb().renameSphereRole(roleToRemove, replacement);
	}
}
