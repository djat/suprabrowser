/**
 * 
 */
package ss.client.ui.docking;

import java.io.IOException;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

import ss.client.configuration.ApplicationConfiguration;
import ss.client.configuration.XulRunnerRegisterState;
import ss.client.event.HystorySelectionListener;
import ss.client.event.ScrollLockSelectionlistener;
import ss.client.event.ShowAllButtonSelectionListener;
import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.Listeners.emailbrowser.EmailControlPanelComposeListener;
import ss.client.ui.Listeners.preview.PreviewEmailDiscussListener;
import ss.client.ui.Listeners.preview.PreviewEmailToContactListener;
import ss.client.ui.Listeners.preview.PreviewForwardEmailListener;
import ss.client.ui.Listeners.preview.PreviewReplyEmailListener;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.MultiCompositeContainer;
import ss.client.ui.tempComponents.researchcomponent.ReSearchToolItemComponent;
import ss.client.ui.widgets.SearchPane;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.util.ImagesPaths;
import swtdock.ILayoutPart;

/**
 * @author zobo
 * 
 */
public class PreviewAreaDocking extends AbstractDockingComponent implements
		ISearchable {
	
	private static final String PREWIEV_AREA = "PREVIEWAREADOCKING.PREVIEW_AREA";

	private static final String SHOW_ALL_MESSAGES = "PREVIEWAREADOCKING.SHOW_ALL_MESSAGES";

	private static final String SCROLL_LOCK = "PREVIEWAREADOCKING.SCROLL_LOCK";

	private static final int MAX_BUTTON_COUNT = 32;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_DOCKING_PREVIEWAREADOCKING);

	private MessagesPane mP;

	private MultiCompositeContainer container;

	private SupraBrowser sb = null;

	private boolean browserShown = false;

	private Composite parent;

	private Image scrollLockIcon = null;

	private Image showAllIcon = null;

	private Image bookmarkIcon = null;

	private Image backwardIcon = null;

	private Image forwardIcon = null;
	
	private Image composeIcon = null;
	
	private Image replyIcon = null;
	
	private Image forwardEmailIcon = null;

	private LocationListener locationListener = null;

	private int browserLocationLevel = 0;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreviewAreaDocking.class);

	private SearchPane searchPane = null;

	private Button scrollLock;

	private Button hForward;

	private Button hBackward;

	private Button showButton;

	private Button saveBookmark;

	private Button back;

	private Button forward;
	
	private Button forwardEmailButton;
	
	private Button composeEmailButton;
	
	private Button replyEmailButton;
	
	private ReSearchToolItemComponent researchEmailButton;
	
	private Button discussEmailButton;
	
	private Button emailToContactButton;

	private Composite toolComp;

	private boolean isBrowserShaken = false;

	private Image discussIcon;

	private Image researchIcon;

	private Image emailToContactIcon;

	private ToolBar researchToolBar;

	/**
	 * @param dm
	 */
	public PreviewAreaDocking(SupraDockingManager dm, MessagesPane mP) {
		super(dm);
		this.mP = mP;
	}

	@Override
	public String getName() {
		return this.bundle.getString(PREWIEV_AREA);
	}

	@Override
	public void createContent(Composite parent) {

		parent.setLayout(new FillLayout());

		this.container = new MultiCompositeContainer(parent);

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.container.setLayout(layout);
		addBrowser();
		this.parent = parent;
	}

	private void addBrowser() {
		try {
			this.sb = new SupraBrowser(this.container, SWT.MOZILLA | SWT.BORDER);
		}
		catch( Throwable ex ) {
			final String message = "Can't create mozilla browser";
			logger.error( message, ex );
			ApplicationConfiguration appCfg = ApplicationConfiguration.loadUserConfiguration();
			if ( appCfg.getXulrunnerRegistered() == XulRunnerRegisterState.REGISTERED ) {
				appCfg.setXulRunnerRegistered(XulRunnerRegisterState.UNKNOWN);
				appCfg.save();
			}
			UserMessageDialogCreator.error( message );
		}

		this.container.addComposite(this.sb);

		this.sb.setVisible(true);

		this.sb.setMP(this.mP);
		if (this.mP.getPreviewHtmlText() != null) {
			this.showBrowser(true, null,
					this.mP.getPreviewHtmlText().getText(), null, null);
		}

		GridData data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.sb.setLayoutData(data);

		this.locationListener = new PreviewLocationListener();
		this.sb.addLocationListener(this.locationListener);
		if (this.researchEmailButton != null) {
			this.researchEmailButton.activate( this.sb );
		}
	}

	public SupraBrowser getBrowser() {
		if (logger.isDebugEnabled()) 
			logger.debug("browser call from preview");
		return this.sb;
	}

	public void showBrowser(boolean show, String URL, String content,
			final Statement statement, final Document highlightDoc) {
		if (logger.isDebugEnabled()) {
			logger.debug("Calling show browser");
		}
		if (show) {
			if (logger.isDebugEnabled()) {
				logger.debug("Inside show of browser");
			}

			if (content == null) {
				getBrowser().setUrl(URL);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("content not null : " + content);
				}
				getBrowser().addProgressListener(new ProgressListener() {
					public void changed(ProgressEvent e) {
					}

					public void completed(ProgressEvent e) {
						progressCompleteAction(statement, highlightDoc, this);
					}
				});

				getBrowser().resetText(content, statement);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Not inside show of browser");
			}
			if (this.browserShown) {
				this.browserShown = false;
				this.container.doubleSwitch();
			}
		}
		if (highlightDoc != null) {
			getBrowser().addProgressListener(new ProgressListener() {
				public void changed(ProgressEvent e) {
				}

				public void completed(ProgressEvent e) {
					if (logger.isDebugEnabled()) {
						logger
								.info("Completed loading browser...now call highlight inside browser");
					}
					getMessagesPane().highlightInsideBrowser(highlightDoc);
					getBrowser().removeProgressListener(this);

				}
			});
		}
		this.control.layout(true, true);
	}

	@Override
	public SupraBrowser getContent() {
		return this.sb;
	}

	@Override
	public int getMinimumWidth() {
		return 0;
	}

	@Override
	public int getMinimumHeight() {
		return 0;
	}

	@Override
	public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
		if (target == null) {
		} else if (!super.checkPossibilityOfDocking(direction, target))
			return false;
		return true;
	}

	@Override
	public boolean checkIfCanDockOn(int direction) {
		return true;
	}

	public void addSearchPane() {
		this.searchPane = new SearchPane(this);
		this.searchPane.setVisible(true);
		this.container.redraw();
		this.container.layout();
	}

	public void removeSearchPane() {
		this.container.removeComposite(this.searchPane);
		GridData data = (GridData) this.searchPane.getLayoutData();
		data.exclude = true;
		this.searchPane.setLayoutData(data);
		this.searchPane.dispose();
		this.container.redraw();
		this.container.layout();
		this.searchPane = null;
		getBrowser().setFocus();
	}

	public MultiCompositeContainer getMultiContainer() {
		return this.container;
	}

	public boolean containsSearchPane() {
		return this.searchPane != null;
	}

	public SearchPane getSearchPane() {
		return this.searchPane;
	}

	public MessagesPane getMessagesPane() {
		return this.mP;
	}

	public Composite getParent() {
		return this.parent;
	}

	@Override
	protected void createToolBar(Composite parent) {

		try {
			this.scrollLockIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SCROLL_LOCK_ICON).openStream());
			this.showAllIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SHOW_ALL_MESSAGES).openStream());
			this.bookmarkIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.BOOKMARK).openStream());
			this.forwardIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.FORWARD_IN_HISTORY_ICON)
					.openStream());
			this.backwardIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.BACK_IN_HISTORY_ICON).openStream());
			this.forwardEmailIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_FORWARD_ICON).openStream());
			this.replyIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_REPLY_ICON).openStream());
			this.composeIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_COMPOSE_ICON).openStream());
			this.discussIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_DISCUSS_ICON).openStream());
			this.researchIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_RESEARCH_ICON).openStream());
			this.emailToContactIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_COMPOSE_ICON).openStream());
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}

		this.toolComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = MAX_BUTTON_COUNT;
		layout.makeColumnsEqualWidth = false;
		this.toolComp.setLayout(layout);

		GridData data = new GridData();
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.toolComp.setLayoutData(data);

		this.hBackward = new Button(this.toolComp, SWT.PUSH);
		this.hBackward.setText("<-");
		this.hBackward.addSelectionListener(new ScrollLockSelectionlistener(
				this));
		this.hBackward.setToolTipText("Backward");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.hBackward.setLayoutData(data);
		if (logger.isDebugEnabled()) {
			logger.debug("Backward button created");
		}

		this.hForward = new Button(this.toolComp, SWT.PUSH);
		this.hForward.setText("->");
		this.hForward
				.addSelectionListener(new ScrollLockSelectionlistener(this));
		this.hForward.setToolTipText("Forward");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.hForward.setLayoutData(data);
		if (logger.isDebugEnabled()) {
			logger.debug("Forward button created");
		}
		this.hBackward.addSelectionListener(new HystorySelectionListener(this,
				false));
		this.hForward.addSelectionListener(new HystorySelectionListener(this,
				true));
		this.mP.getLocker().addControl(this.hBackward);
		this.mP.getLocker().addControl(this.hForward);

		this.back = new Button(this.toolComp, SWT.PUSH);
		this.back.setImage(this.backwardIcon);
		this.back.setToolTipText("Back");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.back.setLayoutData(data);
		this.back.setEnabled(false);
		this.back.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				getBrowser().back();
				downgradeLocationLevel();
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug("BackBookmark button created");
		}

		this.forward = new Button(this.toolComp, SWT.PUSH);
		this.forward.setImage(this.forwardIcon);
		this.forward.setToolTipText("Forward");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.forward.setLayoutData(data);
		this.forward.setEnabled(false);
		this.forward.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				getBrowser().forward();
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug("ForwardBookmark button created");
		}

		this.scrollLock = new Button(this.toolComp, SWT.TOGGLE);
		this.scrollLock.setImage(this.scrollLockIcon);
		this.scrollLock.addSelectionListener(new ScrollLockSelectionlistener(
				this));
		this.scrollLock.setToolTipText(this.bundle.getString(SCROLL_LOCK));
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.scrollLock.setLayoutData(data);
		if (logger.isDebugEnabled()) {
			logger.debug("ScrollLock button created");
		}

		this.showButton = new Button(this.toolComp, SWT.FLAT);
		this.showButton.setImage(this.showAllIcon);
		this.showButton
				.addSelectionListener(new ShowAllButtonSelectionListener(this));
		this.showButton
				.setToolTipText(this.bundle.getString(SHOW_ALL_MESSAGES));

		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.showButton.setLayoutData(data);
		if (logger.isDebugEnabled()) {
			logger.debug("Show button created");
		}

		this.saveBookmark = new Button(this.toolComp, SWT.PUSH);
		this.saveBookmark.setToolTipText("Save page as Bookmark");
		this.saveBookmark.setImage(this.bookmarkIcon);

		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.saveBookmark.setLayoutData(data);

		this.saveBookmark.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				String sendText = getBrowser().getUrl();
				AbstractDelivery delivery = getMessagesPane().getDeliveryType();

				CreateBookmarkAction
						.saveAsBookmark(null, sendText, null, delivery, getMessagesPane().getRawSession());
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug("SaveBookMark button created");
		}
		
		
		this.composeEmailButton = new Button(this.toolComp, SWT.FLAT);
		this.composeEmailButton.setImage(this.composeIcon);
		this.composeEmailButton
				.addSelectionListener(new EmailControlPanelComposeListener(this.mP, this.mP.client.session));
		this.composeEmailButton
				.setToolTipText("Compose Email");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.composeEmailButton.setLayoutData(data);
		
		
		this.replyEmailButton = new Button(this.toolComp, SWT.FLAT);
		this.replyEmailButton.setImage(this.replyIcon);
		this.replyEmailButton
				.addSelectionListener(new PreviewReplyEmailListener(this.mP));
		this.replyEmailButton
				.setToolTipText("Reply Email");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.replyEmailButton.setLayoutData(data);
		
		this.forwardEmailButton = new Button(this.toolComp, SWT.FLAT);
		this.forwardEmailButton.setImage(this.forwardEmailIcon);
		this.forwardEmailButton
				.addSelectionListener(new PreviewForwardEmailListener(this.mP));
		this.forwardEmailButton
				.setToolTipText("Forward Email");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.forwardEmailButton.setLayoutData(data);
		
		this.discussEmailButton = new Button(this.toolComp, SWT.TOGGLE);
		this.discussEmailButton.setImage(this.discussIcon);
		this.discussEmailButton
				.addSelectionListener(new PreviewEmailDiscussListener(this.mP));
		this.discussEmailButton
				.setToolTipText("Discuss");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.discussEmailButton.setLayoutData(data);
		
		this.researchToolBar = new ToolBar( this.toolComp, SWT.RIGHT );
		this.researchEmailButton = new ReSearchToolItemComponent( this.researchToolBar );
		this.researchEmailButton.setImage(this.researchIcon);
		this.researchEmailButton.setToolTipText("Re-Search");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.researchToolBar.setLayoutData(data);
		this.researchToolBar.setEnabled( true );
		
		this.emailToContactButton = new Button(this.toolComp, SWT.FLAT);
		this.emailToContactButton.setImage(this.emailToContactIcon);
		this.emailToContactButton
				.addSelectionListener(new PreviewEmailToContactListener(this.mP));
		this.emailToContactButton
				.setToolTipText("Email to Contact");
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.END;
		this.emailToContactButton.setLayoutData(data);
		
		hideButton(this.back);
		hideButton(this.forward);
		hideButton(this.saveBookmark);
		hideButton(this.composeEmailButton);
		hideButton(this.replyEmailButton);
		hideButton(this.forwardEmailButton);
		hideButton(this.researchToolBar);
		hideButton(this.discussEmailButton);
		hideButton(this.emailToContactButton);
		
		computeAndSetToolCompSize();
	}

	private void hideButton( final Control control ) {
		if(!control.getVisible()) {
			return;
		}
		switchControlState(control, false);
	}

	private void showButton( final Control control ) {
		if(control.getVisible()) {
			return;
		}
		switchControlState(control, true);
	}

	private void switchControlState(Control control, boolean flag) {
		if (control.getVisible() != flag) {
			control.setVisible(flag);
			Object layoutData = control.getLayoutData();
			if (layoutData instanceof GridData) {
				GridData gridData = (GridData) layoutData;
				gridData.exclude = !flag;
			} else if (layoutData instanceof RowData) {
				RowData rowData = (RowData) layoutData;
				rowData.exclude = !flag;
			}
		}
	}

	public boolean needScrollToBottom(Statement statement) {
		return (statement == null && !this.mP.isScrollLocked() && ((this.mP
				.getLastSelectedDoc() != null && !Statement.wrap(
				this.mP.getLastSelectedDoc()).isComment()) || this.mP
				.getLastSelectedDoc() == null));
	}

	public boolean needHighLightElement(Statement statement) {
		return statement != null && !statement.isComment();
	}

	public void computeAndSetToolCompSize() {
		dumpButtonsStates();
		this.toolComp.pack();
		this.toolComp.layout();
		this.toolComp.getParent().layout();
		this.toolComp.redraw();
		dumpButtonsStates();
	}

	public void dumpButtonsStates() {
		if (!logger.isDebugEnabled()) {
			return;
		}
		final Control[] children = this.toolComp.getChildren();
		logger.debug("Dumps tool buttons. Count: " + children.length);
		for (int n = 0; n < children.length; n++) {
			Control ctrl = children[n];
			logger.debug(n + " " + ctrl.getBounds());
		}
	}

	public void toggleSearchPane() {
		if (this.searchPane == null) {
			addSearchPane();
		} else {
			removeSearchPane();
		}
	}

	private void progressCompleteAction(final Statement statement,
			final Document highlightDoc, final ProgressListener listener) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				final SupraBrowser browser = getBrowser();
				if ( browser.isReady() ) {
					if (statement != null) {
						browser.scrollToSelectedElement(
								statement.getMessageId());
					}
					if (highlightDoc != null) {
						getMessagesPane().highlightInsideBrowser(highlightDoc);
					}
					if (needScrollToBottom(statement)) {
						browser.scrollToBottom();
					}
				}
			}
		});
		getBrowser().removeProgressListener(listener);
	}

	/**
	 * 
	 */
	protected void refreshNavigateButtonEnabled() {
		this.forward.setEnabled(getBrowser().isForwardEnabled());
		this.back.setEnabled(getBrowser().isBackEnabled()
				&& getLocationLevel() != 0);
	}

	private class PreviewLocationListener implements LocationListener {

		public void changed(LocationEvent arg0) {
			upgradeLocationLevel();
			refreshNavigateButtonEnabled();
		}

		public void changing(LocationEvent arg0) {
		}
	}

	public void reorganizeButtons(final Statement selection) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("ReorganizeButtons");
				}
				hideTemporalButtons();
				if (selection.isBookmark() || selection.isRss()) {
					showButton(PreviewAreaDocking.this.saveBookmark);
					showButton(PreviewAreaDocking.this.back);
					showButton(PreviewAreaDocking.this.forward);
					if ( selection.isBookmark() ) {
						showButton(PreviewAreaDocking.this.researchToolBar);
					}
				} else if(selection.isEmail()) {
					showButton(PreviewAreaDocking.this.composeEmailButton);
					showButton(PreviewAreaDocking.this.forwardEmailButton);
					
					ExternalEmailStatement email = ExternalEmailStatement.wrap(selection.getBindedDocument());
					if(email.isInput()) {
						showButton(PreviewAreaDocking.this.replyEmailButton);
					}
					if(getMessagesPane().hasComment(email)) {
						showButton(PreviewAreaDocking.this.discussEmailButton);
					}
					
					showButton(PreviewAreaDocking.this.researchToolBar);
				} else if(selection.isContact()) {
					ContactStatement contact = ContactStatement.wrap(selection.getBindedDocument());
					if(contact.getEmailAddress()!=null && !contact.getEmailAddress().trim().equals("")) {
						showButton(PreviewAreaDocking.this.emailToContactButton);
					}
				} else {
					showButton(PreviewAreaDocking.this.scrollLock);
					showButton(PreviewAreaDocking.this.hForward);
					showButton(PreviewAreaDocking.this.hBackward);
				}
				computeAndSetToolCompSize();
			}
		});
	}

	/**
	 * 
	 */
	protected void hideTemporalButtons() {
		hideButton(this.scrollLock);
		hideButton(this.hForward);
		hideButton(this.hBackward);
		hideButton(this.saveBookmark);
		hideButton(this.back);
		hideButton(this.forward);
		hideButton(this.forwardEmailButton);
		hideButton(this.replyEmailButton);
		hideButton(this.composeEmailButton);
		hideButton(this.discussEmailButton);
		hideButton(this.researchToolBar);
		hideButton(this.emailToContactButton);
	}

	public void setBeginLocationLevel() {
		this.browserLocationLevel = -1;
	}

	public int getLocationLevel() {
		return this.browserLocationLevel;
	}

	private void upgradeLocationLevel() {
		this.browserLocationLevel++;
	}

	private void downgradeLocationLevel() {
		this.browserLocationLevel = this.browserLocationLevel - 2;
	}

	// TODO this mathod may be not necessary.
	public void shakeBrowser() {
		if(this.sb==null || this.sb.isDisposed() ) {
			logger.error("browser is null or not ready");
			return;
		}
		if (this.isBrowserShaken) {
			return;
		}
// 		Looks like swt browser contains fix already
//		Point p = this.sb.getSize();
//		this.sb.setSize(p.x + 1, p.y + 1);
//		this.sb.setSize(p);
		this.isBrowserShaken = true;
	}

	/**
	 * 
	 */
	public void checkStatesOfHystory() {
		Document doc = this.mP.getSphereDefinition();
		if(logger.isDebugEnabled())
		{
			logger.debug("spheredef="+doc.asXML());
		}
		Element paging = doc.getRootElement().element("paging");
		if (paging != null) {
			Element totalPages = paging.element("total_pages");
			if (totalPages != null) {
				int pages = Integer
						.parseInt(totalPages.attributeValue("value"));
				Element page = paging.element("page");
				if (page != null) {
					int currentPage = Integer.parseInt(page
							.attributeValue("value"));
					if(logger.isDebugEnabled())
					{
						logger.debug("pages="+pages+" curr="+currentPage);
					}
					this.hBackward.setEnabled(currentPage < pages);
					this.hForward.setEnabled(currentPage > 1);
				}
			}
		}
	}

}
