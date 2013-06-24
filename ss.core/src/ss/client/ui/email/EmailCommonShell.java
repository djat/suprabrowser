/**
 * 
 */
package ss.client.ui.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SMessageBrowser;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.LocationUtils;
import ss.common.StringUtils;
import ss.domainmodel.ExternalEmailStatement;
import ss.framework.networking2.ReplyObjectHandler;

/**
 * @author zobo
 * 
 */
public abstract class EmailCommonShell implements EmailShellButtonsActions {

	private static final String ADD_ROUTING_NUMBER = "EMAILCOMMONSHELL.ADD_ROUTING_NUMBER";

	private static final String SEND_TO_LABEL_STRING = "EMAILCOMMONSHELL.SEND_TO";

	private static final String REPLY_TO_LABEL_STRING = "EMAILCOMMONSHELL.REPLY_TO";

	private static final String FROM_LABEL_STRING = "EMAILCOMMONSHELL.FROM";

	private static final String CC_LABEL_STRING = "EMAILCOMMONSHELL.CC";

	private static final String BCC_LABEL_STRING = "EMAILCOMMONSHELL.BCC";

	private static final String SUBJECT_LABEL_STRING = "EMAILCOMMONSHELL.SUBJECT";

	private static final String BODY_LABEL_STRING = "EMAILCOMMONSHELL.BODY";
	
	private static final String RECIPIENT_SHOULD_BE_SPECIFIED = "EMAILCOMMONSHELL.RECIPIENT_SHOULD_BE_SPECIFIED";
	
	private static final String SENDER_SHOULD_BE_SPECIFIED = "EMAILCOMMONSHELL.SENDER_SHOULD_BE_SPECIFIED";
	
	private static final String SUBJECT_SHOULD_BE_SPECIFIED = "EMAILCOMMONSHELL.SUBJECT_SHOULD_BE_SPECIFIED";
	
	protected static final String BREAK = "<br>";
	
	protected static final String ARROW = "&gt;&nbsp;";
	
