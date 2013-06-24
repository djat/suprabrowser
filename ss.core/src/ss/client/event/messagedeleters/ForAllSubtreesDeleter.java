/**
 * 
 */
package ss.client.event.messagedeleters;

import java.util.List;

import org.dom4j.Document;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class ForAllSubtreesDeleter extends AbstractForAllSelectedDeleter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForAllSinglesDeleter.class);
	
	public ForAllSubtreesDeleter(MessagesPane mp, AllSelectedDeleteManager manager, Statement statement) {
		super(mp, manager, statement);
	}

	@Override
	protected void cancelAsking() {
		this.manager.setCanDeleteAllSubtree(true);
	}

	@Override
	protected boolean canDeleteAll() {
		return this.manager.canDeleteAllSubtree();
	}

	@Override
	protected String getText() {
		return "\""+this.statement.getSubject()+"\" has replies. You can't delete single message. Delete entire subtree?";
	}

	@Override
	protected void performDeleteAction(Statement statement) {
		if (logger.isDebugEnabled()){
			logger.debug("Delete subtree of messages performed.");
		}
		
		List<Document> docsToRemove = this.mp.getMessagesTree().getInvertedSubtreeForDoc(this.statement);
		
		String sphereId = this.mp.getSphereId();
		
		for(Document doc : docsToRemove) {
			SupraSphereFrame.INSTANCE.client.recallMessage(this.mp.getRawSession(), doc, sphereId);
		}
	}

	@Override
	protected void cancelDeleting() {
		this.manager.cancelDeleting();
	}

	@Override
	protected void cancelDeletingForAllSimilar() {
		this.manager.cancelDeleteForAllSubtrees();
	}
}
