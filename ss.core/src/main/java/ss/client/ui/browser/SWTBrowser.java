/**
 * 
 */
package ss.client.ui.browser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.mozilla.interfaces.nsIBoxObject;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentRange;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLAnchorElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMHTMLElement;
import org.mozilla.interfaces.nsIDOMKeyEvent;
import org.mozilla.interfaces.nsIDOMMouseEvent;
import org.mozilla.interfaces.nsIDOMNSDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsIFind;
import org.mozilla.interfaces.nsIScrollable;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebNavigation;
import org.mozilla.xpcom.Mozilla;

import ss.client.hotkeys.HotKeysManager;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.manager.WebBrowserManager;
import ss.client.ui.tempComponents.SavePageWindow;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;
import ss.client.ui.viewers.comment.CommentApplicationWindow;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.search.URLParser;
import ss.util.SessionConstants;

/**
 * 
 */
class SWTBrowser extends Browser implements IBrowser {

	private static final Logger logger = SSLogger.getLogger(SWTBrowser.class);

	public static final String NS_FIND_CONTRACTID = "@mozilla.org/embedcomp/rangefind;1"; //$NON-NLS-1$

	public static final String NS_SCROLLBOX_CONTRACTID = "@mozilla.org/layout/xul-boxobject-scrollbox;1";
	
	public static final String NS_WEBBROWSER_CONTRACTID = "@mozilla.org/embedding/browser/nsWebBrowser;1";
	
	private static final String commentStyle = "background-color: silver; cursor: help;";

	private static final String findResultStyle = "background-color: rgb(110, 255, 100);";
	
	private static final String STYLE = "style";
	
	private static final String ID = "id";
	
	private static final String SPAN = "span";
	
	private static final String ONCLICK = "onclick"; 

	private static final String FOUND_ELEMENT_ID = "found";
	
