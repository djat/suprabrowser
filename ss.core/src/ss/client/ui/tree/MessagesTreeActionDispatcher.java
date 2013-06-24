/**
 * 
 */
package ss.client.ui.tree;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Menu;

import ss.client.MethodProcessing;
import ss.client.debug.DebugConsoleWindow;
import ss.client.event.createevents.CreateMessageAction;
import ss.client.event.executors.StatementExecutor;
import ss.client.event.executors.StatementExecutorFactory;
import ss.client.networking.ByteRouterClient;
import ss.client.networking.DialogsMainCli;
import ss.client.networking.SupraClient;
import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SearchInputWindow;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SimpleBrowserDataSource;
import ss.client.ui.docking.ControlPanelDocking;
import ss.client.ui.email.EmailController;
import ss.client.ui.forward.NotifyTrayDialog;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.viewers.NewContact;
import ss.client.ui.viewers.NewWeblink;
import ss.client.ui.viewers.ViewMessageSWT;
import ss.common.GenericXMLDocument;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.FileStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.rss.RSSParser;
import ss.util.VariousUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 *
 */
public class MessagesTreeActionDispatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeActionDispatcher.class);
	
	private final MessagesPane mp;

	private final DialogsMainCli client;

	private final Hashtable session;

	private final SupraSphereFrame sF;
	
	public MessagesTreeActionDispatcher(MessagesPane mp) {
		super();
		this.mp = mp;
		this.sF = mp.sF;
		this.session = mp.getRawSession();
		this.client = mp.client;
	}

	public void processDoubleClickOnSelectedNode() {
		final Document doc = this.mp.getMessagesTree().getSelectedDoc();
		if (doc != null) {
			open(doc);
		} else {
			logger.warn("Document for node is null. Skip event processing");
		}
	}

	private void open(final Document doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("Opening document: " + doc.asXML());
		}
		final Statement messageTreeStatement = Statement.wrap(doc);
		if (messageTreeStatement.isBookmark()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Bookmark");
			}
			final String link = messageTreeStatement.getAddress();

			BookmarkStatement bookmark = BookmarkStatement.wrap(doc);

			this.mp.getSupraSphereFrame().addMozillaTab(
					this.mp.getRawSession(), null,
					new SimpleBrowserDataSource(link), true, bookmark);
			this.client.voteDocument(this.session, messageTreeStatement
					.getMessageId(), doc);
			
		} else if (messageTreeStatement.isEmail()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Email");
			}
			EmailController contr = new EmailController(this.mp, this.mp.getRawSession());
			contr.emailDoubleClicked(messageTreeStatement);
		} else if (messageTreeStatement.isFileSystem()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is FileSystem");
			}
			String dirName = messageTreeStatement.getSubject();

			File f = new File(dirName);

			if (f.exists()) {

				logger.info("wont have to transfer it");
				this.mp.client.getSubList(this.mp.getMessagesTree()
						.getUserSession().getImeplementationHashtable(), doc);

			} else {

				logger.info("will have to transfer it");

				this.mp.client.getSubList(this.mp.getMessagesTree()
						.getUserSession().getImeplementationHashtable(), doc);

			}

		} else if (messageTreeStatement.isRss()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is RSS");
			}
			logger.info("Try to open: " + doc.asXML());

			Thread t = new Thread() {

				public void run() {

					// mP.client.useDocument(mp.getSession(),doc);
					String link = messageTreeStatement.getAddress();

					SyndFeed feed = RSSParser.checkForRSS(link);

					// boolean loadURL = true;

					if (feed != null) {

						// boolean loadURL = false;
						java.util.List items = RSSParser.getFeedItems(feed);

						for (int i = 0; i < items.size(); i++) {

							// System.out.println("ONE: "+item.get(i));
							SyndEntry entry = (SyndEntry) items.get(i);

							// System.out.println("Entry Title:
							// "+entry.getTitle());
							String title = entry.getTitle();

							String subjectText = title;
							String bodyText = null;
							if (entry.getDescription() == null) {
								bodyText = "No body available for this item";

							} else {

								bodyText = entry.getDescription().getValue();

							}

							GenericXMLDocument genericDoc = MessagesTreeActionDispatcher.this.mp
									.getMessagesTree().getUserSession()
									.CreateGenericXMLDocument();
							if (messageTreeStatement.getThreadId() == null) {
								logger
										.info("That was the problem...thread id null");
							}
							final Document createDoc = genericDoc.XMLDoc(
									subjectText, bodyText, messageTreeStatement
											.getThreadId(), false);

							createDoc.getRootElement()
									.addElement("response_id").addAttribute(
											"value",
											doc.getRootElement().element(
													"message_id")
													.attributeValue("value"));
							createDoc.getRootElement().addElement("type")
									.addAttribute("value", "bookmark");
							createDoc.getRootElement().addElement("address")
									.addAttribute("value", entry.getLink());
							createDoc.getRootElement().addElement(
									"current_sphere").addAttribute(
									"value",
									MessagesTreeActionDispatcher.this.mp
											.getMessagesTree().getUserSession()
											.getSphereId());

							logger
									.info("GENERIC DOC IN FEED CREATION...is it unique: "
											+ createDoc.asXML());
							Statement createSt = Statement.wrap(createDoc);
							// MessagesTree.messagesPane.addToAllMessages(createSt.getMessageId(),
							// createSt);

							// new Thread() {
							// public void run() {
							SupraSphereFrame.INSTANCE.client
									.setAsSeenAndIndexJustInCase(
											MessagesTreeActionDispatcher.this.mp
													.getMessagesTree()
													.getUserSession()
													.getImeplementationHashtable(),
											createDoc);
							// }
							// }.start();

							boolean isOpen = false;
							if (i == (items.size() - 1)) {
								isOpen = true;
							}

							// MessagesTree.messagesPane.insertUpdate(createDoc,
							// false, false, true);
							DeliverersManager.INSTANCE
									.insert(DeliverersManager.FACTORY
											.createSimple(createDoc, isOpen,
													false, createSt
															.getCurrentSphere()));

						}
					}
				}
			};
			ThreadUtils.startDemon(t, "RSS processor");
			this.client.useDocument(this.mp.getRawSession(), doc, "50");
		} else if (messageTreeStatement.isMessage()
				|| messageTreeStatement.isReply()
				|| messageTreeStatement.isComment()
				|| messageTreeStatement.isTerse()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is one of: (Message, Bookmark, Reply, Comment, Terse)");
			}
			new ViewMessageSWT(this.mp
					.getMessagesTree().getUserSession()
					.getImeplementationHashtable(), doc, this.mp);

		} else if (messageTreeStatement.isContact()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is contact");
			}
			Thread t = new Thread() {
				public void run() {

					final NewContact newUI = new NewContact(
							MessagesTreeActionDispatcher.this.mp
									.getMessagesTree().getUserSession()
									.getImeplementationHashtable(),
									MessagesTreeActionDispatcher.this.mp);

					newUI.fillDoc(doc);
					newUI.createViewToolBar();
					newUI.runEventLoop();

				}
			};
			t.start();

		} else if (messageTreeStatement.isSphere()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Sphere");
			}
			SphereStatement sphereSt = SphereStatement
					.wrap(messageTreeStatement.getBindedDocument());
			SphereOpenManager.INSTANCE.request(sphereSt.getSystemName());

		} else if (messageTreeStatement.isAudio()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Audio");
			}
		} else if (messageTreeStatement.isKeywords()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Keywords");
			}
			final Document sphereDocument = this.mp.getSphereDefinition();

			final Vector<String> assetTypes = new Vector<String>();

			assetTypes.add("bookmark");
			assetTypes.add("keywords");

			Thread t = new Thread() {
				public void run() {
					Vector<String> oneSphere = new Vector<String>();

					oneSphere.add(MessagesTreeActionDispatcher.this.mp
							.getMessagesTree().getUserSession().getSphereId());
					SearchInputWindow siw = new SearchInputWindow(
							SupraSphereFrame.INSTANCE,
							MessagesTreeActionDispatcher.this.mp,
							MessagesTreeActionDispatcher.this.mp
									.getMessagesTree().getUserSession()
									.getImeplementationHashtable(), oneSphere,
							assetTypes, true);

					siw.layoutUIAndSetFocus();

					siw.saveSheduleForm(sphereDocument);
					siw.setKeywordFieldValue(doc.getRootElement().element(
							"subject").attributeValue("value"));

				}
			};
			ThreadUtils.startDemon(t, "Keyword processor");
		} else if (messageTreeStatement.isFile()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is file");
			}
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					final NewBinarySWT nb = new NewBinarySWT(
							MessagesTreeActionDispatcher.this.mp
									.getMessagesTree().getUserSession()
									.getImeplementationHashtable(),
							MessagesTreeActionDispatcher.this.mp, null,
							false);
					nb.fillDoc(FileStatement.wrap(doc));
					nb.addFillButtons();
				}
			});
		} else if (messageTreeStatement.isSource()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Source");
			}
		} else if (messageTreeStatement.isLibrary()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Library");
			}
		} else if (messageTreeStatement.isPersona()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Persona");
			}
		} else if (messageTreeStatement.isTool()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is Tool");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("other type Document");
			}
		}
		this.client.useDocument(this.mp
				.getRawSession(), doc, "10");
		return;
	}

	public void showMessagePageDebugConsole() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				DebugConsoleWindow consoleWnd = new DebugConsoleWindow(
						MessagesTreeActionDispatcher.this.mp);
				consoleWnd.open();
			}
		});
	}
	
	public void doShowHasTagAction() {
		try {
			new Thread() {

				@Override
				@SuppressWarnings("unchecked")
				public void run() {
					
					List<NodeDocumentsBundle> bundles = (List<NodeDocumentsBundle>) UiUtils.swtEvaluate(new Callable() {
						public Object call() throws Exception {
							return MessagesTreeActionDispatcher.this.mp
							.getMessagesTree().getSelectedDocumentsWithParents();
						}
					});

					for (NodeDocumentsBundle documentBundle : bundles) {
						try {
							Document parentDoc = null;
							try {
								parentDoc = documentBundle.getParentStatement().getBindedDocument();
							} catch (Exception e) {
							}

							Document doc = documentBundle.getNodeStatement().getBindedDocument();

							String currentSphere = null;
							try {
								currentSphere = doc.getRootElement().element(
										"current_sphere").attributeValue(
										"value");
							} catch (Exception e) {
							}
							if (currentSphere == null) {
								currentSphere = (String) MessagesTreeActionDispatcher.this.session
										.get("sphere_id");
							}

							String docUnique = doc.getRootElement().element(
									"unique_id").attributeValue("value");

							Document uniqueDoc = MessagesTreeActionDispatcher.this.client
									.getKeywordsWithUnique(docUnique, currentSphere);

							if (uniqueDoc != null) {
								doc = uniqueDoc;
							}
							Element root = doc.getRootElement();

							String unique = root.element("unique_id")
									.attributeValue("value");

							String keywordSphereId = null;

							try {
								keywordSphereId = root
										.element("current_sphere")
										.attributeValue("value");
							} catch (Exception e) {

							}

							if (keywordSphereId == null) {
								keywordSphereId = (String) MessagesTreeActionDispatcher.this.session
										.get("sphere_id");
							}
							logger.warn("keyword sphere: " + keywordSphereId);

							String messageId = root.element("message_id")
									.attributeValue("value");

							Vector messageIdsToExclude = UiUtils.swtEvaluate(new Callable<Vector<String>>() {
								public Vector<String> call() throws Exception {
									return MessagesTreeActionDispatcher.this.mp.getMessagesTree().getAllMessagesIdForSelectedThread();
								}
							});

							Vector spheresToTry = new Vector();

							if (doc.getRootElement()
									.element("multi_loc_sphere") != null) {

								Vector list = new Vector(doc.getRootElement()
										.elements("multi_loc_sphere"));

								for (int i = 0; i < list.size(); i++) {

									Element one = (Element) list.get(i);
									if (!(one.attributeValue("value"))
											.equals((String) MessagesTreeActionDispatcher.this.session
													.get("sphere_id"))) {
										spheresToTry.add(one
												.attributeValue("value"));
									}

								}

							}

							if (parentDoc != null) {

								Vector list = new Vector(parentDoc
										.getRootElement().elements(
												"multi_loc_sphere"));
								messageId = parentDoc.getRootElement().element(
										"message_id").attributeValue("value");

								logger.warn("THe size; " + list.size());

								for (int i = 0; i < list.size(); i++) {

									Element one = (Element) list.get(i);
									String value = one.attributeValue("value");

									if (!VariousUtils.vectorContains(value,
											spheresToTry)) {
										spheresToTry.add(value);

									}

								}
							}

							if (!VariousUtils.vectorContains(keywordSphereId,
									spheresToTry)) {
								spheresToTry.add(keywordSphereId);
							}

							for (int k = 0; k < spheresToTry.size(); k++) {
								String toTry = (String) spheresToTry.get(k);
								logger.warn("WILL TRY THIS ONE: " + toTry);

								MessagesTreeActionDispatcher.this.client
										.findAssetsInSameConceptSet(
												MessagesTreeActionDispatcher.this.session,
												unique, messageId, toTry,
												messageIdsToExclude);
								// }
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}

			}.start();
		} catch (Exception ex) {

		}
	}

	
	public void singleRightMouseClicked() {
		Document viewDoc = this.mp.getMessagesTree().getSelectedDoc();
			
		if (viewDoc == null){
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					Menu swtMenu = new Menu(MessagesTreeActionDispatcher.this.mp.getTreeDocking().getMainControl());
					MessagesTreeMenuCreator creator = new MessagesTreeMenuCreator(MessagesTreeActionDispatcher.this, MessagesTreeActionDispatcher.this.mp);
					creator.createPopupMenu(swtMenu);
				}
			});
	}	
	
	public void singleLeftMouseClicked() {
		if (logger.isDebugEnabled()) {
			logger.debug("mp : "+this.mp);
			logger.debug("mp tree : "+this.mp.getMessagesTree());
		}
		final Document viewDoc = this.mp.getMessagesTree().getSelectedDoc();

		if( viewDoc == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug(" viewDoc is null, skipping processing ");
			}
			return;
		}
		
		try {
			final Statement statement = Statement.wrap(viewDoc);
			if(statement == null) {
				logger.error("statement is null");
				return;
			}
			StatementExecutor exec = StatementExecutorFactory.createExecutor(this.mp, statement);
			exec.performExecute(true, true);
		} catch (NullPointerException npe) {
			logger.error("", npe);
		}	
	}
	
	/**
	 * 
	 */
	public void mouseDoubleClicked() {
		Document doc = this.mp.getMessagesTree().getSelectedDoc();
		if(doc==null) {
			logger.warn("Document is null, returning");
			return;
		}
		Statement statement = Statement.wrap(doc);
		doc = statement.getBindedDocument();
		String type = statement.getType();
		
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Processing doubleClick on bookmark");
			}
			if (type.equals("bookmark")) {
				this.client.voteDocument(this.session, statement
						.getMessageId(), doc);
			}
			this.client.useDocument(this.mp
					.getRawSession(), doc, "10");
			if (statement.getResponseId() != null) {

				Statement rootSt = Statement.wrap(this.mp
						.getRootDocument(doc));
				if (rootSt.getType().equals("rss")) {
					this.client.useDocument(this.mp.getRawSession(), rootSt
							.getBindedDocument(), "50");
				}
			}

		} catch (Throwable ex) {
			logger.error("Error in processing doubleClick", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveToIndexAction() {
		Document viewDoc = this.mp.getMessagesTree().getSelectedDoc();
		
		Element root = viewDoc.getRootElement();

		Hashtable newSession = (Hashtable) this.session.clone();
		newSession.put("sphere_id", this.sF.client.getVerifyAuth()
				.getSystemName((String) this.session.get("real_name")));
		
		String url = root.element("address").attributeValue("value");
		String title = root.element("subject").attributeValue("value");
		String body = root.element("body").getText();
		
		NewWeblink nw = new NewWeblink(newSession, this.mp, "normal", url, title, body);
		Document parentDoc = this.mp.getMessagesTree().getParentDocFor(viewDoc);

		nw.setSavedFrom(parentDoc);
	}
	
	/**
	 * 
	 */
	public void findLiveURLAction() {
		for (NodeDocumentsBundle documentBundle : this.mp.getMessagesTree()
				.getSelectedDocumentsWithParents()) {
			Document viewDoc = documentBundle.getNodeStatement()
					.getBindedDocument();
			Element root = viewDoc.getRootElement();
			String link = root.element("address").attributeValue("value");

			SyndFeed feed = RSSParser.checkForRSS(link);

			if (feed == null) {

				java.util.List items = RSSParser.findRSSURL(link);

				logger.warn("LINK: " + link);

				logger.warn("THIS IS THE SIZE: " + items.size());

				for (int i = 0; i < items.size(); i++) {

					String url = (String) items.get(i);

					String title = RSSParser.getTitleFromURL(url);

					String subjectText = title;
					String bodyText = null;

					bodyText = "No body available for this item";

					GenericXMLDocument genericDoc = new GenericXMLDocument(
							this.session);

					Document createDoc = genericDoc.XMLDoc(subjectText,
							bodyText);

					createDoc.getRootElement().addElement("response_id")
							.addAttribute(
									"value",
									viewDoc.getRootElement().element(
											"message_id").attributeValue(
											"value"));
					createDoc.getRootElement().addElement("thread_id")
							.addAttribute(
									"value",
									createDoc.getRootElement().element(
											"message_id").attributeValue(
											"value"));
					createDoc.getRootElement().element("type").addAttribute(
							"value", "rss");

					createDoc.getRootElement().addElement("address")
							.addAttribute("value", url);
					createDoc.getRootElement().addElement("current_sphere")
							.addAttribute("value",
									(String) this.session.get("sphere_id"));

					Statement createSt = Statement.wrap(createDoc);

					DeliverersManager.INSTANCE.insert(DeliverersManager.FACTORY
							.createSimple(createDoc, true, false, createSt
									.getCurrentSphere()));
				}
			}
		}
	}
	
	/**
	 * @param doc
	 */
	public void replyAction(final Document doc) {
		try {
			this.sF.setReplyChecked(true);
			this.mp.setTerseAsActiveAction();
			this.mp.setLastSelected(doc);
		} catch (Exception ex) {
		}
	}
	
	
	/**
	 * @param doc
	 */
	public void tagAction(final Document doc) {
		try {

			this.mp.setLastSelected(doc);
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					ControlPanelDocking controlDocking = MessagesTreeActionDispatcher.this.mp
							.getControlPanelDocking();

					if (controlDocking.getContent() instanceof ControlPanel) {
						ControlPanel controlPanel = (ControlPanel) controlDocking
								.getContent();
						controlPanel.getTagBox().setSelection(true);
						controlPanel.setIsTagSelected();
						controlPanel.setFocusToSendField();
					}

				}
			});

		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void findRelatedConceptsAction() {
		String allNewKeywords = "";
		String newKeywords = "";
		try {
			final Vector assetTypes = new Vector();

			int j = 0;
			for (NodeDocumentsBundle documentBundle : this.mp.getMessagesTree().getSelectedDocumentsWithParents()) {
				Document doc = documentBundle.getNodeStatement().getBindedDocument();

				String keywords = doc.getRootElement().element("subject")
						.attributeValue("value");
				logger.info("TRYING KEYWORDS: " + keywords);
				StringTokenizer st = new StringTokenizer(keywords, " ");

				int i = 0;
				while (st.hasMoreTokens()) {
					String last = st.nextToken();
					if (!last.equals("or") && !last.equals("and")
							&& !last.equals("not")) {
						if (i == 0) {

							newKeywords = last;
						} else if (i == 1) {
							newKeywords = newKeywords + " or " + last;
						} else {

							newKeywords = newKeywords + " or " + last;
						}
						i++;
					}
				}
				if (i == 0) {

					newKeywords = keywords;

				}

				if (j == 0) {

					allNewKeywords = newKeywords;
				} else {
					allNewKeywords = allNewKeywords + " or " + newKeywords;
				}

				j++;
			}

			assetTypes.add("keywords");

			Vector oneSphere = new Vector();

			oneSphere.add((String) this.session.get("sphere_id"));

			Vector docsToMatch = new Vector();

			for (NodeDocumentsBundle documentBundle : this.mp.getMessagesTree().getSelectedDocumentsWithParents()) {
				final Document doc = documentBundle.getNodeStatement().getBindedDocument();

				docsToMatch.add(doc);

			}

			SearchInputWindow siw = new SearchInputWindow(this.mp.sF, this.mp,
					this.session, oneSphere, assetTypes, true);

			siw.layoutUIAndSetFocus();

			siw.saveSheduleForm(this.mp.getSphereDefinition());
			siw.setKeywordFieldValue(allNewKeywords);

		} catch (Exception exec) {
		}
	}

	/**
	 * 
	 */
	public void matchHistoryAction(Vector<Document> docs) {
		this.client.matchAgainstHistory(this.mp.getRawSession(), docs);
	}

	/**
	 * 
	 */
	public void matchRecentHistoryAction(Vector<Document> docs) {
		this.client.matchAgainstRecentHistory(this.mp.getRawSession(),
				docs);
	}
	
	/**
	 * 
	 */
	public void matchOtherHistory(Vector<Document> docs) {
		this.client.matchAgainstOtherHistory(this.mp.getRawSession(),
				docs);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void searchForSpecificAction() {
		try {
			final Vector assetTypes = new Vector();

			assetTypes.add("bookmark");

			Vector oneSphere = new Vector();

			oneSphere.add((String) this.session.get("sphere_id"));

			Vector docsToMatch = new Vector();

			for (NodeDocumentsBundle documentBundle : this.mp.getMessagesTree().getSelectedDocumentsWithParents()) {

				final Document doc = documentBundle.getNodeStatement().getBindedDocument();

				docsToMatch.add(doc);

			}

			SearchInputWindow siw = new SearchInputWindow(this.mp.sF, this.mp,
					this.session, oneSphere, assetTypes, true);
			siw.setSearchForSpecific(true);
			// siw.setMatchDoc(doc);
			siw.setMatchDocs(docsToMatch);

			siw.layoutUIAndSetFocus();
			siw.saveSheduleForm(this.mp.getSphereDefinition());

			// siw.setKeywordFieldValue();

		} catch (Exception exec) {
		}
	}

	/**
	 * @param doc
	 */
	public void showURLAction(final Document doc) {
		try {
			String address = doc.getRootElement().element("address")
					.attributeValue("value");
			this.mp.setPreviewHtmlText(new PreviewHtmlTextCreator(this.mp));
			this.mp.getPreviewHtmlText().addText(address);
			this.mp.showSmallBrowser(this.session, true, null, this.mp
					.getPreviewHtmlText().getText(), null, null);
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 */
	public void allMemberEscalateAction() {
		Document doc = this.mp.getMessagesTree().getSelectedDoc();
		
		Vector memberList = this.sF.client.getMembersFor(this.session);

		logger.warn("memberlist size before escalate; " + memberList.size());

		this.client
				.sendPopupNotification(this.mp.getRawSession(), memberList, doc);
	}

	/**
	 * @param e
	 * @param rectSource
	 * @param ptSource
	 */
	public void specificMemberNotifyAction(final Document doc) {
		final Vector<String> memberList = this.mp.getMembers();
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				Dialog notifyDialog = new NotifyTrayDialog(memberList, doc);
				notifyDialog.open();
			}
		});
	}

	/**
	 * 
	 */
	public void allMembersAction(final Document doc) {
		if (doc != null) {
			new Thread() {
				public void run() {
					final Vector memberList = SupraSphereFrame.INSTANCE.client.getMembersFor(MessagesTreeActionDispatcher.this.session);
					logger.info("MP SESSION IN SEND TO ALL :"
							+ (String) (MessagesTreeActionDispatcher.this.mp.getRawSession()).get("sphere_id"));
					
					MessagesTreeActionDispatcher.this.client.notifySystemTray(
							MessagesTreeActionDispatcher.this.mp
									.getRawSession(), memberList, doc);
				}
			}.start();
		}

	}

	/**
	 * @param homeSphereURL
	 * @param homeSphereId
	 * @param homeMessageId
	 */
	public void showRecentQueriesAction(final String homeSphereURL,
			final String homeSphereId, final String homeMessageId) {
		String localSphereId = (String) this.mp.getRawSession().get("sphere_id");
		DialogsMainCli dialogsCli = this.sF.getActiveConnections()
				.getActiveConnection(homeSphereURL);

		dialogsCli.getRecentQueries(dialogsCli.getSession(), homeSphereId,
				homeMessageId, localSphereId);
	}

	/**
	 * @param homeSphereURL
	 * @param homeSphereId
	 * @param homeMessageId
	 */
	public void showRecentBookmarksAction(final String homeSphereURL,
			final String homeSphereId, final String homeMessageId) {
		String localSphereId = (String) this.mp.getRawSession().get("sphere_id");
		logger.info("local session: "
				+ (String) this.mp.getRawSession().get("sphere_id"));
		DialogsMainCli dialogsCli = this.sF.getActiveConnections()
				.getActiveConnection(homeSphereURL);

		dialogsCli.getRecentBookmarks(dialogsCli.getSession(), homeSphereId,
				homeMessageId, localSphereId);
	}
	
	/**
	 * 
	 */
	public void stopAction() {
		
	}

	/**
	 * @param doc
	 */
	@SuppressWarnings("unchecked")
	public void openAction(final Document doc) {
		if (doc == null){
			logger.warn("Document is null");
			return;
		}
		logger.info("Open action performed for " + doc.asXML());
		Hashtable brcSession = this.sF.getRegisteredSession(
				(String) this.session.get("supra_sphere"), "ByteRouterClient");

		ByteRouterClient br = null;
		if (brcSession != null) {
			br = this.sF.getActiveByteRouters().getLatestByteRouter(doc);

		}

		if (br == null) {

			logger.info("byte router was null");
			this.client.sendByteRouterInit(this.session, doc);

			final Hashtable sendSession = (Hashtable) this.session.clone();

			Hashtable bootStrapInfo = new Hashtable();
			bootStrapInfo.put("doc", doc);
			bootStrapInfo.put("senderOrReceiver", "receiver");
			sendSession.put("bootStrapInfo", bootStrapInfo);

			SupraClient sClient = new SupraClient((String) sendSession
					.get("address"), (String) sendSession.get("port"));

			sClient.setSupraSphereFrame(this.sF);
			sendSession.put("passphrase", this.sF.getTempPasswords().getTempPW(
					((String) this.session.get("supra_sphere"))));
			sClient.startZeroKnowledgeAuth(sendSession, "StartByteRouter");

		} else {

			logger
					.info("see if you can use the existing byte router...will be hard...may have to change it");

			if (br.isReusable()) {

				logger.info("ITS Reusable");
				Hashtable bootStrapInfo = new Hashtable();
				bootStrapInfo.put("doc", doc);
				bootStrapInfo.put("senderOrReceiver", "receiver");
				this.session.put("bootStrapInfo", bootStrapInfo);
				this.client.sendByteRouterInit(this.session, doc);

			} else {

				logger.info("DO EXACTLY THE SAME THING THIS TIME!: ");
				this.client.sendByteRouterInit(this.session, doc);

				final Hashtable sendSession = (Hashtable) this.session.clone();

				Hashtable bootStrapInfo = new Hashtable();
				bootStrapInfo.put("doc", doc);
				bootStrapInfo.put("senderOrReceiver", "receiver");
				sendSession.put("bootStrapInfo", bootStrapInfo);

				SupraClient sClient = new SupraClient((String) sendSession
						.get("address"), (String) sendSession.get("port"));

				sClient.setSupraSphereFrame(this.sF);
				sendSession
						.put("passphrase", this.sF.getTempPasswords()
								.getTempPW(
										((String) this.session
												.get("supra_sphere"))));
				sClient.startZeroKnowledgeAuth(sendSession, "StartByteRouter");

			}

		}

		/*
		 * logger.info("now open it! "+doc.asXML());
		 * client.sendByteRouterInit(session,doc);
		 * 
		 * final Hashtable sendSession = (Hashtable)session.clone();
		 * 
		 * Hashtable bootStrapInfo = new Hashtable();
		 * bootStrapInfo.put("doc",doc);
		 * bootStrapInfo.put("senderOrReceiver","receiver");
		 * sendSession.put("bootStrapInfo",bootStrapInfo);
		 * 
		 * SupraClient sClient = new SupraClient((String)
		 * sendSession.get("address"), (String) sendSession.get("port"));
		 * 
		 * sClient.setSupraSphereFrame(sF);
		 * sendSession.put("passphrase",sF.getTempPW());
		 * sClient.startZeroKnowledgeAuth(sendSession,"StartByteRouter");
		 * 
		 */
	}

	/**
	 * @param doc
	 */
	public void serverBuildAction(final Document doc) {
		Element physicalLocation = doc.getRootElement().element(
				"physical_location");

		String locationName = null;

		if (physicalLocation != null) {
			locationName = physicalLocation.attributeValue("value");
		}

		logger.info("Got request to start!: " + doc.asXML());

		String currentProfileId = (String) this.sF.getMainRawSession().get("profile_id");

		logger.info("currentProfileId: " + currentProfileId);
		this.client.startRemoteBuild(this.session, doc, "server", "false");
		if (locationName.equals(currentProfileId)) {

			// MethodProcessing.startBuild(session,doc,sF,"server");

		} else {

			// MethodProcessing.initByteRouter(session,doc,null,sF,"startBuild");

		}
	}

	/**
	 * @param doc
	 */
	public void servantBuildAction(final Document doc) {
		this.client.startRemoteBuild(this.session, doc, "server", "true");

		MethodProcessing.startBuild(this.session, doc, this.sF, "client",
				"true");
	}

	/**
	 * @param doc
	 */
	public void clientBuildAction(final Document doc) {
		Element physicalLocation = doc.getRootElement().element(
				"physical_location");

		String locationName = null;

		if (physicalLocation != null) {
			locationName = physicalLocation.attributeValue("value");
		}

		MethodProcessing.startBuild(this.session, doc, this.sF, "client",
				"false");
		logger.info("Got request to start!: " + doc.asXML());

		String currentProfileId = (String) this.sF.getMainRawSession().get("profile_id");

		logger.info("currentProfileId: " + currentProfileId);

		if (locationName.equals(currentProfileId)) {

		} else {

			// MethodProcessing.initByteRouter(session,doc,null,sF,"startBuild");

		}
	}

	/**
	 * @param viewNode
	 * @param doc
	 */
	public void mirrorToServerAction(final Document doc) {
		Document rootDoc = this.mp.getMessagesTree().getSelectedDoc();

		String currentProfileId = (String) this.sF.getMainRawSession().get("profile_id");

		logger.info("currentProfileId: " + currentProfileId);

		MethodProcessing.initByteRouter(this.session, doc, rootDoc, this.sF,
				"mirrorToServer");
	}

	/**
	 * 
	 */
	public void removeAction() {
		
		for (NodeDocumentsBundle documentBundle : this.mp.getMessagesTree().getSelectedDocumentsWithParents()) {

			Document doc = documentBundle.getNodeStatement().getBindedDocument();

			this.mp.removeFromTable(documentBundle.getNodeStatement().getMessageId());
			this.mp.getMessagesTree().removeNodeFromParent(documentBundle.getNodeStatement());
			
			if(this.mp.isInsertable()) {
				this.mp.getSmallBrowser().deleteMessage(documentBundle.getNodeStatement());
			} else {
				this.mp.loadWindow(documentBundle.getParentStatement());
			}
			
			if(documentBundle.getParentStatement()!=null) {
				this.mp.getMessagesTree().selectMessage(documentBundle.getParentStatement().getMessageId());
				this.mp.selectItemInTable(documentBundle.getParentStatement().getMessageId());
				
				this.mp.getSmallBrowser().highlightSelectedString(documentBundle.parentStatement.getMessageId());
				
				this.mp.setLastSelected(documentBundle.parentStatement.getBindedDocument());
			} else {
				this.mp.setLastSelected(null);
			}
			
			if (!this.mp.existsAgainInThread(doc.getRootElement().element(
					"message_id").attributeValue("value"))) {
				this.mp.removeDocFromHash(doc.getRootElement().element(
						"message_id").attributeValue("value"));
			}
		}
	}

	public String getSelectedSphereId() {
		Document selectedDocumet = this.mp.getLastSelectedDoc();
		if ( selectedDocumet == null ) {
			return null;
		}
		SphereStatement sphere = SphereStatement.wrap(selectedDocumet);
		return sphere.getSystemName();
	}

	public boolean isRootSphereSelected() {
		Document selectedDocument = this.mp.getLastSelectedDoc();
		if (selectedDocument == null) {
			return false;
		}
		Element eType = selectedDocument.getRootElement().element("type");
		if (eType == null) {
			return false;
		} else {
			String type = eType.attributeValue("value");
			if (!type.equals("sphere")) {
				return false;
			} else {
				String supraSphereName = this.sF.client.getVerifyAuth()
						.getSupraSphereName();
				String system_name = selectedDocument.getRootElement()
						.attributeValue("system_name");
				return supraSphereName.equals(system_name);
			}
		}
	}

	public String getSelectedUserName() {
		Document selectedDocumet = this.mp.getLastSelectedDoc();
		return XmlDocumentUtils.selectAttibuteValueByXPath(selectedDocumet,
				"/contact/login/@value");
	}

	/**
	 * @param doc
	 */
	public void replyMessageAction(Document doc) {
		try {
			this.sF.setReplyChecked(true);
			this.mp.setLastSelected(doc);
		} catch (Exception ex) {
		}
		CreateMessageAction action = new CreateMessageAction(this.session, Statement.wrap(doc));
		
		action.perform(false);
	}

	/**
	 * 
	 */
	public void showThreadsSystemMessages(final String threadId) {
		if(threadId==null) {
			return;
		}
		this.mp.showSystemMessages(threadId);
		this.mp.getSystemMessagesController().addThread(threadId);
	}
	
	public void showCurrentSystemMessage( final String messageId ) {
		if(messageId==null) {
			return;
		}
		this.mp.currentSystemMessageShow(messageId);
	}

	/**
	 * @param threadId
	 */
	public void hideThreadsSystemMessages(final String threadId) {
		if(threadId==null) {
			return;
		}
		this.mp.hideSystemMessages(threadId);
		this.mp.getSystemMessagesController().hideThread(threadId);
	}
	
	public void hideCurrentSystemMessages( final String messageId ) {
		if(messageId==null) {
			return;
		}
		this.mp.currentSystemMessagesHide(messageId);
	}
}
