package ss.refactor.supraspheredoc.old.unused;

import java.util.Hashtable;
import java.util.Vector;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @deprecated
 * @author dankosedin
 * 
 * TODO:#member-refactoring
 * Not used.
 * Previosly used in ss.client.ui.viewer.NewSphere
 * // mp.client.publishTerse(session,doc);
 * // mp.client.registerSphereWithMembers(registermembers,doc.getRootElement().attributeValue("system_name"),doc.getRootElement().attributeValue("display_name"));
 * 
 * 					NewSphere.this.mp.client.openSphereForMembers(
 * called XmldbUtils.registerSphereWithMembers
 * but this method also called by OpenSphereForMembersHandler  
 * 
 * 
 */
public class RegisterSphereWithMembersHandlerClient implements ProtocolHandler {

	private DialogsMainCli cli;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RegisterSphereWithMembersHandlerClient.class);

	/**
	 * @deprecated
	 * @param cli
	 */
	public RegisterSphereWithMembersHandlerClient(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REGISTER_SPHERE_WITH_MEMBERS;
	}

	public void handle(Hashtable update) {
		// to-server-only handler
	}

	@SuppressWarnings("unchecked")
	public void registerSphereWithMembers(Vector members, String system_name,
			String display_name) {
		Hashtable toSend = (Hashtable) this.cli.session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.REGISTER_SPHERE_WITH_MEMBERS);

		update.put(SessionConstants.MEMBERS, members);
		update.put(SessionConstants.SYSTEM_NAME, system_name);// RC
		update.put(SessionConstants.DISPLAY_NAME, display_name);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