	private static final String BOOKMARK = "SUPRABROWSER.BOOKMARK";
	private static final String COMMENT = "SUPRABROWSER.COMMENT";
	private static final String RELOAD = "SUPRABROWSER.RELOAD";
	private static final String OPEN_NEW_TAB = "SUPRABROWSER.OPEN_NEW_TAB";
	private static final String SELECT_ANY_TEXT = "SUPRABROWSER.SELECT_ANY_TEXT";
	private static final String BACK = "SUPRABROWSER.BACK";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_BROWSER_SUPRABROWSER);

	protected boolean isContextMenuShow = true;

	protected boolean canComment = false;

	private boolean showAllCommentedPlaces = true;
	
	private nsIFind finder;
	
	protected nsIDOMDocument domDocument = null;
	
	protected nsIDOMHTMLDocument htmlDomDocument = null;
	
	private boolean belongToSBrowserDocking = false;
	
	protected SupraBrowserProxy parent = null;
	
	private List<CompletedListener> completedListeners = new ArrayList<CompletedListener>();
	
	private Queue<CompletedListener> listenersToRemove = new LinkedList<CompletedListener>();

	private String selection = null;
	
	private boolean activeRightClickMenuCreated = false;

	public SWTBrowser(SupraBrowserProxy parent, int style, boolean belongToBrowserPane) {
		super(parent, style);
		logger.info("start create browser");
		this.parent = parent;
		this.belongToSBrowserDocking = belongToBrowserPane;
		addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(ProgressEvent pe) {
				onProgressCompleted(pe);
			}

			@Override
			public void changed(ProgressEvent pe) {
				logger.info( "progress changed:" + ( pe != null ? pe.current : null ) );
				SWTBrowser.this.canComment = false;
			}
		});		
	}
	/**
	 * @param arg0
	 * @param arg1
	 */
	public SWTBrowser(SupraBrowserProxy parent, int style) {
		this(parent, style, false);
	}

	public void deleteMessage(final Statement statement) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("scroller_object.deleteMessage('"+statement.getMessageId()+"');");
				if(!getMP().isScrollLocked())
					scrollToBottom();
			}
		});
	}

	public void deleteMessage(final String messageId) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("scroller_object.deleteMessage('"+messageId+"');");
				if(!getMP().isScrollLocked())
					scrollToBottom();
			}
		});
	}
	
	public boolean findAtSamePosition(String str) {
		checkWidget();
		checkExistFinder();
		final nsIDOMHTMLDocument domHtmlDocument = this.getDomHtmlDocument();
		nsIDOMHTMLElement body = domHtmlDocument.getBody();

		int bodyLength = (int)body.getChildNodes().getLength();

		nsIDOMNode found  = domHtmlDocument.getElementById(FOUND_ELEMENT_ID);

		nsIDOMRange startRange = getRangeByParameters(found, 0, 0);
		nsIDOMRange endRange = getRangeByParameters(body, bodyLength, bodyLength);
		
		unhighlightFindResult();

		nsIDOMRange result = null;
		result = this.getFinder().find(str, getSearchRange(), startRange, endRange);
		if(result!=null) {
			processResultNotNull(result);	
			return true;
		}
		return false;
	}

	public void findCommentedPlace(CommentStatement cs, boolean unhighlightAll) {
		checkWidget();
		if(unhighlightAll) {
			this.unhighlightCommentedPlaces();
		}
		String str = cs.getSelectedBody();
		String messageId = cs.getMessageId();
		int number = Integer.parseInt(cs.getNumber());

		checkExistFinder();

		final nsIDOMHTMLDocument domHtmlDocument = this.getDomHtmlDocument();
		nsIDOMHTMLElement body = domHtmlDocument.getBody();
		int bodyLength = (int)body.getChildNodes().getLength();

		nsIDOMRange startRange = getRangeByParameters(body, 0, 0);
		nsIDOMRange endRange = getRangeByParameters(body, bodyLength, bodyLength);

		nsIDOMRange result = null;
		while(number>0) {
			number--;
			result = this.getFinder().find(str, getSearchRange(), startRange, endRange);
			nsIDOMElement temp = domHtmlDocument.createElement(SPAN);

			if(result!=null) {	
				temp.appendChild(result.cloneContents());
				result.deleteContents();
				result.insertNode(temp);

				startRange.setEndAfter(temp);
				startRange.setStartAfter(temp);
			}

			if(number==0) {
				temp.setAttribute(STYLE, commentStyle);
				str = PreviewHtmlTextCreator.prepearText(str);
				temp.setAttribute(ID, str);
				temp.setAttribute(ONCLICK, "comment_object.register_click('"+str+"','"+messageId+"')");
				if(unhighlightAll) {
					this.scrollToElement(temp);
				}
			}
		}
	}

	public boolean findFirst(String str) {
		checkWidget();
		checkExistFinder();
		nsIDOMHTMLElement body = this.getDomHtmlDocument().getBody();
		int bodyLength = (int)body.getChildNodes().getLength();
		
		unhighlightFindResult();
		nsIDOMRange startRange = getRangeByParameters(body, 0, 0);
		nsIDOMRange endRange = getRangeByParameters(body, bodyLength, bodyLength);
		
		nsIDOMRange result = null;
		result = this.getFinder().find(str, getSearchRange(), startRange, endRange);

		if(result!=null) {
			processResultNotNull(result);	
			return true;
		}

		return false;
	}

	public void findHighlightNext(String soughtString) {
		checkWidget();
		checkExistFinder();
		logger.info("start to find : "+soughtString);
		final nsIDOMHTMLDocument domHtmlDocument = this.getDomHtmlDocument();
		nsIDOMHTMLElement body = domHtmlDocument.getBody();
		int bodyLength = (int)body.getChildNodes().getLength();
		
		nsIDOMRange startRange = getRangeByParameters(body, 0, 0);
		nsIDOMRange endRange = getRangeByParameters(body, bodyLength, bodyLength);

		nsIDOMNode found = domHtmlDocument.getElementById(FOUND_ELEMENT_ID);
		if(found != null) {
			startRange = getRangeByParameters(found.getFirstChild(), 1, 1);
			unhighlightFindResult();
		}

		nsIDOMRange result = this.getFinder().find(soughtString, getSearchRange(), startRange, endRange);

		if(result!=null) {
			processResultNotNull(result);
		} else {
			findHighlightNext(soughtString);
		}
	}

	public nsIDOMHTMLDocument getDomHtmlDocument() {
		if ( canAccessDocument() ) {
			ensureDomDocumentInitialized();
			if ( this.htmlDomDocument == null ) {
				// logger.info("Return null dom html document" );
			}
			return this.htmlDomDocument;
		}
		else {
			logger.info("RETURNING NULL DOC");
		return null;
	}
	}
	
	private void ensureDomDocumentInitialized() {
		try {
			setDomDocument( getDomDocumentForUiThread() );
		} catch (Exception ex) {
			this.htmlDomDocument = null;
			this.domDocument = null;
			logger.error("ensureDomDocumentInitialized document failed", ex);
		}
	}
	
	private void addKeyDownListener(nsIDOMEventTarget eventTarget) {
		eventTarget.addEventListener("keydown", new nsIDOMEventListener() {

			public void handleEvent(nsIDOMEvent domEvent ) {
				nsISupports eventObj = domEvent.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
				if ( eventObj instanceof nsIDOMKeyEvent ) {
					onKeyPressed( ((nsIDOMKeyEvent) eventObj) );
				}
			}

			public nsISupports queryInterface(String arg0) {
				return null;
			}
		}, false);
	}
	
	protected void onKeyPressed(nsIDOMKeyEvent event) {
		HotKeysManager.getInstance().execAction(event);
	}
	
	protected void onClick(nsIDOMMouseEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("click in document: "+event.getButton());
		}
		if(event.getButton()==2) {
			showContextMenu(event);
			return;
		}
		if(event.getButton()==0) {
			unhighlightFindResult();
		}
		if(getMP()!=null) {
			getMP().recheckPeopleListColors();
		}
	}

	private void addClickListener(nsIDOMEventTarget eventTarget) {
		eventTarget.addEventListener("mousedown", new nsIDOMEventListener() {
			public void handleEvent(nsIDOMEvent domEvent ) {

				nsISupports eventObj = domEvent.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
				if ( eventObj instanceof nsIDOMMouseEvent ) {

					onClick( ((nsIDOMMouseEvent) eventObj) );
				}
			}

			public nsISupports queryInterface(String arg0) {
				return null;
			}
		}, false);
	}
	
	private nsIDOMDocument getDomDocumentForUiThread() {
		if ( canAccessDocument()  &&
				getWebBrowser() != null) {
			final nsIWebNavigation webNavigation = (nsIWebNavigation) getWebBrowser()
			.queryInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
			if (webNavigation != null) {
				return webNavigation.getDocument();
			}
		}
		return null;
	}

	private boolean canAccessDocument() {
		final Display display = getDisplay();
		return display != null &&
		!display.isDisposed() &&
		display.getThread() == Thread.currentThread() &&
		!isDisposed();
	}
	
	private synchronized void setDomDocument( final nsIDOMDocument newDomDocument ) {
		if ( this.domDocument != newDomDocument ) {
			this.domDocument = newDomDocument;
			afterDomDocumentChanged();
		}
	}

	private void afterDomDocumentChanged() {
		if ( this.domDocument == null ) {
			this.htmlDomDocument = null;
		} else {
			intitalizeHtmlDomDocument();
		}
	}

	private void intitalizeHtmlDomDocument() {
		nsIDOMEventTarget eventTarget = (nsIDOMEventTarget) this.domDocument
		.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		
		addClickListener(eventTarget);
		
		addKeyDownListener(eventTarget);

		this.htmlDomDocument = (nsIDOMHTMLDocument) this.domDocument
		.queryInterface(nsIDOMHTMLDocument.NS_IDOMHTMLDOCUMENT_IID);
	}

	/**
	 * @param target
	 */
	private void addAnchorClickListener(final nsIDOMNode node) {
		nsIDOMEventTarget target = (nsIDOMEventTarget)node.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		target.addEventListener("mousedown", new nsIDOMEventListener() {
			public void handleEvent(nsIDOMEvent domEvent ) {
				if (logger.isDebugEnabled()) {
					logger.debug("handle event: "+domEvent);
				}
				nsISupports eventObj = domEvent.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
				if ( eventObj instanceof nsIDOMMouseEvent ) {
					onAnchorClick( ((nsIDOMMouseEvent) eventObj), node );
				}
			}
			
			public nsISupports queryInterface(String arg0) {
				return null;
			}
		}, false);
	}
	
	/**
	 * @param event
	 */
	protected void onAnchorClick(nsIDOMMouseEvent domMouseEvent, nsIDOMNode node) {
		if (logger.isDebugEnabled()) {
			logger.debug("on anchor click");
		}
		if(domMouseEvent.getButton()!=2) {
			return;
		}
		nsIDOMHTMLAnchorElement anchor = (nsIDOMHTMLAnchorElement)node.queryInterface(nsIDOMHTMLAnchorElement.NS_IDOMHTMLANCHORELEMENT_IID);
		final String newTabURL = anchor.getAttribute("href");
		
		int screenX = domMouseEvent.getScreenX();
		int screenY = domMouseEvent.getScreenY();

		Event aEvent = new Event();
		aEvent.x = screenX;
		aEvent.y = screenY;
		notifyListeners(SWT.MenuDetect, aEvent);
		
		Menu menu = getMenu();
//		boolean existed = true;
		if(menu==null || menu.isDisposed()) {
			menu = new Menu(this.getShell(), SWT.POP_UP);
//			existed = false;
			this.activeRightClickMenuCreated = true;
		} else {
			return;
		}

		MenuItem openNewTab = new MenuItem(menu, SWT.PUSH);
		openNewTab.setText(this.bundle.getString(OPEN_NEW_TAB));

		openNewTab.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						String loadURL = newTabURL;
						String domain = null;

						if (newTabURL.toLowerCase().startsWith("//")) {
							loadURL = "http:" + newTabURL;
						} else if (!newTabURL.toLowerCase().startsWith(
								"http")) {

							String loadedURL = getUrl();
							domain = URLParser
									.getLeadingDomain(loadedURL);
						}
						if (domain != null) {
							loadURL = domain + loadURL;

						}
						SWTBrowser.this.parent
								.getMozillaBrowserController()
								.openNewTabAction(loadURL, false);
						setFocus();
					}
				};
				UiUtils.swtBeginInvoke(t);
			}
		});
