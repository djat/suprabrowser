package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring 
 * Should be changed to smth 
 * 
 */
public class EntitleContactForSphereHandler extends AbstractOldActionBuilder {
	
	private final DialogsMainCli cli;	

	public EntitleContactForSphereHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {		
		return SSProtocolConstants.ENTITLE_CONTACT_FOR_SPHERE;
	}

	@SuppressWarnings("unchecked")
	public String entitleContactForSphere(final Hashtable session, final Document sendDoc, final String sphereType) {
	
		String tableId = new Long(this.cli.getNextTableId()).toString();
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ENTITLE_CONTACT_FOR_SPHERE);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.TABLE_ID, tableId);
		update.put(SessionConstants.SPHERE_TYPE2, sphereType);//right constant
		update.put(SessionConstants.DOCUMENT, sendDoc);
		this.cli.sendFromQueue(update);
		return tableId;
	
	}
	

}
