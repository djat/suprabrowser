/*
 * Created on May 8, 2004
 */
package ss.client.ui;

/**
 * @author david
 */

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.peoplelist.SphereMember;
import ss.client.ui.progressbar.DownloadProgressBar;
import ss.client.ui.tempComponents.MessagesPanePositionsInformation;
import ss.common.SearchCriteria;
import ss.common.UiUtils;
import ss.util.ImagesPaths;
import ss.util.NameTranslation;
import ss.util.SupraXMLConstants;

public class SearchInputWindow {

	@SuppressWarnings("unused")
	private static final Logger logger = ss.global.SSLogger
			.getLogger(SearchInputWindow.class);

	boolean selected = false;

	private Button radioUsed;

	private Button radioCreated;

	final Display display;

	Shell shell = null;

	SupraSphereFrame sF = null;

	MessagesPane mP = null;

	Image im = null;

	Combo combo = null;

	private Button search = null;

	Vector selectedSpheres = new Vector();

	Vector assetTypes = new Vector();

	Text keywords = null;

	Combo author = null;

	Combo critCombo = null;

	Combo scopeCombo = null;

	boolean isSupraQuery = false;

	Table table = null;

	private static Random tableIdGenerator = new Random();

	private boolean searchForSpecific = false;

	private Vector docsToMatch = new Vector();

	Hashtable session = null;

