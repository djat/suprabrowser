package ss.obsolete;

/*

 Window both to create and view a binary asset

 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.event.InputTextPaneListener;
import ss.client.localization.LocalizationLinks;
import ss.client.networking.SupraClient;
import ss.client.ui.MessagesPane;
import ss.client.ui.viewers.ViewersUtil;
import ss.common.FileMonitor;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

public class NewBinary extends ContentTypeViewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 819395100513036920L;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWBINARY);

	private static final String FILENAME = "NEWBINARY.FILENAME";

	private static final String SAVE = "NEWBINARY.SAVE";

	private static final String AUTHOR = "NEWBINARY.AUTHOR";

	private static final String APPLICATION = "NEWBINARY.APPLICATION";

	private static final String NAME = "NEWBINARY.NAME";

	private static final String OPEN_FOR_EDITING = "NEWBINARY.OPEN_FOR_EDITING";

	private static final String END_EDITING = "NEWBINARY.END_EDITING";

	private static final String OPEN = "NEWBINARY.OPEN";

	private static final String OPEN_AS_PDF = "NEWBINARY.OPEN_AS_PDF";

	private static final String SAVE_AS = "NEWBINARY.SAVE_AS";

	private static final String SAVE_AS_PDF = "NEWBINARY.SAVE_AS_PDF";

	private static final String DELETE = "NEWBINARY.DELETE";

	private static final String YES = "NEWBINARY.YES";

	private static final String NO = "NEWBINARY.NO";

	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE = "NEWBINARY.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE";

	private static final String WARNING = "NEWBINARY.WARNING";

	private Logger logger = SSLogger.getLogger(this.getClass());

	// private JTextField receiver = new JTextField(10);

	private JLabel giver = new JLabel();

	private JTextField subject = new JTextField(10);

	// private JLabel dialog = null;

	// private JLabel dialoglabel = new JLabel("Dialog:");

	// private JLabel moment = null;

	// private String bdir = System.getProperty("user.dir");

	// private String fsep = System.getProperty("file.separator");

	// private JLabel momentLabel = new JLabel("Moment:");

	// private JTextField application = new JTextField(10);

	private JLabel extlabel = new JLabel(this.bundle.getString(FILENAME));

	private JTextField extension = new JTextField(10);

	// private String message_id = null;

	private JPanel labelPanel = new JPanel();

	private JPanel fieldPanel = new JPanel();

	// private Hashtable me_privs = new Hashtable();

	// private String selected_body = null;

	// private String orig_body = null;

	// private String[][] rec_data = null;

	// private JButton reply = new JButton("Reply");

	// private JButton vote = new JButton("Vote");

	// private JButton edit = new JButton("Edit");

	private JButton publish = new JButton(this.bundle.getString(SAVE));

	private JTextPane bodyEditor;

	// private String mailbody;

	// private String maildir;

	// private String dialogpass = null;

	// private JComboBox recBox = null;

	// private CommentStyledDocument comment_body = null;

	// private String path = null;

	// private String type = null;

	private GridBagLayout gridbag = null;

	private GridBagConstraints c = null;

	// private String grab = "";

	// private int pos = -1;

	// private Character ch = null;

	// private boolean change = false;

	// private StringBuffer newbody = new StringBuffer();

	private DefaultElement body = null;

	private JPanel allPanel = null;

	private String response_id = null;

	private String fileName = null;

	private FileMonitor fm = null;

	public NewBinary(Hashtable session, MessagesPane mP, String send_delivery) {

		this.session = session;

		setVisible(false);

		this.setFillMessage(false);
		this.mP = mP;
		this.model_path = "//file/create";

		this.giver.setText((String) session.get("real_name"));
		setSize(new Dimension(640, 480));
		this.bodyEditor = new JTextPane();
		this.fm = new FileMonitor();

	}

	public NewBinary(Hashtable session, MessagesPane mP, String send_delivery,
			Document viewDoc) {

		this.session = session;
		this.response_id = viewDoc.getRootElement().element("message_id")
				.attributeValue("value");

		this.viewDoc = viewDoc;
		setVisible(false);

		this.setFillMessage(false);
		this.mP = mP;
		this.model_path = "//library/create";

		this.giver.setText((String) session.get("real_name"));
		setSize(new Dimension(640, 480));
		this.bodyEditor = new JTextPane();
		this.fm = new FileMonitor();

	}

	public NewBinary(Hashtable session, MessagesPane mP, String send_delivery,
			String response_id) {

		this.session = session;
		this.response_id = response_id;

		setVisible(false);

		this.setFillMessage(false);
		this.mP = mP;
		this.model_path = "//library/create";

		this.giver.setText((String) session.get("real_name"));
		setSize(new Dimension(640, 480));
		this.bodyEditor = new JTextPane();
		this.fm = new FileMonitor();

	}

	public ContentTypeViewer getFrame() {

		return this;

	}

	public void addKeyListener(InputTextPaneListener itpl) {

		this.subject.addKeyListener(itpl);
		this.bodyEditor.addKeyListener(itpl);

	}

	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	public void disposeAll() {

		dispose();

	}

	public void createUI() {

		setUpUIImageIcon();

		createAllPanel();

		ViewersUtil.centerFrame(this);

		pack();

		setVisible(true);

		toFront();

	}

	/**
	 * 
	 */
	private void createAllPanel() {
		this.allPanel = new JPanel();

		// this.dialoglabel.setSize(20, 10);

		this.gridbag = new GridBagLayout();
		this.c = new GridBagConstraints();

		this.allPanel.setLayout(this.gridbag);

		this.c.gridx = 0;
		this.c.gridy = 0;
		this.c.weightx = 0.0;
		this.c.weighty = 0.0;
		this.labelPanel = createLabelPanel();
		this.gridbag.setConstraints(this.labelPanel, this.c);
		this.allPanel.add(this.labelPanel);

		this.c.gridx = 1;
		this.c.gridy = 0;
		this.c.gridwidth = 2;
		this.c.weightx = 1.0;
		this.c.weighty = 0.0;
		this.c.fill = GridBagConstraints.HORIZONTAL;
		this.fieldPanel = createFieldPanel();
		this.gridbag.setConstraints(this.fieldPanel, this.c);
		this.allPanel.add(this.fieldPanel);

		this.c.gridwidth = 3;
		this.c.gridx = 0;
		this.c.gridy = 1;
		this.c.anchor = GridBagConstraints.NORTH;
		this.c.fill = GridBagConstraints.BOTH;
		this.c.weightx = 1.0;
		this.c.weighty = 1.0;
		JScrollPane bodyJScrollPane = createBodyScrollPane();
		this.gridbag.setConstraints(bodyJScrollPane, this.c);
		this.allPanel.add(bodyJScrollPane);

		this.allPanel.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(this.allPanel);
	}

	/**
	 * 
	 */
	private JPanel createFieldPanel() {
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new GridLayout(0, 1, 0, 3));
		fieldPanel.add(this.giver);
		fieldPanel.add(this.subject);
		// fieldPanel.add(application);
		return fieldPanel;
	}

	/**
	 * @return
	 */
	private JScrollPane createBodyScrollPane() {
		JScrollPane bodyJScrollPane = new JScrollPane(this.bodyEditor);
		bodyJScrollPane.setPreferredSize(new Dimension(600, 400));
		return bodyJScrollPane;
	}

	/**
	 * 
	 */
	private JPanel createLabelPanel() {

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(0, 1, 0, 3));

		// JLabel reclabel = new JLabel("Receiver:");
		// reclabel.setSize(20, 10);

		JLabel label = new JLabel(this.bundle.getString(AUTHOR));
		label.setSize(20, 10);
		labelPanel.add(label);

		label = new JLabel(this.bundle.getString(NAME));
		label.setSize(20, 10);
		labelPanel.add(label);
		JLabel applabel = new JLabel(this.bundle.getString(APPLICATION));
		applabel.setSize(20, 10);
		// labelPanel.add(applabel);
		this.extlabel.setSize(20, 10);
		return labelPanel;
	}

	/**
	 * 
	 */
	private void setUpUIImageIcon() {
		ImageIcon img = new ImageIcon(getClass()
				.getResource(ImagesPaths.SPHERE));

		setIconImage(img.getImage());
	}

	public void doLaunchAction(final boolean isForEditing) {

		Thread t = new Thread() {
			private NewBinary newBinary = NewBinary.this;

			@SuppressWarnings("unchecked")
			public void run() {
				Hashtable sendSession = (Hashtable) this.newBinary.getSession()
						.clone();
				this.newBinary.getMessagesPane().sF.client.voteDocument(
						this.newBinary.getSession(), this.newBinary.viewDoc
								.getRootElement().element("message_id")
								.attributeValue("value"),
						this.newBinary.viewDoc);

				Element root = this.newBinary.viewDoc.getRootElement();

				SupraClient sClient = new SupraClient((String) sendSession
						.get("address"), (String) sendSession.get("port"));

				sClient
						.setSupraSphereFrame(this.newBinary.getMessagesPane().sF);

				Hashtable getInfo = new Hashtable();

				getInfo.put("isForEditing", new Boolean(isForEditing)
						.toString());
				getInfo.put("document", this.newBinary.viewDoc);

				this.newBinary.logger.info("DOC :"
						+ this.newBinary.viewDoc.asXML());
				String dataId = root.element("data_id").attributeValue("value");

				getInfo.put("data_filename", dataId);
				String internal = dataId;
				StringTokenizer st = new StringTokenizer(internal, "_____");
				// String fname = st.nextToken();
				this.newBinary.logger
						.info("only filename...doesn't really matter: "
								+ st.nextToken());
				// getInfo.put("fname",fname);

				sendSession.put("passphrase",
						this.newBinary.getMessagesPane().sF.getTempPasswords()
								.getTempPW(
										((String) sendSession
												.get("supra_sphere"))));
				sendSession.put("getInfo", getInfo);
				sClient.startZeroKnowledgeAuth(sendSession, "GetBinary");

			}
		};
		t.start();

	}

	public void addButtons() {

		JPanel buttons = new JPanel();

		buttons.add(this.publish);
		this.c.gridwidth = 1;
		this.c.gridx = 0;
		this.c.gridy = 3;
		this.c.weightx = this.c.weighty = 0.0;
		this.c.anchor = GridBagConstraints.WEST;
		this.gridbag.setConstraints(buttons, this.c);
		this.allPanel.add(buttons);

//		this.publish
//				.addActionListener(new NewBinaryPublishActionListener(this));
		this.allPanel.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(this.allPanel);

		pack();
		setVisible(true);

	} // addButtons()

	public void addFillButtons() {

		this.setFillMessage(true);
		JPanel buttons = new JPanel();

		JButton openForEditing = new JButton(this.bundle
				.getString(OPEN_FOR_EDITING));

		openForEditing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent g) {
				doLaunchAction(true);
				dispose();
			}
		});

		JButton closeEditing = new JButton(this.bundle.getString(END_EDITING));

		if (this.viewDoc != null) {
			if (this.fm.isAlreadyEditing(this.viewDoc.getRootElement().element(
					"data_id").attributeValue("value"))) {

				buttons.add(closeEditing);
			} else {
				buttons.add(openForEditing);
			}

		}

		closeEditing.addActionListener(new ActionListener() {
			private NewBinary newBinary = NewBinary.this;

			public void actionPerformed(ActionEvent g) {

				this.newBinary.fm.removeFromEditing(this.newBinary.viewDoc
						.getRootElement().element("data_id").attributeValue(
								"value"));
				dispose();

			}
		});

		JButton launch = new JButton(this.bundle.getString(OPEN));

		buttons.add(launch);

		launch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent g) {
				doLaunchAction(false);
				dispose();
			}
		});
		JButton launchPDF = new JButton(this.bundle.getString(OPEN_AS_PDF));

		String lowerCase = this.viewDoc.getRootElement().element("data_id")
				.attributeValue("value");

		if (lowerCase.endsWith("doc") || lowerCase.endsWith("ppt")
				|| lowerCase.endsWith("xls") || lowerCase.endsWith("pdf")
				|| lowerCase.endsWith("rtf")) {

			buttons.add(launchPDF);

		}

		//launchPDF.addActionListener(new NewBinaryLaunchPDFActionListener(this));

		JButton saveAsPDF = new JButton(this.bundle.getString(SAVE_AS_PDF));

		//saveAsPDF.addActionListener(new NewBinarySaveAsPDFActionListener(this)); // saveAsPDF.addActionListener

		JButton saveAs = new JButton(this.bundle.getString(SAVE_AS));

		buttons.add(saveAs);

		if (lowerCase.endsWith("doc") || lowerCase.endsWith("ppt")
				|| lowerCase.endsWith("xls") || lowerCase.endsWith("pdf")) {

			buttons.add(saveAsPDF);

		}

		//saveAs.addActionListener(new NewBinarySaveAsActionListener(this)); // saveAs.addActionListener

		// Element root = this.viewDoc.getRootElement();
		// final String person_invited = root.element("giver").attributeValue(
		// "value");

		JButton recall = new JButton(this.bundle.getString(DELETE));

		recall.addMouseListener(new MouseAdapter() {
			private NewBinary newBinary = NewBinary.this;

			public void mousePressed(MouseEvent e) {

				try {

					String sphereId = (String) this.newBinary.getSession().get(
							"sphere_id");

					Object[] options = { NewBinary.this.bundle.getString(YES),
							NewBinary.this.bundle.getString(NO) };

					final int YES = 0;

					int choice = JOptionPane
							.showOptionDialog(
									null,
									NewBinary.this.bundle
											.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE),
									NewBinary.this.bundle.getString(WARNING),
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE, null, options,
									options[0]);
					if (choice == YES) {

						this.newBinary.getMessagesPane().client.recallMessage(
								this.newBinary.getSession(),
								this.newBinary.viewDoc, sphereId);

						this.newBinary.dispose();
					}

				} catch (NullPointerException npe) {
				}

			}

		});

		String giver = this.viewDoc.getRootElement().element("giver")
				.attributeValue("value");

		String systemName = this.mP.client.getVerifyAuth().getSystemName(
				(String) this.session.get("real_name"));
		if (giver.equals((String) this.session.get("real_name"))
				|| systemName.equals((String) this.session.get("sphere_id"))) {
			// if (giver.equals((String)session.get("real_name"))) {
			buttons.add(recall);
		}

