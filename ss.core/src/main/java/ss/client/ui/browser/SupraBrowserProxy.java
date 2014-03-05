/**
 * 
 */
package ss.client.ui.browser;

import ss.suprabrowser.AdvancedSearchHelper;
import ss.suprabrowser.MozillaBrowserController;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIFind;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.UiUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
class SupraBrowserProxy extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraBrowserProxy.class);
		
	private IBrowser browser;
	
	private MozillaBrowserController mbc;

	private MessagesPane mp;
	
	protected boolean belongsToBP = false;
	
	public SupraBrowserProxy(Composite parent, int style) {
		this(parent, style, false);
	}
	
	public SupraBrowserProxy(Composite parent, int style, boolean belongsToBrowserPane) {
		super(parent, SWT.NONE);
		this.belongsToBP = belongsToBrowserPane;
		setLayout(new GridLayout());
		this.mbc = new MozillaBrowserController( (SupraBrowser) this );
		createContent(style, belongsToBrowserPane);
	}

	protected void createContent(int style, boolean belongsToBrowserPane) {
		//initializeBrowser( new SWTBrowser(SupraBrowserProxy.this, style, belongsToBrowserPane) );
		//initializeBrowser( new NullBrowser(SupraBrowserProxy.this, this.mp) );
		initializeBrowser( new LightSWTBrowser(SupraBrowserProxy.this, style) );
		this.getCheckedBrowser().asComposite().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	/**
	 * @param browser2
	 */
	protected void initializeBrowser(IBrowser browser) {
		if ( browser == null ) {
			 throw new NullPointerException( "browser" );
		}
		if ( this.browser != null ) {
			throw new IllegalStateException( "Objec already initialized " + this );
		}
		this.browser = browser;
	}

	public MozillaBrowserController getMozillaBrowserController() {
		return this.mbc;
	}
		
	public void setText(final String text) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				SupraBrowserProxy.this.getCheckedBrowser().setText(text);
			}
		});
	}
	
	public void setUrl(String url) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().setUrl(url);
	}
	
	public String getUrl() {
		return this.getCheckedBrowser().getUrl();
	}
	
	/**
	 * @return
	 */
	protected String getSelection() {
		return this.getCheckedBrowser().getSelection();
	}

	/**
	 * 
	 */
	protected void extractSelection() {
		this.getCheckedBrowser().extractSelection();
	}


	public void scrollToBottom() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().scrollToBottom();
	}

	public void scrollToTop() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().scrollToTop();
	}

	public void scrollTo(final int width, final int height) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().scrollTo(width, height);
	}

	public void highlightSelectedString(final String message_id) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		if(SupraSphereFrame.INSTANCE.isReplyChecked()) {
			this.getCheckedBrowser().setYellowParentMessage(message_id);
		} else {
			this.getCheckedBrowser().highlightSelectedString(message_id);
		}
	}
	
	public void setMP(MessagesPane mp) {
		this.mp = mp;
	}

	public MessagesPane getMP() {
		return this.mp;
	}
	
	/**
	 * @param isContextMenuShow the isContextMenuShow to set
	 */
	public void setContextMenuShow(boolean isContextMenuShow) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().setContextMenuShow(isContextMenuShow);
	}
	
	public void scrollToSelectedElement(final String message_id) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().scrollToSelectedElement(message_id);
	}
	
	public void insertNewMessageString(final Statement statement) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().insertNewMessageString(statement);
	}
	
	public void findHighlightNext(String soughtString) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().findHighlightNext(soughtString);
	}
	
	public void highlightAllCommentedPlaces(String commentId) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().highlightAllCommentedPlaces(commentId);
	}
	
	public void findCommentedPlace(CommentStatement cs, boolean unhighlightAll) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().findCommentedPlace(cs, unhighlightAll);
	}
	
	public void unhighlightCommentedPlaces() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().unhighlightCommentedPlaces();
	}
	
	public void setShowAllCommentedPlaces(boolean value) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().setShowAllCommentedPlaces(value);
	}
	
	public void unhighlightFindResult() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().unhighlightFindResult();
	}
	
	public boolean findFirst(String str) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().findFirst(str);
	}
	
	public boolean findAtSamePosition(String str) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().findAtSamePosition(str);
	}

	public nsIDOMHTMLDocument getDomHtmlDocument() {
		return this.getCheckedBrowser().getDomHtmlDocument();
	}
	
	public Mozilla getMozBrowser() {
		return this.getCheckedBrowser().getMozzila();
	}
	
	public void deleteMessage(final Statement statement) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().deleteMessage(statement);
	}
	
	public void deleteMessage(final String messageId) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().deleteMessage(messageId);
	}
	
	public void hiddenDeleteMessage(final String messageId) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().hiddenDeleteMessage(messageId);
	}
	
	public void setSelection(String selection) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().setSelection(selection);
	}
	
	public void resetText(String content, final Statement statement) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().resetText(content, statement);
	}
	
	public void resetText(String content) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().resetText(content);
	}
	
	public void execute(String script) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().execute(script);
	}
	
	public nsISelection getBrowserSelection() {
		return this.getCheckedBrowser().getBrowserSelection();
	}
	
	public nsIWebBrowser getWebBrowser() {
		return this.getCheckedBrowser().getWebBrowser();
	}
	
	public void addLocationListener(LocationListener listener) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return;
		}
		this.getCheckedBrowser().addLocationListener(listener);
	}
	
	public boolean back() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().back();
	}
	
	public boolean isBackEnabled() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().isBackEnabled();
	}
	
	public boolean isForwardEnabled() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().isForwardEnabled();
	}
	
	public boolean forward() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
			return false;
		}
		return this.getCheckedBrowser().forward();
	}
	
	public void refresh() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
		}
		this.getCheckedBrowser().refresh();
	}
	
	public void addProgressListener(ProgressListener listener) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
		}
		this.getCheckedBrowser().addProgressListener(listener);
	}
	
	public void removeProgressListener(ProgressListener listener) {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
		}
		this.getCheckedBrowser().removeProgressListener(listener);
	}
	
	/**
	 * 
	 */
	public void setYellowParentMessage() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
		}
		Document doc = getMP().getLastSelectedDoc();
		if(doc == null) {
			return;
		}
		Statement statement = Statement.wrap(doc);
		String messageId = statement.getMessageId();
		this.getCheckedBrowser().setYellowParentMessage(messageId);
	}
	
	/**
	 * 
	 */
	public void revertParentMessageColor() {
		if ( !isBrowserInitialized() ) {
			logger.warn( "Browser is not initialized " + this );
		}
		Document doc = getMP().getLastSelectedDoc();
		if(doc == null) {
			return;
		}
		Statement statement = Statement.wrap(doc);
		String messageId = statement.getMessageId();
		highlightSelectedString(messageId);
	}
	
	/**
	 * 
	 */
	public void testWebBrowser() {
		this.getCheckedBrowser().testWebBrowser();
	}

	public boolean isReady() {
		return !isDisposed() && this.isBrowserInitialized();
	}
	
	@Override
	public void dispose() {
		deinitializeBrowser();
		super.dispose();
	}
	
	/**
	 * 
	 */
	private void deinitializeBrowser() {
		if ( this.browser != null ) { 
			this.browser.dispose();
			this.browser = null;
		}
	}

	/**
	 * @return the sb
	 */
	protected IBrowser getCheckedBrowser() {
		if ( !isBrowserInitialized() ) {
			throw new IllegalStateException( "SupraBrowser is not initalized " + this );
		}
		return this.browser;
	}

	/**
	 * @return
	 */
	private boolean isBrowserInitialized() {
		return this.browser != null && !this.browser.isDisposed();
	}
	
	/**
	 * @param action
	 */
	public void performAdvancedSearchAction(String action) {
		if(action==null) {
			return;
		}
		if(action.equals("collapse")) {
			collapseAdvanced();
		} else if(action.equals("expand")) {
			expandAdvanced();
		}
	}
	
	/**
	 * 
	 */
	private void expandAdvanced() {
		nsIDOMHTMLDocument doc = getDomHtmlDocument();
		if(doc==null) {
			return;
		}
		String html = AdvancedSearchHelper.getAdvancedBlock(SupraSphereFrame.INSTANCE.client);
		execute("advanced_object.fillAdvancedBlock('"+html+"')");
		
		AdvancedSearchHelper.addImagesToAdvancedBlock(getDomHtmlDocument());
	}

	/**
	 * 
	 */
	private void collapseAdvanced() {
		nsIDOMHTMLDocument doc = getDomHtmlDocument();
		if(doc==null) {
			return;
		}
		execute("advanced_object.clearSpoiler();");
	}
	
	/**
	 * @return
	 */
	public nsIFind getFinder() {
		return this.getCheckedBrowser().getFinder();
	}
}
