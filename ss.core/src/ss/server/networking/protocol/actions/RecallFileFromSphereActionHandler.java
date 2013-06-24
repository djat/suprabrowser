/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.protocol.actions.RecallFileFromSphereAction;
import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.domainmodel.FileStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class RecallFileFromSphereActionHandler extends AbstractActionHandler<RecallFileFromSphereAction> {

	private static final Logger logger = SSLogger.getLogger(RecallFileFromSphereActionHandler.class);
	
	private static final String RECALL = "recall";
	
	public RecallFileFromSphereActionHandler(final DialogsMainPeer peer) {
		super(RecallFileFromSphereAction.class, peer);
	}

	@Override
	protected void execute(RecallFileFromSphereAction action) {
		Hashtable update = action.getSessionArg();
		List<String> sphereList = (List<String>)update.get(SessionConstants.SPHERE_LIST);
		if(sphereList.size()<1) {
			return;
		}
		String dataId = (String)update.get(SessionConstants.DATA_ID);
		
		List<Document> removedFiles = this.peer.getXmldb().removeFileFromSpheres(sphereList, dataId);
		
		for(Document fileDoc : removedFiles) {
			FileStatement file = FileStatement.wrap(fileDoc);
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.RECALL_MESSAGE);
			dmpResponse.setDocumentValue(SC.DOCUMENT, fileDoc);
			dmpResponse.setStringValue(SC.SPHERE, file.getCurrentSphere());
			dmpResponse.setStringValue(SC.DELIVERY_TYPE, RECALL);
			for (DialogsMainPeer handler : DmpFilter.filterOrAdmin(file.getCurrentSphere()) ) {
				handler.sendFromQueue(dmpResponse);
			}
		}
	}

}
