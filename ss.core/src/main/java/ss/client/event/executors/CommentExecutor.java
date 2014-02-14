/**
 * 
 */
package ss.client.event.executors;

import java.util.concurrent.atomic.AtomicReference;

import ss.client.ui.MessagesPane;
import ss.common.UiUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class CommentExecutor extends StatementExecutor {

	@SuppressWarnings("unused")
	private CommentStatement comment;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommentExecutor.class);

	/**
	 * @param mp
	 * @param statement
	 */
	public CommentExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.comment = CommentStatement.wrap(statement.getBindedDocument());
	}

	private CommentStatement getComment() {
		return this.comment;
	}

	@Override
	protected void browserExecute() {
		if (logger.isDebugEnabled()) {
			logger.debug("Browser executor performed");
		}
		
		final AtomicReference<Statement> item = new AtomicReference<Statement>();

		this.mp.setNeedOpenComment(true);

		for (Statement statement : getMP().getTableStatements()) {
			if (statement.getMessageId().equals(this.comment.getCommentId())) {
				item.set(statement);
			}
		}

		this.mp.setViewComment(this.comment);

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (getComment().getCommentThread().equals("bookmark")) {
					if (logger.isDebugEnabled()) {
						logger.debug("Bookmark comment thread");
					}
					String url = getBrowser().getUrl();
					if (logger.isDebugEnabled()) {
						logger.debug("URL for bookmark: " + url);
					}
					if (url != null && !url.equals(getComment().getAddress())) {
						if (logger.isDebugEnabled()) {
							logger.debug("URL is not null and !url.equals(getComment().getAddress())");
						}
						getMP().showSmallBrowserNoFocusSteal(getMP().getRawSession(),
								true, getComment().getAddress(), null, null,
								null);
					} else if (url == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("url is null");
						}
						getMP().showSmallBrowserNoFocusSteal(getMP().getRawSession(),
								true, getComment().getAddress(), null, null,
								null);
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Url is not null but second condition not true");
						}
						getMP().showCommentWindow();
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Not Bookmark comment thread");
					}
					StatementExecutorFactory
							.createExecutor(getMP(), item.get())
							.browserExecute();
				}
				selectMessageInMessagesPane();
			}
		});
		
	}
	
	
}