	private DocUtils docUtils = new DocUtils();

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_SEARCHINPUTWINDOW);

	private static final String SEARCH_WINDOW = "SEARCHINPUTWINDOW.SEARCH_WINDOW";

	private static final String KEYWORDS = "SEARCHINPUTWINDOW.KEYWORDS";

	private static final String DATE_RANGE = "SEARCHINPUTWINDOW.DATE_RANGE";

	private static final String NONE = "SEARCHINPUTWINDOW.NONE";

	private static final String HOUR_1 = "SEARCHINPUTWINDOW.HOUR_1";

	private static final String HOURS_2 = "SEARCHINPUTWINDOW.HOURS_2";

	private static final String HOURS_3 = "SEARCHINPUTWINDOW.HOURS_3";

	private static final String HOURS_4 = "SEARCHINPUTWINDOW.HOURS_4";

	private static final String HOURS_6 = "SEARCHINPUTWINDOW.HOURS_6";

	private static final String DAY_1 = "SEARCHINPUTWINDOW.DAY_1";

	private static final String DAYS_4 = "SEARCHINPUTWINDOW.DAYS_4";

	private static final String WEEK_1 = "SEARCHINPUTWINDOW.WEEK_1";

	private static final String WEEKS_2 = "SEARCHINPUTWINDOW.WEEKS_2";

	private static final String WEEKS_4 = "SEARCHINPUTWINDOW.WEEKS_4";

	private static final String WEEKS_6 = "SEARCHINPUTWINDOW.WEEKS_6";

	private static final String WEEKS_8 = "SEARCHINPUTWINDOW.WEEKS_8";

	private static final String ALL = "SEARCHINPUTWINDOW.ALL";

	private static final String SINCE_LAST_LAUNCHED = "SEARCHINPUTWINDOW.SINCE_LAST_LAUNCHED";

	private static final String SINCE_LOCAL_MARK = "SEARCHINPUTWINDOW.SINCE_LOCAL_MARK";

	private static final String SINCE_GLOBAL_MARK = "SEARCHINPUTWINDOW.SINCE_GLOBAL_MARK";

	private static final String CREATED = "SEARCHINPUTWINDOW.CREATED";

	private static final String USED = "SEARCHINPUTWINDOW.USED";

	private static final String AUTHOR = "SEARCHINPUTWINDOW.AUTHOR";

	private static final String TYPES = "SEARCHINPUTWINDOW.TYPES";

	private static final String CRITERIA = "SEARCHINPUTWINDOW.CRITERIA";

	private static final String MOST_USED_BY_AUTHOR = "SEARCHINPUTWINDOW.MOST_USED_BY_AUTHOR";

	private static final String LEAST_USED_BY_AUTHOR = "SEARCHINPUTWINDOW.LEAST_USED_BY_AUTHOR";

	private static final String SCOPE = "SEARCHINPUTWINDOW.SCOPE";

	private static final String EVERYTHING_IN_SELECTED_ONLY = "SEARCHINPUTWINDOW.EVERYTHING_IN_SELECTED_ONLY";

	private static final String EVERYONE_ELSE_BOOKMARKS_AND_FEEDS = "SEARCHINPUTWINDOW.EVERYONE_ELSE_BOOKMARKS_AND_FEEDS";

	private static final String MINE_AND_EVERYONE_BOOKMARKS_AND_FEEDS = "SEARCHINPUTWINDOW.MINE_AND_EVERYONE_BOOKMARKS_AND_FEEDS";

	private static final String SELECT_DESELECT_ALL = "SEARCHINPUTWINDOW.SELECT_DESELECT_ALL";

	private static final String SELECT_OPPOSITE = "SEARCHINPUTWINDOW.SELECT_OPPOSITE";

	private static final String SEARCH = "SEARCHINPUTWINDOW.SEARCH";

	private static final String CLOSE = "SEARCHINPUTWINDOW.CLOSE";

	private static final String SEARCHING_DOWNLOADBAR_TITLE = "SEARCHINPUTWINDOW.SEARCHING_DOWNLOADBAR_TITLE";

	@SuppressWarnings("unchecked")
	public SearchInputWindow(SupraSphereFrame sF, MessagesPane mP,
			Hashtable session, String lastSelectedTab, boolean isSupraQuery) {

		this.display = Display.getDefault();
		this.sF = sF;
		this.mP = mP;
		if (lastSelectedTab != null) {
			this.selectedSpheres.add(lastSelectedTab);
		}
		this.isSupraQuery = isSupraQuery;
	}

	public SearchInputWindow(SupraSphereFrame sF, MessagesPane mP,
			Hashtable session, Vector selectedSpheres, Vector assetTypes,
			boolean isSupraQuery) {

		this.display = Display.getDefault();
		this.sF = sF;
		this.mP = mP;
		this.selectedSpheres = selectedSpheres;
		this.isSupraQuery = isSupraQuery;
		this.assetTypes = assetTypes;
		this.session = session;

	}

	public synchronized long getNextTableId() {
		return Math.abs(tableIdGenerator.nextLong());
	}

	public void setKeywordFieldValue(final String value) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				SearchInputWindow.this.keywords.setText(value);
			}
		});
	}

	public void closeFromWithin() {
		logger.info("Closing from withing");
		Thread t = new Thread() {
			public void run() {
				SearchInputWindow.this.im.dispose();
				SearchInputWindow.this.shell.dispose();
			}
		};
		UiUtils.swtBeginInvoke(t);
	}

	public void forceActive() {
		Thread t = new Thread() {
			public void run() {
				SearchInputWindow.this.shell.forceActive();
				SearchInputWindow.this.shell.forceFocus();
			}
		};
		UiUtils.swtBeginInvoke(t);
	}

	public void setFocus() {
		Thread t = new Thread() {
			public void run() {
				SearchInputWindow.this.shell.setFocus();
			}
		};
		UiUtils.swtBeginInvoke(t);
	}

	public ResourceBundle getBundle() {
		return this.bundle;
	}

	public void callLayoutGUI() {
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				layoutGUI();
			}
		});

	}

	public void saveSheduleForm(final Document sphereDefinition) {
		if (Thread.currentThread() != Display.getDefault().getThread()) {
			Runnable r = new Runnable() {
				public void run() {
					sheduleForm(sphereDefinition);
				}
			};
			UiUtils.swtInvoke(r);
		} else {
			sheduleForm(sphereDefinition);
		}

	}

	@SuppressWarnings("unchecked")
	public void doAction() {
		if (this.isSupraQuery) {
			logger.info("Starting supraSearch");
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setSearchKeywords(this.keywords.getText());
			// sF.client.searchSupraSphere(main.getSession(), searchCriteria);
			closeFromWithin();
		} else {
			try {
				for (int i = 0; i < this.selectedSpheres.size(); i++) {
					final String sphereId = (String) this.selectedSpheres
							.get(i);
					processSearchForOneSphere(sphereId);
				}
				closeFromWithin();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
	private Hashtable checkMessPane(MessagesPane mes, SupraSphereFrame sphere) {
		return ( mes != null ) ? mes.getRawSession() : sphere.getMainRawSession();
	}

	@SuppressWarnings("unchecked")
	private void processSearchForOneSphere(final String sphereId) {
		final MessagesPane main = this.sF.getMessagePanesController()
				.findFirstMessagePaneBySphereId(sphereId);
		logger.warn("Sphere URL " + (String) this.session.get("sphereURL"));
		DialogsMainCli cli = this.sF.getActiveConnections()
				.getActiveConnection(((String) this.session.get("sphereURL")));
		String title = cli.getVerifyAuth().getDisplayName(sphereId);
		Document newDefinition = this.docUtils.getSphereDefinition(this, sphereId,
				main, title);

		String spheretype = cli.getVerifyAuth().getSphereType(sphereId);
		if (main != null) {
			main.getRawSession().put("sphere_type", spheretype);
		}

		Hashtable newSession = new Hashtable();

		if (main == null) {
		//	newSession = (Hashtable) this.sF.getMainRawSession().clone();
			newSession = (Hashtable) checkMessPane(this.mP, this.sF).clone();
			newSession.put("sphere_type", spheretype);
			newSession.put("sphere_id", sphereId);
			newSession.put("session", (String) (this.sF.getRegisteredSession(
					(String) newSession.get("supra_sphere"), "DialogsMainCli"))
					.get("session"));

		} else {
			newSession = (Hashtable) main.getRawSession().clone();
		}

		logger.warn("Starting connection again...");

		String loginName = this.sF.client.getVerifyAuth().getLoginForContact(
				title);
		if (loginName == null) {
			loginName = (String) this.session.get("username");
		}

		logger.warn(" login : " + loginName);

		try {
			MessagesPanePositionsInformation inf = main.calculateDivs();

			newSession.put("div0", new Double(inf.getDiv0()));
			newSession.put("div1", new Double(inf.getDiv1()));
			newSession.put("div2", new Double(inf.getDiv2()));
			newSession.put("div3", new Double(inf.getDiv3()));
			logger.info("div0 =" + inf.getDiv0() + " div1=" + inf.getDiv1()
					+ " div2=" + inf.getDiv2() + " div3=" + inf.getDiv3());

		} catch (NullPointerException ex) {
			logger.info("cannot calculates divs for this MessagesPane");
		}
		boolean started = false;

		if (this.session.get("externalConnection") == null) {

			Document contactDoc = this.sF.client.getContactFromLogin(this.sF
					.getMainRawSession(), loginName);

			String sphereURL = null;
			try {
				sphereURL = contactDoc.getRootElement().element("home_sphere")
						.attributeValue("value");
			} catch (NullPointerException npe) {

			}

			if (sphereURL != null) {

				if (!sphereURL.equals((String) this.sF.getMainRawSession().get(
						"sphereURL"))) {

					if (contactDoc.getRootElement().element("reciprocal_login") != null) {
						logger.warn("sphere url url: " + sphereURL);
						String reciprocalLogin = contactDoc.getRootElement()
								.element("reciprocal_login").attributeValue(
										"value");

						String cname = NameTranslation
								.createContactNameFromContactDoc(contactDoc);
						String system = cli.getVerifyAuth()
								.getSystemName(cname);
						newSession.put("localSphereId", system);
						newSession.put("externalConnection", "true");
						this.sF.startConnection(newDefinition, checkMessPane(this.mP, this.sF),
								sphereURL, reciprocalLogin, system);
						DownloadProgressBar dpb = new DownloadProgressBar(
								this.bundle
										.getString(SEARCHING_DOWNLOADBAR_TITLE));
						this.sF.client.setActiveProgressBar(dpb);
						started = true;

					}
				}
			}
		}

		if (!started) {
			DownloadProgressBar dpb = new DownloadProgressBar(this.bundle
					.getString(SEARCHING_DOWNLOADBAR_TITLE));

			this.sF.client.setActiveProgressBar(dpb);

			if (this.searchForSpecific == false) {
				logger.error("@@@@@@@@@@@@@N2");
				logger.error("session="+newSession);
				logger.error("newDefinition="+newDefinition);
				cli.searchSphere(newSession, newDefinition, "false");

			} else {				
				cli.searchForSpecificInIndex(newSession, newDefinition,
						this.docsToMatch);

			}

		}
	}

	public void runEventLoop() {

		UiUtils.swtInvoke(new Runnable() {
			SearchInputWindow siw = SearchInputWindow.this;

			public void run() {

				try {
					this.siw.shell.layout();
					Rectangle parentBounds = this.siw.sF.getShell().getBounds();

					Rectangle childBounds = this.siw.shell.getBounds();

					int x = parentBounds.x
							+ (parentBounds.width - childBounds.width) / 2;

					int y = parentBounds.y
							+ (parentBounds.height - childBounds.height) / 2;

					this.siw.shell.setLocation(x, y);
					this.siw.shell.setVisible(true);
					this.siw.keywords.setFocus();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				// shell.open();
				while (!this.siw.shell.isDisposed()) {
					if (!this.siw.display.readAndDispatch()) {
						this.siw.display.sleep();
					}
				}
			}
		});

	}

	/**
	 * @param b
	 */
	public void setSearchForSpecific(boolean b) {
		this.searchForSpecific = b;

	}

	/**
	 * @param docsToMatch
	 */
	public void setMatchDocs(Vector docsToMatch) {
		this.docsToMatch = docsToMatch;

	}

	public void layoutUIAndSetFocus() {
		this.callLayoutGUI();
		this.setFocus();
		// this.forceActive();
		this.runEventLoop();
	}

	/**
	 * 
	 */
	private void layoutGUI() {
		this.shell = new Shell(this.display, SWT.TITLE | SWT.RESIZE
				| SWT.BORDER);

//		String fsep = System.getProperty("file.separator");
//		String bdir = System.getProperty("user.dir");

		try {
			this.im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			this.shell.setImage(this.im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
		this.shell.setText(this.getBundle().getString(SEARCH_WINDOW));

		GridLayout gl = new GridLayout();
		gl.numColumns = 2;

		SearchInputWindow.this.shell.setLayout(gl);

		Label label = new Label(this.shell, SWT.NONE);
		label.setText(this.getBundle().getString(KEYWORDS));

		this.keywords = new Text(this.shell, SWT.BORDER);

		GridData kD = new GridData();
		kD.grabExcessHorizontalSpace = true;
		kD.horizontalAlignment = GridData.FILL;

		this.keywords.setLayoutData(kD);

		Label range = new Label(this.shell, SWT.NONE);
		range.setText(this.getBundle().getString(DATE_RANGE));

		this.combo = new Combo(this.shell, SWT.DROP_DOWN);

		GridData cD = new GridData();
		cD.grabExcessHorizontalSpace = true;
		cD.horizontalAlignment = GridData.FILL;
		this.combo.setLayoutData(cD);

		this.combo.add(this.getBundle().getString(NONE), 0);
		this.combo.add(this.getBundle().getString(HOUR_1), 1);
		this.combo.add(this.getBundle().getString(HOURS_2), 2);
		this.combo.add(this.getBundle().getString(HOURS_3), 3);
		this.combo.add(this.getBundle().getString(HOURS_4), 4);
		this.combo.add(this.getBundle().getString(HOURS_6), 5);
		this.combo.add(this.getBundle().getString(DAY_1), 6);
		this.combo.add(this.getBundle().getString(DAYS_4), 7);
		this.combo.add(this.getBundle().getString(WEEK_1), 8);
		this.combo.add(this.getBundle().getString(WEEKS_2), 9);
		this.combo.add(this.getBundle().getString(WEEKS_4), 10);
		this.combo.add(this.getBundle().getString(WEEKS_6), 11);
		this.combo.add(this.getBundle().getString(WEEKS_8), 12);
		this.combo.add(this.getBundle().getString(ALL), 13);
		this.combo.add(this.getBundle().getString(SINCE_LAST_LAUNCHED), 14);
		this.combo.add(this.getBundle().getString(SINCE_LOCAL_MARK), 15);
		this.combo.add(this.getBundle().getString(SINCE_GLOBAL_MARK), 16);

		// combo.select(11);
		this.combo.select(7);

		this.combo.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent evt) {

				if (evt.character == SWT.CR) {

					doAction();

				}
			}
		});

		this.radioUsed = new Button(this.shell, SWT.RADIO);
		this.radioCreated = new Button(this.shell, SWT.RADIO);

		this.radioCreated.setText(this.getBundle().getString(CREATED));
		this.radioCreated.setSelection(true);

		this.radioUsed.setText(this.getBundle().getString(USED));

		Label authorLabel = new Label(this.shell, SWT.NONE);
		authorLabel.setText(this.getBundle().getString(AUTHOR));

		this.author = new Combo(this.shell, SWT.DROP_DOWN);

		GridData aD = new GridData();
		aD.grabExcessHorizontalSpace = true;
		aD.horizontalAlignment = GridData.FILL;
		this.author.setLayoutData(aD);

		try {
			for (String memberName : ((this.mP!=null) ? this.mP.getMembers() :
						this.sF.getRootTab().getPeopleTable().getMembers())) {
				this.author.add(memberName);
			}
		} catch (NullPointerException npe) {
			logger.error( "Error getting members" );
		}
		Label types = new Label(this.shell, SWT.NONE);
		types.setText(this.bundle.getString(TYPES));

		GridData typeData = new GridData();
		typeData.verticalAlignment = GridData.FILL;
		typeData.horizontalAlignment = GridData.FILL;

		typeData.horizontalSpan = 2;
		typeData.grabExcessHorizontalSpace = false;
		typeData.grabExcessVerticalSpace = false;
		types.setLayoutData(typeData);

		this.table = new Table(this.shell, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);

		this.table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				TableItem item = (TableItem) event.item;
				if (event.detail != SWT.CHECK) {

					if (item.getChecked()) {

						item.setChecked(false);

					} else {
						item.setChecked(true);

					}

				} else {

					int index = SearchInputWindow.this.table
							.getSelectionIndex();

					//if (index > 0) {
						TableItem selected = SearchInputWindow.this.table
								.getItem(index);

						if (selected == item) {
							item.setChecked(!item.getChecked());

						}

					//}
					// item.setChecked(false);
					// Point pt = new Point (event.x, event.y);

				}

			}

			// }
		});

		GridData tD = new GridData();
		tD.grabExcessHorizontalSpace = true;
		tD.horizontalAlignment = GridData.FILL;
		tD.horizontalSpan = 2;
		tD.grabExcessVerticalSpace = true;

		this.table.setLayoutData(tD);

		for (int i = 0; i < this.assetTypes.size(); i++) {

			TableItem item = new TableItem(this.table, SWT.NONE);

			item.setText((String) this.assetTypes.get(i));

			// if (!(item.getText().toLowerCase()).equals("keywords")) {

			item.setChecked(true);
			// }
		}
		Label criteria = new Label(this.shell, SWT.NONE);
		criteria.setText(this.bundle.getString(CRITERIA));

		this.critCombo = new Combo(this.shell, SWT.DROP_DOWN);

		GridData crit = new GridData();
		crit.grabExcessHorizontalSpace = true;
		crit.horizontalAlignment = GridData.FILL;
		this.critCombo.setLayoutData(crit);

		this.critCombo.add(this.bundle.getString(MOST_USED_BY_AUTHOR), 0);
		this.critCombo.add(this.bundle.getString(LEAST_USED_BY_AUTHOR), 1);

		Label scope = new Label(this.shell, SWT.NONE);

		scope.setText(this.bundle.getString(SCOPE));

		this.scopeCombo = new Combo(this.shell, SWT.DROP_DOWN);

		GridData scop = new GridData();
		scop.grabExcessHorizontalSpace = true;
		scop.horizontalAlignment = GridData.FILL;
		this.scopeCombo.setLayoutData(scop);

		this.scopeCombo.add(getBundle().getString(EVERYTHING_IN_SELECTED_ONLY),
				0);
		this.scopeCombo.add(getBundle().getString(
				EVERYONE_ELSE_BOOKMARKS_AND_FEEDS), 1);
		this.scopeCombo.add(this.getBundle().getString(
				MINE_AND_EVERYONE_BOOKMARKS_AND_FEEDS), 2);
		this.scopeCombo.select(0);

		Button selectDeselect = new Button(this.shell, SWT.PUSH);
		selectDeselect.setText(this.getBundle().getString(SELECT_DESELECT_ALL));

		GridData selData = new GridData();
		selData.verticalAlignment = GridData.FILL;
		selData.horizontalAlignment = GridData.FILL;

		selData.grabExcessVerticalSpace = false;
		selData.grabExcessHorizontalSpace = false;

		selectDeselect.setLayoutData(selData);

		selectDeselect.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				TableItem[] tableItems = SearchInputWindow.this.table
						.getItems();

				for (int i = 0; i < tableItems.length; i++) {

					TableItem item = tableItems[i];

					if (SearchInputWindow.this.selected == false) {

						item.setChecked(false);

					} else {

						item.setChecked(true);

					}
				}
				SearchInputWindow.this.selected = !SearchInputWindow.this.selected;
			}
		});

		Button selectOpposite = new Button(this.shell, SWT.PUSH);
		selectOpposite.setText(this.getBundle().getString(SELECT_OPPOSITE));

		GridData oppData = new GridData();
		oppData.horizontalAlignment = 1;

		selectOpposite.setLayoutData(oppData);

		selectOpposite.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem[] tableItems = SearchInputWindow.this.table
						.getItems();

				for (int i = 0; i < tableItems.length; i++) {

					TableItem item = tableItems[i];

					if (item.getChecked()) {
						item.setChecked(false);

					} else {
						item.setChecked(true);
					}

				}

			}

		});

		Composite bar = new Composite(this.shell, SWT.NONE);
		bar.setLayout(new GridLayout(2, false));

		this.search = new Button(bar, SWT.PUSH);
		this.search.setText(this.getBundle().getString(SEARCH));

		this.search.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				doAction();
			}
		});

		Button titem1 = new Button(bar, SWT.PUSH);

		titem1.setText(this.getBundle().getString(CLOSE));

		titem1.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				closeFromWithin();
			}
		});

		this.keywords.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent evt) {

				if (evt.character == SWT.CR) {

					doAction();

				}
			}
		});

		logger.info("got here");
		this.shell.pack();
		// shell.setSize(400,450);

		this.shell.layout();
		// shell.setVisible(true);
	}

	private void sheduleForm(final Document sphereDefinition) {
		try {
			String keys = sphereDefinition.getRootElement().element("search")
					.element("keywords").attributeValue("value");
			SearchInputWindow.this.keywords.setText(keys);

		} catch (NullPointerException npe) {
		}
		try {
			String expText = sphereDefinition.getRootElement().element(
					"expiration").attributeValue("value");
			String[] items = SearchInputWindow.this.combo.getItems();
			for (int i = 0; i < items.length; i++) {

				String item = items[i];

				if (item.toLowerCase().indexOf(expText) != -1) {

					SearchInputWindow.this.combo.select(i);
				}
			}
		} catch (NullPointerException npe) {
		}
		TableItem[] tableItems = SearchInputWindow.this.table.getItems();
		for (int i = 0; i < tableItems.length; i++) {
			TableItem item = tableItems[i];
			String text = item.getText();
			text = text.toLowerCase();
			String xPath = "//sphere/thread_types/" + text
					+ "[@enabled='true']";

			logger.info("xpath : " + xPath);
			Element isEnabled = null;
			try {
				isEnabled = (Element) sphereDefinition.selectObject(xPath);
			} catch (ClassCastException cce) {
			}
			if (isEnabled == null) {
				item.setChecked(false);
			}
		}
	}

	private class DocUtils {

		/**
		 * @param window
		 *            TODO
		 * @param sphereId
		 * @param main
		 * @param title
		 * @return
		 */
		Document getSphereDefinition(SearchInputWindow window,
				final String sphereId, MessagesPane main, String title) {
			Document newDefinition = null;
			if (main != null) {
				if (main.getSphereDefinition() != null) {
					newDefinition = (Document) (main.getSphereDefinition())
							.clone();
				}
			}
			if (newDefinition == null) {
				newDefinition = createSphereDefinition(sphereId, title);
			}
			newDefinition = editSphereDefinition(window, newDefinition);
			detachElement(newDefinition, "paging");
			detachElementFromElement(newDefinition, "message_pane_id", "search");
			return newDefinition;
		}

		/**
		 * @param window
		 *            TODO
		 * @param newDefinition
		 * @return
		 */
		Document editSphereDefinition(SearchInputWindow window,
				Document newDefinition) {
			
			detachElement(newDefinition, "isSupraQuery");
			detachElement(newDefinition, "query");
			detachElement(newDefinition, "scope");
			detachElement(newDefinition, "criteria");
			Element types = detachElement(newDefinition, "thread_types");
			
			newDefinition.getRootElement().addElement("isSupraQuery")
					.addAttribute("value", "false");			
			newDefinition.getRootElement().addElement("query").addAttribute(
					"value", "true").addAttribute("query_id",
					Long.toString(window.getNextTableId()));			 
				
			Element newType = newDefinition.getRootElement().addElement(
					"thread_types");
			// Iterate through and add a new type element for
			// each type that has a checkbox
			final TableItem[] selection = window.table.getItems();
			for (int j = 0; j < selection.length; j++) {
				if (selection[j].getChecked()) {
					String type = selection[j].getText();
					type = type.toLowerCase();
					newType.addElement(type).addAttribute("enabled", "true")
							.addAttribute("modify", "own");
				}
			}
			if (types == null) {
				if (window.assetTypes.size() == 0) {
					window.docUtils.addAssetsTypes(newType);
				}
			}
			
			int selectedScope = window.scopeCombo.getSelectionIndex();

			SearchInputWindow.logger.info("Selected scope...." + selectedScope);
			if (selectedScope != -1) {
				SearchInputWindow.logger
						.info("Adding this scope to the definition: "
								+ (String) window.scopeCombo
										.getItem(selectedScope));

				newDefinition.getRootElement().addElement("scope")
						.addAttribute(
								"value",
								(String) window.scopeCombo
										.getItem(selectedScope));
			}
			
			int selected = window.critCombo.getSelectionIndex();
			SearchInputWindow.logger.info("Selected index...." + selected);
			if (selected != -1) {
				SearchInputWindow.logger
						.info("Adding this criteria to the definition: "
								+ (String) window.critCombo.getItem(selected));

				newDefinition.getRootElement().addElement("criteria")
						.addAttribute("value",
								(String) window.critCombo.getItem(selected));

			}

			Element testSearch = newDefinition.getRootElement().element(
					"search");
			if (testSearch != null) {
				Element testKeywords = testSearch.element("keywords");
				SearchInputWindow.logger.info("Removing keywords");
				if (testKeywords != null) {
					testSearch.remove(testKeywords);
				}
				Element testAuthor = testSearch.element("author");
				if (testAuthor != null) {
					testSearch.remove(testAuthor);
				}
			} else {
				testSearch = newDefinition.getRootElement()
						.addElement("search");
			}

			String keys = window.keywords.getText();
			if (keys.length() > 0) {
				testSearch.addElement("keywords").addAttribute("value", keys);
			}

			String authorName = window.author.getText();
			if (authorName.length() > 0) {
				testSearch.addElement("author").addAttribute("value",
						SphereMember.normalizeName(authorName));
			}

			String text = window.combo.getText();
			Element expiration = newDefinition.getRootElement().element(
					"expiration");
			if (text.equals(window.bundle.getString(SearchInputWindow.NONE))) {
				expiration.addAttribute("value", "none");
			} else if (text.equals(window.bundle
					.getString(SearchInputWindow.SINCE_LAST_LAUNCHED))) {
				expiration.addAttribute("value", "New since last launched");
			} else if (text.equals(window.bundle
					.getString(SearchInputWindow.SINCE_LOCAL_MARK))) {
				expiration.addAttribute("value", "New since local mark");
			} else if (text.equals(window.bundle
					.getString(SearchInputWindow.SINCE_GLOBAL_MARK))) {
				expiration.addAttribute("value", "New since global mark");
			} else if (text.equals(window.bundle
					.getString(SearchInputWindow.ALL))) {
				expiration.addAttribute("value", "all");
			} else {// if (one.equals("One hour")) {
				expiration.addAttribute("value", text);
			}

			Element crit = newDefinition.getRootElement().element(
					"date_criteria");
			if (crit != null) {
				newDefinition.getRootElement().remove(crit);
			}
			newDefinition.getRootElement().addElement("date_criteria")
					.addAttribute(
							"value",
							window.radioUsed.getSelection() ? "used"
									: "created");

			return newDefinition;
		}

		private Element detachElement(Document newDefinition, String elementName) {
			Element element = newDefinition.getRootElement().element(
					elementName);
			if (element != null) {
				element.detach();
			}
			return element;
		}
		private Element detachElementFromElement(Document newDefinition, String elementName,String parentElementName) {
			Element parentElement = newDefinition.getRootElement().element(
					parentElementName);
			if (parentElement == null) {
				return null;
			}
			Element element = parentElement.element(
					elementName);
			if (element != null) {
				element.detach();
			}
			return element;
		}

		/**
		 * @param newType
		 */
		void addAssetsTypes(Element newType) {
			if (newType.getName().equals("thread_types")) {
				newType.addElement("terse").addAttribute("modify", "own");
				newType.addElement("message").addAttribute("modify", "own");
				newType.addElement(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL)
						.addAttribute("modify", "own");
				newType.addElement("bookmark").addAttribute("modify", "own");
				// newType.addElement("file").addAttribute("modify","own");
				newType.addElement("contact").addAttribute("modify", "own");
				newType.addElement("rss").addAttribute("modify", "own");
				newType.addElement("keywords").addAttribute("modify", "own");
				newType.addElement("filesystem").addAttribute("modify", "own");
				newType.addElement("sphere").addAttribute("modify", "own");
			}
		}

		/**
		 * @param sphereId
		 * @param title
		 * @return
		 */
		Document createSphereDefinition(final String sphereId, String title) {
			Document newDefinition;
			Document createDoc = DocumentHelper.createDocument();
			Element root = createDoc.addElement("sphere").addAttribute(
					"display_name", title)
					.addAttribute("system_name", sphereId);
			root.addElement("expiration").addAttribute("value", "");
			root.addElement("default_delivery").addAttribute("value",
					"confirm_receipt");
			root.addElement("default_type").addAttribute("value", "terse");
			root.addElement("voting_model").addAttribute("type", "absolute")
					.addAttribute("desc", "Absolute without qualification");
			newDefinition = (Document) createDoc.clone();
			return newDefinition;
		}

	}

}
