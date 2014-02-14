/**
 * 
 */
package ss.client.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.event.UnifiedListeners;
import ss.client.event.messagedeleters.AllSelectedDeleteManager;
import ss.client.event.messagedeleters.EntireThreadDeleter;
import ss.client.event.messagedeleters.SingleMessageDeleter;
import ss.client.event.messagedeleters.SubtreeDeleter;
import ss.client.event.tagging.TagManager;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.clubdealmanagement.contact.ContactEditor;
import ss.client.ui.clubdealmanagement.fileassosiation.ClubDealsSelectionForFileAssosiationWindow;
import ss.client.ui.email.EmailController;
import ss.client.ui.forward.CurrentMessageForwardingDialog;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;
import ss.client.ui.viewers.ViewMessageSWT;
import ss.common.UiUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.FileStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.clubdeals.ClubDealUtils;
import ss.domainmodel.clubdeals.ClubdealCollection;
import ss.util.SessionConstants;
import ss.util.SupraXMLConstants;

/**
 * @author roman
 *
 */
	
public class MessagesTreeMenuCreator {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeMenuCreator.class);
	
	MessagesTreeActionDispatcher listener;
	
	MessagesPane mp;
	
	private static final String FORWARD = "MESSAGESTREEMOUSELISTENER.FORWARD";

	@SuppressWarnings("unused")
	private static final String SPHERE = "MESSAGESTREEMOUSELISTENER.SPHERE";

	@SuppressWarnings("unused")
	private static final String AS_LINK = "MESSAGESTREEMOUSELISTENER.AS_LINK";

	private static final String EMAIL = "MESSAGESTREEMOUSELISTENER.EMAIL";

	private static final String SAVE_TO_INDEX = "MESSAGESTREEMOUSELISTENER.SAVE_TO_INDEX";

	private static final String FIND_RSS_FEEDS = "MESSAGESTREEMOUSELISTENER.FIND_RSS_FEEDS";

	private static final String REPLY = "MESSAGESTREEMOUSELISTENER.REPLY";
	
	private static final String REPLY_TERSE = "MESSAGESTREEMOUSELISTENER.TERSE";
	
	private static final String REPLY_MESSAGE = "MESSAGESTREEMOUSELISTENER.MESSAGE";

	private static final String TAG_ASSET = "MESSAGESTREEMOUSELISTENER.TAG_ASSET";

	private static final String SEARCH = "MESSAGESTREEMOUSELISTENER.SEARCH";

	private static final String SHOW_TAGS = "MESSAGESTREEMOUSELISTENER.SHOW_TAGS";

	private static final String SHOW_HAS_THIS_TAG = "MESSAGESTREEMOUSELISTENER.SHOW_HAS_THIS_TAG";

	private static final String FIND_RELATED_CONCEPTS = "MESSAGESTREEMOUSELISTENER.FIND_RELATED_CONCEPTS";

	private static final String MATCH_AGAINST_HISTORY = "MESSAGESTREEMOUSELISTENER.MATCH_AGAINST_HISTORY";

	private static final String MATCH_RECENT_CONTACT_HISTORY = "MESSAGESTREEMOUSELISTENER.MATCH_RECENT_CONTACT_HISTORY";

	private static final String MATCH_OTHER_MEMBER_HISTORY = "MESSAGESTREEMOUSELISTENER.MATCH_OTHER_MEMBER_HISTORY";
	
	private static final String MATCH = "MESSAGESTREEMOUSELISTENER.MATCH";

	private static final String SEARCH_FOR_KEYWORDS = "MESSAGESTREEMOUSELISTENER.SEARCH_FOR_KEYWORDS";

	private static final String SHOW_URL = "MESSAGESTREEMOUSELISTENER.SHOW_URL";

	private static final String SPECIFIC_MEMBER = "MESSAGESTREEMOUSELISTENER.SPECIFIC_MEMBER";

	private static final String ALL_MEMBERS = "MESSAGESTREEMOUSELISTENER.ALL_MEMBERS";

	private static final String NOTIFY_TRAY = "MESSAGESTREEMOUSELISTENER.NOTIFY_TRAY";

	private static final String SHOW_RECENT_KEYWORDS_QUERIES = "MESSAGESTREEMOUSELISTENER.SHOW_RECENT_KEYWORDS_QUERIES";

	private static final String SHOW_RECENT_BOOKMARKS = "MESSAGESTREEMOUSELISTENER.SHOW_RECENT_BOOKMARKS";
	
	private static final String SHOW_RECENT = "MESSAGESTREEMOUSELISTENER.SHOW_RECENT";

	@SuppressWarnings("unused")
	private static final String STOP = "MESSAGESTREEMOUSELISTENER.STOP";

	private static final String REMOVE_FROM_VIEW = "MESSAGESTREEMOUSELISTENER.REMOVE_FROM_VIEW";

	private static final String EDIT_DOC_XML = "MESSAGESTREEMOUSELISTENER.EDIT_DOC_XML";

	private static final String OPEN_SPHERE = "MESSAGESTREEMOUSELISTENER.OPEN_SPHERE";
	
	private static final String DELETE_SPHERE = "MESSAGESTREEMOUSELISTENER.DELETE_SPHERE";
	
	private static final String CURRENT_MESSAGE = "MESSAGESTREEMOUSELISTENER.REMOVE_CURRENT_MESSAGE";
	
	private static final String REMOVE_ENTIRE_THREAD = "MESSAGESTREEMOUSELISTENER.REMOVE_ENTIRE_THREAD";
	
	private static final String SUBTREE = "MESSAGESTREEMOUSELISTENER.REMOVE_SUBTREE";
	
	private static final String REMOVE_MENU_SELECTOR = "MESSAGESTREEMOUSELISTENER.REMOVE_MENU_SELECTOR";
	
	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_EVENT_MESSAGESTREEMOUSELISTENER);
	
	public MessagesTreeMenuCreator(MessagesTreeActionDispatcher listener, MessagesPane mp) {
		this.listener = listener;
		this.mp = mp;
	}
	
	public void createPopupMenu(final Menu swtMenu) {
		final Document doc = (Document)this.mp.getMessagesTree().getSelectedDoc().clone();
		final String type = doc.getRootElement().element("type")
		.attributeValue("value");
		final boolean isAdmin = this.mp.sF.client.getVerifyAuth()
		.getUserSession().isAdmin();
		final boolean isRootSphere = this.listener.isRootSphereSelected();

		if (!isRootSphere && type.equals("sphere")){
			MenuItem openSphereMenuItem = new MenuItem(swtMenu, SWT.PUSH);
			openSphereMenuItem.setText(this.bundle
					.getString(OPEN_SPHERE));
			openSphereMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					SphereOpenManager.INSTANCE.request(SphereStatement.wrap(doc).getSystemName());
				}
			});
			
			insertMenuSeparator(swtMenu);
		}

		if (!type.equals("sphere")) {
			MenuItem tagItem = new MenuItem(swtMenu, SWT.PUSH);
			tagItem.setText(this.bundle.getString(TAG_ASSET));
			tagItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().tagAction(doc);
				}
			});

			MenuItem showTags = new MenuItem(swtMenu, SWT.PUSH);
			showTags.setText(this.bundle.getString(SHOW_TAGS));
			showTags.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					new Thread() {
						public void run() {
							TagManager.INSTANCE.openForSelected( getMP() );
						}
					}.start();
				}
			});
			
		} else {
			MenuItem search = new MenuItem(swtMenu, SWT.PUSH);
			search.setText(this.bundle.getString(SEARCH));
			UnifiedListeners ul = new UnifiedListeners(
					SupraSphereFrame.INSTANCE);
			ul.addQueryListener(search);
		}
		
		if (type.equals("file")) {
			MenuItem assosiate = new MenuItem(swtMenu, SWT.PUSH);
			assosiate.setText("Assosiate with clubdeal");
			assosiate.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						@Override
						public void run() {
							final FileStatement file = FileStatement.wrap(doc);
							String realName = (String)SupraSphereFrame.INSTANCE.client.session.get(SessionConstants.REAL_NAME);
							final ClubdealCollection cdCollection = ClubDealUtils.INSTANCE.getClubdealsForContact(SupraSphereFrame.INSTANCE.client, realName);
							final List<String> associatedClubdeals = SupraSphereFrame.INSTANCE.client.getAssotiatedClubdeald(file);
							UiUtils.swtBeginInvoke(new Runnable() {
								public void run() {
									final ClubDealsSelectionForFileAssosiationWindow window = new ClubDealsSelectionForFileAssosiationWindow(
											cdCollection, file, associatedClubdeals);
									window.open();
								}
							});
						}
					};
					t.start();
				}
			});
		}
		
		boolean flag = false;
		MenuItem showOneOrAll = new MenuItem(swtMenu, SWT.CASCADE);
		showOneOrAll.setText("Show System Messages");
		Menu showMenu = new Menu(showOneOrAll);
		showOneOrAll.setMenu(showMenu);

		final String threadId = Statement.wrap(doc).getThreadId();
		final String messageId = Statement.wrap(doc).getMessageId();
		
		List<Document> listDoc = this.mp.getMessagesTree().getChildrenFor(messageId);
		for(Document document : listDoc) {
			if(Statement.wrap(document).getType().equals(SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE)) {
				flag = true;
				break;
			}
		}
		if(flag) {
			MenuItem showSystem = new MenuItem(showMenu, SWT.PUSH);
			showSystem.setText("Hide Current System Message");
			showSystem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().hideCurrentSystemMessages(messageId);
				}
			});
		} else {
			MenuItem showSystem = new MenuItem(showMenu, SWT.PUSH);
			showSystem.setText("Show Current System Message");
			showSystem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().showCurrentSystemMessage(messageId);
				}
			});
		}
		
		if(this.mp.getSystemMessagesController().isShown(threadId)) {
			MenuItem showSystem = new MenuItem(showMenu, SWT.PUSH);
			showSystem.setText("Hide Thread's System Messages");
			showSystem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().hideThreadsSystemMessages(threadId);
				}
			});
		} else {
			MenuItem showSystem = new MenuItem(showMenu, SWT.PUSH);
			showSystem.setText("Show Thread's System Messages");
			showSystem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().showThreadsSystemMessages(threadId);
				}
			});
		}

		if (type.equals("keywords")) {
			MenuItem findAssets = new MenuItem(swtMenu, SWT.PUSH);
			findAssets.setText(this.bundle
					.getString(SHOW_HAS_THIS_TAG));
			findAssets.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					getListener().doShowHasTagAction();
				}
			});

			MenuItem findRelatedConcepts = new MenuItem(swtMenu, SWT.PUSH);
			findRelatedConcepts.setText(this.bundle
					.getString(FIND_RELATED_CONCEPTS));
			findRelatedConcepts.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						@Override
						public void run() {
							getListener().findRelatedConceptsAction();
						}
					};
					t.start();
				}
			});
		}

		insertMenuSeparator(swtMenu);
		
		if(type.equals("bookmark") || type.equals("rss")) {
			MenuItem saveToIndex = new MenuItem(swtMenu, SWT.PUSH);
			saveToIndex.setText(this.bundle
					.getString(SAVE_TO_INDEX));
			saveToIndex.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						public void run() {
							getListener().saveToIndexAction();
						}
					};
					t.start();
				}
			});

			MenuItem showURL = new MenuItem(swtMenu, SWT.PUSH);
			showURL.setText(this.bundle.getString(SHOW_URL));
			showURL.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						public void run() {
							getListener().showURLAction(doc);
						}
					};
					t.start();
				}
			});
		}	

		if(type.equals("bookmark")) {
			MenuItem findLiveURL = new MenuItem(swtMenu, SWT.PUSH);
			findLiveURL.setText(this.bundle
					.getString(FIND_RSS_FEEDS));
			findLiveURL.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						public void run() {
							getListener().findLiveURLAction();
						}
					};
					t.start();
				}
			});

			MenuItem searchForSpecific = new MenuItem(swtMenu, SWT.PUSH);
			searchForSpecific.setText(this.bundle.getString(SEARCH_FOR_KEYWORDS));

			searchForSpecific.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Thread t = new Thread() {
						public void run() {
							getListener().searchForSpecificAction();
						}
					};
					t.start();
				}
			});
		}


		if(type.equals("message") || type.equals("terse") || type.equals("file")
				|| type.equals("bookmark")) {
			MenuItem matchItem = new MenuItem(swtMenu, SWT.CASCADE);
			matchItem.setText(this.bundle
					.getString(MATCH));
			
			Menu matchMenu = new Menu(matchItem);
			matchItem.setMenu(matchMenu);
			
			MenuItem matchHistory = new MenuItem(matchMenu, SWT.PUSH);
			matchHistory.setText(this.bundle
					.getString(MATCH_AGAINST_HISTORY));
			matchHistory.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					final Vector<Document> docs = MessagesTreeMenuCreator.this.mp.getMessagesTree().getSelectedDocs();
					Thread t = new Thread() {
						public void run() {
							getListener().matchHistoryAction(docs);
						}
					};
					t.start();
				}
			});

			MenuItem matchRecentHistory = new MenuItem(matchMenu, SWT.PUSH);
			matchRecentHistory.setText(this.bundle
					.getString(MATCH_RECENT_CONTACT_HISTORY));
			matchRecentHistory.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					final Vector<Document> docs = MessagesTreeMenuCreator.this.mp.getMessagesTree().getSelectedDocs();
					Thread t = new Thread() {
						public void run() {
							getListener().matchRecentHistoryAction(docs);
						}
					};
					t.start();
				}
			});

			if (!(this.mp.client.getVerifyAuth()
					.getDisplayName((String) this.mp.getRawSession().get("sphere_id")))
					.equals((String) this.mp.getRawSession().get("real_name"))) {
				MenuItem matchOtherHistory = new MenuItem(matchMenu, SWT.PUSH);
				matchOtherHistory.setText(this.bundle
						.getString(MATCH_OTHER_MEMBER_HISTORY));
				matchOtherHistory.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						final Vector<Document> docs = MessagesTreeMenuCreator.this.mp.getMessagesTree().getSelectedDocs();
						Thread t = new Thread() {
							public void run() {
								getListener().matchOtherHistory(docs);
							}
						};
						t.start();
					}
				});
			}
		}

		if(type.equals("contact")) {
			try {
				final ContactStatement contactStatment = ContactStatement.wrap(doc);
				if (ContactEditor.isOwnContact( contactStatment, this.mp.client )) {
					final MenuItem editContactItem = new MenuItem(swtMenu, SWT.PUSH);
					editContactItem.setText("Edit...");
					editContactItem.addSelectionListener(new SelectionAdapter(){
						@Override
						public void widgetSelected(SelectionEvent e) {
							final ContactEditor editor = new ContactEditor( contactStatment,
									MessagesTreeMenuCreator.this.mp.client, MessagesTreeMenuCreator.this.mp.getSystemName());
							editor.open();
						}
					});
				}
				
				String checkURL = null;
				String checkSphere = null;
				String checkMessage = null;

				if (doc.getRootElement().element("home_sphere") != null) {

					checkURL = doc.getRootElement().element("home_sphere")
					.attributeValue("value");
					checkSphere = doc.getRootElement().element("home_sphere")
					.attributeValue("sphere_id");
					checkMessage = doc.getRootElement().element("home_sphere")
					.attributeValue("message_id");
				} else {
					checkURL = (String) this.mp.getRawSession().get("sphereURL");

					checkSphere = (String) this.mp.getRawSession().get("sphere_id");
					checkMessage = doc.getRootElement().element("message_id")
					.attributeValue("value");
				}

				if (checkURL.length() <= 0) {
					checkURL = (String) this.mp.getRawSession().get("sphereURL");

					checkSphere = (String) this.mp.getRawSession().get("sphere_id");
					checkMessage = doc.getRootElement().element("message_id")
					.attributeValue("value");
				}

				final String homeSphereURL = checkURL;
				final String homeSphereId = checkSphere;
				final String homeMessageId = checkMessage;
				
				MenuItem showRecentItem = new MenuItem(swtMenu, SWT.CASCADE);
				showRecentItem.setText(this.bundle.getString(SHOW_RECENT));

				Menu showRecentDropDown = new Menu(showRecentItem);
				showRecentItem.setMenu(showRecentDropDown);
				
				MenuItem showRecentQueries = new MenuItem(showRecentDropDown, SWT.PUSH);
				showRecentQueries.setText(this.bundle
						.getString(SHOW_RECENT_KEYWORDS_QUERIES));
				showRecentQueries.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						new Thread() {
							public void run() {
								getListener().showRecentQueriesAction(homeSphereURL,
										homeSphereId, homeMessageId);
							}
						}.start();

					}
				});

				MenuItem showRecentBookmarks = new MenuItem(showRecentDropDown, SWT.PUSH);
				showRecentBookmarks.setText(this.bundle
						.getString(SHOW_RECENT_BOOKMARKS));
				showRecentBookmarks.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						new Thread() {
							public void run() {
								getListener().showRecentBookmarksAction(homeSphereURL,
										homeSphereId, homeMessageId);
							}
						}.start();
					}
				});
			} catch (Exception ex) {
			}
		}


		MenuItem notifyTray = new MenuItem(swtMenu, SWT.CASCADE);
		notifyTray.setText(this.bundle.getString(NOTIFY_TRAY));

		Menu notifyMenu = new Menu(notifyTray);
		notifyTray.setMenu(notifyMenu);

		MenuItem allMembers = new MenuItem(notifyMenu, SWT.PUSH);
		allMembers.setText(this.bundle.getString(ALL_MEMBERS));
		allMembers.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getListener().allMembersAction(MessagesTreeMenuCreator.this.mp.getMessagesTree().getSelectedDoc());
			}
		});

		final MenuItem specificMemberNotify = new MenuItem(notifyMenu, SWT.PUSH);
		specificMemberNotify.setText(this.bundle
				.getString(SPECIFIC_MEMBER));
		specificMemberNotify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				Thread t = new Thread() {
					public void run() {
						getListener().specificMemberNotifyAction(doc);
					}
				};
				t.start();
			}
		});