//		JButton reply = new JButton(NewBinary.this.bundle.getString(APPLY));
//		reply.addActionListener(new NewBinaryReplyActionListener(this));
//
//		JButton update = new JButton(NewBinary.this.bundle.getString(UPDATE1));
//
//		update.addActionListener(new NewBinaryUpdateActionListener(this));

		// String apath = "//file/reply";

		// buttons.add(reply);

		// buttons.add(update);

		//

		// apath = "//file/vote";

//		Vector list = this.mP.getVotesOn(this.viewDoc);

//		for (int i = 0; i < list.size(); i++) {

//			Element votelm = ((Document) list.get(i)).getRootElement();

//			String member_name = votelm.element("giver")
//					.attributeValue("value");

//			if (member_name.equals((String) this.session.get("real_name"))) {
//
//				already = true;
//
//			}

//		}

		//
		this.c.gridwidth = 2;
		this.c.gridx = 0;
		this.c.gridy = 3;
		this.c.weightx = this.c.weighty = 0.0;
		this.c.anchor = GridBagConstraints.WEST;
		this.gridbag.setConstraints(buttons, this.c);
		this.allPanel.add(buttons);

		this.allPanel.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(this.allPanel);

		pack();
		setVisible(true);

	}

	public void fillDoc(Document xmldoc) {
		this.labelPanel.add(this.extlabel);

		this.fieldPanel.add(this.extension);

		this.viewDoc = xmldoc;

		Element fill = this.viewDoc.getRootElement();

		this.giver.setText(fill.element("giver").attributeValue("value"));
		this.subject.setText(fill.element("subject").attributeValue("value"));
		this.subject.setEditable(false);

		String internal = fill.element("data_id").attributeValue("value");
		StringTokenizer st = new StringTokenizer(internal, "_____");
		st.nextToken();

		this.extension.setText(st.nextToken());
		this.logger.warn("Extension..: " + this.extension.getText());
		this.extension.setEditable(false);
		// application.setText(fill.element("extension").attributeValue("value"));

		this.bodyEditor.setText(fill.element("body").getText());

	}

	@SuppressWarnings("unchecked")
	public Document XMLDoc() {

		Document createDoc = DocumentHelper.createDocument();
		Element email = createDoc.addElement("email");
		Hashtable clientSession = this.mP.sF.getRegisteredSession(
				(String) this.session.get("supra_sphere"), "DialogsMainCli");

		this.session.put("session", (String) clientSession.get("session"));
		Hashtable all = this.mP.sF.client.createMessageIdOnServer(this.session);

		String messageId = (String) all.get("messageId");

		email.addElement("original_id").addAttribute("value", messageId);
		email.addElement("message_id").addAttribute("value", messageId);
		email.addElement("thread_id").addAttribute("value", messageId);

		email.addElement("giver").addAttribute("value", this.giver.getText());

		email.addElement("subject").addAttribute("value",
				this.subject.getText());
		// email.addElement("moment").addAttribute("value",moment);
		// email.addElement("last_updated").addAttribute("value",moment);
		email.addElement("last_updated_by").addAttribute("value",
				(String) this.session.get("real_name"));

		if (this.response_id == null) {

			email.addElement("thread_type").addAttribute("value", "file");
		} else {
			email.addElement("response_id").addAttribute(
					"value",
					this.viewDoc.getRootElement().element("message_id")
							.attributeValue("value"));
			email.addElement("original_id").addAttribute(
					"value",
					this.viewDoc.getRootElement().element("original_id")
							.attributeValue("value"));
			email.addElement("thread_type").addAttribute(
					"value",
					this.viewDoc.getRootElement().element("thread_type")
							.attributeValue("value"));
		}
		email.addElement("type").addAttribute("value", "file");

		// email.addElement("application").addAttribute("value",application.getText());
		// email.addElement("extension").addAttribute("value",extension.getText());

		this.body = new DefaultElement("body");
		this.body.setText(this.bodyEditor.getText());

		this.body.addElement("version").addAttribute("value", "3000");

		this.body.addElement("orig_body").setText(this.bodyEditor.getText());

		email.add(this.body);

		return createDoc;

	}

	@Override
	public void giveBodyFocus() {
		this.bodyEditor.requestFocus();
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return this.fileName;
	}

	public void setSubject(JTextField subject) {
		this.subject = subject;
	}

	public JTextField getSubject() {
		return this.subject;
	}

}
