/**
 * 
 */
package ss.client.ui.browser;

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

/**
 *
 */
public interface IBrowser {

	void resetText(final String content);
	
	void scrollToBottom();
	
	void scrollToTop();
	
	void scrollTo(final int width, final int height);
	
	void highlightSelectedString(final String message_id);
	
	MessagesPane getMP();
	
	void setContextMenuShow(boolean isContextMenuShow);
	
	void scrollToSelectedElement(final String message_id);
	
	void insertNewMessageString(final Statement statement);
	
	void findHighlightNext(String soughtString);
	
	void highlightAllCommentedPlaces(String commentId);
	
	void findCommentedPlace(CommentStatement cs, boolean unhighlightAll);
	
	void unhighlightCommentedPlaces();
	
	void setShowAllCommentedPlaces(boolean value);
	
	void unhighlightFindResult();
	
	boolean findFirst(String str);
	
	boolean findAtSamePosition(String str);
	
	nsIDOMHTMLDocument getDomHtmlDocument();
	
	Mozilla getMozzila();
	
	void deleteMessage(final Statement statement);
	
	void deleteMessage(final String messageId);
	
	void hiddenDeleteMessage(final String messageId);
	
	void setSelection(String selection);
	
	void dispose();
	
	void resetText(String content, final Statement statement);
	
	void setYellowParentMessage(final String messageId);
	
	Composite asComposite();
	
	boolean setText(String text);
	
	boolean setUrl(String url);
	
	String getUrl();
	
	String getSelection();
	
	void extractSelection();
	
	nsISelection getBrowserSelection();
	
	boolean execute(String script);
	
	nsIWebBrowser getWebBrowser();
	
	boolean back();
	
	boolean forward();
	
	void refresh();
	
	boolean isForwardEnabled();
	
	boolean isBackEnabled();
	
	void addLocationListener(LocationListener listener);
	
	void addProgressListener(ProgressListener listener);
	
	void removeProgressListener(ProgressListener listener);

	void testWebBrowser();

	boolean isDisposed();

	/**
	 * @return
	 */
	nsIFind getFinder();
}
