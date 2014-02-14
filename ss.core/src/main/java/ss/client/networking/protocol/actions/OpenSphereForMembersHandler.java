/**
 * Jul 5, 2006 : 5:56:44 PM
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 *
 */
public class OpenSphereForMembersHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(OpenSphereForMembersHandler.class);
	
	private final DialogsMainCli cli;
	
	public OpenSphereForMembersHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.OPEN_SPHERE_FOR_MEMBERS;
	}

	@SuppressWarnings("unchecked")
	public void openSphereForMembers(Hashtable session, Document doc,
			Vector members, String system_name, String display_name) {
		
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.OPEN_SPHERE_FOR_MEMBERS);
		update.put(SessionConstants.MEMBERS, members);
		update.put(SessionConstants.SYSTEM_NAME, system_name);
		update.put(SessionConstants.DISPLAY_NAME, display_name);
		update.put(SessionConstants.DOCUMENT, doc);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}
}