//		if (type.equals("systemfile")) {
//			final File file = new File(this.bdir
//					+ this.fsep
//					+ "Assets"
//					+ this.fsep
//					+ "File"
//					+ this.fsep
//					+ doc.getRootElement().element("subject")
//					.attributeValue("value"));
//			if (doc.getRootElement().element("subject").attributeValue(
//			"value").toLowerCase().endsWith("mp3")) {
//
//				MenuItem play = new MenuItem(swtMenu, SWT.PUSH);
//				play.setText(this.bundle.getString(PLAY));
//				play.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						Thread t = new Thread() {
//							public void run() {
//								getListener().playAction(file);
//							}
//						};
//						t.start();
//					}
//				});
//
//
//			}
//
//			MenuItem open = new MenuItem(swtMenu, SWT.PUSH);
//			open.setText(this.bundle.getString(OPEN));
//			open.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					getListener().openAction(doc);
//				}
//			});
//
//		}

//		if(type.equals("filesystem")) {
//			MenuItem openAll = new MenuItem(swtMenu, SWT.PUSH);
//			openAll.setText(this.bundle.getString(OPEN_ALL));
//			openAll.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					MethodProcessing.initByteRouter(
//							getMP().getRawSession(), doc,
//							null, getMP().sF, null);
//				}
//			});
//
//			MenuItem buildFromHere = new MenuItem(swtMenu, SWT.CASCADE);
//			buildFromHere.setText(this.bundle.getString(BUILD));
//
//			Menu buildFromHereMenu = new Menu(buildFromHere);
//			//buildFromHere.setMenu(buildFromHereMenu);
//
//
//			MenuItem clientBuild = new MenuItem(buildFromHereMenu, SWT.PUSH);
//			clientBuild.setText(this.bundle
//					.getString(CLIENT));
//			clientBuild.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					getListener().clientBuildAction(doc);
//				}
//			});
//
//			MenuItem serverBuild = new MenuItem(buildFromHereMenu, SWT.PUSH);
//			serverBuild.setText(this.bundle
//					.getString(SERVER));
//			serverBuild.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					getListener().serverBuildAction(doc);
//				}
//			});
//
//			MenuItem servantBuild = new MenuItem(buildFromHereMenu, SWT.PUSH);
//			servantBuild.setText(this.bundle
//					.getString(SERVANT));
//			servantBuild.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					getListener().servantBuildAction(doc);
//				}
//			});
//		}

