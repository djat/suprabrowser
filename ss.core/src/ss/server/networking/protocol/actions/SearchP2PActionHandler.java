/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.SearchP2PAction;
import ss.common.SphereDefinitionCreator;
import ss.common.XmlDocumentUtils;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SearchP2PActionHandler extends AbstractActionHandler<SearchP2PAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchP2PActionHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SearchP2PActionHandler(DialogsMainPeer peer) {
		super(SearchP2PAction.class, peer);
	}

	@Override
	protected void execute(SearchP2PAction action) {
		Hashtable session = action.getSessionArg();
		
		String contactName = action.getStringArg(SessionConstants.CONTACT_NAME);
		
		String systemName = action.getStringArg(SessionConstants.SYSTEM_NAME);

		String supraSphere = (String)session.get(SessionConstants.SUPRA_SPHERE);
		
		Document sphereDefinition = setUpSphereDefinition(null, systemName, supraSphere, contactName);
		
		this.peer.sendPrivateSphereDefinition(session, sphereDefinition,
				this.peer.getVerifyAuth(), "false");
	}
	
	
	private Document setUpSphereDefinition(Document sphereDefinition,
			String sphere_id, String supraSphere, String contactName) {
		if (sphereDefinition == null) {
			sphereDefinition = this.peer.getXmldb().getSphereDefinition(
					sphere_id, sphere_id);

			if (sphereDefinition == null) {
				sphereDefinition = this.peer.getXmldb().getSphereDefinition(
						supraSphere, sphere_id);
			}

		} 
		
		logger.warn(XmlDocumentUtils.toPrettyString(sphereDefinition));

		if (sphereDefinition == null) {
			SphereDefinitionCreator sdc = new SphereDefinitionCreator();
			sphereDefinition = sdc.createDefinition(contactName, sphere_id);
		}
		return sphereDefinition;
	}

}
