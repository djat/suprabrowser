/**
 * 
 */
package ss.client.event.executors;

import java.util.concurrent.atomic.AtomicReference;

import ss.client.ui.MessagesPane;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.rss.SphereDocTransform;

/**
 * @author roman
 *
 */
public class SphereExecutor extends StatementExecutor {

	private SphereStatement sphere;

	/**
	 * @param mp
	 * @param statement
	 */
	public SphereExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.sphere = SphereStatement.wrap(statement.getBindedDocument());
	}

	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		final AtomicReference<Statement> item = new AtomicReference<Statement>();
		item.set(this.sphere);
		Thread t = new Thread() {
			@Override
			public void run() {
				String content = SphereDocTransform
						.getString(SphereExecutor.this.sphere
								.getBindedDocument());
				getMP().showSmallBrowser(getSession(), true, "sphere",
						content, item.get(), null);

			}
		};
		t.start();
	}
	
	
}
