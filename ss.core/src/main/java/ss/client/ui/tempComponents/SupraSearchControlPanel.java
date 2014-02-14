/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.Listeners.browser.SSControlPanelReSearchListener;
import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SSBrowser;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.SupraSearchControlPanelDocking;
import ss.global.SSLogger;
import ss.search.SupraSearchDataSource;

/**
 * @author roman
 * 
 */
public class SupraSearchControlPanel extends Composite {

	private SupraBrowser mb = null;

	private final static Logger logger = SSLogger
			.getLogger(SupraSearchControlPanel.class);

	private ToolItem[] items = new ToolItem[6];

	private SupraSearchPane ssp;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_SUPRASEARCHCONTROLPANEL);

	private static final String RESEARCH = "SUPRASEARCHCONTROLPANEL.RESEARCH";

	private static final String FIRST = "SUPRASEARCHCONTROLPANEL.FIRST";

	private static final String PREV = "SUPRASEARCHCONTROLPANEL.PREV";

	private static final String NEXT = "SUPRASEARCHCONTROLPANEL.NEXT";

	private static final String LAST = "SUPRASEARCHCONTROLPANEL.LAST";

	private ToolBar toolBar;

	/**
	 * 
	 */
	public SupraSearchControlPanel(SupraSearchControlPanelDocking docking,
			Composite parentComposite) {
		super(parentComposite, SWT.NONE);
		this.ssp = docking.getSupraSearchPane();
		layoutComposite();
		logger.info("control panel for suprasearch pane created");
	}

	public SupraSearchControlPanel(SupraSphereFrame sF,
			Composite parentComposite) {
		super(parentComposite, SWT.NONE);
		layoutComposite();
	}

	public void activate(SupraBrowser mb, BrowserDataSource source) {
		if (mb == null || mb.isDisposed())
			return;
		this.mb = mb;
		this.items[0].addSelectionListener(new SSControlPanelReSearchListener(
				this.mb));
		this.items[0].setEnabled(true);
		disposeItems();
		createItems(source);
	}

	/**
	 * @param source
	 */
	private void createItems(BrowserDataSource source) {
		if (source instanceof SupraSearchDataSource) {
			SupraSearchDataSource sSource = (SupraSearchDataSource) source;
			int pageCount = sSource.getPageCount();
			int pageId = sSource.getPageId();
			int queryId = sSource.getQueryId();
			logger.info("queryId=" + queryId + " pageId=" + pageId
					+ " pageCount=" + pageCount);
			createHistoryItems();
			this.items[1] = createItem(queryId, 0,
					this.bundle.getString(FIRST), sSource.getSQuery());
			this.items[2] = createItem(queryId, pageId - 1, this.bundle
					.getString(PREV), sSource.getSQuery());
			this.items[3] = createItem(queryId, pageId, "" + (pageId + 1),
					sSource.getSQuery());
			this.items[4] = createItem(queryId, pageId + 1, this.bundle
					.getString(NEXT), sSource.getSQuery());
			this.items[5] = createItem(queryId, pageCount - 1, this.bundle
					.getString(LAST), sSource.getSQuery());
			if (pageId == 0) {
				this.items[1].setEnabled(false);
			}
			if (pageId == 0) {
				this.items[2].setEnabled(false);
			}
			this.items[3].setEnabled(false);
			if (pageId == pageCount - 1) {
				this.items[4].setEnabled(false);
			}
			if (pageId == pageCount - 1) {
				this.items[5].setEnabled(false);
			}
		}
	}

	/**
	 * @return
	 */
	private void createHistoryItems() {
		final ToolItem backItem = new ToolItem(this.toolBar, SWT.PUSH);
		backItem.setText("Back");
		backItem.setEnabled(this.mb.isBackEnabled());
		backItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SupraSearchControlPanel.this.mb.back();
			}
		});
		
		final ToolItem forwardItem = new ToolItem(this.toolBar, SWT.PUSH);
		forwardItem.setText("Forward");
		forwardItem.setEnabled(this.mb.isForwardEnabled());
		forwardItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SupraSearchControlPanel.this.mb.forward();
			}
		});
		
		this.mb.addLocationListener(new LocationAdapter() {
			@Override
			public void changed(LocationEvent event) {
				forwardItem.setEnabled(SupraSearchControlPanel.this.mb.isForwardEnabled());
				backItem.setEnabled(SupraSearchControlPanel.this.mb.isBackEnabled());
			}
		});
		
	}

	/**
	 * @param queryId
	 * @param pageId
	 * @return
	 */
	private ToolItem createItem(final int queryId, final int pageId,
			String name, final String sQuery) {
		ToolItem item = new ToolItem(this.toolBar, SWT.PUSH);
		item.setText(name);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final Hashtable session = SupraSearchControlPanel.this.mb
						.getMozillaBrowserController().getCurrentSession();
				final DialogsMainCli cli = SupraSphereFrame.INSTANCE
						.getActiveConnections().getActiveConnection(
								((String) session.get("sphereURL")));
				cli.showPage(String.valueOf(queryId), String.valueOf(pageId),
						SupraSearchControlPanel.this.mb,
						SupraSearchControlPanel.this, sQuery);
			}
		});

		return item;
	}

	/**
	 * 
	 */
	private void disposeItems() {
		for (int i = 1; i < this.items.length; i++) {
			if (this.items[i] != null) {
				this.items[i].dispose();
				this.items[i] = null;
			}
		}

	}

	private void layoutComposite() {

		GridLayout layout = new GridLayout(7, false);
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		this.toolBar = new ToolBar(this, SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 4;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.BEGINNING;
		this.toolBar.setLayoutData(data);

		this.items[0] = new ToolItem(this.toolBar, SWT.PUSH);
		this.items[0].setText(this.bundle.getString(RESEARCH));
		this.items[0].setEnabled(false);

		this.toolBar.setVisible(true);
	}

	public SupraBrowser getBrowser() {
		logger.debug("browser call");
		return this.mb;
	}

	public SupraSearchPane getSupraSearchPane() {
		return this.ssp;
	}

}
