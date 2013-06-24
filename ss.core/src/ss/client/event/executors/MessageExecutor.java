/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class MessageExecutor extends StatementExecutor {

	/**
	 * @param mp
	 * @param statement
	 */
	public MessageExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
	}


	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		if(doWebHighlight()) {
			return;
		}
		this.mp.showMessagesBrowser(this.statement.getBindedDocument());
		
	}
	
	

}
