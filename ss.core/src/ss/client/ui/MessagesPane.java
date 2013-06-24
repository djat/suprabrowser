package ss.client.ui;

/*
 * MessagesPane is both the main interface to suprasphere, and also the message center for messages that are passed through the system. It's kind of the "glue"
 * that holds the pieces together, so that, for example, certain other classes can get access to the networking by calling something like: mP.client.method The
 * variable "mP" is therefore present in almost all of the compontents of suprasphere, as a container for variables and as a way to process events in the
 * interface. For example, the client through DialogsMainCli.java waits for a message update, and then will call "mP.insertUpdate(Document doc, boolean sort)"
 * to insert a message into the interface.
 */

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import ss.client.event.InputTextPaneListener;
import ss.client.event.executors.StatementExecutorFactory;
import ss.client.event.tagging.TagManager;
import ss.client.networking.DialogsMainCli;
import ss.client.presence.ClientPresence;
import ss.client.presence.ClientPresenceManager;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.ControlPanelDocking;
import ss.client.ui.docking.MessagesTreeDocking;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.client.ui.docking.SupraTableDocking;
import ss.client.ui.docking.positioner.DockingPositioner;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.client.ui.peoplelist.IPeopleList;
import ss.client.ui.peoplelist.SphereMember;
import ss.client.ui.processing.TagActionProcessor;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.tempComponents.CompositeMessagesPane;
import ss.client.ui.tempComponents.MessagesPanePositionsInformation;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tree.MessagesTree;
import ss.client.ui.tree.RemoveThenInsertOperation;
import ss.client.ui.tree.ThreadSystemMessagesController;
import ss.client.ui.viewers.comment.CommentApplicationWindow;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.UnexpectedRuntimeException;
import ss.common.UserSession;
import ss.common.XmlDocumentUtils;
import ss.common.debug.DebugUtils;
import ss.common.operations.IMessagesTable;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.SystemMessageStatement;
import ss.domainmodel.TerseStatement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.print.DocumentRenderer;
import ss.util.SessionConstants;
import ss.util.StringProcessor;
import ss.util.VariousUtils;
import ss.util.VotingEngine;

/**
 * Description of the Class
 * 
 * @author david
 * @created September 19, 2003
 */