	private final static ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_EMAILCOMMONSHELL);

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailCommonShell.class);

	private Shell shell;

	private SupraSphereFrame sF;

	protected ExternalEmailStatement originalEmail;

	private SMessageBrowser bodyText;

	private EmailSimpleShellCompositeUnit bccText;

	private EmailSimpleShellCompositeUnit ccText;

	private EmailSimpleShellCompositeUnit toText;

	private EmailSimpleShellCompositeUnit subjectText;

	private EmailFromCompositeUnit fromText;

	private EmailButtonsShellCompositeUnit buttons;

	private EmailCheckedShellCompositeUnit replyTo;

	private EmailController emailController;

	private AttachFileComponent attachFileComponent;

	private boolean addressable;

	private Composite compBody;

	public EmailCommonShell(SupraSphereFrame sF,
			ExternalEmailStatement originalEmail, EmailController controller,
			boolean addressable) {
		this.sF = sF;
		this.originalEmail = originalEmail;
		this.emailController = controller;
		this.addressable = addressable;
		this.shell = new Shell(this.sF.getDisplay());
		centerComponent(this.shell);
		this.shell.setText(getTitle());
		initIcons();
		createCommonGUI(this.shell);
		this.shell.open();
		if (logger.isDebugEnabled()){
			logger.debug("Email Shell Created");
		}
	}

	public EmailCommonShell(SupraSphereFrame sF,
			ExternalEmailStatement originalEmail, EmailController controller) {
		this(sF, originalEmail, controller, true);
	}

	protected abstract String getImagePath();

	private void centerComponent(Composite comp) {

		Monitor primary = this.sF.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();

		Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		comp.setLocation(x, y);
	}

	protected abstract String getTitle();

	private void initIcons() {
		try {
			Image image = new Image(SDisplay.display.get(), getClass()
					.getResource(getImagePath()).openStream());
			this.shell.setImage(image);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	private void createCommonGUI(Shell main) {
		if (logger.isDebugEnabled()){
			logger.debug("Create Common GUI started");
		}

		boolean enabled = this.addressable;

		GridData data;

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		main.setLayout(layout);

		boolean isEmailSphere = this.emailController.isEmailSphere();
		Composite multiSend = createSendComposite(main, enabled, isEmailSphere);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		multiSend.setLayoutData(data);

		activateTypeAheadToControls(enabled, isEmailSphere);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.compBody = new Composite(main, SWT.BORDER);
		this.compBody.setLayoutData(data);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		this.compBody.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.subjectText = new EmailSimpleShellCompositeUnit(this.compBody,
				SWT.NULL, getSubjectLabelText(), "", true);
		this.subjectText.setLayoutData(data);
		setSubjectFromEmail(this.originalEmail);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		Label label = new Label(this.compBody, SWT.LEFT);
		label.setText(getBodyLabelText());
		label.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.bodyText = new SMessageBrowser(this);
		this.bodyText.setLayoutData(data);
		
		File f = new File(LocationUtils.getTinymceBase()+"tinymce/ss_richtext.html");
		this.bodyText.setUrl(f.getAbsolutePath());
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.BEGINNING;
		data.verticalAlignment = SWT.BEGINNING;
		this.buttons = new EmailButtonsShellCompositeUnit(this.compBody, SWT.NULL,
				this);

		main.layout();
		if (logger.isDebugEnabled()){
			logger.debug("Create Common GUI finished");
		}
	}

	private Composite createSendComposite(Composite parent, boolean enabled, boolean isEmailSphere) {
		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		layout.numColumns = 5;
		main.setLayout(layout);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 4;
		Composite compFields = new Composite(main, SWT.BORDER);
		compFields.setLayoutData(data);

		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		layout.numColumns = 1;
		compFields.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		Composite compSend = new Composite(compFields, SWT.NULL);
		compSend.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		Composite compReply = new Composite(compFields, SWT.NULL);
		compReply.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.BEGINNING;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 1;
		Composite compAttach = new Composite(main, SWT.BORDER);
		compAttach.setLayoutData(data);

		// Send composite:
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		layout.numColumns = 1;
		compSend.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.toText = new EmailSimpleSetShellCompositeUnit(compSend, SWT.NULL,
				getSendToLabelText(), "", enabled);
		this.toText.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.ccText = new EmailSimpleAddShellCompositeUnit(compSend, SWT.NULL,
				getCCLabelText(), "", enabled);
		this.ccText.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.bccText = new EmailSimpleAddShellCompositeUnit(compSend, SWT.NULL,
				getBCCLabelText(), "", enabled);
		this.bccText.setLayoutData(data);

		// From composite:
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		layout.numColumns = 1;
		compReply.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.fromText = new EmailFromCompositeUnit(compReply,
				SWT.NULL, bundle.getString(FROM_LABEL_STRING), "", enabled);
		this.fromText.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.replyTo = new EmailCheckedShellCompositeUnit(compReply, SWT.NULL,
				bundle.getString(REPLY_TO_LABEL_STRING), bundle.getString(ADD_ROUTING_NUMBER), "", enabled);
		this.replyTo.setLayoutData(data);
		this.replyTo.setCheckSelected(false);//!isEmailSphere);
		this.replyTo.setContragentUnit(this.fromText);

		// Attach composite:
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginTop = 1;
		layout.marginBottom = 1;
		layout.numColumns = 1;
		compAttach.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.attachFileComponent = new AttachFileComponent(compAttach);
		this.attachFileComponent.getContent().setLayoutData(data);

		return main;
	}

	@SuppressWarnings("unchecked")
	private void activateTypeAheadToControls(boolean enabled, boolean isEmailSphere) {

		if (logger.isDebugEnabled()){
			logger.debug("Activate Type Ahead To Controls performing");
		}
		final List<String> emailNamesOfCurrentSphere = this.emailController
				.getCurrentSphereEmailsList();
		if (logger.isDebugEnabled()){
			if (emailNamesOfCurrentSphere.isEmpty()){
				logger.debug("Set of addresses for From field is empty");
			} else {
				logger.debug("Elements in emailNamesOfCurrentSphere: " + emailNamesOfCurrentSphere.size());
				for (String s : emailNamesOfCurrentSphere){
					logger.debug("Next in emailNamesOfCurrentSphere: " + s);
				}
			}
		}

		this.replyTo.setRoutingNumber(this.emailController.getRoutingNumber());

		if (enabled) {
			SupraSphereFrame.INSTANCE.client.queryEmailsOfPossibleRecipientsForUi(this.emailController.getSphereId(), new ReplyObjectHandler<ArrayList<String>>( (Class)ArrayList.class ) {
				@Override
				protected void objectReturned(final ArrayList<String> reply) {
					activateTypeAheadBody(emailNamesOfCurrentSphere, reply);
				}
			});
		}

		setSendToFromEmail(this.originalEmail);

		setCCFromEmail();

		setBCCFromEmail();

		if (!setFromFromEmail(this.originalEmail)) {
			if (!emailNamesOfCurrentSphere.isEmpty())
				this.fromText.setText(emailNamesOfCurrentSphere.get(0));
		}

		if (!isEmailSphere){
			if (!setReplyToFromEmail(this.originalEmail)) {
				if (!emailNamesOfCurrentSphere.isEmpty())
					this.replyTo.setText(emailNamesOfCurrentSphere.get(0));
			}
		}

	}
	
	private void activateTypeAheadBody(final List<String> emailNamesOfCurrentSphere, final List<String> emailNamesOfPossibleSendTo){
		if (logger.isDebugEnabled()){
			if ((emailNamesOfPossibleSendTo==null)||(emailNamesOfPossibleSendTo.isEmpty())){
				logger.debug("Set of addresses for To/CC/BCC fields is empty");
			} else {
				logger.debug("Elements in emailNamesOfPossibleSendTo: " + emailNamesOfPossibleSendTo.size());
				for (String s : emailNamesOfPossibleSendTo){
					logger.debug("Next in emailNamesOfPossibleSendTo: " + s);
				}
			}
		}
		if (emailNamesOfPossibleSendTo != null) {
			this.toText.activateTypeAhead(new ArrayList<String>(
				emailNamesOfPossibleSendTo));
			this.ccText.activateTypeAhead(emailNamesOfPossibleSendTo);
			this.bccText.activateTypeAhead(emailNamesOfPossibleSendTo);
		}
		this.fromText.activateTypeAhead(emailNamesOfCurrentSphere);
		this.replyTo.activateTypeAhead(emailNamesOfCurrentSphere);
	}

	protected String getBodyLabelText() {
		return bundle.getString(BODY_LABEL_STRING);
	}

	protected void setBCCFromEmail() {
	}

	protected void setCCFromEmail() {
	}

	protected void setSendToFromEmail(ExternalEmailStatement email) {

	}

	protected String getSubjectLabelText() {
		return bundle.getString(SUBJECT_LABEL_STRING);
	}

	protected void setSubjectFromEmail(ExternalEmailStatement email) {
		if (email != null)
			this.subjectText.setText(email.getSubject());
	}

	protected void setBodyFromEmail(ExternalEmailStatement email) {
		if (email != null) {
			setTextToEditor(email.getBody());
		}	
	}
	
	protected void setTextToEditor(String text) {
		if(text==null) {
			return;
		}
		this.bodyText.setTextToTextEditor(text);
	}

	/**
	 * @param email
	 *            ExternalEmailStatement that is replied
	 * @return true if From field was set from email, false otherwise.
	 */
	protected boolean setFromFromEmail(ExternalEmailStatement email) {
		return false;
	}

	/**
	 * @param email
	 *            email ExternalEmailStatement that is forwarded
	 * @return true if ReplyTo field was set from email, false otherwise.
	 */
	protected boolean setReplyToFromEmail(ExternalEmailStatement email) {
		return false;
	}

	protected String getBCCLabelText() {
		return bundle.getString(BCC_LABEL_STRING);
	}

	protected String getCCLabelText() {
		return bundle.getString(CC_LABEL_STRING);
	}

	protected String getSendToLabelText() {
		return bundle.getString(SEND_TO_LABEL_STRING);
	}

	/**
	 * @return the bccText
	 */
	public String getBccText() {
		return this.bccText.getText();
	}

	/**
	 * @param bccText
	 *            the bccText to set
	 */
	public void setBccText(String text) {
		this.bccText.setText(text);
	}

	/**
	 * @param bodyText
	 *            the bodyText to set
	 */
	public void setBodyText(String text) {
		if (text != null) {
			this.bodyText.setText(text);
		}	
	}

	/**
	 * @return the ccText
	 */
	public String getCcText() {
		return this.ccText.getText();
	}

	/**
	 * @param ccText
	 *            the ccText to set
	 */
	public void setCcText(String text) {
		this.ccText.setText(text);
	}

	/**
	 * @return the toText
	 */
	public String getToText() {
		return this.toText.getText();
	}

	/**
	 * @param toText
	 *            the toText to set
	 */
	public void setToText(String text) {
		this.toText.setText(text);
	}

	/**
	 * @return the subjectText
	 */
	public String getSubjectText() {
		return this.subjectText.getText();
	}

	/**
	 * @param subjectText
	 *            the subjectText to set
	 */
	public void setSubjectText(String text) {
		this.subjectText.setText(text);
	}

	public void buttonOKPerformed() {
		logger.info("Button OK in Email Shell clicked");
		try {
			checkField(getToText(), bundle.getString(RECIPIENT_SHOULD_BE_SPECIFIED));
			checkField(getFromText(), bundle.getString(SENDER_SHOULD_BE_SPECIFIED));
			checkField(getSubjectText(), bundle.getString(SUBJECT_SHOULD_BE_SPECIFIED));

			this.bodyText.invokeRichTextJSMonitor();
		} catch (Exception e) {
			logger.info("Wrong fields filling");
		}
	}

	public void createAndPublishEmail(String bodyText) {
		final String CCText = SpherePossibleEmailsSet.removeComma(getCcText());
		final String BCCText = SpherePossibleEmailsSet.removeComma(getBccText());
		EmailAddressesContainer addressesContainer = new EmailAddressesContainer(
				getToText(), getFromText(), getReplyTo(), CCText,
				BCCText);
		this.emailController.makeAndDeliverEmail(addressesContainer,
				getSubjectText(), new StringBuffer(bodyText),
				this.attachFileComponent.getFiles(), getIsReply(),
				isAddRotingNumber());
	}

	private void checkField(String field, String alertMessage) throws Exception {
		if (StringUtils.isBlank(field)) {
			UserMessageDialogCreator.error(alertMessage);
//			JOptionPane.showMessageDialog(null, alertMessage, bundle.getString(ALERT),
//					JOptionPane.ERROR_MESSAGE);
			throw new Exception();
		}
	}

	public void buttonCancelPerformed() {
		logger.info("Button CANCEL in Email Shell clicked");
		this.shell.close();
	}

	protected abstract boolean getIsReply();

	public void close() {
		this.shell.dispose();
	}

	/**
	 * @return the originalEmail
	 */
	public ExternalEmailStatement getOriginalEmail() {
		return this.originalEmail;
	}

    /**
     * turn buttons enabled or disabled
     * @param lock true makes buttons disabled, false makes enabled
     */
	public void lockButtons(boolean lock) {
		this.buttons.lockButtons(lock);
	}

	/**
	 * @return the fromText
	 */
	public String getFromText() {
		return this.fromText.getText();
	}

	protected void setFromText(String text) {
		this.fromText.setText(text);
	}

	/**
	 * @return the replyTo
	 */
	public String getReplyTo() {
		return this.replyTo.getText();
	}

	protected void setReplyTo(String text) {
		this.replyTo.setText(text);
	}

	private boolean isAddRotingNumber() {
		return this.replyTo.isChecked();
	}

	protected void setCheckButtonEnabled(boolean checked, boolean enabled) {
		this.replyTo.setCheckEnabled(checked, enabled);
	}
	
	public Composite getCompBody() {
		return this.compBody;
	}
	
	public String getOrigBody() {
		return "";
	}

	/**
	 * @return the attachFileComponent
	 */
	public AttachFileComponent getAttachFileComponent() {
		return this.attachFileComponent;
	}
}
