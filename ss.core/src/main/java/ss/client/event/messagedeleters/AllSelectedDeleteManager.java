/**
 * 
 */
package ss.client.event.messagedeleters;

import ss.client.ui.MessagesPane;
import ss.client.ui.tree.NodeDocumentsBundle;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class AllSelectedDeleteManager {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AllSelectedDeleteManager.class);

	private MessagesPane mp;
	
	private boolean canDeleteForAllSingle = false;
	
	private boolean isCanceledForAllSingle = false;
	
	private boolean isCanceledForAllMulti = false;
	
	private boolean canDeleteForAllMulti = false;
	
	private boolean isCanceledDeleting = false;
	
	/**
	 * @param mp
	 */
	public AllSelectedDeleteManager(MessagesPane mp) {
		this.mp = mp;
	}

	public void executeDeleting() {
		
		logger.debug("selected "+this.mp.getMessagesTree().getSelectedNodesCount()+" nodes");
		
		for(NodeDocumentsBundle documentBundle : this.mp.getMessagesTree().getSelectedDocumentsWithParents()) {
			if(this.isCanceledDeleting) {
				return;
			}
			
			Statement statement = documentBundle.getNodeStatement();
	
			AbstractForAllSelectedDeleter deleter = null;
			
			if (this.mp.getMessagesTree().getChildCountForDoc(statement)>0) {
				if(!this.isCanceledForAllMulti) {
					deleter = new ForAllSubtreesDeleter(this.mp, this, statement);
				}
			} else {
				if(!this.isCanceledForAllSingle) {
					deleter = new ForAllSinglesDeleter(this.mp, this, statement);
				}
			}
			if(deleter!=null) {
				deleter.executeDeleting();
			}
		}
	}

	public void setCanDeleteForSingle(boolean value) {
		this.canDeleteForAllSingle = value;
	}

	public void setCanDeleteAllSubtree(boolean value) {
		this.canDeleteForAllMulti = value;
	}
	
	public boolean canDeleteAllForSingle() {
		return this.canDeleteForAllSingle;
	}
	
	public boolean canDeleteAllSubtree() {
		return this.canDeleteForAllMulti;
	}
	
	public void cancelDeleteForSingle() {
		this.isCanceledForAllSingle = true;
	}

	public void cancelDeleteForAllSubtrees() {
		this.isCanceledForAllMulti = true;
	}
	
	public boolean isCancelDeleteAllForSingle() {
		return this.isCanceledForAllSingle;
	}
	
	public boolean isCancelDeleteAllSubtree() {
		return this.isCanceledForAllMulti;
	}

	/**
	 * 
	 */
	public void cancelDeleting() {
		this.isCanceledDeleting = true;
	}
}
