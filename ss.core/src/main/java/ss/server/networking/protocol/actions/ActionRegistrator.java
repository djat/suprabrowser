/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.framework.networking2.Protocol;
import ss.server.networking.AbstractRegistrator;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class ActionRegistrator extends AbstractRegistrator {

	/**
	 * @param peer
	 * @param protocol
	 */
	public ActionRegistrator(DialogsMainPeer peer, Protocol protocol) {
		super(peer, protocol);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.AbstractRegistrator#registerHandlers()
	 */
	@Override
	public void registerHandlers() {
		register(SearchSupraSphereFreeHandler.class);
		register(ForwardMessagesSubTreeHandler.class);
		register(SearchPrivateSphereActionHandler.class);
		register(SearchP2PActionHandler.class);
		register(PublishMessageActionHandler.class);
		register(DeleteSpheresActionHandler.class);
		register(SaveClubDealsHandler.class);
		register(AssosiateFileWithClubDealsHandler.class);
		register(RecallFileFromSphereActionHandler.class);
		register(RecallContactActionHandler.class);
		register(UpdateClubdealVisibilityActionHandler.class);
		register(SetMarkActionHandler.class);
		register(PublishFileActionHandler.class);
		register(MoveSphereHandler.class);
		register(SaveSphereRelationsHandler.class);
		register(UpdateSphereDefinitionHandler.class);
		register(SphereRoleRenameHandler.class);
		register(TagActionCommandHandler.class);
		register(ChangeContactActionHandler.class);
		register(SendOutContactEmailActionHandler.class);
		register(LockContactActionHandler.class);
		register(UnlockContactActionHandler.class);
	}
}
