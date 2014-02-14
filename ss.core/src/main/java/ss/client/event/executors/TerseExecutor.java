/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.ConversationManager;
import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;

/**
 * @author roman
 *
 */
public class TerseExecutor extends StatementExecutor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TerseExecutor.class);
	
	TerseStatement terse;

	/**
	 * @param mp
	 * @param statement
	 */
	public TerseExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.terse = TerseStatement.wrap(statement.getBindedDocument());
	}

	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		if (!this.mp.isThreadView()) {
			if (!this.mp.isInsertable()) {
				this.mp.loadWindow(this.terse);
			} else { 
				getBrowser().scrollToSelectedElement(this.terse.getMessageId());
				getBrowser().highlightSelectedString(this.terse.getMessageId());
			}
		} else {
			ConversationManager cm = new ConversationManager(this.mp,
					this.terse);
			cm.showConversation();
		}
	}
}
