/**
 * 
 */
package ss.client.event.executors;

import ss.client.event.tagging.TagInPreviewDisplayer;
import ss.client.ui.MessagesPane;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class KeywordsExecutor extends StatementExecutor {

	/**
	 * @param mp
	 * @param statement
	 */
	public KeywordsExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
	}


	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		final TagInPreviewDisplayer displayer = new TagInPreviewDisplayer(this.mp, this.statement, getSession());
		displayer.processKeywordSelected();
	}
}
