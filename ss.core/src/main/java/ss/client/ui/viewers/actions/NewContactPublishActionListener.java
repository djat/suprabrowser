/**
 * 
 */
package ss.client.ui.viewers.actions;

import java.text.DateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.viewers.NewContact;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.CreateMembership;
import ss.util.NameTranslation;
import ss.util.XMLSchemaTransform;

/**
 * @author zobo
 *
 */
public class NewContactPublishActionListener implements Listener {
    private NewContact newContact;
    
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NewContactPublishActionListener.class);

    /**
     * @param newContact
     */
    public NewContactPublishActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    @SuppressWarnings("unchecked")
	public void handleEvent(Event event) {
    	if (logger.isDebugEnabled()){
    		logger.debug("handleEvent save new contact performed");
    	}
        final Document contactDocument = this.newContact.XMLDoc();
        if ( contactDocument == null ) {
        	return;
        }
        this.newContact.getSession().put("delivery_type", "normal");
        final String login = this.newContact.getLogin();
    	if (logger.isDebugEnabled()){
    		logger.debug("New contact login: " + login);
    	}
        if ( login != null && login.length() > 0 ) {
            final String contactName = NameTranslation.createContactNameFromContactDoc(contactDocument);
        	if (logger.isDebugEnabled()){
        		logger.debug("New contact contactName: " + contactName);
        	}
        	boolean isUserWithContactNameExists = false;
        	if (this.newContact.getClient().getVerifyAuth().getLoginForContact(contactName) != null){
        		isUserWithContactNameExists = true;
        	}
            boolean isUserWithLoginNameExists = this.newContact.getClient().getVerifyAuth().isUserExist(login);

            boolean registered = false;
            if (!isUserWithContactNameExists && !isUserWithLoginNameExists) {
            	logger.info( "begin creating new user " + login );
            	String password = this.newContact.getPassword();
                if ( password != null && password.length() > 0) {
                    if (this.newContact.isAdmin()
                            || this.newContact.getClient()
                                    .getVerifyAuth()
                                    .isPersonal(
											(String) this.newContact
													.getSession().get(
															"sphere_id"),
											(String) this.newContact
													.getSession().get(
															"username"),
											(String) this.newContact
													.getSession().get(
															"real_name")))
                    	registered = createContact(contactDocument, login, contactName);
                } else {
                	if (logger.isDebugEnabled()){
                		logger.warn("Password is not specified for user" + contactName);
                	}
                	UserMessageDialogCreator.warning("Please specify valid password for user");
                	return;
                }
            } 
            if (!registered) {
            	if (logger.isDebugEnabled()){
            		logger.warn("The user already exists, publishing copy of contact");
            	}
                contactDocument.getRootElement().element("login").addAttribute("value", "");
                this.newContact.getClient().publishTerse(
                        this.newContact.getSession(), contactDocument);
            }
        } 
        else {
        	if (logger.isDebugEnabled()){
        		logger.warn("Login is null, publishing copy of contact");
        	}
            this.newContact.getClient().publishTerse(this.newContact.getSession(),
                    contactDocument);
        }

        NewContact.getSShell().dispose();
    }

	private boolean createContact(Document contactDocument, final String login,
			final String contactName) {

		final CreateMembership membership = new CreateMembership();
		final Document memDoc = membership.createMember(contactName,
				this.newContact.getLogin(), this.newContact.getPassword());

		logger.debug("membership doc: " + memDoc.asXML());

		final Element root = memDoc.getRootElement();
		long longnum = System.currentTimeMillis();

		final String message_id = (Long.toString(longnum));

		final Date current = new Date();
		final String moment = DateFormat.getTimeInstance(DateFormat.LONG)
				.format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("subject").addAttribute(
				"value",
				"New Membership: " + contactName + " : "
						+ this.newContact.getLogin());
		root.addElement("giver").addAttribute("value", contactName);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);

		final String supraSphere = (String) this.newContact.getSession().get(
				"supra_sphere");
		final String inviteUsername = (String) this.newContact.getSession()
				.get("username");
		final String inviteContact = (String) this.newContact.getSession().get(
				"real_name");

		final String sphereId = (String) this.newContact.getSession().get(
				"sphere_id");
		final String sphereName = this.newContact.getClient()
				.getVerifyAuth().getDisplayName((sphereId));

		final String oldLogin = getOldLogin();
		// TODO: check next condition it is verfy strange
		final boolean haveOldDifferentLogin = (oldLogin != null && !oldLogin
				.equals(login));

		if (haveOldDifferentLogin) {
			logger.info("have old different login");
			final boolean loginExists = this.newContact.getClient()
					.getVerifyAuth().isUserExist(login);
			if (!loginExists) {
				logger.info("its not an existing login ...can change it");
				this.newContact.getClient()
						.replaceUsernameInMembership(this.newContact
								.getSphereViewOwner().getRawSession(), oldLogin,
								login, memDoc.getRootElement().element(
										"verifier").attributeValue("salt"),
								memDoc.getRootElement().element("verifier")
										.getText());

			}
			return false;
		} else {
			logger.info("it was not a new login");
			final String sphereType = this.newContact.getClient()
					.getVerifyAuth().getSphereType(sphereId);
			XMLSchemaTransform.setThreadAndOriginalAsMessage(contactDocument);
			XMLSchemaTransform.addOneLocationToDoc(contactDocument,
					(String) this.newContact.getSession().get("sphereURL"),
					sphereId, sphereName);
			this.newContact.getClient().publishTerse(
					this.newContact.getSession(), contactDocument);
			this.newContact.getClient().publishTerse(
					this.newContact.getSession(), memDoc);
			this.newContact.getClient().registerMember(
					this.newContact.getSession(), supraSphere, contactDocument,
					inviteUsername, inviteContact, sphereName, sphereId,
					contactName, login, sphereType);
			return true;
		}
	}

	private String getOldLogin() {
		if (this.newContact.getOrigDoc() != null) {
			logger.info("orig doc not null");
			return this.newContact.getOrigDoc().getRootElement()
					.element("username").attributeValue("value");
		} else {
			logger.info("ORIG DOC NULL, THAT IS WHY");
		}
		return null;
	}
}