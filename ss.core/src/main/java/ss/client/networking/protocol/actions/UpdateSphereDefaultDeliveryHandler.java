/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class UpdateSphereDefaultDeliveryHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(UpdateSphereDefaultDeliveryHandler.class);

	private final DialogsMainCli cli;
	
	public UpdateSphereDefaultDeliveryHandler(DialogsMainCli cli) {
		this.cli = cli;
	}
	
	public String getProtocol() {
		return SSProtocolConstants.UPDATE_DEFAULT_DELIVERY;
	}
	
	@SuppressWarnings("unchecked")
	public void handleUpdateDefaultDelivery(Hashtable session, String sphereId, String newDelivery) {
		Hashtable toSend = (Hashtable)session.clone();
		
		Hashtable update = new Hashtable();
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DEFAULT_DELIVERY, newDelivery);
		update.put(SessionConstants.SPHERE_ID, sphereId);
		update.put(SessionConstants.PROTOCOL, getProtocol());
		
		this.cli.sendFromQueue(update);
	}

}