public class MessagesPane extends CompositeMessagesPane implements ISphereView {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesPane.class);

	private static final int WAIT_UNTIL_LAYOUT_GUI_SLEEP_TIME = 200;

	private static final int WAIT_UNTIL_LAYOUT_GUI_TIMEOUT = 60000;

	private static final List<String> EMPTY_LIST = new Vector<String>();

	private IMessagesTable messagesTable = new MessagesTable();

	private ClientPresence clientPresence = null;

	private InputTextPaneListener itpl = null;

	private Hashtable<String, Statement> allMessages = new Hashtable<String, Statement>();

	private Hashtable<String, String> threadIds = new Hashtable<String, String>();

	// private static SOptionPane so = null; // Popup window on Windows

	private static Random tableIdGenerator = new Random();

	private VotingEngine votingEngine = new VotingEngine();

	// private JPanel treePanel; // Holds the JTree, threaded

	// view

	// in the upper-right window of
	// the JSplitPane
	private MessagesTree messagesTree; // Extends JTree primarily for mouse

	private Document lastSelectedDoc = null;

	private Statement currentStatement = null;

	private int unseenNumber = 0;

	private int replyNumber = 0;

	// table

	// it

	// keeps track of its position so that when a
	// new message is added, it can move the
	// selection interval so that the users place
	// is not lost

	// confirm_receipt or normal, only used in the first "main" MessagesPane
	private Document sphereDefinition = null; // Base definition of a sphere,

	private Document createDefinition = null; // The "create_sphere" is used

	// public static Vector<Document> popups = new Vector<Document>(); // Most
	// of

	// the
	// critical
	// methods

	// to

	// create a popup synchronize on this
	// both to add a new popup object
	// (Hashtable), and to remove them
	// afterwards
	private final VerbosedSession verbosedSession = new VerbosedSession();

	private final String fsep = System.getProperty("file.separator");

	private final String bdir = System.getProperty("user.dir");

	// thread gets set by searching through the thread
	// and finding the message that does not have a
	// response_id element

	// public static Hashtable balloons = new Hashtable();

	public volatile DialogsMainCli client = null;

	// class

	// that shows the initial login window, gets
	// disposed when the inital loading is done
	public SupraSphereFrame sF = null; // Main frame that holds the

	// private org.eclipse.swt.graphics.Point current_popup = null; // Accessed

	// removed from a view
	// so that when those
	// exceptions and
	// positive hits are
	// recorded it will know
	// how to get to them

	private String uniqueId;

	private PreviewHtmlTextCreator preview_html_text = null;

	private boolean isInsertable = true;

	private boolean isScrollLocked = false;

	private boolean isThreadView = false;

	private String currentThread;

	private CommentStatement viewComment;

	private boolean needOpenComment = true;

	private boolean activateP2PLater = false;

	private List<Statement> tableStatements = new ArrayList<Statement>();

	private ArrayList<Statement> systemMessages = new ArrayList<Statement>();

	private String cashedMessage_id;

	private final DockingPositioner positioner;

	private SupraCTabItem tabItem = null;

	private boolean searchSphere = false;

	private ControlLocker locker = new ControlLocker();

	private volatile boolean unlocked = false;
	
	private volatile boolean deployed = false;
	
	private ThreadSystemMessagesController systemMessagesController = null;

	/**
	 * Constructor for the MessagesPane object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param mainFrame
	 *            Description of the Parameter
	 * @param supraFrame
	 *            Description of the Parameter
	 * @param sF.tabbedPane
	 *            Description of the Parameter
	 */
	public MessagesPane(Hashtable session, SupraSphereFrame supraFrame,
			String type, DialogsMainCli cli) {
		super(supraFrame.tabbedPane);
		this.positioner = new DockingPositioner(this);
		constructMessagesPane(session, supraFrame, cli);
	}

	public MessagesPane(Hashtable session, SupraSphereFrame supraFrame,
			String type, DialogsMainCli cli, Document sphereDefinition) {
		super(supraFrame.tabbedPane);
		this.positioner = new DockingPositioner(this);
		checkQueryAndSetSphereDefinition(sphereDefinition);
		constructMessagesPane(session, supraFrame, cli);
	}

	public MessagesPane(Hashtable session, SupraSphereFrame supraFrame,
			String type, DialogsMainCli cli, double div0, double div1,
			double div2, double div3) {
		super(supraFrame.tabbedPane);
		this.positioner = new DockingPositioner(this, div0, div1, div2, div3);
		this.positioner.setSavedPositions(true);
		constructMessagesPane(session, supraFrame, cli);
	}

	public MessagesPane(Hashtable session, SupraSphereFrame supraFrame,
			String type, DialogsMainCli cli, Document sphereDefinition,
			double div0, double div1, double div2, double div3) {
		super(supraFrame.tabbedPane);
		this.positioner = new DockingPositioner(this, div0, div1, div2, div3);
		this.positioner.setSavedPositions(true);
		checkQueryAndSetSphereDefinition(sphereDefinition);
		constructMessagesPane(session, supraFrame, cli);
	}

	/**
	 * @param session
	 * @param mainFrame
	 * @param supraFrame
	 * @param cli
	 */
	private void constructMessagesPane(Hashtable session,
			SupraSphereFrame supraFrame, DialogsMainCli cli) {
		this.setBackground(new org.eclipse.swt.graphics.Color(Display
				.getDefault(), 255, 255, 255));
		this.client = cli;
		this.setSession(session);
		this.sF = supraFrame;
		this.client = cli;

		createAndSetUnique();

		this.itpl = new InputTextPaneListener(this.sF, this);
		ThreadUtils.start(new Runnable() {
			public void run() {
				createContent();
			}
		}, "MessagesPaneCreator");
	}

	/**
	 * Constructor for the MessagesPane object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param filter_doc
	 *            Description of the Parameter
	 * @param highlight_type
	 *            Description of the Parameter
	 * @param sF.tabbedPane
	 *            Description of the Parameter
	 */
	/**
	 * Description of the Method
	 */

	public InputTextPaneListener getInputListener() {

		return this.itpl;

	}

	@SuppressWarnings("unchecked")
	private void createAndSetUnique() {
		String uniqueId = new Long(VariousUtils.getNextUniqueId()).toString();
		this.setUniqueId(uniqueId);
		this.getRawSession().put("unique_id", uniqueId);
	}

	/**
	 * Sets the defaultDelivery attribute of the MessagesPane object
	 */
	public void setReplyChecked(boolean value) {
		if (!(getControlPanel() instanceof ControlPanel)) {
			return;
		}
		final ControlPanel controlPanel = (ControlPanel) getControlPanel();
		controlPanel.setReplyChecked(value);
		getControlPanelDocking().setFocusToTextField();
	}

	public PreviewAreaDocking getPreviewAreaDocking() {
		return this.positioner.getPreviewDocking();
	}

	public AbstractDelivery getDefaultDelivery() {
		return this.sF.getDefaultDelivery(this.getRawSession());
	}

	public boolean isSupraQuery(Document parentDoc) {
		String parentSphere = parentDoc.getRootElement().element(
				"current_sphere").attributeValue("value");
		Object thisSphere = this.getRawSession().get("sphere_id");
		return !parentSphere.equals(thisSphere);
	}

	public void doTagAction(Document parentDoc, String tagText) {
		TagActionProcessor tagDocs = new TagActionProcessor(SupraSphereFrame.INSTANCE.client, getSystemName(), parentDoc);
		tagDocs.doTagAction(tagText);
		
		setTagChecked(false);
	}

	/**
	 * @param value
	 */
	public void setTagChecked(final boolean value) {
		if (getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) getControlPanel();
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					controlPanel.setTagChecked(value);
					getControlPanelDocking().setFocusToTextField();
				}
			});
		}
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUniqueId() {
		return this.uniqueId;
	}

	public void addToThreadIds(String threadId, String uniqueId) {
		synchronized (this.threadIds) {
			this.threadIds.put(threadId, uniqueId);
		}

	}

	public boolean checkThreadIds(String threadId) {
		synchronized (this.threadIds) {
			if (this.threadIds != null && threadId != null) {
				if (this.threadIds.containsKey(threadId)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

	}

	public int getUnseenNumber() {
		return this.unseenNumber;
	}

	public void setUnseenNumber(int newValue) {
		this.unseenNumber = newValue;
	}

	public int getReplyNumber() {
		return this.replyNumber;
	}

	public void setReplyNumber(int newValue) {
		this.replyNumber = newValue;
	}

	@SuppressWarnings("unchecked")
	public void setMembers(final Vector available) {
		if (!isUiCreated()) {
			throw new UnexpectedRuntimeException(
					"Cannot set members because UI not ready.");
		}
		logger.info("setting new avaliable members");
		if (getPeopleTable() != null) {
			getPeopleTable().setMembers(available);
		}
	}

	/**
	 * @param presenceInfo
	 */
	public void beginSetMembers(final Vector available) {
		ThreadUtils.startDemon(new Runnable() {
			public void run() {
				waitUntilLayoutGui();
				setMembers(available);
			}
		}, MessagesPane.class);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */

	public TerseStatement returnTerseStatement(String subject) {
		TerseStatement statement = new TerseStatement();
		statement.setGiver((String) this.getRawSession().get(
				SessionConstants.REAL_NAME));
		statement.setGiverUsername((String) this.getRawSession().get(
				SessionConstants.USERNAME));
		if (subject == null) {
			// try {
			// statement.setSubject(this.sendField.getText());
			// } catch (NullPointerException npe) {
			// statement.setSubject(this.bigArea.getText());
			// }
		} else {
			statement.setSubject(subject);
		}

		statement.setMessageId(VariousUtils.createMessageId());
		statement.setOriginalId(statement.getMessageId());
		statement.setThreadId(statement.getMessageId());

		statement.setLastUpdatedBy((String) this.getRawSession().get(
				SessionConstants.REAL_NAME));
		statement.setType("terse");

		if (this.isReplyChecked()) {
			try {
				Statement lastSelected = Statement.wrap(this.lastSelectedDoc);
				statement.setThreadType(lastSelected.getThreadType());
				statement.setThreadId(lastSelected.getThreadId());
				statement.setResponseId(lastSelected.getMessageId());

				if (lastSelected.isReply()) {
					CommentStatement comment = CommentStatement
							.wrap(lastSelected.getBindedDocument());
					statement.setBody(comment.getComment());
					statement.setOrigBody(comment.getComment());
				} else {
					statement.setBody(lastSelected.getBody());
					statement.setOrigBody(lastSelected.getOrigBody());
				}
				if (statement.getOrigBody() == null) {
					statement.setOrigBody("");
				}
				if (statement.getBody() == null) {
					statement.setBody("");
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		} else {
			statement.setThreadType("terse");
		}

		statement.setVotingModelType("absolute");
		statement.setVotingModelDesc("Absolute without qualification");
		statement.setTallyNumber("0.0");
		statement.setTallyValue("0.0");

		return statement;
	}

	public UserSession getUserSession() {
		return new UserSession(this.client.getVerifyAuth(), this
				.getRawSession());
	}

	public String getSphereId() {
		return (String) getRawSession().get(SessionConstants.SPHERE_ID2);
	}

	public String getSystemName() {
		return getSphereStatement().getSystemName();
	}

	/**
	 * Description of the Method
	 * 
	 * @param sphere_name
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public void checkQueryAndSetSphereDefinition(Document sphereDefinition) {

		if (sphereDefinition != null) {

			Element query = sphereDefinition.getRootElement().element("query");
			if (query != null) {
				logger.info("Setting sphere definition: query not null");
			} else {

				logger.info("Setting sphere definition: query null");
			}

			this.sphereDefinition = sphereDefinition;
		}
	}

	public void setCreateDefinition(Document createDefinition) {
		this.createDefinition = createDefinition;
	}

	public Document getCreateDefinition() {
		if (this.createDefinition == null) {
			return null;
		} else {
			return this.createDefinition;
		}
	}

	public void refreshContactPresence(String contactName, boolean isOnline) {
		final IPeopleList peopleTable = getPeopleTable();
		if (peopleTable == null) {
			logger.error("People table is null. Can't refresh contact name "
					+ contactName + ", state " + isOnline);
			return;
		}
		peopleTable.refreshMemberPresence(contactName, isOnline);
	}

	/**
	 * Description of the Method
	 */
	private void createContent() {

		try {
			while (this.client == null) { // Hack related to synchronization
				// of GUI and network startup
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// NOOP
		}
		try {
			// this.messagesTree = new MessagesTree(this.client.getVerifyAuth()
			// .getUserSession(), MessagesPane.this);
			// this.messagesTree.setUpInitialProperties();
		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		}

		// MessagesTreeMouseListener messagesTreeMouseListener = new
		// MessagesTreeMouseListener(
		// this);
		// this.messagesTree.setMouseListener(messagesTreeMouseListener);
		// this.messagesTree.addKeyListener(new
		// MessagesTreeMouseListener(this));

		// if (this.isBigListening == false) {
		// addBigAreaListener();
		// this.isBigListening = true;
		// }

		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				MessagesPane.this.positioner.deploy(getMessagesTree());
				logger.info("GUI is created");
			}
		});
		repaint();
	}

	public void updatePresenceRenderer(final Document doc) {
		if (doc == null)
			return;
		final Statement statement = Statement.wrap(doc);

		Thread t = new Thread() {

			private MessagesPane mPane = MessagesPane.this;

			public void run() {

				while (true) {
					if (isUiCreated() == true
							&& this.mPane.getPeopleTable() != null) {
						if (getLastSelectedDoc() != null) {
							Document lastSelected = getLastSelectedDoc();
							if (logger.isDebugEnabled()) {
								logger.debug("Last selected Doc: "
										+ lastSelected.asXML());
							}
							if (statement.getMessageId()
									.equals(
											Statement.wrap(lastSelected)
													.getMessageId())) {
								if (logger.isDebugEnabled()) {
									logger
											.debug("Last selected Doc is current doc"
													+ doc.asXML());
								}
								MessagesPane.this.votingEngine
										.setLastSelected(doc);
								getPeopleTable().update(true);
								repaintAll();
							}
						} else {
							logger.info("Different....");
							MessagesPane.this.votingEngine.setLastSelected(doc);
							getPeopleTable().update(true);
							repaintAll();
						}
						break;
					} else {
						try {
							sleep(1000);
						} catch (InterruptedException ie) {
							logger.error("", ie);
						}
					}
				}
			}
		};
		t.start();
	}

	/**
	 * Description of the Method
	 * 
	 * @param contact_name
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	@SuppressWarnings("unchecked")
	public boolean hasVoted(String contact_name, Document doc) {
		final String normalizedContactName = SphereMember
				.normalizeName(contact_name);

		boolean has_voted = false;
		try {
			if (doc != null) {
				Element view = doc.getRootElement();
				try {
					Vector mem = new Vector(view.element("voting_model")
							.element("tally").elements());

					for (int i = 0; i < mem.size(); i++) {
						Element one = (Element) mem.get(i);
						String voter = one.attributeValue("value");
						if (voter.equals(normalizedContactName)) {
							has_voted = true;
						}
					}
				} catch (Exception e) {
					return false;
				}
			}
		} catch (NullPointerException npe) {
			logger.error("", npe);
		}
		return has_voted;
	}

	/**
	 * Sets the tabs attribute of the MessagesPane object
	 */
	public void printDoc(String text) {
		try {
			JTextPane newEditor = new JTextPane();
			newEditor.setText(text);
			StyleContext sc3 = new StyleContext();
			Style style3 = sc3.addStyle(null, null);
			StyleConstants.setForeground(style3, Color.black);
			DocumentRenderer d = new DocumentRenderer();
			d.print(newEditor);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public boolean existsAgainInThread(String messageId) {
		for (Statement statement : getTableStatements()) {
			if (statement.getMessageId().equals(messageId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param sort
	 *            Description of the Parameter
	 * @param confirm_delivery
	 *            Description of the Parameter
	 */
	public void recall(final org.dom4j.Document doc) {
		final Statement statement = Statement.wrap(doc);
		final String messageId = statement.getMessageId();
		PopUpController.INSTANCE.recallPopup((Document) doc.clone());

		Runnable runnable = new Runnable() {
			private MessagesPane mPane = MessagesPane.this;

			public void run() {
				removeFromAllMessages(messageId);

				getMessagesTree().recallMessage(messageId);

				this.mPane.repaint();

				removeFromTable(messageId);

				this.mPane.getSmallBrowser().deleteMessage(statement);
			}
		};
		UiUtils.swtBeginInvoke(runnable);
	}

	@SuppressWarnings("unchecked")
	public void insertTableUpdate(final Document doc) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				Statement statement = Statement.wrap(doc);
				MessagesPane.this.messagesTable.addStatement(statement);
			}
		});
	}

	public void checkMessagesOnPopup(Statement statement,
			WorkflowConfiguration configuration) {
		PopUpController.INSTANCE.checkIsPopupElement(statement, configuration,
				this);
	}

	public boolean shouldBeRedInTable(Statement statement,
			WorkflowConfiguration configuration) {
		String deliveryType = statement.getDeliveryType();
		AbstractDelivery delivery = configuration
				.getDeliveryByTypeOrNormal(deliveryType);
		String username = (String) this.getRawSession().get(
				SessionConstants.USERNAME);
		String contactName = (String) this.getRawSession().get(
				SessionConstants.REAL_NAME);

		ResultStatement result = null;
		if (statement.getResultId() != null) {
			result = getResultForMessage(statement);
		}
		return delivery.isNotConfirmedOrNotPassed(statement, result,
				contactName, username);
	}

	public ResultStatement getResultForMessage(Statement statement) {
		for (Statement s : getTableStatements()) {
			if (s.getMessageId().equals(statement.getResultId())
					&& s.isResult()) {
				ResultStatement result = ResultStatement.wrap(s
						.getBindedDocument());
				return result;
			}
		}
		return null;
	}

	public void addToAllMessages(String messageId, Statement st) {
		synchronized (this.allMessages) {
			try {
				this.allMessages.put(messageId, st);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
	public Map<String, Statement> getAllMessages() {
		return Collections.unmodifiableMap(this.allMessages);
	}

	private boolean isInMessages(String messageId) {
		if (messageId == null) {
			return false;
		}
		synchronized (this.allMessages) {
			return this.allMessages.containsKey(messageId);
		}
	}

	public void removeFromAllMessages(String messageId) {
		synchronized (this.allMessages) {
			this.allMessages.remove(messageId);

		}
	}

	public Statement getDocFromHash(String messageId) {
		synchronized (this.allMessages) {
			return this.allMessages.get(messageId);
		}
	}

	public Statement getStatementWithURL(String URL) {
		synchronized (this.allMessages) {
			for (Enumeration enumer = this.allMessages.elements(); enumer
					.hasMoreElements();) {
				Statement mts = (Statement) enumer.nextElement();
				// Statement mts = Statement.wrap( doc);
				String address = mts.getAddress();

				URL = VariousUtils.stripFinalSlashAndWWW(URL).toLowerCase();
				if (address != null) {
					address = VariousUtils.stripFinalSlashAndWWW(address)
							.toLowerCase();
					if (address.equals(URL)) {
						return mts;
					}
				}
			}
		}
		return null;

	}

	public void insertOneThread(final Document[] docsInOrder) {
		InsertStatementListOperation doInsert = new InsertStatementListOperation(
				this, docsInOrder, false, null);
		doInsert.start();
	}

	public void selectItemInTable(Statement statement) {
		selectItemInTable(statement.getMessageId());
	}

	@SuppressWarnings("unchecked")
	public void selectItemInTable(String messageId) {
		this.messagesTable.selectElement(messageId);
	}

	public void performInsert(final org.dom4j.Document doc,
			final String typeOfUpdate, final boolean insertToSelectedOnly,
			boolean openTreeToMessageId, final String sphereId) {
		if (processIsRecallUpdate(doc, typeOfUpdate)) {
			return;
		}
		Statement st = Statement.wrap(doc);
		if (!insertToSelectedOnly) {
			if (isInMessages(st.getMessageId())) {
				return;
			}
		}
		addToAllMessages(st.getMessageId(), st);
		boolean toInsert = insertToSelectedOnly;
		if (toInsert == false) {
			if (doc.getRootElement().element("expand") != null) {
				toInsert = true;
			}
		}
		insertUpdate(doc, false, false, openTreeToMessageId, toInsert);
	}

	private boolean processIsRecallUpdate(final org.dom4j.Document doc,
			final String typeOfUpdate) {
		if (logger.isDebugEnabled()) {
			logger.debug("type of update : " + typeOfUpdate);
		}
		if (typeOfUpdate == null) {
			return false;
		}
		if (typeOfUpdate.equals(SSProtocolConstants.RECALL)) {
			recall(doc);
			return true;
		} else if (typeOfUpdate.equals(SSProtocolConstants.VOTE)) {
			logger.debug("---------------- VOTE!");
			removeThenInsert(doc, true);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param doc
	 * @param sort
	 *            NOT USED
	 * @param confirm_delivery
	 *            NOT USED
	 * @param openTreeToMessageId
	 *            NOT USED
	 */
	public synchronized void insertUpdate(final org.dom4j.Document doc,
			final boolean sort, final boolean confirm_delivery,
			boolean openTreeToMessageId) {
		final boolean insertToSelectedOnly = doc.getRootElement().element(
				"expand") != null;
		if (logger.isDebugEnabled()) {
			logger.debug("inserting statement "
					+ Statement.wrap(doc).getSubject());
			logger.debug("stack trace " + DebugUtils.getCurrentStackTrace());
		}
		insertUpdate(doc, sort, confirm_delivery, true, insertToSelectedOnly);
	}

	/**
	 * 
	 * @param doc
	 * @param sort
	 *            NOT USED
	 * @param confirm_delivery
	 *            NOT USED
	 * @param openTreeToMessageId
	 * @param insertToSelectedOnly
	 */
	public synchronized void insertUpdate(final org.dom4j.Document doc,
			final boolean sort, final boolean confirm_delivery,
			boolean openTreeToMessageId, final boolean insertToSelectedOnly) {

		final Element webHighlight = doc.getRootElement().element(
				"web_highlight");
		if (webHighlight != null) {
			Thread t = new Thread() {
				public void run() {
					try {
						highlightInsideBrowser(doc);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						// Should more gracefully check for null/invalid(when
						// not displayed) mozilla browser from sash4
					}
				}
			};
			UiUtils.swtBeginInvoke(t);
		}

		if (doc.getRootElement().element("expand") != null) {
			openTreeToMessageId = true;
			doc.getRootElement().element("expand").detach();
		}

		subInsertUpdate(doc, openTreeToMessageId, insertToSelectedOnly);
	}

	public void highlightInsideBrowser(Document insertDoc) {
		Element webHighlight = insertDoc.getRootElement().element(
				"web_highlight");

		Document parentDoc = this.getParentDocFromResponseId(insertDoc
				.getRootElement().element("response_id")
				.attributeValue("value"));

		String address = VariousUtils.stripFinalSlashAndWWW(
				Statement.wrap(parentDoc).getAddress()).toLowerCase();

		SupraBrowser browser = MessagesPane.this.getPreviewAreaDocking()
				.getBrowser();
		String browserAddress = null;
		if (browser != null && !browser.isDisposed()) {
			browserAddress = browser.getUrl();

			if (browserAddress != null) {
				browserAddress = VariousUtils.stripFinalSlashAndWWW(
						browserAddress).toLowerCase();
				if (browserAddress.equals(address)) {

					browser.getMozillaBrowserController()
							.highlightTextAreaFromElement(webHighlight,
									parentDoc, insertDoc);

				}
			}
		}
		broadcastHighlightInsideBrowser(insertDoc, webHighlight, parentDoc,
				address);
	}

	/**
	 * @param insertDoc
	 * @param webHighlight
	 * @param parentDoc
	 * @param address
	 * @param browser
	 */
	private void broadcastHighlightInsideBrowser(Document insertDoc,
			Element webHighlight, Document parentDoc, String address) {
		for (final SupraBrowser browser : this.sF.tabbedPane.getBrowsers()) {
			if(browser==null || browser.isDisposed()) {
				continue;
			}
			String browserAddress = browser.getUrl();
			if (browserAddress != null) {
				browserAddress = VariousUtils.stripFinalSlashAndWWW(
						browserAddress).toLowerCase();
				if (browserAddress.equals(address)) {
					browser.getMozillaBrowserController()
							.highlightTextAreaFromElement(webHighlight,
									parentDoc, insertDoc);
				}
			}
		}
	}

	/**
	 * 
	 * @param doc
	 * @param sort
	 *            not used in implementation
	 * @param confirm_delivery
	 *            not used in implementation
	 * @param openTreeToMessageId
	 * @param insertToSelectedOnly
	 */
	public synchronized void subInsertUpdate(final org.dom4j.Document doc,
			final boolean openTreeToMessageId,
			final boolean insertToSelectedOnly) {
		Statement statement = Statement.wrap(doc);
		if (statement.isSystemMessage()
				&& !SystemMessageStatement.wrap(doc).getSystemType().equals(
						SystemMessageStatement.SYSTEM_TYPE_ERROR)) {
			this.systemMessages.add(statement);
			if (isSystemMessagesShowed()) {
				addToAllMessages(statement.getMessageId(), statement);
			} else {
				return;
			}
		}

		subInsertUpdateSecondPart(openTreeToMessageId, insertToSelectedOnly,
				statement);

	}

	private void subInsertUpdateSecondPart(final boolean openTreeToMessageId,
			final boolean insertToSelectedOnly, Statement statement) {
		InsertStatementOperation insertStatementOperation = new InsertStatementOperation(
				this, statement);
		insertStatementOperation.setOpenTreeToMessageId(openTreeToMessageId);
		insertStatementOperation.setInsertToSelectedOnly(insertToSelectedOnly);

		UiUtils.swtBeginInvoke(insertStatementOperation);
		sheckPopUp(statement);

		final String remoteGiver = statement.getGiver();
		final String currentGiver = this.client.getContact();
		if (StringUtils.isNotBlank(remoteGiver) && StringUtils.isNotBlank(currentGiver) &&
				!(remoteGiver.equals(currentGiver))) {
			this.sF.getTrayItem().startBlink();
		}
	}

	@SuppressWarnings("deprecation")
	private void sheckPopUp(Statement statement) {

		if (!statement.isConfirmedDefined()) {
			return;
		}
		if (statement.getConfirmed()) {
			return;
		}
		logger.info("pop up");

		String author = statement.getGiver();

		if (statement.getForwardedBy() != null) {
			author = statement.getForwardedBy();
		}
		if (!author.equals((String) this.client.session
				.get(SessionConstants.REAL_NAME))) {
			try {
				PopUpController.INSTANCE.popup(statement.getBindedDocument());
				try {
					File file = new File("Blip.wav");
					AudioClip clip = Applet.newAudioClip(file.toURL());
					clip.play();
					try {
						Thread.sleep(800);
					} catch (Exception e) {
					}
				} catch (Exception mue) {
					logger.warn("Popping wav 1", mue);
				}
			} catch (Exception ie) {
				logger.warn("Popping wav 3", ie);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param function
	 *            Description of the Parameter
	 * @param e
	 *            Description of the Parameter
	 */
	public void logException(String function, Exception e) {
		try {
			Date current = new Date();
			String moment = DateFormat.getDateInstance(DateFormat.LONG).format(
					current)
					+ " "
					+ DateFormat.getTimeInstance(DateFormat.LONG).format(
							current);
			File out = new File(this.bdir + this.fsep + "error_log.txt");
			RandomAccessFile raf = new RandomAccessFile(out, "rw");
			raf.seek(raf.length());
			// Output the data...
			raf.writeUTF(moment + " : " + function + "\r");
			raf.writeUTF(e.toString() + "\r");
			raf.close();
		} catch (IOException fnfe) {
			logger.error("", fnfe);
			// logger.info("problem with logging itself");
		}
	}

	public void loadWindow(final Statement statement) {
		if (this.isRootView())
			return;
		Runnable loadWindowRunner = new Runnable() {
			public void run() {
				createLoadingText();
				if (MessagesPane.this.getSmallBrowser() != null) {
					MessagesPane.this.showSmallBrowser(MessagesPane.this
							.getRawSession(), true, null, getPreviewHtmlText()
							.getText(), statement, null);
				}
				repaintAll();
			}
		};
		UiUtils.swtBeginInvoke(loadWindowRunner);
	}

	public void createLoadingText() {
		if (this.isRootView())
			return;

		PreviewHtmlTextCreator temp_creator = new PreviewHtmlTextCreator(this);

		Vector<Statement> browserStatements = new Vector<Statement>();
		for (Statement st : this.tableStatements) {
			browserStatements.add(0, st);
		}
		for (Statement st : browserStatements) {
			temp_creator.addDocText(st.getBindedDocument());
		}

		this.setPreviewHtmlText(temp_creator);
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 */
	public void setLastSelected(Document doc) {
		this.setLastSelectedDoc(doc);
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param isupdate
	 *            Description of the Parameter
	 */

	public void removeThenInsert(final org.dom4j.Document doc,
			final boolean onlyIfExists) {
		if (logger.isDebugEnabled()) {
			logger.debug("doc to replace : "
					+ XmlDocumentUtils.toPrettyString(doc));
		}
		RemoveThenInsertOperation removeThenInsertOperation = new RemoveThenInsertOperation(
				this, doc, onlyIfExists);
		UiUtils.swtBeginInvoke(removeThenInsertOperation);
		TagManager.INSTANCE.open(this, doc);
	}

	@SuppressWarnings("unchecked")
	public void replaceDocWith(final org.dom4j.Document doc,
			final Document replaceDoc) {
		try {
			Statement statement = Statement.wrap(doc);
			Statement replaceStatement = Statement.wrap(replaceDoc);

			String author = replaceStatement.getGiver();

			getMessagesTree().replaceDoc(replaceDoc, statement.getMessageId());

			// Now do the same for the table
			boolean threshold = false;

			if (replaceStatement.getConfirmed()) {
				// logger.info("DECISIVE is NOBODY");
				threshold = true;
			}

			if (replaceStatement.getMessageId()
					.equals(statement.getMessageId())) {
				this.messagesTable.replaceStatement(replaceStatement);

				String sphereType = this.client.getVerifyAuth().getSphereType(
						(String) this.getRawSession().get("sphere_id"));

				if (!sphereType.equals("group")) {
					if (!author.equals((String) this.getRawSession().get(
							"real_name"))) {

						PopUpController.INSTANCE
								.recallPopup((Document) statement
										.getBindedDocument().clone());
					}
				} else {
					if (threshold == true) {
						if (hasVoted((String) this.getRawSession().get(
								"real_name"), (Document) statement
								.getBindedDocument().clone())) {
							PopUpController.INSTANCE
									.recallPopup((Document) statement
											.getBindedDocument().clone());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		updatePresenceRenderer(doc);
	}

	public void notifyDeployed() {
		this.deployed = true;
		selectInGUICashed();
		if (this.activateP2PLater) {
			activateP2PDeliveryCheck();
		}
		SphereOpenManager.INSTANCE.register(this);
		tryPerformUnlockingOperation();
	}

	public void notifyUnlock(){
		this.unlocked = true;
		tryPerformUnlockingOperation();
	}
	
	private void tryPerformUnlockingOperation(){
		if (this.deployed && this.unlocked){
			unlock();
		}
	}

	private void selectInGUICashed() {
		if (this.positioner.isDeployed()) {
			if (this.cashedMessage_id != null) {
				Statement statement = getStatementByMessageId(this.cashedMessage_id);
				if (statement != null) {
					// this.messagesTree.getListener().singleLeftMouseClicked(statement);
					StatementExecutorFactory.createExecutor(this, statement)
							.performExecute(true, false);
				} else {
					this.selectMessage(statement);
				}
			}
		}
	}

	/**
	 * @param cashedMessage_id2
	 * @return
	 */
	private Statement getStatementByMessageId(String cashedMessage_id) {
		for (Statement statement : this.tableStatements) {
			if (statement.getMessageId().equals(cashedMessage_id)) {
				return statement;
			}
		}
		return null;
	}

	private void cashMessageId(final String message_id) {
		this.cashedMessage_id = message_id;

	}

	public String getCashedId() {
		return this.cashedMessage_id;
	}

	public void setCashedSelected(final String message_id) {
		cashMessageId(message_id);
		setInsertable(false);
		selectInGUICashed();
	}

	@SuppressWarnings("unchecked")
	public void removeFromTable(final String message_id) {
		logger.info("message id in remove from table: " + message_id);

		getMessagesTable().removeStatement(message_id);

		/*
		 * for (Enumeration enumer = enumerpass; enumer.hasMoreElements();){
		 * MessagesMutableTreeNode node = (MessagesMutableTreeNode)
		 * enumer.nextElement(); Document doc = (Document) node.getUserObject();
		 * String remove_id =
		 * doc.getRootElement().element("message_id").attributeValue("value");
		 * for (int i = 0; i < stable.model.getRowCount(); i++){ String
		 * temp_message_id = (String) stable.model.getValueAt(i, 4); if
		 * (temp_message_id.equals(remove_id)){ stable.model.removeRowAt(i); } } }
		 */
		// repaint();
	}

	public boolean isReplyToMine(Document testDoc) {
		String response_id = Statement.wrap(testDoc).getResponseId();

		if (response_id == null) {
			return false;
		}

		for (Statement st : getTableStatements()) {
			try {
				if (!st.isKeywords()
						&& response_id.equals(st.getMessageId())
						&& st.getGiver().equals(
								(String) getRawSession().get(
										SessionConstants.REAL_NAME))) {
					return true;
				}
			} catch (Exception exep) {
				return false;
			}
		}
		return false;
	}

	public Document getRootDocument(Document testDoc) {
		Document returnStatement = testDoc;

		while (getMessagesTree().getParentDocFor(returnStatement) != null) {
			returnStatement = getMessagesTree()
					.getParentDocFor(returnStatement);
		}

		return returnStatement;
	}

	/**
	 * Sets the currentlySelected attribute of the MessagesPane object
	 * 
	 * @param i
	 *            The new currentlySelected value
	 */

	public Document getParentDoc(Document childDoc) {
		return getMessagesTree().getParentDocFor(childDoc);
	}

	public Document getParentDocFromResponseId(String responseId) {
		return getMessagesTree().getParentDocFromResponseId(responseId);
	}

	/**
	 * @param messageId
	 * @param newSt
	 */
	public void replaceDocInHash(String messageId, Statement newSt) {
		synchronized (this.allMessages) {
			this.allMessages.remove(messageId);
			this.allMessages.put(newSt.getMessageId(), newSt);

		}

	}

	public void removeDocFromHash(String messageId) {
		synchronized (this.allMessages) {
			this.allMessages.remove(messageId);
		}
	}

	/**
	 * 
	 */
	public void closeClientPresence() {
		if (this.clientPresence != null) {
			this.clientPresence.release();
		}
	}

	/**
	 * @return the clientPresence
	 */
	public synchronized ClientPresence getClientPresence() {
		if (this.clientPresence == null) {
			this.clientPresence = ClientPresenceManager.INSTANCE
					.getClientPresence(this);
		}
		return this.clientPresence;
	}

	public void updateStatsForSphere(final String string,
			final Document statsDoc, final String messageId) {
		Runnable runnable = new Runnable() {

			public void run() {

				logger.info("updating stats for sphere: " + messageId + " , "
						+ string);

				for (Statement st : getTableStatements()) {

					Document doc = st.getBindedDocument();
					Element email = doc.getRootElement();
					String testMessage = email.element("message_id")
							.attributeValue("value");

					if (testMessage.equals(messageId)) {

						String existingSubject = null;
						if (doc.getRootElement().element("orig_subject") != null) {
							existingSubject = doc.getRootElement().element(
									"orig_subject").attributeValue("value");
						} else {
							existingSubject = doc.getRootElement().element(
									"subject").attributeValue("value");

							doc.getRootElement().addElement("orig_subject")
									.addAttribute("value", existingSubject);
						}

						doc.getRootElement().element("subject").addAttribute(
								"value", existingSubject + " " + string);

						getMessagesTree().replaceDoc(doc, st.getMessageId());

						repaint();
					}
				}
			}
		};
		UiUtils.swtBeginInvoke(runnable);
	}

	public void setPreviewDocumentText(Document doc) {
		// TODO: implement
	}

	public void showMessagesBrowser(Document doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("We are showing messagesBrowser.." + doc.asXML());
		}
		if(doc==null) {
			return;
		}
		this.setPreviewHtmlText(new PreviewHtmlTextCreator(this));
		String text = null;
		Statement statement = Statement.wrap(doc);

		try {
			if (statement.isComment()) {
				if (logger.isDebugEnabled()) {
					logger.debug("It has comment text!!");
				}
				text = CommentStatement.wrap(doc).getComment();
			} else {

				text = statement.getBody();
			}
		} catch (NullPointerException ex) {
			logger.error(ex);
		}
		showMessagesBrowser(text, doc);
	}

	public void showMessagesBrowser(String text, Document doc) {
		if (VariousUtils.isTextHTML(text)) {
			this.getPreviewHtmlText().setText(text);
		} else {
			this.getPreviewHtmlText().addText(text);
		}

		this.showSmallBrowser(this.getRawSession(), true, null, this
				.getPreviewHtmlText().getText(), Statement.wrap(doc), null);
	}

	public void showEmailBrowser(ExternalEmailStatement email) {
		this.setPreviewHtmlText(new PreviewHtmlTextCreator(this));
		String text = "";

		text += "SENDER: " + StringProcessor.toHTMLView(email.getGiver())
				+ "<br>";
		String str = StringProcessor.toHTMLView(email.getReciever());
		if (str != null)
			text += "RECIEVER: " + str + "<br>";
		str = StringProcessor.toHTMLView(email.getCcrecievers());
		if (str != null)
			text += "CC: " + str + "<br>";
		str = StringProcessor.toHTMLView(email.getBccrecievers());
		if (str != null)
			text += "BCC: " + str + "<br>";
		text += "SUBJECT: " + email.getSubject() + "<br><br>";
		text += "BODY OF THE EMAIL:<br>" + email.getOrigBody();

		/*
		 * if (VariousUtils.isTextHTML(text)) {
		 * this.getPreviewHtmlText().setText(text); } else {
		 */
		this.getPreviewHtmlText().addText(text);

		this.showSmallBrowser(this.getRawSession(), true, null, this
				.getPreviewHtmlText().getText(), email, null);
	}

	public void showPreviousCommentBrowser(Element viewDoc) {
		this.setPreviewHtmlText(new PreviewHtmlTextCreator(this));

		String text = viewDoc.element("body").element("orig_body").getText();

		if (VariousUtils.isTextHTML(text)) {
			this.getPreviewHtmlText().setText(text);
		} else {
			this.getPreviewHtmlText().addText(text);
		}
		this.showSmallBrowser(this.getRawSession(), true, null, this
				.getPreviewHtmlText().getText(), null, null);

		logger.info("PreviousCommentBrowser is shown");
	}

	public Vector<String> getMembers() {
		return getPeopleTable() != null ? getPeopleTable().getMembers()
				: new Vector<String>();
	}

	public static Random getTableIdGenerator() {
		return tableIdGenerator;
	}

	public MessagesTree getMessagesTree() {
		return this.messagesTree;
	}

	public Document getLastSelectedDoc() {
		return this.lastSelectedDoc;
	}

	/**
	 * @param lastSelectedDoc
	 *            The lastSelectedDoc to set.
	 */
	public void setLastSelectedDoc(final Document lastSelectedDoc) {
		this.lastSelectedDoc = lastSelectedDoc;
		logger.info("last selected doc : " + lastSelectedDoc);
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				boolean enabled = (lastSelectedDoc != null);
				try {
					if (MessagesPane.this.getControlPanel() instanceof ControlPanel) {
						ControlPanel controlP = (ControlPanel) MessagesPane.this
								.getControlPanel();
						if (!enabled) {
							if (controlP.getReplyBox().isEnabled()
									&& controlP.getReplyBox().getSelection())
								controlP.getReplyBox().setSelection(false);
							if (controlP.getTagBox().isEnabled()
									&& controlP.getTagBox().getSelection()) {
								controlP.getTagBox().setSelection(false);
								controlP.setIsTagSelected();
							}
						}
						controlP.getReplyBox().setEnabled(enabled);
						controlP.getTagBox().setEnabled(enabled);
						controlP.getDropDownCreateItem()
								.setKeywordActionEnabled(enabled);
					}
				} catch (NullPointerException e) {
				}
			}
		});
	}

	public void setSphereDefinition(Document sphereDefinition) {
		this.sphereDefinition = sphereDefinition;
	}

	public Document getSphereDefinition() {
		return this.sphereDefinition;
	}

	public SphereStatement getSphereStatement() {
		return SphereStatement.wrap(this.sphereDefinition);
	}

	public boolean isUiCreated() {
		return this.positioner.isDeployed();
	}

	public void setPreviewHtmlText(PreviewHtmlTextCreator pr_text) {
		this.preview_html_text = pr_text;
	}

	public PreviewHtmlTextCreator getPreviewHtmlText() {
		return this.preview_html_text;
	}

	public void setRemovedAssets(Hashtable removedAssets) {
	}

	/**
	 * @param session2
	 * @param b
	 * @param object
	 * @param text
	 * @param statement
	 * @param object2
	 */
	public void showSmallBrowser(final Hashtable session, final boolean show,
			final String url, final String content, final Statement statement,
			final Document highlightDoc) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				if (MessagesPane.this.getPreviewAreaDocking() != null) {
					boolean forseNavigate = false;
					if (highlightDoc != null) {
						// TODO: may be we need to check
						// highlightDoc.getRootElement().element(
						// "web_highlight");
						forseNavigate = true;
					}
					showSmallBrowserImpl(session, show, url, content,
							statement, highlightDoc, forseNavigate, true);
				}
			}
		});
	}

	public void showSmallBrowserNoFocusSteal(final Hashtable session,
			final boolean show, final String url, final String content,
			final Statement statement, final Document highlightDoc) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				showSmallBrowserImpl(session, show, url, content, statement,
						highlightDoc, false, false);
			}
		});
	}

	private void showSmallBrowserImpl(final Hashtable session,
			final boolean show, final String url, final String content,
			final Statement statement, final Document highlightDoc,
			boolean forceNavigate, boolean sendFocusToBrowser) {
		logger.info("showSmallBrowserImpl ");
		try {
		if (getPreviewAreaDocking() != null) {
			final SupraBrowser browser = getPreviewAreaDocking().getBrowser();
			if( browser==null || !browser.isReady() ) {
				logger.error("browser is null or disposed");
				return;
			}
			final String existUrl = browser.getUrl();
				if (!existUrl.equals(url) || forceNavigate) {
				browser.getMozillaBrowserController()
						.setCurrentSession(session);
				getPreviewAreaDocking().showBrowser(show, url, content,
						statement, highlightDoc);
				if (sendFocusToBrowser) {
					browser.setFocus();
				}
			}
		} else {
			logger
					.info("showSmallBrowserImpl failed because previewDocking is null");
		}
		} catch (Throwable ex){
			logger.error("Error showing small browser", ex);
	}
	}

	public SupraBrowser getSmallBrowser() {
		logger.debug("call small browser");
		if (getPreviewAreaDocking() != null) {
			return getPreviewAreaDocking().getBrowser();
		} else {
			return null;
		}
	}

	public VotingEngine getVotingEngine() {
		return this.votingEngine;
	}

	public AbstractControlPanel getControlPanel() {
		return getControlPanelDocking() != null ? getControlPanelDocking()
				.getContent() : null;
	}

	public ControlPanelDocking getControlPanelDocking() {
		return this.positioner.getControlPanelDocking();
	}

	public MessagesTreeDocking getTreeDocking() {
		return this.positioner.getMessagesTreeDocking();
	}

	/***************************************************************************
	 * Returns supra sphere frame
	 */
	public SupraSphereFrame getSupraSphereFrame() {
		return this.sF;
	}

	public void setInsertable(boolean value) {
		this.isInsertable = value;
	}

	public boolean isInsertable() {
		return this.isInsertable;
	}

	private void waitUntilLayoutGui() {
		if (Display.getDefault().getThread() == Thread.currentThread()) {
			throw new UnexpectedRuntimeException(
					"Cannot wait for ui from ui thread");
		}
		int waitTime = 0;
		while (!isUiCreated()) {
			try {
				Thread.sleep(WAIT_UNTIL_LAYOUT_GUI_SLEEP_TIME);
				waitTime += WAIT_UNTIL_LAYOUT_GUI_SLEEP_TIME;
				if (waitTime > WAIT_UNTIL_LAYOUT_GUI_TIMEOUT) {
					throw new UnexpectedRuntimeException(
							"waitUntilLayoutGui time out");
				}
			} catch (InterruptedException ex) {
				logger.warn("waitUntilLayoutGui interruted", ex);
			}
		}
	}

	public boolean isScrollLocked() {
		return this.isScrollLocked;
	}

	public void setScrollLock(boolean value) {
		this.isScrollLocked = value;
	}

	public boolean isThreadView() {
		return this.isThreadView;
	}

	public void setThreadView(boolean value) {
		this.isThreadView = value;
	}

	public void setCurrentThread(String value) {
		this.currentThread = value;
	}

	public String getCurrentThread() {
		return this.currentThread;
	}

	public boolean isRootView() {
		// getSphereId().equals( getVerbosedSession().getSurpaSphere() );
		return false;
	}

	public void setViewComment(CommentStatement statement) {
		this.viewComment = statement;
	}

	public CommentStatement getViewComment() {
		return this.viewComment;
	}

	public void showCommentWindow() {

		getSmallBrowser().findCommentedPlace(getViewComment(), true);

		CommentApplicationWindow caw = new CommentApplicationWindow(this, this
				.getViewComment());
		this.sF.getCommentWindowController().addCommentWindow(caw);
		this.setNeedOpenComment(false);
		caw.setBlockOnOpen(true);
		caw.open();

	}

	public boolean needOpenComment() {
		return this.needOpenComment;
	}

	public void setNeedOpenComment(boolean value) {
		this.needOpenComment = value;
	}

	@SuppressWarnings("unchecked")
	public Vector<CommentStatement> findComment(String commentId) {
		Vector<CommentStatement> vec = new Vector<CommentStatement>();

		for (Statement statement : getTableStatements()) {
			if (statement.isComment()) {
				CommentStatement comment = CommentStatement.wrap(statement
						.getBindedDocument());
				if (comment.getCommentId().equals(commentId)) {
					vec.add(comment);
				}
			}
		}

		return vec;
	}

	/**
	 * @return
	 */
	public SupraTableDocking getTableDocking() {
		return this.positioner.getTableDocking();
	}

	public IMessagesTable getMessagesTable() {
		return this.messagesTable;
	}

	/**
	 * 
	 */
	public void activateP2PDeliveryCheck() {
		AbstractControlPanel control = getControlPanel();
		if (control != null) {
			control.activateP2PDeliveryCheck();
		} else {
			this.activateP2PLater = true;
		}
	}

	public List<Statement> getTableStatements() {
		return this.tableStatements;
	}

	public ArrayList<Statement> getSystemMessages() {
		return this.systemMessages;
	}

	public void includeSystemMessageButton() {
		if (getPreviewAreaDocking() == null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				getTreeDocking().getShowSystemButton().setEnabled(true);
			}
		});
	}

	/**
	 * 
	 */
	public void showSystemMessages() {
		for (Statement statement : this.systemMessages) {
			if(statement==null || statement.getMessageId()==null) {
				continue;
			}
			if(getAllMessages().containsKey(statement.getMessageId())) {
				continue;
			}
			addToAllMessages(statement.getMessageId(), statement);
			subInsertUpdateSecondPart(true, false, statement);
			this.systemMessagesController.addThread(statement.getThreadId());
		}
	}
	
	/**
	 * 
	 */
	public void showSystemMessages(final String threadId) {
		for (Statement statement : this.systemMessages) {
			if (statement == null || statement.getThreadId() == null
					|| !statement.getThreadId().equals(threadId)) {
				continue;
			}
			addToAllMessages(statement.getMessageId(), statement);
			subInsertUpdateSecondPart(true, false, statement);
		}
	}
	
	public void currentSystemMessageShow(final String messageId) {
		for (Statement statement : this.systemMessages) {
			if (statement == null || statement.getMessageId() == null
					|| !statement.getResponseId().equals(messageId)) {
				continue;
			}
				addToAllMessages(statement.getMessageId(), statement);
				subInsertUpdateSecondPart(true, false, statement);
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void hideSystemMessages() {
		for (Statement statement : this.systemMessages) {
			this.recall(statement.getBindedDocument());
			this.systemMessagesController.hideThread(statement.getThreadId());
		}
	}
	
	public void hideKeyWords(){

		getMessagesTree().removeAllNonRootKeywords();
		getMessagesTable().removeAllNonRootKeywords();
		loadWindow( null );

	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void hideSystemMessages(final String threadId) {
		for (Statement statement : this.systemMessages) {
			if (statement == null || statement.getThreadId() == null
					|| !statement.getThreadId().equals(threadId)) {
				continue;
			}
			this.recall(statement.getBindedDocument());
		}
	}
	
	public void currentSystemMessagesHide(final String messageId) {
		for (Statement statement : this.systemMessages) {
			if (statement == null || statement.getMessageId() == null
					|| !statement.getResponseId().equals(messageId)) {
				continue;
			}
			this.recall(statement.getBindedDocument());
		}
	}

	public boolean isSystemMessagesShowed() {
		return UiUtils.swtEvaluate(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return getTreeDocking().getShowSystemButton().getSelection();
			}
		}).booleanValue();
	}

	public IPeopleList getPeopleTable() {
		return this.positioner.getPeoples();
	}

	/**
	 * 
	 */
	public void recheckPeopleListColors() {
		recheckPeopleListColors(true);
	}

	public void recheckPeopleListColors(boolean clearSelection) {
		final IPeopleList peopleTable = getPeopleTable();
		if (peopleTable != null) {
			peopleTable.updateRefresh(clearSelection);
		}
	}

	/**
	 * @return
	 */
	public AbstractDelivery getDeliveryType() {
		return getControlPanel().getDeliveryType();
	}

	public void reorganizePreviewButtons(final Statement selection) {
		if (getPreviewAreaDocking() == null) {
			return;
		}
		getPreviewAreaDocking().reorganizeButtons(selection);
	}

	/**
	 * 
	 */
	public void deselectThreadViewButton() {
		getTreeDocking().getThreadViewButton().setSelection(false);
	}

	/**
	 * @param selection
	 */
	public void selectShowSystemButton(boolean selection) {
		getTreeDocking().getShowSystemButton().setSelection(selection);
	}

	/**
	 * @return
	 */
	public Statement getSelectedStatement() {
		return getMessagesTable().getSelectedElement();
	}

	/**
	 * @return
	 */
	public MessagesPanePositionsInformation calculateDivs() {
		return this.positioner.calculateDivs();
	}

	/**
	 * 
	 */
	public void repaintAll() {
		// TODO: fix
		this.positioner.repaintAll();
	}

	/**
	 * @param messagesTable2
	 */
	public void setMessagesTable(MessagesTable messagesTable) {
		this.messagesTable = messagesTable;
	}

	/**
	 * @param messageId
	 */
	public void highlightInsideBrowser(String messageId) {
		if (getSmallBrowser() == null) {
			return;
		}
		getSmallBrowser().highlightSelectedString(messageId);
	}

	public Statement getCurrentStatement() {
		return this.currentStatement;
	}

	public void setCurrentStatement(Statement statement) {
		this.currentStatement = statement;
	}

	// public static void thunder(Shell shell){
	/*
	 * org.eclipse.swt.graphics.Point peopleListPane = shell.getSize(); int i =
	 * 1; //peopleListPane.x -= 10; peopleListPane.y -= i;
	 * shell.setSize(peopleListPane); //peopleListPane.x += 10; peopleListPane.y +=
	 * i; shell.setSize(peopleListPane);
	 */
	// shell.update();
	// shell.redraw();
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.ISendCreationProvider#getSelectedMembersNames()
	 */
	public List<String> getSelectedMembersNames() {
		IPeopleList peopleList = getPeopleTable();
		return peopleList != null ? peopleList.getSelectedMembersNames()
				: EMPTY_LIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.ISphereView#getVerbosedSession()
	 */
	public VerbosedSession getVerbosedSession() {
		return this.verbosedSession;
	}

	/**
	 * @return
	 */
	public boolean isReplyChecked() {
		if (getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) getControlPanel();
			return controlPanel.isReplyChecked();
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isTagChecked() {
		if (getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) getControlPanel();
			return controlPanel.isTagChecked();
		}
		return false;
	}

	/**
	 * @param text
	 */
	public void setSendText(String text) {
		if (getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) getControlPanel();
			controlPanel.setTextToTextField(text);
		}

	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(Hashtable session) {
		this.verbosedSession.setRawSession(session);
	}

	/**
	 * @return the session
	 */
	public Hashtable getRawSession() {
		return this.verbosedSession.getRawSession();
	}

	/**
	 * 
	 */
	public void closeBrowser() {
		final SupraBrowser supraBrowser = getSmallBrowser();
		if (supraBrowser != null && !supraBrowser.isDisposed()) {
			supraBrowser.dispose();
		}
	}

	/**
	 * @param item
	 */
	public void setTabItem(SupraCTabItem item) {
		this.tabItem = item;
	}

	public SupraCTabItem getTabItem() {
		return this.tabItem;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public Document getConcreteSphereDefinition(String sphereId) {
		return this.client.getSphereDefinition(sphereId);
	}

	/**
	 * @param input
	 */
	public void setTableStatements(List<Statement> input) {
		this.tableStatements = input;
	}

	/**
	 * 
	 */
	public void reactOnPreferencesChange() {
		getControlPanel().refreshdeliveryCombo();
	}

	/**
	 * 
	 * @return is sphere View result of search or not
	 */
	public boolean isSearchSphere() {
		return this.searchSphere;
	}

	public void setSearchSphere(boolean searchSphere) {
		this.searchSphere = searchSphere;
	}

	/**
	 * @param statement
	 */
	public void selectMessage(Statement statement) {
		if(statement==null) {
			return;
		}
		highlightInsideBrowser(statement.getMessageId());
		selectItemInTable(statement.getMessageId());
		getMessagesTree().selectMessage(statement.getMessageId());
	}

	/**
	 * 
	 */
	public void removeAll() {
		synchronized (this.allMessages) {
			hiddenRecallAndWait(Collections.list(this.allMessages.keys()));
		}
		/*for (; this.allMessages.size() > 0;) {
			Statement statement = null;
			synchronized (this.allMessages) {
				statement = this.allMessages.values().iterator().next();
			}
			if (statement != null) {
				Document doc = statement.getBindedDocument();
				recallAndWait(doc);
			}
		}*/
	}

	public void hiddenRecallAndWait(final Collection<String> ids) {

		// logger.info("Will recall");
		final AtomicBoolean wait = new AtomicBoolean();
		wait.set(false);
		Runnable runnable = new Runnable() {
			private MessagesPane mPane = MessagesPane.this;

			public void run() {

				for (String messageId : ids) {					

					getMessagesTree().hiddenRecallMessage(messageId);					

					getMessagesTable().hiddenRemoveStatement(messageId);

					this.mPane.getSmallBrowser().hiddenDeleteMessage(messageId);
				}
				getMessagesTable().update();
				getMessagesTree().update();
				this.mPane.repaint();
				synchronized (wait) {
					wait.set(true);
					wait.notifyAll();
				}
			}
		};
		UiUtils.swtBeginInvoke(runnable);
		synchronized (wait) {
			while (!wait.get()) {
				try {

					wait.wait();
				} catch (InterruptedException ex) {
				}
			}
		}		
	}

	public void recallAndWait(final org.dom4j.Document doc) {
		// logger.info("CAlling recall!: "+doc.asXML());
		final Statement statement = Statement.wrap(doc);
		final String messageId = statement.getMessageId();
		PopUpController.INSTANCE.recallPopup((Document) doc.clone());

		// logger.info("Will recall");
		final AtomicBoolean wait = new AtomicBoolean();
		wait.set(false);
		Runnable runnable = new Runnable() {
			private MessagesPane mPane = MessagesPane.this;

			public void run() {

				removeFromAllMessages(messageId);

				getMessagesTree().recallMessage(messageId);

				getMessagesTable().removeStatement(messageId);

				this.mPane.getSmallBrowser().deleteMessage(statement);

				this.mPane.repaint();
				synchronized (wait) {
					wait.set(true);
					wait.notifyAll();
				}
			}
		};
		UiUtils.swtBeginInvoke(runnable);
		synchronized (wait) {
			while (!wait.get()) {
				try {

					wait.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	public void setMessagesTree(MessagesTree tree) {
		this.messagesTree = tree;
	}

	/**
	 * 
	 */
	public void setTerseAsActiveAction() {
		getControlPanel().dropDownCreateItem.selectActiveAction("Terse");
		getControlPanel().setPreviousType("Terse");
		getControlPanel().layout();
	}

	public ControlLocker getLocker() {
		return this.locker;
	}

	private void unlock(){
		getLocker().unLock();	
		getPreviewAreaDocking().checkStatesOfHystory();
	}

	public class ControlLocker {

		private class ControledControls {
			Control control;

			boolean state;

			public ControledControls(Control control) {
				this.control = control;
				this.state = control.getEnabled();
			}
		}

		private ArrayList<ControledControls> cControls = new ArrayList<ControledControls>();

		public ControlLocker() {
		}

		public void addControl(Control control) {
			synchronized (this.cControls) {
				this.cControls.add(new ControledControls(control));
			}
		}

		public void lock() {
			changeState(false);
		}

		public void unLock() {
			changeState(true);
		}

		private void changeState(boolean enabled) {
			if (logger.isDebugEnabled()) {
				logger.debug("changing states of buttons=" + enabled);
			}
			synchronized (this.cControls) {
				for (ControledControls cControl : this.cControls) {
					if (enabled) {
						cControl.control.setEnabled(cControl.state);
					} else {
						cControl.state = cControl.control.getEnabled();
						cControl.control.setEnabled(false);
					}
				}
			}
		}

	}

	/**
	 * @param email
	 * @return
	 */
	public boolean hasComment(ExternalEmailStatement email) {
		return getMessagesTree().hasComment(email);
	}

	/**
	 * 
	 */
	public void setYellowParentMessage() {
		if(getSmallBrowser()==null) {
			return;
		}
		getSmallBrowser().setYellowParentMessage();
	}

	/**
	 * 
	 */
	public void revertParentMessageColor() {
		if(getSmallBrowser()==null) {
			return;
		}
		getSmallBrowser().revertParentMessageColor();
	}

	/**
	 * @return the systemMessagesController
	 */
	public ThreadSystemMessagesController getSystemMessagesController() {
		return this.systemMessagesController;
	}

	/**
	 * @param systemMessagesController the systemMessagesController to set
	 */
	public void setSystemMessagesController(
			ThreadSystemMessagesController systemMessagesController) {
		this.systemMessagesController = systemMessagesController;
	}

	/**
	 * @param st
	 */
	public void tagUpdated( final KeywordStatement st ) {
		if ( st == null ) {
			logger.error(" KeywordStatement is null ");
			return;
		}
		getMessagesTree().updateAllInstancesOfKeywords( st );
		getMessagesTable().updateKeyword( st );
		//loadWindow( null );
	}
}
