/**
 * 
 */
package ss.client.event.tagging.gui;

import org.eclipse.swt.widgets.Composite;

import ss.client.event.tagging.obtainer.BookmarksForTagObtainer;
import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.event.tagging.obtainer.BookmarksForTagObtainer.BookmarkForTag;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SimpleBrowserDataSource;

/**
 * @author zobo
 *
 */
public class BookmarksListForTagComposite extends AbstractListForTagComposite {

	public static final String TITLE = "Bookmars";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BookmarksListForTagComposite.class);

	/**
	 * @param parent
	 * @param style
	 * @param obtainer
	 */
	public BookmarksListForTagComposite(Composite parent, int style, DataForTagObtainer obtainer) {
		super( parent, style, obtainer );
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.AbstractListForTagComposite#getItemsData()
	 */
	@Override
	protected String[] getItemsData() {
		if ((getObtainer().getList() == null) || 
				getObtainer().getList().isEmpty()) {
			return null;
		}
		String[] items = new String[getObtainer().getList().size()];
		int i = 0;
		for (BookmarkForTag bookmark : getObtainer().getList()) {
			items[i++] = bookmark.getSubject();
		}
		return items;
	}

	public String getTitle() {
		return TITLE;
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.gui.AbstractListForTagComposite#load(int)
	 */
	@Override
	protected void load( int index ) {
		final BookmarkForTag bookmarkSelected = getObtainer().getList()
			.get(index);
		final MessagesPane pane = SupraSphereFrame.INSTANCE.tabbedPane
			.getSelectedMessagesPane();
		SupraSphereFrame.INSTANCE.addMozillaTab(
		pane == null ? SupraSphereFrame.INSTANCE.getMainRawSession()
				: pane.getRawSession(), bookmarkSelected.getSubject(),
		new SimpleBrowserDataSource(bookmarkSelected.getURL()), false,
		getObtainer().getBookmarkStatement(bookmarkSelected));
	}
	
	private BookmarksForTagObtainer getObtainer(){
		return this.obtainer.getBoomarks();
	}
}
