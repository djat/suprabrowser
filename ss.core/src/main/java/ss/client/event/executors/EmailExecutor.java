/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.client.ui.email.EmailController;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class EmailExecutor extends StatementExecutor {

	private ExternalEmailStatement email;
	/**
	 * @param mp
	 * @param statement
	 */
	public EmailExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.email = ExternalEmailStatement.wrap(statement.getBindedDocument());
	}
	
	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		(new EmailController( this.mp, getSession()))
		.emailClicked(this.email);
		
	}
	
	

}
