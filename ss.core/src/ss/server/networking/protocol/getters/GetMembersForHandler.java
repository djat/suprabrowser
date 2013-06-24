package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetMembersForCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetMembersForHandler extends
		AbstractGetterCommandHandler<GetMembersForCommand, Vector<Document>> {

	/**
	 * @param peer
	 */
	public GetMembersForHandler(DialogsMainPeer peer) {
		super(GetMembersForCommand.class, peer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Document> evaluate(GetMembersForCommand command)
			throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String system = (String) session.get(SC.SPHERE_ID);
		Vector members = this.peer.getVerifyAuth().getLoginsForMembersEnabled1( system );
		// TODO it seems that return member not depends from handler.
		// Move it up?
		Vector<Document> returnMembers = new Vector<Document>();
		for (int j = 0; j < members.size(); j++) {
			String login = (String) members.get(j);
			String loginSphere = this.peer.getXmldb().getUtils()
					.getLoginSphereSystemName(login);
			Document contactDoc = this.peer.getXmldb().getMembershipDoc(
					loginSphere, login);
			if ( contactDoc != null ) {
				returnMembers.add(contactDoc);
			}
		}
		return returnMembers;
	}

}
