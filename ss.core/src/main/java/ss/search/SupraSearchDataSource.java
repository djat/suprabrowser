/**
 * 
 */
package ss.search;

import ss.suprabrowser.MozillaBrowserController;

import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SupraBrowser;
import ss.common.UiUtils;

/**
 * @author dankosedin
 * 
 */
public class SupraSearchDataSource implements BrowserDataSource {

	private int queryId;

	private int pageId;

	private int pageCount;

	private String URL;

	private String Html;

	private SupraBrowser browser;

	private String sQuery;

	public SupraSearchDataSource(SupraBrowser browser, String html, String url,
			String sQuery, int queryId, int pageId, int pageCount) {
		this.queryId = queryId;
		this.pageId = pageId;
		this.pageCount = pageCount;
		this.URL = url;
		this.Html = html;
		this.browser = browser;
		this.sQuery = sQuery;
	}

	public SupraBrowser getBrowser() {
		return this.browser;
	}

	public String getHtml() {
		return this.Html;
	}

	public int getPageCount() {
		return this.pageCount;
	}

	public int getPageId() {
		return this.pageId;
	}

	public int getQueryId() {
		return this.queryId;
	}

	public String getURL() {
		return this.URL;
	}

	public void setupBrowser(SupraBrowser browser) {
		setUpBrowser(getHtml(), browser);
	}

	private void setUpBrowser(final String html, final SupraBrowser browser) {
		browser.setText(html);
		browser.addProgressListener(new ProgressListener() {

			public void changed(ProgressEvent e) {

			}

			public void completed(ProgressEvent e) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						MozillaBrowserController controller = browser
								.getMozillaBrowserController();
						controller.injectJS();
						browser.scrollToTop();
					}
				});
				browser.removeProgressListener(this);
			}
		});
	}

	public String getSQuery() {
		return this.sQuery;
	}

}