//		if (type.equals("filesystem") || type.equals("systemfile")) {
//			MenuItem mirrorToServer = new MenuItem(swtMenu, SWT.PUSH);
//			mirrorToServer.setText(this.bundle
//					.getString(MIRROR_TO_SERVER));
//			mirrorToServer.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					getListener().mirrorToServerAction(doc);
//				}
//			});
//		}

//		if (type.equals(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL)) {
//			
//
//			ExternalEmailStatement emailStatement = ExternalEmailStatement
//			.wrap(doc);
//			if (emailStatement.isInput()) {
//				MenuItem item = new MenuItem(swtMenu, SWT.PUSH);
//				item.setText(this.bundle.getString(REPLY_EMAIL));
//				item.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						(new EmailController(getMP().sF,
//								getMP(),
//								getMP().getRawSession()))
//								.clickedReplyEmail(Statement.wrap(doc));
//					}
//
//				});
//			}
//		}
		
		insertMenuSeparator(swtMenu);
		
		if(!type.equals("sphere")) {
			MenuItem reply = new MenuItem(swtMenu, SWT.CASCADE);
			reply.setText(this.bundle
					.getString(REPLY));
			Menu dropDownReply = new Menu(reply);
			reply.setMenu(dropDownReply);
			
			MenuItem toThisItem = new MenuItem(dropDownReply, SWT.PUSH);
			toThisItem.setText(this.bundle
					.getString(REPLY_TERSE));
			toThisItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().replyAction(doc);
				}
			});
			
			MenuItem messageItem = new MenuItem(dropDownReply, SWT.PUSH);
			messageItem.setText(this.bundle
					.getString(REPLY_MESSAGE));
			messageItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					getListener().replyMessageAction(doc);
				}
			});
			
			if (type.equals(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL) && ExternalEmailStatement.wrap(doc).isInput()) {
				MenuItem emailItem = new MenuItem(dropDownReply, SWT.PUSH);
				emailItem.setText(this.bundle.getString(EMAIL));
				emailItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						(new EmailController(getMP(),
								getMP().getRawSession()))
								.clickedReplyEmail(Statement.wrap(doc));
					}

				});
			}
		}
		
		if (type.equals("message") || type.equals("bookmark")
				|| type.equals("terse")
				|| type.equals("file") || type.equals("contact")
				|| type.equals("sphere") || type.equals("rss") || type.equals(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL)) {

			final List<Document> docs = new ArrayList<Document>();
			docs.add(doc);
			
			final SpheresCollectionByTypeObject sphereOwner = new SpheresCollectionByTypeObject(SupraSphereFrame.INSTANCE.client);
			
			MenuItem forwardItem = new MenuItem(swtMenu, SWT.CASCADE);
			forwardItem.setText(this.bundle.getString(FORWARD));

			Menu dropDownForward = new Menu(forwardItem);
			forwardItem.setMenu(dropDownForward);
			
			MenuItem currentMessageItem = new MenuItem(dropDownForward, SWT.PUSH);
			currentMessageItem.setText(this.bundle.getString(CURRENT_MESSAGE));
			currentMessageItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					CurrentMessageForwardingDialog dialog = new CurrentMessageForwardingDialog(docs, sphereOwner);
					dialog.open();
				}
			});
			
			
			final List<Document> subTreeDocs = this.mp.getMessagesTree().getSelectedSubtree();
			if(subTreeDocs.size()>1) {
				MenuItem forwardSubtreeItem = new MenuItem(dropDownForward, SWT.PUSH);
				forwardSubtreeItem.setText(this.bundle.getString(SUBTREE));
				forwardSubtreeItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						CurrentMessageForwardingDialog dialog = new CurrentMessageForwardingDialog(subTreeDocs, sphereOwner);
						dialog.open();
					}
				});
			}
			
			if(type.equals(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL)) {
				MenuItem forwardEmailItem = new MenuItem(dropDownForward, SWT.PUSH);
				forwardEmailItem.setText(this.bundle.getString(EMAIL));
				forwardEmailItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						(new EmailController(getMP(),
								getMP().getRawSession()))
								.clickedForwardEmail(Statement.wrap(doc));
					}
				});
			}
		}

		insertMenuSeparator(swtMenu);

		if(isAdmin) {
			MenuItem remove = new MenuItem(swtMenu, SWT.CASCADE);
			remove.setText(this.bundle.getString(REMOVE_MENU_SELECTOR));

			Menu removeMenu = new Menu(remove);
			remove.setMenu(removeMenu);

			if (logger.isDebugEnabled()) {
				logger.debug("child count : "+getMP().getMessagesTree().getChildCountForDoc(Statement.wrap(doc)));
			}
			
			MenuItem removeMessage = new MenuItem(removeMenu, SWT.PUSH);
			removeMessage.setText(this.bundle.getString(CURRENT_MESSAGE));
			removeMessage.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					(new SingleMessageDeleter(getMP())).executeDeliting(doc);
				}
			});
			removeMessage.setEnabled(getMP().getMessagesTree().getSelectedNodesCount()==1 && getMP().getMessagesTree().getViewNodeChildCount()==0);

			MenuItem removeSubtree = new MenuItem(removeMenu, SWT.PUSH);
			removeSubtree.setText(this.bundle.getString(SUBTREE));
			removeSubtree.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					(new SubtreeDeleter(getMP())).executeDeliting(doc);
				}
			});
			removeSubtree.setEnabled(getMP().getMessagesTree().getSelectedNodesCount()==1 && getMP().getMessagesTree().getViewNodeChildCount()>=1);

			MenuItem removeEntireThread = new MenuItem(removeMenu, SWT.PUSH);
			removeEntireThread.setText(this.bundle.getString(REMOVE_ENTIRE_THREAD));
			removeEntireThread.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					(new EntireThreadDeleter(getMP())).executeDeliting(doc);
				}
			});
			removeEntireThread.setEnabled(getMP().getMessagesTree().getSelectedNodesCount()==1);
			
			MenuItem removeAllSelected = new MenuItem(removeMenu, SWT.PUSH);
			removeAllSelected.setText("All Selected Messages");
			removeAllSelected.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					(new AllSelectedDeleteManager(getMP()))
						.executeDeleting();
				}
			});
			removeAllSelected.setEnabled(getMP().getMessagesTree().getSelectedNodesCount()>1);
			
			
			if (type.equals("sphere")) {
				UnifiedListeners ul = new UnifiedListeners(this.mp.sF);

				MenuItem removeSphere = new MenuItem(swtMenu, SWT.PUSH);
				removeSphere.setText(this.bundle.getString(DELETE_SPHERE));

				ul = new UnifiedListeners(this.mp.sF);
				ul.addSphereRemoveListener(removeSphere);
			}
			
			MenuItem editXML = new MenuItem(swtMenu, SWT.PUSH);
			editXML.setText(this.bundle
					.getString(EDIT_DOC_XML));
			editXML.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					new ViewMessageSWT(
							getMP().getRawSession(), doc, getMP());
				}
			});
		}
		
		MenuItem removeFromView = new MenuItem(swtMenu, SWT.PUSH);
		removeFromView.setText(this.bundle
				.getString(REMOVE_FROM_VIEW));
		removeFromView.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getListener().removeAction();
			}
		});
		
		if (swtMenu != null && !swtMenu.isDisposed()) {
			swtMenu.setVisible(true);
		}
	}

	private void insertMenuSeparator(final Menu swtMenu) {
		new MenuItem(swtMenu, SWT.SEPARATOR);
	}
	
	public MessagesPane getMP() {
		return this.mp;
	}
	
	public MessagesTreeActionDispatcher getListener() {
		return this.listener;
	}
}