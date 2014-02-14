package ss.client.ui.browser;

public class SimpleBrowserDataSource implements BrowserDataSource {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SimpleBrowserDataSource.class);

	private String URL;

	public SimpleBrowserDataSource(String url) {
		this.URL = url;
	}

	public String getURL() {
		return this.URL;
	}

	public void setupBrowser(SupraBrowser browser) {
		if (browser == null || browser.isDisposed()) {
			logger.error("browser is null or disposed");
			return;
		}
		if (getURL().length() > 0) {
			
			browser.setUrl(getURL());

		}
	}

}
