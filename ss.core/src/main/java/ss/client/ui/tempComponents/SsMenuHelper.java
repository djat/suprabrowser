/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.processing.TagActionProcessor;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class SsMenuHelper {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SsMenuHelper.class);

	private static final String TAG_WITH_CURRENT_KEYWORD = "Tag with current keyword";

	private static final String TAG_WITH_KEYWORDS = "Tag with keywords";
	
	public static final SsMenuHelper INSTANCE = new SsMenuHelper();
	
	private SsMenuHelper() {
		super();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void showMenu(SupraBrowser browser, final String sphereId, final String messageId) {
		if (logger.isDebugEnabled()) {
			logger.debug( "showMenu in ssMenuHelper performed" );
			logger.debug("SphereId: " + sphereId + ", messageId: " + messageId);
		}
		final Menu menu = new Menu(browser);
		menu.setLocation(SDisplay.display.get().getCursorLocation()); 
		
		MenuItem withKeywordItem = new MenuItem(menu, SWT.PUSH);
		withKeywordItem.setText(TAG_WITH_KEYWORDS);
		withKeywordItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final Dialog dialog = new AssetTagDialog(sphereId, messageId);
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
		
		final String keyword = browser.getDomHtmlDocument().getElementById("keyword").getAttribute("value");
		MenuItem withCurrentKeywordItem = new MenuItem(menu, SWT.PUSH);
		withCurrentKeywordItem.setText(TAG_WITH_CURRENT_KEYWORD);
		withCurrentKeywordItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (logger.isDebugEnabled()) {
					logger.debug(TAG_WITH_CURRENT_KEYWORD + " performed");
					logger.debug("Keyword: " + keyword);
				}
				final Hashtable session = (Hashtable) SupraSphereFrame.INSTANCE.client.session.clone();
				session.put(SessionConstants.SPHERE_ID2, sphereId);
				final Document doc = SupraSphereFrame.INSTANCE.client.getSpecificId(session, messageId);
				if (logger.isDebugEnabled()) {
					logger.debug("Specific Document recieved for tag as current keyword: " + doc.asXML());
				}
				TagActionProcessor processor = new TagActionProcessor( SupraSphereFrame.INSTANCE.client, sphereId, doc );
				processor.doTagAction( keyword );
			}
		});
		
		menu.setVisible(true);
	}
}
