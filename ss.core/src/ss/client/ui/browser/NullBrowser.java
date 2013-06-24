package ss.client.ui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIFind;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;

import ss.client.ui.MessagesPane;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

public class NullBrowser extends Composite implements IBrowser {

	private final MessagesPane mp;
	/**
	 * 
	 */
	public NullBrowser( Composite parent, MessagesPane mp ) {
		super( parent, SWT.BORDER );
		this.mp = mp;
	}

	
	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getBrowserSelection()
	 */
	public nsISelection getBrowserSelection() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getWebBrowser()
	 */
	public nsIWebBrowser getWebBrowser() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getDomHtmlDocument()
	 */
	public nsIDOMHTMLDocument getDomHtmlDocument() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getMP()
	 */
	public MessagesPane getMP() {
		return this.mp;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getMozBrowser()
	 */
	public Mozilla getMozzila() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getSelection()
	 */
	public String getSelection() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getUrl()
	 */
	public String getUrl() {
		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#addLocationListener(org.eclipse.swt.browser.LocationListener)
	 */
	public void addLocationListener(LocationListener listener) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#addProgressListener(org.eclipse.swt.browser.ProgressListener)
	 */
	public void addProgressListener(ProgressListener listener) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#asComposite()
	 */
	public Composite asComposite() {
		return this;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#back()
	 */
	public boolean back() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#deleteMessage(ss.domainmodel.Statement)
	 */
	public void deleteMessage(Statement statement) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#deleteMessage(java.lang.String)
	 */
	public void deleteMessage(String messageId) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#execute(java.lang.String)
	 */
	public boolean execute(String script) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#extractSelection()
	 */
	public void extractSelection() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#findAtSamePosition(java.lang.String)
	 */
	public boolean findAtSamePosition(String str) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#findCommentedPlace(ss.domainmodel.CommentStatement, boolean)
	 */
	public void findCommentedPlace(CommentStatement cs, boolean unhighlightAll) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#findFirst(java.lang.String)
	 */
	public boolean findFirst(String str) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#findHighlightNext(java.lang.String)
	 */
	public void findHighlightNext(String soughtString) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#forward()
	 */
	public boolean forward() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#hiddenDeleteMessage(java.lang.String)
	 */
	public void hiddenDeleteMessage(String messageId) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#highlightAllCommentedPlaces(java.lang.String)
	 */
	public void highlightAllCommentedPlaces(String commentId) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#highlightSelectedString(java.lang.String)
	 */
	public void highlightSelectedString(String message_id) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#insertNewMessageString(ss.domainmodel.Statement)
	 */
	public void insertNewMessageString(Statement statement) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#isBackEnabled()
	 */
	public boolean isBackEnabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#isForwardEnabled()
	 */
	public boolean isForwardEnabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#refresh()
	 */
	public void refresh() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#removeProgressListener(org.eclipse.swt.browser.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#resetText(java.lang.String)
	 */
	public void resetText(String content) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#resetText(java.lang.String, ss.domainmodel.Statement)
	 */
	public void resetText(String content, Statement statement) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#scrollTo(int, int)
	 */
	public void scrollTo(int width, int height) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#scrollToBottom()
	 */
	public void scrollToBottom() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#scrollToSelectedElement(java.lang.String)
	 */
	public void scrollToSelectedElement(String message_id) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#scrollToTop()
	 */
	public void scrollToTop() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setContextMenuShow(boolean)
	 */
	public void setContextMenuShow(boolean isContextMenuShow) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setSelection(java.lang.String)
	 */
	public void setSelection(String selection) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setShowAllCommentedPlaces(boolean)
	 */
	public void setShowAllCommentedPlaces(boolean value) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setText(java.lang.String)
	 */
	public boolean setText(String text) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setUrl(java.lang.String)
	 */
	public boolean setUrl(String url) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#setYellowParentMessage(java.lang.String)
	 */
	public void setYellowParentMessage(String messageId) {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#unhighlightCommentedPlaces()
	 */
	public void unhighlightCommentedPlaces() {
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#unhighlightFindResult()
	 */
	public void unhighlightFindResult() {
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#testWebBrowser()
	 */
	public void testWebBrowser() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#getFinder()
	 */
	public nsIFind getFinder() {
		return null;
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.browser.IBrowser#scriptBack()
	 */
	public void scriptBack() {
		// TODO Auto-generated method stub
		
	}
	
}