//		if (!existed) {
//			addMainElementsToMenu(menu);
//		}
		if (menu != null && !menu.isDisposed()) {
			if (screenX != aEvent.x || screenY != aEvent.y) {
				menu.setLocation(aEvent.x, aEvent.y);
			}
			menu.setVisible(true);
		}
	}
	
	public MessagesPane getMP() {
		return this.parent.getMP();
	}

	public Mozilla getMozzila() {
		checkWidget();
		return Mozilla.getInstance();
	}

	public void hiddenDeleteMessage(final String messageId) {
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				execute("scroller_object.deleteMessage('"+messageId+"');");
				if(!getMP().isScrollLocked())
					scrollToBottom();
			}
		});
	}

	public void highlightAllCommentedPlaces(String commentId) {
		checkWidget();
		Vector<CommentStatement> vec = getMP().findComment(commentId);
		for(CommentStatement cs: vec) {
			findCommentedPlace(cs, false);
		}
		if ( logger.isDebugEnabled() ) {
			logger.debug(getUrl());
		}
	}

	public void highlightSelectedString(final String message_id) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				String executeCommand = "scroller_object.selectElement('"
					+ message_id + "');";
				execute(executeCommand);
				getMP().getControlPanelDocking().setFocusToTextField();
			}
		});
	}

	public void insertNewMessageString(final Statement statement) {
		final String owner = (String) getMP().client.session
		.get(SessionConstants.REAL_NAME);
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				String resp = "resp";
				if (statement.getResponseId() == null) {
					resp = "not resp";
				}

				StringTokenizer stTok = new StringTokenizer(statement
						.getMoment(), " ");
				execute("insert_message_object.insert_message('"
						+ statement.getGiver()
						+ "',' ("
						+ stTok.nextToken()
						+ ") ','"
						+ PreviewHtmlTextCreator
						.prepearText(statement.getSubject())
						+ " ','" + statement.getMessageId() + "','"
						+ resp + "','" + owner + "');");
				if (!getMP().isScrollLocked()) {
					scrollToBottom();
				}
				extractSelection();
			}
		});
	}

	public void resetText(final String content) {
		checkWidget();
		addCompletedListener(new CompletedListener() {
			public void loadingCompleted() {
				logger.info("invoke setting text : "+content);
				setText(content);
				removeCompletedListener(this);
			}
		});
		
		setUrl("about:blank");
	}
	
	private void addCompletedListener(CompletedListener e) {
		this.completedListeners.add(e);
	}
	
	private void removeCompletedListener(CompletedListener listener) {
		if(this.completedListeners.contains(listener)) {
			this.listenersToRemove.add(listener);
		}
	}
	
	private void invokeCompletedListeners() {
		while(!this.listenersToRemove.isEmpty()) {
			this.completedListeners.remove(this.listenersToRemove.poll());
		}
		if(logger.isDebugEnabled()) {
			logger.info("will be invoked "+this.completedListeners.size()+" completed listeners");
		}
		for(CompletedListener listener : this.completedListeners) {
			listener.loadingCompleted();
		}
	}

	public void resetText(String content, final Statement statement) {
		addCompletedListener(new CompletedListener() {
			public void loadingCompleted() {
				if(statement!=null) {
					scrollToSelectedElement(statement.getMessageId());
					if(SupraSphereFrame.INSTANCE.isReplyChecked()) {
						setYellowParentMessage(statement.getMessageId());
					} else {
						highlightSelectedString(statement.getMessageId());
					}
					
				}
				removeCompletedListener(this);
			}
		});
		setText(content);
	}

	public void scrollTo(final int width, final int height) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("window.scrollTo("+width+","+height+");");
			}
		});
	}

	public void scrollToBottom() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if ( !isDisposed() ) {
					execute("scroller_object.scrollToBottom()");
					((ControlPanel)getMP().getControlPanel()).setFocusToSendField();
				}
			}
		});
	}

	public void scrollToSelectedElement(final String message_id) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if ( !isDisposed() ) {
					execute("scroller_object.scroll_to('"+message_id+"', "
							+getSize().y+")");
				}
			}
		});	
	}

	public void scrollToTop() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("scroller_object.scroll_both(0,0)");
			}
		});
	}

	public void setContextMenuShow(boolean isContextMenuShow) {
		this.isContextMenuShow = isContextMenuShow;	
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public void setShowAllCommentedPlaces(boolean value) {
		this.showAllCommentedPlaces = value;	
	}

	public void setYellowParentMessage(String messageId) {
		execute("scroller_object.highlightYellow('"+messageId+"');");
	}

	public void unhighlightCommentedPlaces() {
		checkWidget();
		nsIDOMNodeList spanList = this.getDomHtmlDocument().getElementsByTagName("span");
		for(int i = 0; i < spanList.getLength(); i++) {
			nsIDOMElement elem = (nsIDOMElement)spanList.item(i).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			String style = elem.getAttribute(STYLE);
			if(style!= null && style.equals(commentStyle)) {
				elem.removeAttribute(STYLE);
				elem.removeAttribute(ONCLICK);
				elem.removeAttribute(ID);
			}
		}
	}

	public void unhighlightFindResult() {
		checkWidget();
		nsIDOMElement found = this.getDomHtmlDocument().getElementById(FOUND_ELEMENT_ID);
		if(found!=null) {
			nsIDOMElement elem = (nsIDOMElement)found.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			elem.removeAttribute(STYLE);
			elem.removeAttribute(ID); 
		}
	}
	
	public Composite asComposite() {
		return this;
	}
	
	public String getSelection() {
		String currentSelection = this.selection;
		this.selection = null;
		return currentSelection;
	}
	
	public void extractSelection() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				execute("scroller_object.extractSelection()");
			}
		});
	}
	
	public nsISelection getBrowserSelection() {
		checkWidget();
		return getWebBrowser().getContentDOMWindow().getSelection();
	}

	@Override
	public nsIWebBrowser getWebBrowser() {
		checkWidget();
		return WebBrowserManager.INSTANCE.getSafeWebBrowser( this, (nsIWebBrowser)super.getWebBrowser() );
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	@Override
	public boolean execute(String script) {
		checkWidget();
		if ( logger.isInfoEnabled() ) {
			logger.info("execute:"+script);
		}
		return super.execute(script);
	}
	
	@Override
	protected void checkSubclass() {
		// None
	}
	
	private void showContextMenu(nsIDOMMouseEvent event) {
		checkWidget();
		if (logger.isDebugEnabled()) {
			logger.debug("try show context menu");
		}
		if (this.activeRightClickMenuCreated){
			this.activeRightClickMenuCreated = false;
			return;
		}

		if (!this.isContextMenuShow)
			return;

		if (getMP() != null && getMP().isInsertable()
				&& !this.belongToSBrowserDocking) {
			return;
		}

		nsISelection sel = getWebBrowser().getContentDOMWindow().getSelection();
		if (sel != null) {
			extractSelection();
		}

		nsIDOMMouseEvent domMouseEvent = (nsIDOMMouseEvent) event
				.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
		
		int screenX = domMouseEvent.getScreenX();
		int screenY = domMouseEvent.getScreenY();

		Event aEvent = new Event();
		aEvent.x = screenX;
		aEvent.y = screenY;
		notifyListeners(SWT.MenuDetect, aEvent);

		Menu menu = getMenu();

		if (menu == null) {
			menu = new Menu(this.getShell(), SWT.POP_UP);
			addMainElementsToMenu(menu);
			if (screenX != aEvent.x || screenY != aEvent.y) {
				menu.setLocation(aEvent.x, aEvent.y);
			}
			menu.setVisible(true);
		}
	}
	
	private void addMainElementsToMenu( final Menu menu ){
		if (isBackEnabled()) {
			MenuItem back = new MenuItem(menu, SWT.PUSH);
			back.setText(this.bundle.getString(BACK));

			back.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					back();
				}
			});
		}

		Statement currentStatement = null;
		if (!SWTBrowser.this.belongToSBrowserDocking) {
			currentStatement = getMP().getSelectedStatement();
		}

		if ((this.belongToSBrowserDocking && SupraSphereFrame.INSTANCE.tabbedPane
				.getSelectedBrowserPane() != null)
				|| (currentStatement != null && (currentStatement
						.isCommentable()))) {

			MenuItem comment = new MenuItem(menu, SWT.PUSH);
			comment.setText(this.bundle.getString(COMMENT));

			comment.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final String selection = getSelection();
					if (selection != null
							&& !selection.trim().equals("")
							&& SWTBrowser.this.canComment) {
						nsISelection sel = getWebBrowser()
								.getContentDOMWindow().getSelection();
						nsIDOMRange endRange = sel.getRangeAt(0);

						CommentApplicationWindow caw = new CommentApplicationWindow(
								getMP(), selection, getCountOfMatches(
										selection, endRange));
						caw.setBlockOnOpen(true);
						caw.open();
					} else {
						UserMessageDialogCreator
								.error(SWTBrowser.this.bundle
										.getString(SELECT_ANY_TEXT));
					}

				}
			});
			comment.setEnabled(getMP() != null);

			if (this.belongToSBrowserDocking
					|| currentStatement.isReloadable()) {
				MenuItem index = new MenuItem(menu, SWT.PUSH);
				index.setText(this.bundle.getString(BOOKMARK));

				index.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent se) {
						new Thread() {
							public void run() {
								SpheresCollectionByTypeObject sphereOwner = new SpheresCollectionByTypeObject(SupraSphereFrame.INSTANCE.client);
								SavePageWindow
										.showDialog(
												(SupraBrowser) SWTBrowser.this.parent,
												sphereOwner);
							}
						}.start();
						SWTBrowser.this.setFocus();
					}
				});
				if (getUrl() == null || getUrl().equals("about:blank")) {
					index.setEnabled(false);
				}

				MenuItem reload = new MenuItem(menu, SWT.PUSH);
				reload.setText(this.bundle.getString(RELOAD));
				reload.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						refresh();
					}
				});

			}
		}
	}
	
	
	private int getCountOfMatches(String str, nsIDOMRange endRange) {
		checkExistFinder();
		nsIDOMHTMLElement body = this.getDomHtmlDocument().getBody();
		
		nsIDOMRange startRange = getRangeByParameters(body, 0, 0);

		nsIDOMRange result = null;
		result = this.getFinder().find(str, getSearchRange(), startRange, endRange);
		int i = 0;
		while(result != null) {
			i++;
			result = findRequiredResult(str, endRange, result);
		}
		return i;
	}
	
	
	private void checkExistFinder() {
		setFinderForward();
	}
	
	private nsIDOMRange findRequiredResult(String str, nsIDOMRange endRange, nsIDOMRange result) {
		nsIDOMElement inserting = getDomHtmlDocument().createElement(SPAN);
		inserting.appendChild(result.cloneContents());
		result.deleteContents();
		result.insertNode(inserting);

		result.setEndAfter(inserting);
		result.setStartAfter(inserting);

		result = this.getFinder().find(str, getSearchRange(), result, endRange);
		return result;
	}

	private void processResultNotNull(nsIDOMRange result) {
		nsIDOMElement inserting = getDomHtmlDocument().createElement(SPAN);
		inserting.setAttribute(STYLE, findResultStyle);
		inserting.setAttribute(ID, FOUND_ELEMENT_ID);
		inserting.appendChild(result.cloneContents());

		result.deleteContents();
		result.insertNode(inserting);
		this.scrollToElement(inserting);
	}
	
	private nsIDOMRange getSearchRange() {
		nsIDOMHTMLElement body = this.getDomHtmlDocument().getBody();
		int bodyLength = (int)body.getChildNodes().getLength();
	
		return getRangeByParameters(body, 0, bodyLength);
	}
	
	private nsIDOMDocumentRange getDomDocumentRange() {
		return (nsIDOMDocumentRange)this.getDomHtmlDocument().queryInterface(nsIDOMDocumentRange.NS_IDOMDOCUMENTRANGE_IID);
	}
	
	private nsIDOMRange getRangeByParameters(nsIDOMNode container, int start, int end) {
		nsIDOMDocumentRange domRange = getDomDocumentRange();
		nsIDOMRange range = domRange.createRange();
		range.setStart(container, start);
		range.setEnd(container, end);
		
		return range;
	}
	
	private void setFinderForward() {
		this.getFinder().setFindBackwards(false);
	}
	
	private void scrollToElement(nsIDOMElement elemScrollTo) {
		if ( elemScrollTo == null ) {
			logger.warn( "Can't scroll to null element");
			return;
		}
		final nsIDOMHTMLDocument domHtmlDocument = this.getDomHtmlDocument();
		if ( domHtmlDocument == null ) {
			logger.error( "Dom document is null " + domHtmlDocument );
			return;
		}
		nsIDOMNSDocument nsDoc = (nsIDOMNSDocument)domHtmlDocument.queryInterface(nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID);
		nsIBoxObject boxObject = nsDoc.getBoxObjectFor(elemScrollTo);
		nsIScrollable sc = (nsIScrollable)this.getWebBrowser().queryInterface(nsIScrollable.NS_ISCROLLABLE_IID);

		int top = sc.getCurScrollPos(nsIScrollable.ScrollOrientation_Y)/16;
		int bottom = top+this.getSize().y;

		if(!(boxObject.getY()>=top) || !(boxObject.getY()<=bottom-30)) {
			this.scrollTo(boxObject.getX(), boxObject.getY()-15);
		}

	}
	
	/**
	 * @return the finder
	 */
	public nsIFind getFinder() {
		checkWidget();
		if( this.finder == null ) {
			Mozilla mozilla = getMozzila();
			if (logger.isDebugEnabled()) {
				logger.debug("Mozilla instance: "+mozilla);
			}
			nsIServiceManager serviceManager = mozilla.getServiceManager();
			this.finder = (nsIFind)serviceManager.getServiceByContractID(NS_FIND_CONTRACTID, nsIFind.NS_IFIND_IID);
		}
		return this.finder;
	}
	
	protected void onProgressCompleted(ProgressEvent event) {
		logger.info( "progress completed:" + (event != null ? event.current : null));
		SWTBrowser.this.parent.getMozillaBrowserController().injectJS();
		addAnchorsClickListener();
		invokeCompletedListeners();
		if(getMP() != null && SWTBrowser.this.showAllCommentedPlaces) {
			Statement current = getMP().getCurrentStatement();
			if(current!=null && !current.isTerse() && !current.isComment() && !current.isEmail()) {
				highlightAllCommentedPlaces(current.getMessageId());
			}
			if(getMP().getLastSelectedDoc()!=null && Statement.wrap(getMP().getLastSelectedDoc()).isComment() && getMP().needOpenComment()) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						getMP().showCommentWindow();
					}
				});
			}
		}
		SWTBrowser.this.canComment = true;
	}
	
	/**
	 * 
	 */
	private void addAnchorsClickListener() {
		nsIDOMNodeList list = getDomHtmlDocument().getElementsByTagName("A");
		logger.debug("found "+list.getLength()+" anchors");
		for(int i = 0; i < list.getLength(); i++) {
			nsIDOMHTMLAnchorElement anchor = (nsIDOMHTMLAnchorElement)list.item(i).queryInterface(nsIDOMHTMLAnchorElement.NS_IDOMHTMLANCHORELEMENT_IID);
			logger.debug("next reference: "+anchor.getHref());
			addAnchorClickListener(list.item(i));
		}
	}
	
	@Override
	public void dispose() {
		if ( this.parent != null && this.parent.getMozillaBrowserController() != null ) {
			this.parent.getMozillaBrowserController().closeJSEventMonitor();
		}
		this.canComment = false;
		this.finder = null;
		this.domDocument = null;
		this.htmlDomDocument = null;
		this.parent = null;
		this.completedListeners.clear();
		this.listenersToRemove.clear();
		this.selection = null;
		WebBrowserManager.INSTANCE.release( this );
		super.dispose();
	}
	
	public void testWebBrowser() {
		// logger.warn( "testWebBrowser" );
		// getWebBrowser();
	}
	
	@Override
	public boolean back() {
		return super.back();
	}
	
	
}
