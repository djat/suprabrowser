/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ss.client.event.TabChangeListener;
import ss.client.ui.ControlPanel;
import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.SBrowserDocking;
import ss.client.ui.root.SupraTab;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.SphereStatement;

/**
 * @author zobo
 */
public final class SupraCTabTable extends CTabFolder {

	private SelectionListener selectionListener;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraCTabTable.class);

	private final List<String> openedSpheresId = Collections.synchronizedList( new ArrayList<String>() );

	private final TabSorter tabSorter = new TabSorter();
	
	private Font markFont = null;

	public SupraCTabTable(Composite composite, int arg) {
		super(composite, arg);
		setLayout(new GridLayout());
		this.addCTabFolder2Listener(new CTabFolder2Listener(){

			public void close( final CTabFolderEvent event ) {
				event.doit = false;
				((SupraCTabItem) event.item).safeClose();
			}

			public void maximize(CTabFolderEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void minimize(CTabFolderEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void restore(CTabFolderEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void showList(CTabFolderEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	public final MessagesPane getSelectedMessagesPane() {
		return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				SupraCTabItem item = (SupraCTabItem) getSelection();
				if (item == null) {
					return null;
				}
				return item.getMessagesPane();
			}
		});
	}

	public final BrowserPane getSelectedBrowserPane() {
		return UiUtils.swtEvaluate(new Callable<BrowserPane>() {
			public BrowserPane call() throws Exception {
				SupraCTabItem item = (SupraCTabItem) getSelection();
				if (item == null) {
					return null;
				}
				return item.getBrowserPane();
			}
		});
	}

	public final SupraBrowser getSelectedSupraBrowser() {
		final SupraCTabItem item = getSelectedSupraItem();
		return item != null ? item.getMBrowser() : null;
	}

	public final ExternalEmailPane getSelectedEmailPane() {
		return UiUtils.swtEvaluate(new Callable<ExternalEmailPane>() {
			public ExternalEmailPane call() throws Exception {
				SupraCTabItem item = (SupraCTabItem) getSelection();
				if (item == null) {
					return null;
				}
				return item.getEmailPane();
			}
		});
	}

	public SBrowserDocking getSelectedBrowserDocking() {
		final BrowserPane selectedBrowserPane = getSelectedBrowserPane();
		if (selectedBrowserPane != null) {
			return selectedBrowserPane.getBrowserDocking();
		}
		final ExternalEmailPane selectedEmailPane = getSelectedEmailPane();
		if (selectedEmailPane != null) {
			return selectedEmailPane.getBrowserDocking();
		}
		final SupraSearchPane selectedSupraSearchPane = getSelectedSupraSearchPane();
		if (selectedSupraSearchPane != null) {
			return selectedSupraSearchPane.getBrowserDocking();
		}
		return null;
	}

	public SupraCTabItem getSelectedSupraItem() {
		return UiUtils.swtEvaluate(new Callable<SupraCTabItem>() {
			public SupraCTabItem call() throws Exception {
				return (SupraCTabItem) getSelection();
			}
		});
	}

	public void repaint() {
		Composite[] comps = getComponents();
		for (Composite comp : comps) {
			if (comp != null && comp instanceof MessagesPane
					&& !comp.isDisposed())
				((MessagesPane) comp).repaint();
		}
	}

	public MessagesPane getComponentAt1(final int index) {
		return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				return ((SupraCTabItem) getItem(index)).getMessagesPane();
			}
		});
	}

	public Composite[] getComponents() {
		return UiUtils.swtEvaluate(new Callable<Composite[]>() {
			public Composite[] call() throws Exception {
				Composite[] components = new Composite[getItemCount()];
				for (int i = 0; i < components.length; i++) {
					components[i] = ((SupraCTabItem) getItem(i)).getPane();
				}
				return components;
			}
		});
	}

	public void selectTabByPane(final Composite pane) {
		if (pane == null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				CTabItem[] items = getItems();
				for (int i = 0; i < items.length; i++) {
					SupraCTabItem item = (SupraCTabItem) items[i];
					Composite itemPane = item.getPane();
					if ((itemPane != null) && (itemPane.equals(pane))) {
						setSelection(i);
						return;
					}
				}
			}
		});
	}

	public SupraCTabItem addTab(String title, Image image,
			MessagesPane component) {
		VerifyAuth auth = component.client.getVerifyAuth();
		Element buildOrder = auth.getBuildOrder();
		String systemName = SphereStatement.wrap(
				component.getSphereDefinition()).getSystemName();

		if (!isAlreadyOpenenedSameSphere(systemName)) {
			putSphereToTabQueue(systemName);
		}
		int position = this.tabSorter.queryPosition( getItemCount(), buildOrder, title );
		SupraCTabItem item = new SupraCTabItem(this, position, this);
		component.setTabItem(item);
		this.setAndAddTabItem(item, title, image, component);				
		component.sF.getMenuBar().checkAddRemoveEnabled();
		return item;
	}

	public void addRootTab(final SupraTab root) {
		this.tabSorter.queryPosition( getItemCount(), "Root Tab", -1 );
		SupraCTabItem item = new SupraCTabItem( this, this );
		item.setRootPane(root);
	}

	private void setAndAddTabItem(SupraCTabItem item, String title,
			Image image, MessagesPane component) {
		item.setTabProperties(title, image, component);
	}

	public void addTab(String title, MessagesPane component) {
		addTab(title, null, component);
	}

	public void addChangeListener(TabChangeListener l) {
		this.selectionListener = l;
		addSelectionListener(this.selectionListener);
	}

	/*
	 * public void addMouseListener(MouseListener l) { if (this.listener ==
	 * null){ this.listener = l; addMouseListener(this.listener); } /*for
	 * (SupraCTabItem item : this.tabItems) {
	 * item.setMouseListener(this.listener); }
	 */
	// }
	public SupraCTabItem loadURL(final Hashtable session, final String title,
			final BrowserDataSource browserDataSource,
			final SupraSphereFrame sF, final boolean addListener,
			final BookmarkStatement bookmark) {
		/*
		 * Display.getDefault().asyncExec(new Thread() { public void run() {
		 * cont.activate(item.get().setBrowser(sF,session, title, URI)); } });
		 */
		return UiUtils.swtEvaluate(new Callable<SupraCTabItem>() {
			
			SupraCTabTable pane = SupraCTabTable.this;
			
			public SupraCTabItem call() throws Exception {
				int position = SupraCTabTable.this.tabSorter.queryPosition( getItemCount() );
				SupraCTabItem cTabItem = new SupraCTabItem(this.pane, position,	this.pane);
				logger.info("Calling loadURL: " + title + " : "
						+ browserDataSource.getURL());
				cTabItem.setBrowser(session, title, browserDataSource, sF,
						addListener, bookmark);
				return cTabItem;
			}
		});
	}

	public SupraCTabItem loadSupraSearchResult(final Hashtable session,
			final String title, final BrowserDataSource browserDataSource,
			final SupraSphereFrame sF) {
		/*
		 * Display.getDefault().asyncExec(new Thread() { public void run() {
		 * cont.activate(item.get().setBrowser(sF,session, title, URI)); } });
		 */
		return UiUtils.swtEvaluate(new Callable<SupraCTabItem>() {
			
			SupraCTabTable pane = SupraCTabTable.this;
			
			public SupraCTabItem call() throws Exception {
				int position = SupraCTabTable.this.tabSorter.queryPosition( getItemCount() );
				SupraCTabItem cTabItem = new SupraCTabItem(this.pane, position, this.pane);
				cTabItem.setSupraSearchResult(session, title,
						browserDataSource, sF);
				return cTabItem;
			}
		});
	}

	public SupraCTabItem showEmail(final ExternalEmailStatement email,
			final SupraSphereFrame sF, final Hashtable session,
			final MessagesPane mp) {
		return UiUtils.swtEvaluate(new Callable<SupraCTabItem>() {
			
			SupraCTabTable pane = SupraCTabTable.this;
			
			public SupraCTabItem call() throws Exception {
				int position = SupraCTabTable.this.tabSorter.queryPosition( getItemCount() );
				SupraCTabItem cTabItem = new SupraCTabItem(this.pane, position, this.pane);
				cTabItem.showEmail(email, sF, session, mp);
				return cTabItem;
			}
		});
	}

	public SelectionListener getSelectionListener() {
		return this.selectionListener;
	}

	public void putSphereToTabQueue(String name) {
		this.openedSpheresId.add(name);
	}

	public void removeSphereFromTabQueue(String name) {
		this.openedSpheresId.remove(name);
	}

	public boolean isAlreadyOpenenedSameSphere(String name) {
		return this.openedSpheresId.contains(name);
	}

	public SupraCTabItem getItemByTitle(final String title) {
		return null;
	}

	/**
	 * @return
	 */
	public SupraSearchPane getSelectedSupraSearchPane() {
		return UiUtils.swtEvaluate(new Callable<SupraSearchPane>() {
			public SupraSearchPane call() throws Exception {
				SupraCTabItem item = (SupraCTabItem) getSelection();
				if (item == null) {
					return null;
				}
				return item.getSupraSearchPane();
			}
		});
	}

	/**
	 * @return
	 */
	public ISphereView getSelectedSphereView() {
		return getSelectedMessagesPane();
	}

	/**
	 * 
	 */
	public void resetMarkForSelectedTab() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				SupraCTabItem item = getSelectedSupraItem();
				if (item != null) {
					item.resetMark();
				}
			}
		});
	}

	/**
	 * TODO#review this method looking very strange.
	 */
	public boolean isMessagesPaneSelected() {
		return getSelectedMessagesPane() != null;
	}

	/**
	 * 
	 */
	public boolean setFocusToCurrentSendField() {
		Shell activeShell = SDisplay.display.get().getActiveShell();
		boolean isShellActive = activeShell.equals(SupraSphereFrame.INSTANCE.getShell());
		if(!isShellActive) {
			return false;
		}
		
		final MessagesPane selectedMessagesPane = getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					if (logger.isDebugEnabled()) {
						logger.debug("setFocusToCurrentSendField performed");
					}
					if (selectedMessagesPane.getControlPanel() instanceof ControlPanel) {
						((ControlPanel) selectedMessagesPane.getControlPanel())
								.setFocusToSendField();
					}
				}
			});
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return
	 */
	public boolean isReplyChecked() {
		MessagesPane selectedMessagesPane = this.getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			return selectedMessagesPane.isReplyChecked();
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isTagChecked() {
		MessagesPane selectedMessagesPane = this.getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			return selectedMessagesPane.isTagChecked();
		}
		return false;
	}

	/**
	 * @param value
	 */
	public void setReplyChecked(boolean value) {
		final MessagesPane selectedMessagesPane = this
				.getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			selectedMessagesPane.setReplyChecked(value);
		}
	}

	/**
	 * @param value
	 */
	public void setTagChecked(boolean value) {
		final MessagesPane selectedMessagesPane = this
				.getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			selectedMessagesPane.setTagChecked(value);
		}
	}

	/**
	 * @param text
	 */
	public void setSendText(String text) {
		final MessagesPane selectedMessagesPane = getSelectedMessagesPane();
		if (selectedMessagesPane != null) {
			selectedMessagesPane.setSendText(text);
		}
	}

	/**
	 * @param title
	 */
	public boolean selectTabByTitle(final String title) {
		boolean ret = UiUtils.swtEvaluate(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return unsafeSelectTabByTitle(title);
			}
		});
		if (ret) {
			getSupraSphereFrame().getMenuBar().checkAddRemoveEnabled();
		}
		return ret;
	}

	/**
	 * @return
	 */
	private SupraSphereFrame getSupraSphereFrame() {
		return SupraSphereFrame.INSTANCE;
	}

	private boolean unsafeSelectTabByTitle(String targetTitle) {
		for (int i = 0; i < getItemCount(); i++) {
			final CTabItem item = getItem(i);
			String title = item.getText();
			if (title != null && title.equals(targetTitle)) {
				setSelection(item);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public Iterable<SupraBrowser> getBrowsers() {
		UiUtils.swtEvaluate(new Callable<List<SupraBrowser>>() {
			public List<SupraBrowser> call() throws Exception {
				List<SupraBrowser> list = new ArrayList<SupraBrowser>();
				for( SupraCTabItem item : getSupraItems() ) {
					final SupraBrowser browser = item.getMBrowser();
					if ( browser != null && !browser.isDisposed()) {
						list.add( browser );
					}
				}
				return list;
			}
		});
		return null;
	}

	/**
	 * @return
	 */
	public List<MessagesPane> getMessagesPanes() {
		return UiUtils.swtEvaluate(new Callable<List<MessagesPane>>() {
			public List<MessagesPane> call() throws Exception {
				List<MessagesPane> list = new ArrayList<MessagesPane>();
				for( SupraCTabItem item : getSupraItems() ) {
					final MessagesPane messagesPane = item.getMessagesPane();
					if ( messagesPane != null ) {
						list.add( messagesPane );
					}
				}
				return list;
			}
		});
	}

	public MessagesPane findMessagesPane(String sphereId, String uniqueId) {
		for (MessagesPane messagesPane : getMessagesPanes()) {
			if (messagesPane.getSphereId().equals(sphereId)
					&& messagesPane.getUniqueId().equals(uniqueId)) {
				return messagesPane;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public Iterable<SupraCTabItem> getSupraItems() {
		List<SupraCTabItem> list = new ArrayList<SupraCTabItem>();
		for (CTabItem item : getItems()) {
			list.add((SupraCTabItem) item);
		}
		return list;
	}

	/**
	 * @param item
	 */
	void remove(SupraCTabItem item) {
		MessagesPane mp = item.getMessagesPane();
		final String sphereId = mp != null ? mp.getSphereId() : null;
		if (sphereId != null && isAlreadyOpenenedSameSphere(sphereId)) {
			removeSphereFromTabQueue(sphereId);
			SphereOpenManager.INSTANCE.unregister(mp);
		}
		item.dispose();
	}

	/**
	 * @return
	 */
	public Font getMarkFont() {
		if (this.markFont == null) {
			final FontData[] fontDatas = getFont().getFontData();
			final FontData fontData = fontDatas != null && fontDatas.length > 0 ? fontDatas[0]
					: new FontData("Sans Serif", 12, SWT.NORMAL);
			this.markFont = new Font(getDisplay(), fontData.getName(), fontData
					.getHeight(), SWT.BOLD);
		}
		return this.markFont;
	}

	/**
	 * 
	 */
	public void selectNextTab() {
		final int index = getSelectionIndex();
		if (index == getItemCount() - 1) {
			setSelection(getItem(0));
		} else {
			setSelection(getItem(index + 1));
		}
	}
	
	public boolean isFirstOpeningFromTabOrder(String displayName){
		return this.tabSorter.isFirstOpeningFromTabOrder(displayName);
	}
}
