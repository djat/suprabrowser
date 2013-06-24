package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.Vector;

import ss.client.networking.protocol.getters.GetInitialPresenceCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;

public class GetInitialPresenceHandler extends AbstractGetterCommandHandler<GetInitialPresenceCommand, Vector<String>> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetInitialPresenceHandler.class);

	private static final String GROUP = "group";
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetInitialPresenceHandler(DialogsMainPeer peer) {
		super(GetInitialPresenceCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<String> evaluate(GetInitialPresenceCommand command) throws CommandHandleException {
		logger.info("GOT INITIAL PRESENCE REQUEST");
		Hashtable session = command.getSessionArg();
		String sphere_id = (String) session.get(SC.SPHERE_ID);
		String contact_name = (String) session.get(SC.REAL_NAME);
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		Vector<String> newavailable = new Vector<String>();
		String sphereType = this.peer.getVerifyAuth()
				.getSphereType(sphere_id);
		String displayName = this.peer.getVerifyAuth().getDisplayName(
				sphere_id);
		Vector<String> available = new Vector<String>();
		if (sphereType.equals(GROUP)) {
			available = this.peer.getXmldb().getSubPresence(supraSphere,
						sphere_id);
		} else {
			try {
				available.add(displayName);
				available.add(contact_name);
			} catch (Exception e) {
				logger.error( "Can't modify new avaliable", e);
			}
		}
		String privateSphere = this.peer.getVerifyAuth().getSystemName(
				contact_name);

		Vector<String> possibleAvaliable = this.peer.getVerifyAuth().getMembersFor( contact_name);
		Vector<String> membersWithLoginSphere = this.peer.getXmldb()
				.getMembersWithLoginSphere(supraSphere, privateSphere);
		logger.info("THIS MANY CONTACTS ENABELD> " + possibleAvaliable.size());
		possibleAvaliable.addAll(available);
		possibleAvaliable.addAll(membersWithLoginSphere);
		logger.warn("MEBERS WITH SPHERE CORE: "  + membersWithLoginSphere.size());
		for (int i = 0; i < possibleAvaliable.size(); i++) {
			String contactName = possibleAvaliable.get(i);
			checkForSendNotify(contactName, sphere_id, contact_name, newavailable);
		}
		return newavailable;		
	}

	@SuppressWarnings("unchecked")
	private void checkForSendNotify(String check, String sphere_id,
			String contactName, Vector<String> newavailable) {
		logger.info("Checking now to send notify: " + check);
		boolean found = false;
		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
			String otherLogin = handler.get(HandlerKey.USERNAME);
			String otherContactName = handler.getVerifyAuth().getRealName(otherLogin);
			if (check.equals(otherContactName)) {
				found = true;
				if (handler.get(HandlerKey.SUPRA_SPHERE).equals(sphere_id)) {
					handler.sendRefreshPresence( contactName, true );					
				}
			} else {
				logger.info("apparently not connected: " + check);
			}
		}
		newavailable.add((found) ? ("*" + check + "*") : check);
	}


}
