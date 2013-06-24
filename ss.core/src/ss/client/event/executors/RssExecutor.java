/**
 * 
 */
package ss.client.event.executors;

import java.util.concurrent.atomic.AtomicReference;

import ss.client.ui.MessagesPane;
import ss.domainmodel.RssStatement;
import ss.domainmodel.Statement;
import ss.rss.XSLTransform;

/**
 * @author roman
 *
 */
public class RssExecutor extends StatementExecutor {

	private RssStatement rss;

	/**
	 * @param mp
	 * @param statement
	 */
	public RssExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.rss = RssStatement.wrap(statement.getBindedDocument());
	}

	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		final AtomicReference<Statement> item = new AtomicReference<Statement>();
		item.set(this.rss);
		Thread t = new Thread() {
			public void run() {
				String link = item.get().getAddress();
				String content = XSLTransform.transformRSS(link);

				getMP().showSmallBrowser(getSession(), true, link, content,
						item.get(), null);

			}
		};
		t.start();

	}
	
	
}
