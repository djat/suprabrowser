/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.SearchPrivateSphereAction;
import ss.server.db.SphereDefinitionSetUpper;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SearchPrivateSphereActionHandler extends AbstractActionHandler<SearchPrivateSphereAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchPrivateSphereActionHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SearchPrivateSphereActionHandler(DialogsMainPeer peer) {
		super(SearchPrivateSphereAction.class, peer);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute(SearchPrivateSphereAction action) {
		Hashtable session = action.getSessionArg();
		String contactName = action.getStringArg("contact_name");
		String userName = action.getStringArg("user_name");
		
		String sphereId = this.peer.getVerifyAuth().getPrivateSphereId(userName);
		
		String supraSphere = (String)session.get(SessionConstants.SUPRA_SPHERE);
		
		Document sphereDefinition = SphereDefinitionSetUpper.setUpSphereDefinition(this.peer, null, sphereId, supraSphere, contactName);
		
		this.peer.sendPrivateSphereDefinition(session, sphereDefinition,
				this.peer.getVerifyAuth(), "false");
	}
}
