/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class ResultExecutor extends StatementExecutor {

	private ResultStatement result;

	/**
	 * @param mp
	 * @param statement
	 */
	public ResultExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.result = ResultStatement.wrap(statement.getBindedDocument());
	}


	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		String html = this.result.getHtmlText();
		this.mp.showSmallBrowser(getSession(), true, "sphere", html, null,
						null);
	}
	
	

}
