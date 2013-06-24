/**
 * 
 */
package ss.client.ui.viewers;

import java.io.File;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.InputTextPaneListener;
import ss.client.event.ViewersInputPaneListener;
import ss.client.event.messagedeleters.SingleMessageDeleter;
import ss.client.localization.LocalizationLinks;
import ss.client.networking.SupraClient;
import ss.client.ui.MessagesPane;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.SphereListComponent;
import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;
import ss.client.ui.tempComponents.interfaces.ISphereListOwner;
import ss.client.ui.typeahead.TypeAheadManager;
import ss.client.ui.viewers.actions.NewBinaryLaunchPDFActionListener;
import ss.client.ui.viewers.actions.NewBinaryPublishActionListener;
import ss.client.ui.viewers.actions.NewBinarySaveAsActionListener;
import ss.client.ui.viewers.actions.NewBinarySaveAsPDFActionListener;
import ss.common.FileMonitor;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.util.SessionConstants;

/**
 * @author roman
 * 
 */
public class NewBinarySWT extends ContentTypeViewerSWT implements ISphereListOwner{
	
	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWBINARY);

	private static final String FILENAME = "NEWBINARY.FILENAME";

	private static final String SAVE = "NEWBINARY.SAVE";

	private static final String AUTHOR = "NEWBINARY.AUTHOR";

	private static final String NAME = "NEWBINARY.NAME";

	private static final String OPEN_FOR_EDITING = "NEWBINARY.OPEN_FOR_EDITING";

	private static final String END_EDITING = "NEWBINARY.END_EDITING";

	private static final String OPEN = "NEWBINARY.OPEN";

	private static final String OPEN_AS_PDF = "NEWBINARY.OPEN_AS_PDF";

	private static final String SAVE_AS = "NEWBINARY.SAVE_AS";

	private static final String SAVE_AS_PDF = "NEWBINARY.SAVE_AS_PDF";

	private static final String DELETE = "NEWBINARY.DELETE";

	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE = "NEWBINARY.ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NewBinarySWT.class);

	private Button publish = null;

	private String response_id = null;

	private String fileName = null;

	private FileMonitor fm = null;

	private Text bodyEditor;

	private String giverString = null;

	private Composite buttonComp;

	private Text subjectField;
	
	private Text tagText;

	private Composite labelComp;

	private Composite fieldComp;
	
	private String currentSphere;

	private SpheresCollectionByTypeObject sphereOwner;

	public NewBinarySWT(Hashtable session, MessagesPane mP, String fname, boolean canSelectSphere) {
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("NewBinarySWT creation launched");
		}
		if (fname != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("File name is: " + fname);
			}
			setFileName(fname);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("File name is null");
			}
		}
		this.session = session;

		this.setFillMessage(false);
		this.mP = mP;
		this.model_path = "//file/create";

		this.giverString = (String) session.get(SessionConstants.REAL_NAME);
		this.fm = new FileMonitor();
		
		createUI(canSelectSphere);
	}

	public NewBinarySWT(Hashtable session, MessagesPane mP, String fname,
			Document viewDoc) {
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("NewBinarySWT creation launched");
		}
		if (fname != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("File name is: " + fname);
			}
			setFileName(fname);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("File name is null");
			}
		}
		this.session = session;
		this.response_id = Statement.wrap(viewDoc).getMessageId();

		this.viewDoc = viewDoc;

		this.setFillMessage(false);
		this.mP = mP;
		this.model_path = "//library/create";

		this.giverString = (String) session.get(SessionConstants.REAL_NAME);
		this.fm = new FileMonitor();
		
		createUI(true);
	}

	public ContentTypeViewerSWT getFrame() {
		return this;
	}

	public void addKeyListener(InputTextPaneListener itpl) {
		ViewersInputPaneListener listener = new ViewersInputPaneListener(
				this.mP.sF, this.mP);
		this.subjectField.addKeyListener(listener);
		
		if(this.bodyEditor==null) {
			return;
		}
		
		this.bodyEditor.addKeyListener(listener);
	}

	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	private void createUI(boolean canSelectSphere) {
		if (logger.isDebugEnabled()) {
			logger.debug("createUI for New Binary started");
		}

		getShell().setImage(NewBinarySWT.this.sphereImage);
		getShell().setLayout(new GridLayout());

		createLabelPanel();
		if (logger.isDebugEnabled()) {
			logger.debug("canSelectSphere is " + canSelectSphere);
		}
		if(!canSelectSphere) {
			createBodyEditor();
		} else {
			this.sphereOwner = new SpheresCollectionByTypeObject(SupraSphereFrame.INSTANCE.client);
			new SphereListComponent(getShell(), this);
		}

		getShell().setVisible(true);
		getShell().forceActive();
		setFocusToSubjectField();
		if (logger.isDebugEnabled()) {
			logger.debug("createUI for New Binary finished");
		}
	}

	/**
	 * 
	 */
	private void createBodyEditor() {
		this.bodyEditor = new Text(getShell(), SWT.BORDER | SWT.MULTI
				| SWT.WRAP);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.bodyEditor.setLayoutData(data);
		if (logger.isDebugEnabled()) {
			logger.debug("Body Editor created");
		}
	}

	/**
	 * 
	 */
	private void createLabelPanel() {
		Composite comp = new Composite(getShell(), SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		comp.setLayoutData(data);

		this.labelComp = new Composite(comp, SWT.NONE);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.labelComp.setLayoutData(data);
		this.labelComp.setLayout(new GridLayout());
		
		Label author = new Label(this.labelComp, SWT.LEFT);
		author.setText(this.bundle.getString(AUTHOR));
		
		Label subject = new Label(this.labelComp, SWT.LEFT);
		data = new GridData();
		data.verticalIndent = 4;
		subject.setLayoutData(data);
		subject.setText(this.bundle.getString(NAME));
		
		Label tagLabel = new Label(this.labelComp, SWT.LEFT);
		tagLabel.setText("Tag: ");

		this.fieldComp = new Composite(comp, SWT.NONE);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.fieldComp.setLayoutData(data);
		this.fieldComp.setLayout(new GridLayout());
		
		Label giver = new Label(this.fieldComp, SWT.LEFT);
		giver.setText(this.giverString);
		
		this.subjectField = new Text(this.fieldComp, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		this.subjectField.setLayoutData(data);
		if ( this.viewDoc != null ){
			this.subjectField.setText( Statement.wrap(this.viewDoc).getSubject() );
		} else if ( getFileName() != null ){ 
			setSubjectOnFileName( new File(getFileName()).getName() );
		}
		
		this.tagText = new Text(this.fieldComp, SWT.SINGLE | SWT.BORDER);
		this.tagText.setLayoutData(new GridData(GridData.FILL_BOTH));
		TypeAheadManager.INSTANCE.addKeywordAutoComplete(this.tagText);
		
		if (this.fileName != null) {
			Label filename = new Label(this.labelComp, SWT.LEFT);
			data = new GridData();
			data.verticalIndent = 4;
			filename.setLayoutData(data);
			filename.setText(this.bundle.getString(FILENAME));

			Label name = new Label(this.fieldComp, SWT.LEFT);
			name.setText(this.fileName);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Create label created");
		}
	}
	
	public void setSubjectOnFileName( final String fileName ) {
		 int index = fileName.lastIndexOf(".");
		 if (index == -1) {
			 setSubject( fileName );
		 } else {
			 setSubject( new StringBuffer(fileName).substring(0, index) );
		 }
	}

	public void doLaunchAction(final boolean isForEditing) {
		Thread t = new Thread() {
			private NewBinarySWT newBinary = NewBinarySWT.this;

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

				logger.info("DOC :"
						+ this.newBinary.viewDoc.asXML());
				String dataId = root.element("data_id").attributeValue("value");

				getInfo.put("data_filename", dataId);
				String internal = dataId;
				StringTokenizer st = new StringTokenizer(internal, "_____");
				// String fname = st.nextToken();
				logger
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

		this.buttonComp = new Composite(getShell(), SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.buttonComp.setLayoutData(data);

		this.buttonComp.setLayout(new GridLayout(4, false));

		this.publish = new Button(this.buttonComp, SWT.PUSH);
		this.publish.setText(this.bundle.getString(SAVE));
		this.publish.addSelectionListener(new NewBinaryPublishActionListener(
				this));

		getShell().setVisible(true);
		getShell().layout();

	} // addButtons()

	public void addFillButtons() {
		this.setFillMessage(true);

		if (this.buttonComp == null) {
			this.buttonComp = new Composite(getShell(), SWT.NONE);
			GridData data = new GridData();
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = false;
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;
			this.buttonComp.setLayoutData(data);

			this.buttonComp.setLayout(new GridLayout(6, false));
		}

		if (this.viewDoc != null) {
			if (this.fm.isAlreadyEditing(this.viewDoc.getRootElement().element(
					"data_id").attributeValue("value"))) {
				addCloseForEditingButton();
			} else {
				addOpenForEditingButton();
			}
		}

		Button launch = new Button(this.buttonComp, SWT.PUSH);
		launch.setText(this.bundle.getString(OPEN));
		launch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent g) {
				doLaunchAction(false);
				getShell().dispose();
			}
		});

		String lowerCase = this.viewDoc.getRootElement().element("data_id")
				.attributeValue("value");
		if (lowerCase.endsWith("doc") || lowerCase.endsWith("ppt")
				|| lowerCase.endsWith("xls") || lowerCase.endsWith("pdf")
				|| lowerCase.endsWith("rtf") || lowerCase.endsWith("sxc")
				|| lowerCase.endsWith("odp") || lowerCase.endsWith("odt")) {
			addLaunchPDFButton();
		}

		if (lowerCase.endsWith("doc") || lowerCase.endsWith("ppt")
				|| lowerCase.endsWith("xls") || lowerCase.endsWith("pdf")
				|| lowerCase.endsWith("sxc") || lowerCase.endsWith("odp")
				|| lowerCase.endsWith("odt")) {
			addSaveAsPDFButton();
		}

		Button saveAs = new Button(this.buttonComp, SWT.PUSH);
		saveAs.setText(this.bundle.getString(SAVE_AS));
		saveAs.addSelectionListener(new NewBinarySaveAsActionListener(this)); // saveAs.addActionListener

		String giver = this.viewDoc.getRootElement().element("giver")
				.attributeValue("value");

		String systemName = this.mP.client.getVerifyAuth().getSystemName(
				(String) this.session.get("real_name"));
		if (giver.equals((String) this.session.get("real_name"))
				|| systemName.equals((String) this.session.get("sphere_id"))) {
			addRecallButton();
		}

		getShell().setVisible(true);
		getShell().layout();
	}

	private void addRecallButton() {
		Button recall = new Button(this.buttonComp, SWT.PUSH);
		recall.setText(this.bundle.getString(DELETE));
		recall.addMouseListener(new MouseAdapter() {
			private NewBinarySWT newBinary = NewBinarySWT.this;

			public void mouseDown(MouseEvent e) {
				(new SingleMessageDeleter(this.newBinary.getMessagesPane(), true, this.newBinary.bundle
						.getString(ARE_YOU_SURE_YOU_WANT_TO_DELETE_THIS_FILE), this.newBinary.shell)).executeDeliting(this.newBinary
								.getViewDoc());
			}
		});
	}

	private void addSaveAsPDFButton() {
		Button saveAsPDF = new Button(this.buttonComp, SWT.PUSH);
		saveAsPDF.setText(this.bundle.getString(SAVE_AS_PDF));
		saveAsPDF.addSelectionListener(new NewBinarySaveAsPDFActionListener(
				this)); // saveAsPDF.addActionListener
	}

	private void addLaunchPDFButton() {
		Button launchPDF = new Button(this.buttonComp, SWT.PUSH);
		launchPDF.setText(this.bundle.getString(OPEN_AS_PDF));

		launchPDF.addSelectionListener(new NewBinaryLaunchPDFActionListener(
				this));
	}

	private void addCloseForEditingButton() {
		Button closeEditing = new Button(this.buttonComp, SWT.PUSH);
		closeEditing.setText(this.bundle.getString(END_EDITING));

		closeEditing.addSelectionListener(new SelectionAdapter() {
			private NewBinarySWT newBinary = NewBinarySWT.this;

			public void widgetSelected(SelectionEvent g) {
				this.newBinary.fm.removeFromEditing(this.newBinary.viewDoc
						.getRootElement().element("data_id").attributeValue(
								"value"));
				getShell().dispose();

			}
		});
	}

	private void addOpenForEditingButton() {
		Button openForEditing = new Button(this.buttonComp, SWT.PUSH);
		openForEditing.setText(this.bundle.getString(OPEN_FOR_EDITING));
		openForEditing.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent g) {
				doLaunchAction(true);
				getShell().dispose();
			}
		});
	}

	public void fillDoc(FileStatement file) {
		String fname = file.getDataId();
		StringTokenizer tokenizer = new StringTokenizer(fname, "_____");
		tokenizer.nextToken();
		String realFName = tokenizer.nextToken();

		setFileName(realFName);

		if (logger.isDebugEnabled()) {
			logger.debug("File name is: " + realFName);
		}

		Label filename = new Label(this.labelComp, SWT.LEFT);
		GridData data = new GridData();
		data.verticalIndent = 4;
		filename.setLayoutData(data);
		filename.setText(this.bundle.getString(FILENAME));

		Label name = new Label(this.fieldComp, SWT.LEFT);
		name.setText(realFName);

		if(this.bodyEditor!=null) {
			this.bodyEditor.setText(file.getBody());
		}
		setSubject( file.getSubject() );
		this.viewDoc = file.getBindedDocument();
	}

	@SuppressWarnings("unchecked")
	public FileStatement createFileStatement() {
		final FileStatement file = new FileStatement();

		Hashtable clientSession = SupraSphereFrame.INSTANCE.getRegisteredSession(
				(String) this.session.get("supra_sphere"), "DialogsMainCli");

		this.session.put("session", (String) clientSession.get("session"));
		Hashtable all = this.mP.sF.client.createMessageIdOnServer(this.session);

		String messageId = (String) all.get("messageId");

		file.setMessageId(messageId);
		file.setThreadId(messageId);
		file.setOriginalId(messageId);
		file.setGiver(this.giverString);
		file.setGiverUsername(this.mP.client.getVerifyAuth()
				.getLoginForContact(this.giverString));

		String subjectText = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
					return getSubject();
			}	
		});
		file.setSubject(subjectText);
		
		file.setLastUpdatedBy((String) this.session
				.get(SessionConstants.REAL_NAME));

		if (this.response_id == null) {
			file.setThreadType("file");
		} else {
			file.setResponseId(this.viewDoc.getRootElement().element(
			"message_id").attributeValue("value"));
			file.setOriginalId(this.viewDoc.getRootElement().element(
			"original_id").attributeValue("value"));
			file.setThreadType(this.viewDoc.getRootElement().element(
			"original_id").attributeValue("value"));
		}
		file.setType("file");

		String bodyText = UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return NewBinarySWT.this.bodyEditor.getText();
			}
		});

		if(this.bodyEditor!=null) {
			file.setBody(bodyText);
			file.setOrigBody(bodyText);
		} else {
			file.setBody("");
			file.setOrigBody("");
		}

		file.setVersion("3000");
		return file;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return this.fileName;
	}

	public String getSubject() {
		return this.subjectField.getText();
	}
	
	public void setSubject( final String subject ){
		if ( StringUtils.isBlank( subject ) ) {
			logger.warn("Trying to set blank subject, ignoring");
			return;
		}
		this.subjectField.setText( subject );
	}

	@Override
	public void giveBodyFocus() {
		if(this.bodyEditor==null) {
			return;
		}
		this.bodyEditor.setFocus();
	}

	public static String createFileName() {
		final Shell parentShell = SDisplay.display.get().getActiveShell();

		FileDialog fd = new FileDialog(parentShell);

		fd.open();
		String directory = fd.getFilterPath();
		String fname = fd.getFileName();
		String fullName = directory + System.getProperty("file.separator") + fname;

		return fullName;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.ISphereListOwner#getSF()
	 */
	public SupraSphereFrame getSF() {
		return this.mP.sF;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.ISphereListOwner#setCurrent(java.lang.String)
	 */
	public void setCurrent(String current) {
		this.currentSphere = current;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.ISphereListOwner#setFocusToSubjectField()
	 */
	public void setFocusToSubjectField() {
		this.subjectField.setFocus();
	}

	/**
	 * @return
	 */
	public String getCurrentSphere() {
		if(this.currentSphere==null) {
			return this.mP.getSphereStatement().getSystemName();
		} else {
			return this.mP.client.getVerifyAuth().getSystemName(this.currentSphere);
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.tempComponents.interfaces.ISphereListOwner#getSphereOwner()
	 */
	public SpheresCollectionByTypeObject getSphereOwner() {
		return this.sphereOwner;
	}

	/**
	 * @return
	 */
	public String getTagText() {
		return this.tagText.getText();
	}
}
