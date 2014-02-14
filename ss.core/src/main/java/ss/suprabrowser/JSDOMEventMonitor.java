package ss.suprabrowser;
	
import java.util.Hashtable;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;

import ss.client.event.executors.StatementExecutor;
import ss.client.event.executors.StatementExecutorFactory;
import ss.client.event.tagging.TagManager;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SMessageBrowser;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.email.EmailCommonShell;
import ss.client.ui.tempComponents.SsMenuHelper;
import ss.client.ui.viewers.NewMessage;
import ss.common.IdentityUtils;
import ss.common.UiUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.Statement;

public class JSDOMEventMonitor {

	/**
	 * 
	 */
	private static final int SYNCHRONIZER_SLEEP_TIME = 100;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(JSDOMEventMonitor.class);

	private volatile boolean monitoring = false;

	private final SupraBrowser mb;

	private final Runnable domSycnronizer;

	public JSDOMEventMonitor(SupraBrowser mb) {
		this.mb = mb;
		this.mb.addDisposeListener( new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				stopMonitoring();
			}
		});
		this.domSycnronizer = new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				dispatchDomEvent();
			}
		};
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					eventLoop();
				} catch (Throwable ex) {
					logger.error("JSDOMEventMonitor failed", ex);
				}
				logger.info("JSDOMEventMonitor stopped");
			}
		});
		thread.setName(IdentityUtils.getNextRuntimeIdForThread(getClass()));
		thread.start();
	}

	private void dispatchDomEvent() {
		if ( !this.mb.isReady() ) {
			return;
		}

		nsIDOMHTMLDocument document = this.mb.getDomHtmlDocument();
		if (document == null) {
			return;
		}
		try {
			final nsIDOMElement elem = document
			.getElementById("js_dom_event_monitor");
			final nsIDOMElement advanceElement = document
			.getElementById("advance");
			final nsIDOMElement repeatSearchElement = document
			.getElementById("repeat_search");
			final nsIDOMElement showSsMenuElement = document
			.getElementById("show_ss_menu");
			final nsIDOMElement clickElement = document
			.getElementById("click_id");
			final nsIDOMElement dblclickElement = document
			.getElementById("dblclick_id");
			final nsIDOMElement showSphere = document
			.getElementById("showSphere");		
			final nsIDOMElement showURL = document.getElementById("showURL");
			final nsIDOMElement commentSupportElem = document
			.getElementById("supportDiv");
			final nsIDOMElement richTextElem = document
			.getElementById("richtext");
			final nsIDOMElement selectionElem = document
			.getElementById("selection");
			final nsIDOMElement researchElem = document
			.getElementById("research");
			final nsIDOMElement researchClickElem = document
			.getElementById("research_click");
			final nsIDOMElement keywordNameNotUnique = document
			.getElementById("keyword_name_not_unique");
			final nsIDOMElement keywordNameToShowInPreview = document
			.getElementById("keyword_name_to_show_in_preview");
			if (elem != null) {
				openNewTab(elem.getAttribute("event_url"),false);
				document.getBody().removeChild(elem);
			}
			if(advanceElement!=null) {
				toggleAdvancedBlock(advanceElement);
			}
			if(repeatSearchElement!=null) {
				repeatSearch(repeatSearchElement);
			}
			if (clickElement != null&&(showSphere==null&&showURL==null)) {
				processClick(document, clickElement);
			}
			if (dblclickElement != null&&(showSphere==null&&showURL==null)) {
				processDoubleClick(document, dblclickElement);
			}
			if(showSsMenuElement!=null) {
				processShowSsMenu(showSsMenuElement);
			}
			if (showSphere != null) {
				logger.warn("Loading of sphere from SearchResult");
				String messageId = showSphere.getAttribute("messageId");
				String sphereId = showSphere.getAttribute("sphereId");
				String keywords = showSphere.getAttribute("keywords");

				document.getBody().removeChild(showSphere);
				openSphereWithSearchResult(sphereId, messageId,keywords);

				document.getBody().removeChild(clickElement);
			}				
			if (showURL != null) {
				openNewTab(showURL.getAttribute("url"), true);
				document.getBody().removeChild(showURL);
				document.getBody().removeChild(clickElement);
			}
			if (commentSupportElem != null) {
				processFindComment(document, commentSupportElem);
			}
			if(richTextElem != null) {
				processFindRichText(document, richTextElem);
			}
			if(selectionElem!=null) {
				if (logger.isDebugEnabled()) {
					logger.debug("start process selection");
				}
				processSelection(document, selectionElem);
			}
			if (keywordNameNotUnique != null) {
				final String name = new String( keywordNameNotUnique.getAttribute("tag_name") );
				document.getBody().removeChild(keywordNameNotUnique);
				TagManager.INSTANCE.tagClicked( name );
			}
			if (keywordNameToShowInPreview != null) {
				final String unique = new String( keywordNameToShowInPreview.getAttribute("unique_to_load") );
				final String sphereId = new String( keywordNameToShowInPreview.getAttribute("sphere_id_to_load") );
				document.getBody().removeChild(keywordNameToShowInPreview);
				TagManager.INSTANCE.tagNewForPreviewClicked( unique , sphereId, this.mb.getMP() );
			}
			if(researchElem!=null) {
				processResearch(document, researchElem);
			} else if(researchClickElem != null) {
				processResearchClick(document, researchClickElem);
			}
		} catch (Exception e) {
			logger.error(" jdom monitor loop failed ", e);
		}
	}

	/**
	 * @param showSsMenuElement
	 */
	private void processShowSsMenu(nsIDOMElement showSsMenuElement) {
		String messageId = showSsMenuElement.getAttribute("messageId");
		String sphereId = showSsMenuElement.getAttribute("sphereId");
		SsMenuHelper.INSTANCE.showMenu(this.mb, sphereId, messageId);
		showSsMenuElement.getParentNode().removeChild(showSsMenuElement);
	}

	/**
	 * @param repeatSearchElement
	 */
	private void repeatSearch(nsIDOMElement repeatSearchElement) {
		String text = repeatSearchElement.getAttribute("text");
		AdvancedSearchHelper.performSearch(this.mb.getDomHtmlDocument(), text);
		repeatSearchElement.getParentNode().removeChild(repeatSearchElement);
	}

	/**
	 * @param advanceElement
	 */
	private void toggleAdvancedBlock(nsIDOMElement advanceElement) {
		String action = advanceElement.getAttribute("action");
		this.mb.performAdvancedSearchAction(action);
		advanceElement.getParentNode().removeChild(advanceElement);
	}

	/**
	 * @param document
	 * @param researchClickElem
	 */
	private void processResearchClick(nsIDOMHTMLDocument document,
			nsIDOMElement researchClickElem) {
		String keyword = researchClickElem.getAttribute("value");
		SupraSphereFrame.INSTANCE.client.showKeywordSearchResult(this.mb, keyword);
		document.getBody().removeChild(researchClickElem);
	}

	/**
	 * @param document
	 * @param researchElem
	 */
	private void processResearch(nsIDOMHTMLDocument document,
			nsIDOMElement researchElem) {
		String keyword = researchElem.getAttribute("value");
		SupraSphereFrame.INSTANCE.client.showKeywordSearchResultFlyer(this.mb, keyword);
		document.getBody().removeChild(researchElem);
		
	}

	/**
	 * @param document
	 * @param selectionElem
	 */
	private void processSelection(nsIDOMHTMLDocument document, nsIDOMElement selectionElem) {
		String selection = selectionElem.getAttribute("value");
		document.getBody().removeChild(selectionElem);
		this.mb.setSelection(selection);
	}

	/**
	 * @param document
	 * @param richTextElem
	 */
	private void processFindRichText(nsIDOMHTMLDocument document, nsIDOMElement richTextElem) {
		String bodyMessage = richTextElem.getAttribute("value");
		
		bodyMessage = PreviewHtmlTextCreator.prepareTextWithoutAmps(bodyMessage);
		bodyMessage = PreviewHtmlTextCreator.excludeReferences(bodyMessage);
		
		document.getBody().removeChild(richTextElem);
		
		SMessageBrowser browser = (SMessageBrowser)this.mb;
		NewMessage nm = browser.getNewMessageWindow();
		if(nm != null) {
			nm.publishMessage(bodyMessage);
			return;
		}
		
		EmailCommonShell emailShell = browser.getEmailShell();
		if(emailShell != null) {
			emailShell.createAndPublishEmail(bodyMessage);
		}
	}

	private void processClick(nsIDOMHTMLDocument document,
			final nsIDOMElement clickElement) {
		final MessagesPane mp = this.mb.getMP();
		if (mp != null && this.mb.belongToMP()) {
			mp.recheckPeopleListColors();
			String messageId = clickElement.getAttribute("messageId");
			Statement statement = mp.getDocFromHash(messageId);
			StatementExecutor executor = StatementExecutorFactory.createExecutor(mp, statement);
			if(executor!=null) {
				executor.performExecute(false, false);
			}
		}
		document.getBody().removeChild(clickElement);
	}

	private void processDoubleClick(nsIDOMHTMLDocument document,
			final nsIDOMElement dblclickElement) {
		logger.info("double-click in browser");
		document.getBody().removeChild(dblclickElement);
		JSDOMEventMonitor.this.mb.getMP().setReplyChecked( true );
	}

	private void processFindComment(nsIDOMHTMLDocument document,
			final nsIDOMElement commentSupportElem) {
		SupraSphereFrame.INSTANCE.getCommentWindowController()
		.disposeCommentWindow();
		String messageId = commentSupportElem.getAttribute("message_id");
		document.getBody().removeChild(commentSupportElem);
		CommentStatement requiredComment = null;

		for (Statement statement : this.mb.getMP().getTableStatements()) {
			if (statement.getMessageId().equals(messageId)) {
				this.mb.getMP().setLastSelectedDoc(
						statement.getBindedDocument());
				this.mb.getMP().selectMessage(statement);
				requiredComment = CommentStatement.wrap(statement
						.getBindedDocument());
				continue;
			}
		}

		if (requiredComment == null) {
			return;
		}
		
		this.mb.getMP().setViewComment(requiredComment);
		if (SupraSphereFrame.INSTANCE.tabbedPane.isMessagesPaneSelected()) {
			this.mb.getMP().showCommentWindow();
		} else {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					if (SupraSphereFrame.INSTANCE.tabbedPane
							.getSelectedBrowserPane() != null) {
						SupraSphereFrame.INSTANCE.tabbedPane
						.getSelectedBrowserPane().getControlPanel()
						.getDiscuss().setSelection(false);
					} else if (SupraSphereFrame.INSTANCE.tabbedPane
							.getSelectedEmailPane() != null) {
						SupraSphereFrame.INSTANCE.tabbedPane
						.getSelectedEmailPane().getControlPanel()
						.getContent().getDiscuss().setSelection(
								false);
					}
				}
			});

			SupraSphereFrame.INSTANCE.tabbedPane
			.getSelectedBrowserDocking().showCommentWindow();
		}
	}

	public void openNewTab(final String event, boolean selectBrowser) {
		this.mb.getMozillaBrowserController().openNewTabAction(event, selectBrowser);
	}

	private void eventLoop() {
		logger.info("Starting DOM monitor event loop");
		try {
			this.setMonitoring(true);
			while (this.isMonitoring()) {
				try {
					Thread.sleep(SYNCHRONIZER_SLEEP_TIME);
				} catch (InterruptedException ex) {
					logger.warn("sleep interrupted ", ex);
				}
				if (!this.isMonitoring()) {
					break;
				}
				UiUtils.swtInvoke(this.domSycnronizer);
			}
		} finally {
			logger.info("Ending DOM monitor event loop");
		}
		/*
		 * for (int i = 0; i <
		 * mb.getDocument().getElementsByTagName("js_dom_event_monitor").getLength();
		 * i++) { //if (nsIDOMNode.TEXT_NODE == docFrag.getChildNodes().item(i) //
		 * .getNodeType()) { try { this.logger.info("One event to process:
		 * "+mb.getDocument().getElementsByTagName("js_dom_event_monitor").item(i).getFirstChild().getNodeValue()); }
		 * catch (Exception e) { e.printStackTrace(); } }
		 */
	}

	/**
	 * @param sphereId
	 * @param messageId
	 */
	@SuppressWarnings("unchecked")
	private void openSphereWithSearchResult(String sphereId, String messageId, String keywords) {
		final Hashtable session = SupraSphereFrame.INSTANCE.client.getSession();
		final DialogsMainCli cli = SupraSphereFrame.INSTANCE.getActiveConnections()
				.getActiveConnection(((String) session.get("sphereURL")));
		cli.searchSphere(sphereId, messageId, keywords, "false");
	}	

	public synchronized void stopMonitoring() {
		logger.debug("stop monitoring");
		this.setMonitoring( false );
	}

	/**
	 * @param monitoring the monitoring to set
	 */
	private synchronized  void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	/**
	 * @return the monitoring
	 */
	private synchronized boolean isMonitoring() {
		return this.monitoring;
	}

}
