package ss.client.ui.viewers.actions;

import java.text.DateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.common.CreateMembership;
import ss.client.ui.viewers.NewContact;
import ss.util.NameTranslation;
import ss.util.XMLSchemaTransform;

/**
 * @author zobo
 *
 */
public class NewContactUpdateActionListener implements Listener {
    
	private NewContact newContact;
    
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NewContactUpdateActionListener.class);
    
    /**
     * @param newContact
     */
    public NewContactUpdateActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    public void handleEvent(Event event) {
        Document contactDocument = prepareContactDocument();
        if ( contactDocument == null ) { 
        	return;
        }
        // TODO: check what replaceDoc do
        this.newContact.getClient().replaceDoc(this.newContact.getSession(), contactDocument);
        final String password = this.newContact.getPassword();
        final String login = this.newContact.getLogin();
        final boolean loginSame = isSameUserName(login);
        if (canCreateNewContact(password, login) ) {
            final boolean loginExists = this.newContact.getClient()
                    .checkExistingUsername(this.newContact.getSession(), login);

            if ( !loginExists || loginSame) {
                logger.info("It is not a username already");

                if (this.newContact.getOrigDoc().getRootElement().element(
                        "login").attributeValue("value").length() > 0) {
                    updateLogin(contactDocument, login);
                } else {
                    if (!loginExists) {
                        invite(contactDocument, login);
                    }
                }
            } else {
                logger.info ("It is already a username");
            }

        }

        NewContact.getSShell().dispose();

    }

	private void invite(Document contactDocument, final String login) {
		String sphereId = (String) this.newContact.getSession()
		        .get("sphere_id");
		String sphereType = this.newContact.getClient()
		        .getVerifyAuth().getSphereType(sphereId);
		String sphereName = this.newContact.getClient()
		        .getVerifyAuth().getDisplayName((sphereId));
		XMLSchemaTransform.setThreadAndOriginalAsMessage(contactDocument);

		final String cname =  NameTranslation
		        .createContactNameFromContactDoc(contactDocument);
		// xmlDoc =
		// XMLSchemaTransform.addOneLocationToDoc(xmlDoc,xmlDoc,(String)session.get("sphereURL"),sphereId,sphereName);

		CreateMembership membership = new CreateMembership();

		Document memDoc = membership.createMember(cname,
		        this.newContact.getLogin(),
		        this.newContact.getPassword());

		logger.info("membership doc: " + memDoc.asXML());

		// Element root = memDoc.getRootElement();

		String supraSphere = (String) this.newContact.getSession()
		        .get("supra_sphere");

		String inviteUsername = (String) this.newContact.getSession()
		        .get("username");
		String inviteContact = (String) this.newContact.getSession()
		        .get("real_name");

		Document newMemDoc = membership.createMember(cname,
		        this.newContact.getLogin(),
		        this.newContact.getPassword());

		this.newContact.getClient().publishTerse(
		        this.newContact.getSession(), newMemDoc);

		this.newContact.getClient().registerMember(
		        this.newContact.getSession(), supraSphere,
		        contactDocument, inviteUsername, inviteContact,
		        sphereName, sphereId, cname, login,
		        sphereType);
		//ZOBO: no time for this yet
		//(new EmailSphereCreator(this.newContact.getMessagesPane())).
		//    createEmailSphere(xmlDoc.getRootElement().element("login").attributeValue("value"));
	}

	private void updateLogin(Document contactDocument, final String login) {
		final CreateMembership membership = new CreateMembership();
		final String cname = NameTranslation.createContactNameFromContactDoc(contactDocument);
		Document memDoc = membership.createMember(cname,
		        this.newContact.getLogin(),
		        this.newContact.getPassword());

		logger.info("membership doc: " + memDoc.asXML());

		final Element root = memDoc.getRootElement();
		final long longnum = System.currentTimeMillis();

		final String message_id = (Long.toString(longnum));

		final Date current = new Date();
		final String moment = DateFormat.getTimeInstance(
		        DateFormat.LONG).format(current)
		        + " "
		        + DateFormat.getDateInstance(DateFormat.MEDIUM)
		                .format(current);
		root.addElement("original_id").addAttribute("value",
		        message_id);
		root.addElement("subject").addAttribute(
		        "value",
		        "New Membership: " + cname + " : "
		                + this.newContact.getLogin());
		root.addElement("giver").addAttribute("value", cname);
		root.addElement("message_id").addAttribute("value",
		        message_id);
		root.addElement("thread_id").addAttribute("value",
		        message_id);
		root.addElement("last_updated").addAttribute("value",
		        moment);
		root.addElement("moment").addAttribute("value", moment);

		/*
		 * String supraSphere = (String) this.newContact.session
		 * .get("supra_sphere");
		 * 
		 * String inviteUsername = (String)
		 * this.newContact.session .get("username"); String
		 * inviteContact = (String) this.newContact.session
		 * .get("real_name");
		 * 
		 * String sphereId = (String)this.newContact.
		 * session.get("sphere_id"); String sphereName =
		 * this.newContact.mP.client.getVerifyAuth()
		 * .getDisplayName((sphereId));
		 * 
		 * String realName = cname;
		 */

		final String oldUsername = getOldLogin();
		// if (newUsername==true) {
		logger.info( "its not an existing username...can change it" );

		this.newContact.getClient().replaceUsernameInMembership(
				this.newContact.getSphereViewOwner().getRawSession(), oldUsername,
				login, memDoc.getRootElement().element("verifier")
						.attributeValue("salt"), memDoc.getRootElement()
						.element("verifier").getText());		
	}

	private boolean canCreateNewContact(final String password, final String login) {
		return (password != null && password.length() > 0 && 
        	 login != null && login.length() > 0 )
             && this.newContact.isAdmin();
	}

	private String getOldLogin() {
		if (this.newContact.getOrigDoc() != null) {
		    logger.info("orig doc not null");
		    try {
		        return this.newContact.getOrigDoc()
		                .getRootElement().element("login")
		                .attributeValue("value");
		    } catch (NullPointerException npe) {
		    }

		} else {
		    logger.warn("ORIG DOC NULL, THAT IS WHY");
		}
		return null;
	}

	private boolean isSameUserName(final String login) {
		try {
            if (this.newContact.getOrigDoc().getRootElement().element("login")
                    .attributeValue("value").equals(login)) {
                return true;
            }

        } catch (NullPointerException npe) {
        }
		return false;
	}

	private Document prepareContactDocument() {
		final Document xmlDoc = this.newContact.XMLDoc();
		if ( xmlDoc == null ) {
			return null;
		}
		if (this.newContact.getOrigDoc().getRootElement().element(
                "current_sphere") == null) {
            this.newContact.getOrigDoc().getRootElement().addElement(
                    "current_sphere").addAttribute("value",
                    (String) this.newContact.getSession().get("sphere_id"));

        }
        if (this.newContact.getOrigDoc().getRootElement().element("thread_id") == null) {
            XMLSchemaTransform.setThreadAndOriginalAsMessage( this.newContact.getOrigDoc() );
        }

        final Document replaceDoc = (Document) this.newContact.getOrigDoc().clone();

        xmlDoc.getRootElement().remove(
                xmlDoc.getRootElement().element("message_id"));
        xmlDoc.getRootElement().add(
                (Element) (replaceDoc.getRootElement()
                        .element("message_id")).clone());

        xmlDoc.getRootElement().remove(
                xmlDoc.getRootElement().element("thread_id"));

        xmlDoc.getRootElement()
                .add(
                        (Element) (replaceDoc.getRootElement()
                                .element("thread_id")).clone());

        xmlDoc.getRootElement().remove(
                xmlDoc.getRootElement().element("original_id"));

        xmlDoc.getRootElement().add(
                (Element) (replaceDoc.getRootElement()
                        .element("original_id")).clone());

        if (replaceDoc.getRootElement().element("locations") != null) {
            xmlDoc.getRootElement().add(
                    (Element) replaceDoc.getRootElement().element(
                            "locations").clone());
        } else {

        }
        // xmlDoc.getRootElement().add((Element)replaceDoc.getRootElement().element("interest").clone());
        return xmlDoc;
	}
}