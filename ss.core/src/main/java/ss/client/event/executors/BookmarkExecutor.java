/**
 * 
 */
package ss.client.event.executors;

import ss.client.ui.MessagesPane;
import ss.common.UiUtils;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class BookmarkExecutor extends StatementExecutor {

	private BookmarkStatement bookmark;

	public BookmarkExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
		this.bookmark = BookmarkStatement.wrap(statement.getBindedDocument());
	}

	@Override
	protected void browserExecute() {
		if(doWebHighlight()) {
			return;
		}
		
		final String address = this.bookmark.getAddress();

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				String url = getBrowser().getUrl();
				if (url != null && !address.equals(url))
					getMP().showSmallBrowser(getSession(), true, address, null,
							null, null);
				getMP().getPreviewAreaDocking().setBeginLocationLevel();
			}
		});
	}
	
	

}
