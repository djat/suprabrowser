/**
 * 
 */
package ss.client.ui.email;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.event.createevents.CreateEmailAction;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereMember;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.VotedMember;
import ss.domainmodel.SphereItem.SphereType;
import ss.util.EmailUtils;
import ss.util.SessionConstants;
import ss.util.StringProcessor;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class EmailController {

	private MessagesPane mp;

	private EmailCommonShell emailShell;

	private Hashtable session;

	private String message_id;

	@SuppressWarnings("unused")
    private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailController.class);

	public EmailController(MessagesPane mp,
			Hashtable session) {
		this.mp = mp;
		this.session = session;
	}

	public void emailDoubleClicked(Statement statement) {
		ExternalEmailStatement email = getEmail(statement);
		if (email != null) {
			SupraSphereFrame.INSTANCE.tabbedPane.showEmail(email, SupraSphereFrame.INSTANCE,
					EmailController.this.session, this.mp);
		}
	}

	public void emailClicked(Statement statement) {
		ExternalEmailStatement email = getEmail(statement);
		if (email != null) {
			this.mp.showEmailBrowser(email);
		}
	}

	private ExternalEmailStatement getEmail(Statement statement) {
		if (statement.isEmail()) {
			return ExternalEmailStatement.wrap(statement.getBindedDocument());
		}
		return null;
	}

	public void createEmail(final String sendText) {
		try {
			checkEnabledEmail();
			logger.info("Starting composing email");
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					EmailController.this.emailShell = new ComposeEmailShell(
							SupraSphereFrame.INSTANCE, EmailController.this);
					EmailController.this.emailShell.setToText(sendText);
				}
			});
		} catch (SupraEmailException ex) {
			logger.warn("Composing error", ex);
			UserMessageDialogCreator.error("Composing email is not allowed in current sphere");
		}
	}

	/**
	 * Forward email processing
	 * 
	 * @param statement
	 */
	public void clickedForwardEmail(Statement statement) {
		clickedForwardEmail(getEmail(statement));
	}

	/**
	 * Reply email processing
	 * 
	 * @param statement
	 */
	public void clickedReplyEmail(Statement statement) {
		clickedReplyEmail(getEmail(statement));
	}

	/**
	 * Forward email processing
	 * 
	 * @param statement
	 */
	public void clickedForwardEmail(final ExternalEmailStatement statement) {
		try {
			checkEnabledEmail();
			logger.info("Starting forwarding email");
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					EmailController.this.emailShell = new ForwardEmailShell(
							SupraSphereFrame.INSTANCE, statement,
							EmailController.this);
				}
			});
		} catch (SupraEmailException ex) {
			logger.warn("Forwarding with not enabled", ex);
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					EmailController.this.emailShell = new ForwardEmailShell(
							SupraSphereFrame.INSTANCE, statement,
							EmailController.this, false);
				}
			});
		}
	}

	/**
	 * Reply email processing
	 * 
	 * @param statement
	 */
	public void clickedReplyEmail(final ExternalEmailStatement statement) {
		try {
			checkEnabledEmail();
			logger.info("Starting replying email");
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					EmailController.this.emailShell = new ReplyEmailShell(
							SupraSphereFrame.INSTANCE, statement,
							EmailController.this);
				}
			});
		} catch (SupraEmailException ex) {
			logger.warn("Replying error", ex);
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					EmailController.this.emailShell = new ReplyEmailShell(
							SupraSphereFrame.INSTANCE, statement,
							EmailController.this, false);
				}
			});
		}
	}
	
	public void clickedComposeEmail(String address) {
		createEmail(address);
	}
	
	public boolean isEmailSphere(){
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();
		String sphere_id = (String) this.session.get("sphere_id");
		String emailSphereId = verify.getEmailSphere(
				(String) this.session.get("username"), 
				(String) this.session.get("real_name"));
		if ((emailSphereId != null)&&(sphere_id != null)){
			if (sphere_id.equals(emailSphereId)){
				return true;
			}
		}
		return false;
	}

	public List<String> getCurrentSphereEmailsList() {
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();

//		String domain = verify.getDomain();
		String sphere_id = (String) this.session.get("sphere_id");
//		if ((domain == null) || (domain.equals("$loginAddress")))
//			domain = (String) this.session.get(SC.ADDRESS);

		String emailSphereId = verify.getEmailSphere(
				(String) this.session.get("username"), 
				(String) this.session.get("real_name"));
		
		SphereEmail emailSphereEmail = null;
		if ((emailSphereId != null) && (!sphere_id.equals(emailSphereId))){
			emailSphereEmail = verify.getSpheresEmails()
										.getSphereEmailBySphereId(emailSphereId);
		}
		
		SphereEmail currentSphereEmail = verify.getSpheresEmails()
				.getSphereEmailBySphereId(sphere_id);

		List<String> toReturn = new ArrayList<String>();
		
		if (currentSphereEmail != null){
			toReturn.addAll(currentSphereEmail.getEmailNames().getParsedEmailAddresses());
		}
		if (emailSphereEmail != null){
			toReturn.addAll(emailSphereEmail.getEmailNames().getParsedEmailAddresses());
		}
		
		return toReturn;
	}

	public SpherePossibleEmailsSet getSphereEmailsList(String sphereId) {
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();

		SphereEmail sphereEmail = verify.getSpheresEmails()
				.getSphereEmailBySphereId(sphereId);

		if (sphereEmail == null)
			return new SpherePossibleEmailsSet("");
		return sphereEmail.getEmailNames();
	}

	public SpherePossibleEmailsSet getAllAliasesOfAllSpheres() {
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();

		SphereEmailCollection sphereEmails = verify.getSpheresEmails();
		SpherePossibleEmailsSet ret = new SpherePossibleEmailsSet();

		for (SphereEmail sphereEmail : sphereEmails) {
			SpherePossibleEmailsSet set = sphereEmail.getEmailNames();
			if (set != null) {
				String str = sphereEmail.getEmailNames()
						.getSingleStringEmails();
				if (str != null)
					ret.addAddresses( str );
			}
		}
		return ret;
	}
	
	public List<String> getPossibleSendToEmailsList() {
		return getPossibleSendToEmailsList(this.mp);
	}

	public static List<String> getPossibleSendToEmailsList(MessagesPane pane) {

		List<String> emails = new ArrayList<String>();
		if (pane == null){
			return emails;
		}

		for(Statement statement : pane.getTableStatements()) {
			if (statement != null) {
				try {
					if (statement.getType().equals("contact")) {
						ContactStatement contact = ContactStatement.wrap(statement.getBindedDocument());
						String simpleAddress = SpherePossibleEmailsSet
								.parseSingleAddress(contact.getEmailAddress());
						if (!simpleAddress.trim().equals("")) {
							String address = SpherePossibleEmailsSet
									.cleanUpAddressString(
											contact
													.getContactNameByFirstAndLastNames(),
											simpleAddress);
							if (!emails.contains(address))
								emails.add(address);
						}
					}
					if (statement.getType().equals(
							SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL)) {
						ExternalEmailStatement email = ExternalEmailStatement
								.wrap(statement.getBindedDocument());
						SpherePossibleEmailsSet set = new SpherePossibleEmailsSet();
						set.addAddresses(email.getGiver());
						set.addAddresses(email.getReciever());
						set.addAddresses(email.getCcrecievers());
						set.addAddresses(email.getBccrecievers());

						for (String s : set.getParsedEmailAddresses()) {
							if (!emails.contains(s)) {
								emails.add(s);
							}
						}
					}
				} catch (NullPointerException npe) {
					logger.error("Error processing contacts in sphere", npe);
				}
			}
		}

		return emails;
	}

	@SuppressWarnings("unchecked")
	public void makeAndDeliverEmail(EmailAddressesContainer addressesContainer,
			String subject, StringBuffer body, AttachedFileCollection files,
			boolean reply, boolean isAddRoutingNumber) {

		this.emailShell.lockButtons(true);

		Hashtable toPublish = (Hashtable) this.session.clone();
		Document doc = XMLDoc(toPublish, addressesContainer.getFrom(), subject,
				body.toString(), addressesContainer.getSendTo(),
				addressesContainer.getOriginalCC(), addressesContainer
						.getOriginalBCC(), reply, getRoutingNumber());

		toPublish.remove(SessionConstants.PASSPHRASE);
		toPublish.put(SessionConstants.DOCUMENT, doc);
		SupraSphereFrame.INSTANCE.client.sendEmailFromServer(toPublish, addressesContainer,
				files, body, subject, "");

		this.emailShell.close();
	}

	@SuppressWarnings("unchecked")
	private Document XMLDoc(Hashtable toSend, String giver, String subject,
			String bodyText, String sendTo, String CCSend, String BCCSend,
			boolean reply, String message_id) {

		Document createDoc = DocumentHelper.createDocument();
		Element email = createDoc.addElement("email");

		email.addElement("giver").addAttribute("value", giver);

		email.addElement("subject").addAttribute("value", subject);

		email.addElement("last_updated_by").addAttribute("value",
				(String) toSend.get("contact_name"));
		/*
		 * if (response_id == null) { } else {
		 * 
		 * email.addElement("response_id").addAttribute("value", response_id); }
		 */

		if (reply) {
			email.addElement("original_id").addAttribute("value", message_id);
			email.addElement("message_id").addAttribute("value", message_id);
			email.addElement("thread_id").addAttribute("value",
					this.emailShell.getOriginalEmail().getThreadId());
			email.addElement("response_id").addAttribute("value",
					this.emailShell.getOriginalEmail().getMessageId());
		} else {
			email.addElement("original_id").addAttribute("value", message_id);
			email.addElement("message_id").addAttribute("value", message_id);
			email.addElement("thread_id").addAttribute("value", message_id);
		}

		email.addElement("type").addAttribute("value",
				SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		email.addElement("input").addAttribute("value", "false");
		email.addElement("thread_type").addAttribute("value",
				SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);

		DefaultElement body = new DefaultElement("body");
		body.setText(bodyText);

		body.addElement("version").addAttribute("value", "3000");

		body.addElement("orig_body").setText(bodyText);

		email.add(body);

		Element root = createDoc.getRootElement();

		root.addElement("reciever").addAttribute("value", sendTo);
		root.addElement("cc").addAttribute("value", CCSend);
		root.addElement("bcc").addAttribute("value", BCCSend);

		root.addElement("voting_model").addAttribute("type", "absolute")
				.addAttribute("desc", "Absolute without qualification");
		root.element("voting_model").addElement("tally").addAttribute("number",
				"0.0").addAttribute("value", "0.0");

		toSend.put("delivery_type", "normal");

		root.addElement("confirmed").addAttribute("value", "true");

		logger.info("DELIVERY TYPE NORMAL");

		return createDoc;

	}

	public static SphereStatement createEmailSphereStatement(String login,
			String contact, String sphereCore) {
		SphereStatement sphere = new SphereStatement();

		String display_name = EmailUtils.getEmailSphereOnLogin(login);
		sphere.setDisplayName(display_name);
		String sphere_id = Long.toString(Math.abs((new Random()).nextLong()));
		sphere.setSystemName(sphere_id);
		sphere.setType("sphere");
		sphere.setDefaultType("normal");
		sphere.setSphereType(SphereType.GROUP);
		sphere.setVotingModelType("absolute");
		sphere.setVotingModelDesc("Absolute without qualification");
		sphere.setSpecificMemberContactName("__NOBODY__");
		sphere.setTallyNumber("0.0");
		sphere.setTallyValue("0.0");
		sphere.getVotedMembers().add(new VotedMember(contact, ""));
		sphere.setThreadType("sphere");
		sphere.setType("sphere");

		SphereMember member = new SphereMember();
		member.setContactName(contact);
		member.setLoginName(login);
		sphere.addMember(member);
		// sphere.setMessageId(sphere_id);

		// no body
		sphere.setSubject(display_name);
		sphere.setGiver(contact);
		sphere.setGiverUsername(login);
		sphere.setDefaultDelivery("normal");
		sphere.setDefaultType(CreateEmailAction.EMAIL_TITLE);

		sphere.setTerseModify("own");
		sphere.setTerseEnabled(true);

		sphere.setMessageModify("own");
		sphere.setMessageEnabled(false);

		sphere.setExternalEmailModify("own");
		sphere.setExternalEmailEnabled(true);

		sphere.setBookmarkModify("own");
		sphere.setBookmarkEnabled(true);

		sphere.setRssModify("own");
		sphere.setRssEnabled(true);

		sphere.setKeywordsModify("own");
		sphere.setKeywordsEnabled(true);

		sphere.setContactModify("own");
		sphere.setContactEnabled(false);

		sphere.setFileModify("own");
		sphere.setFileEnabled(true);

		sphere.setSphereModify("own");
		sphere.setSphereEnabled(true);

		sphere.setExpiration("All");

		sphere.setSphereCoreId(sphereCore);

		sphere.setCurrentSphere(sphere_id);

		return sphere;
	}

	public String getRoutingNumber() {
		if (this.message_id == null)
			this.message_id = VariousUtils.createMessageId();
		return this.message_id;
	}

	/*public void openManageSphereAliasesDialog(String sphereId) {
		ManageEmailAliasesDialog dialog = new ManageEmailAliasesDialog(this,
				sphereId);
		dialog.show(SupraSphereFrame.INSTANCE.getShell());
	}*/

	public void saveNewSphereEmails(SphereEmail sphereEmail) {
		SupraSphereFrame.INSTANCE.client.saveNewSpheresEmails(sphereEmail);
	}

	private void checkEnabledEmail() throws SupraEmailException {
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();

		String sphereId = (String) this.session.get("sphere_id");
		SphereEmail sphereEmail = verify.getSpheresEmails()
				.getSphereEmailBySphereId(sphereId);

		if (sphereEmail == null)
			throw new SupraEmailException("No sphere email for sphere_id: "
					+ sphereId);

		if (!sphereEmail.getEnabled())
			throw new SupraEmailException(
					"No email addressable sphere with sphere_id: " + sphereId);
	}

	/**
	 * @param sphere_id Sphere Id
	 * @return true if sphere with such sphere_id is email addressable, false otherwise.
	 */
	public boolean isSphereEmailAddressable(String sphere_id) {
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();

		SphereEmail sphereEmail = verify.getSpheresEmails()
				.getSphereEmailBySphereId(sphere_id);

		if (sphereEmail == null)
			return false;
		if (sphereEmail.getEnabled())
			return true;
		return false;
	}
	
	public static String getTextOnEmail(ExternalEmailStatement email, MessagesPane mp){
        String text = "";

        text += "SENDER: " + StringProcessor.toHTMLView(email.getGiver()) + "<br>";
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
        
        String subject = email.getSubject();
        logger.info("Email loading with subject: "+subject);
        
        PreviewHtmlTextCreator creator = new PreviewHtmlTextCreator(mp); 

        creator.addText(text);
		return null;
	}

	public String getSphereId() {
		if (this.mp != null){
			return this.mp.getSystemName();
		} else {
			return null;
		}
	}
}
