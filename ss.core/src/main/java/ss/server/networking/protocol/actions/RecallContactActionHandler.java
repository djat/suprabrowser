/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.protocol.actions.RecallContactActionCommand;
import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.domainmodel.ContactStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class RecallContactActionHandler extends AbstractActionHandler<RecallContactActionCommand> {

	private static final String RECALL = "recall";
	
	private static final Logger logger = SSLogger.getLogger(RecallContactActionHandler.class);
	
	public RecallContactActionHandler(final DialogsMainPeer peer) {
		super(RecallContactActionCommand.class, peer);
	}
	
	@Override
	protected void execute(RecallContactActionCommand action) {
		String sphereId = action.getStringArg(SessionConstants.SPHERE_ID2);
		Document contactDoc = action
				.getDocumentArg(SessionConstants.CONTACT_DOC);
		Document existedContact = this.peer.getXmldb().getContactExists(
				ContactStatement.wrap(contactDoc), sphereId);
		try {
			this.peer.getXmldb().removeDoc(existedContact, sphereId);
		} catch (SQLException ex) {
			logger.error("Cannot remove contact doc from sphere "+sphereId, ex);
			return;
		}
		final DmpResponse dmpResponse = new DmpResponse();
		existedContact.getRootElement().addElement("current_sphere").addAttribute("value",
				sphereId);
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.RECALL_MESSAGE);
		dmpResponse.setDocumentValue(SC.DOCUMENT, existedContact);
		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		dmpResponse.setStringValue(SC.DELIVERY_TYPE, RECALL);

		for (DialogsMainPeer handler : DmpFilter.filterOrAdmin(sphereId) ) {
			handler.sendFromQueue(dmpResponse);
		}
	}
}
