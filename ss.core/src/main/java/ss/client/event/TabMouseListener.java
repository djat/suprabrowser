/*
 * Created on Apr 29, 2004
 * 
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package ss.client.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JTabbedPane;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.event.createevents.CreateContactAction;
import ss.client.event.createevents.CreateFileAction;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.event.createevents.CreateMessageAction;
import ss.client.event.createevents.CreateRssAction;
import ss.client.event.createevents.CreateSphereAction;
import ss.client.event.createevents.CreateTerseAction;
import ss.client.event.supramenu.listeners.CloseAllTabsSelectionListener;
import ss.client.event.supramenu.listeners.OpenBlankSelectionListener;
import ss.client.event.supramenu.listeners.PreferencesMenuListener;
import ss.client.event.supramenu.listeners.SaveGlobalMarkSelectionListener;
import ss.client.event.supramenu.listeners.SaveOrderSelectionListener;
import ss.client.event.supramenu.listeners.SavePositionSelectionListener;
import ss.client.event.supramenu.listeners.SupraSearchSelectionListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SearchInputWindow;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.util.ImagesPaths;
import ss.util.NameTranslation;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author david
 * 
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TabMouseListener implements MouseListener {

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_EVENT_TABMOUSELISTENER);
	
	private static final String PREFERENCES = "TABMOUSELISTENER.PREFERENCES";

	private static final String SEARCH = "TABMOUSELISTENER.SEARCH";	
	
	private static final String SUPRA_SEARCH = "TABMOUSELISTENER.SUPRA_SEARCH";

	private static final String SAVE_WINDOW_POSITION = "TABMOUSELISTENER.SAVE_WINDOW_POSITION";

	private static final String SAVE_TAB_ORDER = "TABMOUSELISTENER.SAVE_TAB_ORDER";

	private static final String CLOSE_ALL_TABS = "TABMOUSELISTENER.CLOSE_ALL_TABS";

	private static final String TAG_WITH_SEARCH_KEYWORD = "TABMOUSELISTENER.TAG_WITH_SEARCH_KEYWORD";

	private static final String PRINT = "TABMOUSELISTENER.PRINT";

	private static final String CLOSE = "TABMOUSELISTENER.CLOSE";
	
	private static final String SET_GLOBAL_MARK = "TABMOUSELISTENER.SET_GLOBAL_MARK";
	
	private static final String OPEN_BLANK_TAB = "TABMOUSELISTENER.OPEN_BLANK_TAB";

	/**
	 * @author zobo
	 *
	 */
	private final class CloseBrowserTabMenuMouseListener implements SelectionListener {
		public void widgetSelected(SelectionEvent arg0) {
			SupraCTabItem item = TabMouseListener.this.sF.tabbedPane.getSelectedSupraItem();
			if ( item != null ) {
				item.safeClose();
			}
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		
		}
	}

	/**
	 * @author zobo
	 *
	 */
	private final class CloseMenuMouseListener implements SelectionListener {
		public void widgetSelected(SelectionEvent arg0) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					final SupraCTabItem item = TabMouseListener.this.sF.tabbedPane.getSelectedSupraItem();
					if ( item != null ) {
						item.safeClose();	
					}
				}
			});
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {
		
		}
	}

	private class PrintSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent arg0) {
			// int tit = TabMouseListener.this.sF.tabbedPane.getSelectedIndex();
			// String title =
			// TabMouseListener.this.sF.tabbedPane.getTitleAt(tit);
			// String sphere_id =
			// TabMouseListener.this.sF.client.getVerifyAuth().getSystemName(title);
			MessagesPane mp = TabMouseListener.this.sF
					.getMessagesPaneFromSphereId(((String) TabMouseListener.this.session
							.get("sphere_id")));

			mp.printDoc("");//mp.printDoc(mp.getPreview().getText());
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {

		}
	}

	private SupraSphereFrame sF = null;

	private MenuItem supraQuery = null;

	private MenuItem search = null;
	
	private MenuItem supraSearch = null;

	private Image searchImage = null;

	private MenuItem saveWindow = null;

	private Image saveWindowImage = null;

	private MenuItem close = null;

	private Image closeImage = null;

	private Hashtable session = new Hashtable();

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TabMouseListener.class);

	private MenuItem save = null;

	private Image saveImage = null;

	private Image closeAllImage = null;

	private Image keywordsImage = null;

	private Image printImage = null;

	private Image closeBrowserImage = null;

	private Image supraSearchImage = null;

	private Image setGlobalImage;
	
	private static Image preferencesImage = null;

	public TabMouseListener(Hashtable session, SupraSphereFrame sF,
			JTabbedPane tabbedPane) {
		this.sF = sF;
		this.session = session;
		loadImages();
	}

	/**
	 * 
	 */
	private void loadImages() {
		try {

			this.searchImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SEARCH).openStream());
			
			this.supraSearchImage  = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRASEARCH).openStream());

			this.saveWindowImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SAVE_WINDOW_POSITIONS)
					.openStream());

			this.closeImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.CLOSE_TAB).openStream());

			this.saveImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SAVE_TAB_ORDER).openStream());

			this.closeAllImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.CLOSE_ALL_TABS).openStream());

			this.keywordsImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.KEYWORDS).openStream());

			this.printImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PRINT).openStream());
			
			this.setGlobalImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SET_GLOBAL_MARK).openStream());

			this.closeBrowserImage = this.closeImage;
			
			preferencesImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PREFERENCES_ICON).openStream());

		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	public void depricatedTagAction() {
		/*
		 * GenericXMLDocument generic = new GenericXMLDocument();
		 * 
		 * Document genericDoc = generic.XMLDoc(finalElem
		 * .attributeValue("value"), "", (String) session.get("real_name"));
		 * genericDoc.getRootElement().addElement("type") .addAttribute("value",
		 * "keywords"); genericDoc.getRootElement().addElement(
		 * "thread_type").addAttribute("value", "keywords");
		 * 
		 * genericDoc.getRootElement() .addElement("status").addAttribute(
		 * "value", "confirmed");
		 * 
		 * //genericDoc.getRootElement().addElement("associations");
		 * 
		 * for (Enumeration enumer = ((MessagesMutableTreeNode) lookUp.mainnode
		 * .getRoot()).preorderEnumeration(); enumer .hasMoreElements();) {
		 * MessagesMutableTreeNode temp = (MessagesMutableTreeNode) enumer
		 * .nextElement();
		 * 
		 * if (!temp.isRoot()) { org.dom4j.Document doc1 = (Document) temp
		 * .getUserObject(); Element searchElem = doc1
		 * .getRootElement().element( "search"); if (searchElem == null) { //
		 * Following part may not be // necessary... Hashtable newSession =
		 * lookUp .getSession();
		 * 
		 * newSession.put("sphere_id", sphere_id);
		 * 
		 * 
		 * sF.client.saveQueryView(newSession, doc1, finalElem); } else { Vector
		 * list = new Vector( (Collection) doc1 .getRootElement()
		 * .element("search") .element("interest") .elements());
		 * logger.info("list size: " + list.size()); for (int i = 0; i <
		 * list.size(); i++) { Element keyword = (Element) list .get(i);
		 * 
		 * String value = keyword .attributeValue("value"); boolean found =
		 * false; Vector docList = new Vector( (Collection) genericDoc
		 * .getRootElement() .elements( "keywords")); for (int j = 0; j <
		 * docList .size(); j++) { Element test = (Element) docList .get(j); if
		 * (test.attributeValue( "value").equals( value)) { found = true; } } if
		 * (found == false) { Element key = (Element) keyword .clone();
		 * key.addAttribute( "multiple", "1"); genericDoc.getRootElement()
		 * .add(key); } else {
		 * 
		 * String apath = "//keywords[@value=\"" + value + "\"";
		 * logger.info("apath: " + apath); Element addTo = (Element) genericDoc
		 * .selectObject(apath); String oldMultiple = null; try { oldMultiple =
		 * addTo .attributeValue("multiple"); } catch (NullPointerException npe) {
		 * oldMultiple = "0"; } Integer one = new Integer( oldMultiple); addTo
		 * .addAttribute( "multiple", new Integer( one .intValue() + 1)
		 * .toString()); } }
		 * 
		 * Hashtable newSession = lookUp .getSession();
		 * 
		 * newSession.put("sphere_id", sphere_id);
		 * 
		 * sF.client.saveQueryView(newSession, doc1, finalElem); } } }
		 * 
		 * Hashtable newSession = lookUp.getSession();
		 * 
		 * newSession.put("sphere_id", sphere_id);
		 * 
		 * genericDoc.getRootElement().addElement("unique_id").addAttribute("value",unique);
		 * sF.client.publishTerse(newSession, genericDoc);
		 * sF.client.addQueryToContact(newSession, finalElem);
		 * 
		 */

	}

	public void addQueryListener() {
		this.search.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				Thread t = new Thread() {

					@SuppressWarnings("unchecked")
					public void run() {
						TabMouseListener.this.sF.tabbedPane.resetMarkForSelectedTab();
						final MessagesPane mP = TabMouseListener.this.sF.tabbedPane.getSelectedMessagesPane();
						if (mP == null ) {
							return;
						}
						final String systemName = mP.getSphereId();
						final Document doc = mP.getSphereDefinition();
						final String apath = "//sphere/thread_types/*";
						final Vector results = new Vector();
						final Vector<String> assetTypes = new Vector<String>();
						if (doc != null) {
							results.addAll( XmlDocumentUtils.selectElementListByXPath(doc, apath) );

							for (int j = 0; j < results.size(); j++) {

								Element elem = (Element) results.get(j);
								String enabled = elem.attributeValue("enabled");
								if (enabled != null) {
									if (enabled.equals("true")) {

										String type = elem.getName();
										char originalStart = type.charAt(0);
										char upperStart = Character
												.toUpperCase(originalStart);
										type = upperStart + type.substring(1);

										if (!assetTypes.contains(type)) {

											assetTypes.add(type);

										}

									}

								}
							}

						} else {
							logger.info("it was null...");

							assetTypes.add(CreateTerseAction.TERSE_TITLE);
							assetTypes.add(CreateMessageAction.MESSAGE_TITLE);
							assetTypes.add(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL_BIG);
							assetTypes.add(CreateBookmarkAction.BOOKMARK_TITLE);
							assetTypes.add(CreateContactAction.CONTACT_TITLE);
							assetTypes.add(CreateKeywordsAction.KEYWORD_TITLE);
							assetTypes.add(CreateRssAction.RSS_TITLE);
							assetTypes.add(CreateFileAction.FILE_TITLE);
							assetTypes.add(CreateSphereAction.SPHERE_TITLE);

						}
						Vector<String> oneSphere = new Vector<String>();
						oneSphere.add(systemName);
						SearchInputWindow siw = new SearchInputWindow(
								TabMouseListener.this.sF, mP, mP.getRawSession(),
								oneSphere, assetTypes, false);
						siw.layoutUIAndSetFocus();

					}
				};
				t.start();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

	}

	public void addCloseMenuListener() {
		this.close.addSelectionListener(new CloseMenuMouseListener());
	}

	public void mouseDoubleClick(MouseEvent arg0) {
				SupraCTabItem selectedItem = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedSupraItem();
				if ( selectedItem != null && 
					 selectedItem.getContent() == SupraCTabItem.CONTENT_MESSAGES_PANE ) {
					messagesPaneTabDoubleClicked();
				}
	}

	public void mouseDown(final MouseEvent event) {
		UiUtils.swtBeginInvoke(new Runnable() {
			private TabMouseListener listener = TabMouseListener.this;

			public void run() {
				MessagesPane mp = this.listener.sF.tabbedPane.getSelectedMessagesPane();
				if(mp!=null) {
					mp.recheckPeopleListColors();
				}
				SupraCTabItem item = this.listener.sF.tabbedPane.getSelectedSupraItem();
				if ( item == null ) {
					return;
				}
				final int itemContent = item.getContent();
				if (1 == event.button) {
					if (itemContent == SupraCTabItem.CONTENT_MESSAGES_PANE) {
						MessagesPane messagesPane = item.getMessagesPane();
						if ( messagesPane != null ) {
							messagesPane.recheckPeopleListColors();
						}
					}

				} else if (1 < event.button) {
					int type = itemContent;
					if (type == SupraCTabItem.CONTENT_MESSAGES_PANE) {
						messagesPaneTabRightClicked(event);
					} else if (type == SupraCTabItem.CONTENT_BROWSER) {
						browserTabRightClicked(event);
					} else if (type == SupraCTabItem.CONTENT_EMAIL) {
						emailTabRightClicked(event);
					} else  {
						rootComponentRightClicked(event);
					}
				}

			}

		});
	}

	public void mouseUp(MouseEvent arg0) {
		try {
			SupraCTabItem selectedItem = TabMouseListener.this.sF.tabbedPane
			.getSelectedSupraItem();
			if ( selectedItem != null ) {
				selectedItem.resetMark();
				if ( selectedItem.getContent() == SupraCTabItem.CONTENT_MESSAGES_PANE ) {
					MessagesPane main = selectedItem.getMessagesPane();
					main.setReplyNumber(0);
					main.setUnseenNumber(0);
				}
			}
		} catch (Exception ex) {
			logger.error("Mouse up failed", ex);
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void messagesPaneTabDoubleClicked() {
//		final MessagesPane pane = (MessagesPane) TabMouseListener.this.sF.tabbedPane
//				.getSelectedMessagesPane();

		final ISphereView selectedSphereView = this.sF.tabbedPane
				.getSelectedSphereView();
		if (selectedSphereView == null) {
			logger.warn("Selected sphere view is null");
			return;
		}
		final String systemName = selectedSphereView.getSphereId();

		if (!selectedSphereView.isRootView()) {

			Document doc = selectedSphereView.getSphereDefinition();

			if (doc != null) {
				if (logger.isDebugEnabled()) {
					logger.debug( "doc: " + doc.asXML());
				}
			}

			String apath = "//sphere/thread_types/*";

			Vector results = new Vector();
			final Vector<String> assetTypes = new Vector<String>();
			if (doc != null) {
				try {
					java.util.List list = (ArrayList) doc.selectObject(apath);
					results = new Vector(list);
				} catch (ClassCastException cce) {

					results.add((Element) doc.selectObject(apath));
				}

				for (int j = 0; j < results.size(); j++) {

					Element elem = (Element) results.get(j);
					String enabled = elem.attributeValue("enabled");
					if (enabled != null) {
						if (enabled.equals("true")) {

							String type = elem.getName();
							char originalStart = type.charAt(0);
							char upperStart = Character
									.toUpperCase(originalStart);
							type = upperStart + type.substring(1);

							if (!assetTypes.contains(type)) {

								assetTypes.add(type);

							}

						}

					}
				}

			} else {

				assetTypes.add(CreateTerseAction.TERSE_TITLE);
				assetTypes.add(CreateMessageAction.MESSAGE_TITLE);
				assetTypes.add(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL_BIG);
				assetTypes.add(CreateBookmarkAction.BOOKMARK_TITLE);
				assetTypes.add(CreateKeywordsAction.KEYWORD_TITLE);
				assetTypes.add(CreateRssAction.RSS_TITLE);
				assetTypes.add(CreateContactAction.CONTACT_TITLE);
				assetTypes.add(CreateFileAction.FILE_TITLE);

			}
			Thread t = new Thread() {
				public void run() {

					Vector<String> oneSphere = new Vector<String>();

					oneSphere.add(systemName);
					if (selectedSphereView instanceof MessagesPane) {
						MessagesPane selectedMessagesPane = (MessagesPane) selectedSphereView;
						SearchInputWindow siw = new SearchInputWindow(
								TabMouseListener.this.sF, selectedMessagesPane, selectedSphereView.getRawSession(),
								oneSphere, assetTypes, false);
	
						siw.layoutUIAndSetFocus();
						siw.saveSheduleForm(selectedSphereView.getSphereDefinition());					
					}

				}
			};
			t.start();
		}

	}

	/**
	 * @param event
	 */
	private void messagesPaneTabRightClicked(final MouseEvent event) {
		
		final MessagesPane lookUp = this.sF.tabbedPane.getSelectedMessagesPane();
		
		if (lookUp != null) {
			Menu menu = createMessagesPanePopupMenu(lookUp, event);
			locateMenu(event, menu);
		}
		else {
			logger.warn("Selected message pane is null");
		}
	}
	
	/**
	 * @param event
	 */
	private Menu createMessagesPanePopupMenu(final MessagesPane mp, MouseEvent event) {

		Menu menu = new Menu(this.sF.getShell(), SWT.POP_UP);
		MenuItem keywords = null;

		final String sphere_id = mp.getSphereId();
		logger.info("sphere id before save: " + sphere_id);

		String queryId = NameTranslation.returnQueryId(mp
				.getSphereDefinition());

		Document sphereDef = mp.getSphereDefinition();
		Element elem = null;

		try {
			elem = sphereDef.getRootElement().element("search").element(
			"keywords").createCopy();

		} catch (NullPointerException npe) {
		}


		this.supraSearch = new MenuItem(menu, SWT.PUSH);
		this.supraSearch.setText(TabMouseListener.this.bundle
				.getString(SUPRA_SEARCH));
		this.supraSearch.setImage(this.supraSearchImage);			
		this.supraSearch.addSelectionListener(new SupraSearchSelectionListener(this.sF,sphere_id));
		
		
		this.search = new MenuItem(menu, SWT.PUSH);
		this.search.setText(TabMouseListener.this.bundle
				.getString(SEARCH));
		this.search.setImage(this.searchImage);
		addQueryListener();

		if (queryId != null && elem != null) {

			keywords = new MenuItem(menu, SWT.PUSH);
			keywords.setText(TabMouseListener.this.bundle
					.getString(TAG_WITH_SEARCH_KEYWORD));
			keywords.setImage(this.keywordsImage);

			mp.setRemovedAssets(new Hashtable());

			final Element finalElem = elem.createCopy();

			final String unique = new Long(VariousUtils.getNextUniqueId())
			.toString();
			finalElem.addAttribute("unique_id", unique);

			keywords.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent arg0) {

					Thread t = new Thread() {
						public void run() {




							Document doc = mp.getMessagesTree().getSelectedDoc();
							if (doc!=null) {

								mp.doTagAction(
										doc,
										finalElem
										.attributeValue("value"));


							}
							/*

									 boolean didOnce = false;


									for (Enumeration enumer = ((MessagesMutableTreeNode) lookUp
											.getMainnode().getRoot())
											.children(); enumer
											.hasMoreElements();) {
										MessagesMutableTreeNode temp = (MessagesMutableTreeNode) enumer
												.nextElement();

										if (!temp.isRoot()) {

											Document doc = ((Statement) temp
													.getUserObject())
													.getBindedDocument();
											if (didOnce == false) {
												lookUp
														.doTagAction(
																doc,
																finalElem
																		.attributeValue("value"),
																doc, true);
												didOnce = true;
											} else {
												lookUp
														.doTagAction(
																doc,
																finalElem
																		.attributeValue("value"),
																doc, false);

											}
										}
										}
							 */


						}
					};
					t.start();

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}

			});

		}

		final String sphereId = mp.getSphereId();

		MenuItem preferencesItem = new MenuItem(menu, SWT.PUSH);
		preferencesItem.setText(TabMouseListener.this.bundle
				.getString(PREFERENCES));
		preferencesItem.setImage(preferencesImage);
		preferencesItem.addSelectionListener(new PreferencesMenuListener(sphereId));

		MenuItem print = new MenuItem(menu, SWT.PUSH);
		print.setText(TabMouseListener.this.bundle.getString(PRINT));
		print.setImage(this.printImage);
		print.addSelectionListener(new PrintSelectionListener());

		this.close = new MenuItem(menu, SWT.PUSH);
		this.close.setText(TabMouseListener.this.bundle
				.getString(CLOSE));
		this.close.setImage(this.closeImage);
		addCloseMenuListener();



		// }

		/** *****test for chat ***** */
		// menu.add(chat);
		/** *****test for chat ***** */


		return menu;
	}

	/**
	 * @param event
	 */
	private Menu createRootPanePopupMenu(MouseEvent event) {
		Menu menu = new Menu(this.sF.getShell(), SWT.POP_UP);

		this.supraQuery = new MenuItem(menu, SWT.PUSH);
		this.supraQuery.setText(this.bundle.getString(SUPRA_SEARCH));
		this.supraQuery.setImage(this.supraSearchImage);
		this.supraQuery.addSelectionListener(new SupraSearchSelectionListener(this.sF));

		
		MenuItem setGlobalMark = new MenuItem(menu, SWT.PUSH);
		setGlobalMark.setText(this.bundle.getString(SET_GLOBAL_MARK));
		setGlobalMark.setImage(this.setGlobalImage);
		setGlobalMark.addSelectionListener(new SaveGlobalMarkSelectionListener(this.sF));
		/**
		 * ** Test for chat window *************************************
		 */
		/*
		 * JMenuItem chat = new JMenuItem("chat");
		 * chat.addMouseListener(new MouseListener() { public void
		 * mouseClicked(MouseEvent e) {} public void
		 * mouseReleased(MouseEvent e) {} public void
		 * mouseEntered(MouseEvent e) {} public void
		 * mouseExited(MouseEvent e) {} public void
		 * mousePressed(MouseEvent e) { ChatDialog chatwin = new
		 * ChatDialog(); chatwin.setSupraSphereFrame(sF);
		 * chatwin.addListeners(); chatwin.showDialog(); } });
		 */
		/**
		 * ****Test for chat end
		 * *****************************************
		 */

		this.saveWindow = new MenuItem(menu, SWT.PUSH);
		this.saveWindow.setText(TabMouseListener.this.bundle
				.getString(SAVE_WINDOW_POSITION));
		this.saveWindow.setImage(this.saveWindowImage);
		this.saveWindow
				.addSelectionListener(new SavePositionSelectionListener(this.sF));

		this.save = new MenuItem(menu, SWT.PUSH);
		this.save.setText(TabMouseListener.this.bundle
				.getString(SAVE_TAB_ORDER));
		this.save.setImage(this.saveImage);
		this.save.addSelectionListener(new SaveOrderSelectionListener(this.sF));

		MenuItem closeAll = new MenuItem(menu, SWT.PUSH);
		closeAll.setText(TabMouseListener.this.bundle
				.getString(CLOSE_ALL_TABS));
		closeAll.setImage(this.closeAllImage);
		closeAll.addSelectionListener(new CloseAllTabsSelectionListener(this.sF));
		
		
		MenuItem openBlankTab = new MenuItem(menu, SWT.PUSH);
		openBlankTab.setText(this.bundle.getString(OPEN_BLANK_TAB));
		openBlankTab.setImage(this.setGlobalImage);
		openBlankTab.addSelectionListener(new OpenBlankSelectionListener(this.sF));
		
		// for test purpose TODO remove in future
		// MenuItem openAllSpheres = new MenuItem(menu, SWT.PUSH);
		// openAllSpheres.setText( "Open all spheres" );
		// openAllSpheres.setImage(this.setGlobalImage);
		// openAllSpheres.addSelectionListener(new OpenAllSpheresSelectionListener());
		
		return menu;
	}
	
	private void rootComponentRightClicked(final MouseEvent event) {
		Menu menu = createRootPanePopupMenu(event);
		locateMenu(event, menu);
	}
	
	private void browserTabRightClicked(final MouseEvent event) {
		Menu menu = createBrowserTabPopUpMenu();
		locateMenu(event, menu);
	}

	private void emailTabRightClicked(final MouseEvent event) {
		Menu menu = createEmailTabPopUpMenu();
		locateMenu(event, menu);
	}

	private Menu createEmailTabPopUpMenu() {
		return createSingleCloseItemMenu();
	}

	/**
	 * @return
	 */
	private Menu createBrowserTabPopUpMenu() {
		return createSingleCloseItemMenu();
	}

	/**
	 * @return
	 */
	private Menu createSingleCloseItemMenu() {
		Menu menu = new Menu(this.sF.getShell(), SWT.POP_UP);

		MenuItem closeBrowser = new MenuItem(menu, SWT.PUSH);
		closeBrowser.setText(this.bundle.getString(CLOSE));
		closeBrowser.setImage(this.closeBrowserImage);
		closeBrowser.addSelectionListener(new CloseBrowserTabMenuMouseListener());
		return menu;
	}

	/**
	 * @param event
	 * @param menu
	 */
	private void locateMenu(final MouseEvent event, Menu menu) {
		org.eclipse.swt.graphics.Point p = ((CTabFolder) event.widget)
				.toDisplay(event.x, event.y);

		menu.setLocation(p);
		menu.setVisible(true);
	}

	public ResourceBundle getBundle() {
		return this.bundle;
	}
}