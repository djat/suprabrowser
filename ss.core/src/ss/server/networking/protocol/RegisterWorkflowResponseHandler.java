/**
 * 
 */
package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class RegisterWorkflowResponseHandler implements ProtocolHandler {

	private DialogsMainPeer peer;
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(RegisterWorkflowResponseHandler.class);
	
	public RegisterWorkflowResponseHandler(DialogsMainPeer dmp) {
		this.peer = dmp;
	}
	
	public String getProtocol() {
		return SSProtocolConstants.REGISTER_WORKFLOW_RESPONSE;
	}

	
	public void handle(Hashtable update) {
		handleRegisterWorkflowResponse(update);
	}
	
	public void handleRegisterWorkflowResponse(Hashtable update) {
		String sphereId = (String)update.get(SessionConstants.SPHERE_ID);
		String resultId = (String)update.get(SessionConstants.RESULT_ID);
		Document doc = (Document)update.get(SessionConstants.DOCUMENT);
	
		Document[] docs = this.peer.getXmldb().registerResponse(doc, resultId, sphereId);
		
		for(Document newDoc: docs) {
			if(newDoc != null) {
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				dmpResponse.setDocumentValue(SC.DOCUMENT, newDoc);
				
				for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
					handler.sendFromQueue(dmpResponse);
				}
			}
		}
	}
}
