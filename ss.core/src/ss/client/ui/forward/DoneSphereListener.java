/**
 * 
 */
package ss.client.ui.forward;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class DoneSphereListener extends AbstractDoneSelectionListener {

	private static final Logger logger = SSLogger.getLogger(DoneSphereListener.class);
	
	public DoneSphereListener(final CurrentMessageForwardingDialog dialog) {
		super(dialog);
		logger.warn("creates done sphere selection listener");
	}

	@SuppressWarnings("unchecked")
	private void handleNotEmailForwarding() {
		Hashtable sendSession = (Hashtable) SupraSphereFrame.INSTANCE.client.session
				.clone();

		List<String> sphereList = new ArrayList<String>();
		if(getSelection().size()==0) {
			return;
		}
		for (String sphere : getSelection()) {
			String sphereId = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
					.getSystemName(sphere);
			sphereList.add(sphereId);
		}

		List<Document> docsWithAttachments = getDocsWithAttachments(); 

		SupraSphereFrame.INSTANCE.client.forwardMessagesSubTree(sphereList, docsWithAttachments);
	}

	/**
	 * @return
	 */
	private List<Document> getDocsWithAttachments() {
		List<String> messageIdsToForward = new ArrayList<String>();
		for(Document doc : getDialog().getDocsToForward()) {
			messageIdsToForward.add(Statement.wrap(doc).getMessageId());
		}
		List<Document> docs = new ArrayList<Document>();
		for (Document doc : getDialog().getDocsToForward()) {
			docs.add(doc);
			Statement statement = Statement.wrap(doc);
			if (!statement.isEmail() && !statement.isMessage()) {
				continue;
			}
			Vector<Document> fileDocs = SupraSphereFrame.INSTANCE.client
					.getAttachments(statement.getCurrentSphere(), statement
							.getMessageId());
			for(Document fileDoc : fileDocs) {
				FileStatement file = FileStatement.wrap(fileDoc);
				if(!messageIdsToForward.contains(file.getMessageId())) {
					docs.add(fileDoc);
				}
			}
		}
		return docs;
	}

	@Override
	void performSpecificAction() {
		handleNotEmailForwarding();
	}
}
