/**
 * 
 */
package ss.client.event.messagedeleters;

import java.util.concurrent.LinkedBlockingQueue;

import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class ForAllSinglesDeleter extends AbstractForAllSelectedDeleter {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForAllSinglesDeleter.class);
	
	final LinkedBlockingQueue<Object> locker = new LinkedBlockingQueue<Object>();
	
	public ForAllSinglesDeleter(MessagesPane mp, AllSelectedDeleteManager manager, Statement statement) {
		super(mp, manager, statement);
	}

	@Override
	protected void cancelAsking() {
		this.manager.setCanDeleteForSingle(true);
	}

	@Override
	protected boolean canDeleteAll() {
		return this.manager.canDeleteAllForSingle();
	}

	@Override
	protected String getText() {
		return "Are you sure you want to delete single message \""+this.statement.getSubject()+"\"?";
	}

	@Override
	protected void performDeleteAction(Statement statement) {
		if (logger.isDebugEnabled()){
			logger.debug("Delete single message performed.");
		}
		String sphereId = (String)this.mp.getSphereId();
		this.mp.client.recallMessage(this.mp.getRawSession(), statement.getBindedDocument(), sphereId);
	}

	@Override
	protected void cancelDeleting() {
		this.manager.cancelDeleting();
	}

	@Override
	protected void cancelDeletingForAllSimilar() {
		this.manager.cancelDeleteForSingle();
	}
}
