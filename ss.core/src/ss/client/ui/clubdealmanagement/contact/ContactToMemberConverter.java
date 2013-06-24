/**
 * 
 */
package ss.client.ui.clubdealmanagement.contact;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.CreateMembership;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.util.XMLSchemaTransform;

/**
 * @author zobo
 *
 */
public class ContactToMemberConverter {
	
	private static final String CONTACT_NAS_NO_CONTACT_NAME = "Contact nas no ContactName";

	private static final String USER_EXISTS = "Such user already exists";

	private static final String CONTACT_DOCUMENT_IS_NULL = "Contact Document is not available";

	private static final String ALREADY_HAS_LOGIN = "Contact document already with login";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ContactToMemberConverter.class);
	
	private final DialogsMainCli client;
	
	private final Hashtable session;

    public ContactToMemberConverter( final DialogsMainCli client, final Hashtable session ) {
		super();
		if ( client == null ) {
			throw new NullPointerException( "DialogsMainCli can not be null" );
		}
		if ( session == null ) {
			throw new NullPointerException( "session can not be null" );
		}
		this.client = client;
		this.session = session;
	}

	public String promoteContactToUser( final ContactStatement contactStatement, 
			final String loginNew, final String passwordNew ){
    	if ( contactStatement == null ) {
    		logger.error(CONTACT_DOCUMENT_IS_NULL);
    		return CONTACT_DOCUMENT_IS_NULL;
    	}
    	if ( StringUtils.isNotBlank(contactStatement.getLogin())) {
    		if (logger.isDebugEnabled()) {
				logger.debug(ALREADY_HAS_LOGIN);
			}
    		return ALREADY_HAS_LOGIN;
    	}
    	final String contactNameNew = contactStatement.getContactNameByFirstAndLastNames();
    	if (  StringUtils.isBlank(contactNameNew) ) {
    		if (logger.isDebugEnabled()) {
				logger.debug(CONTACT_NAS_NO_CONTACT_NAME);
			}
    		return CONTACT_NAS_NO_CONTACT_NAME;
    	}
    	final Document contactDocument = contactStatement.getBindedDocument();
    	boolean isUserWithContactNameExists = getClient().getVerifyAuth().getLoginForContact(contactNameNew) != null;
        boolean isUserWithLoginNameExists = getClient().getVerifyAuth().isUserExist(loginNew);
        if ( isUserWithContactNameExists || isUserWithLoginNameExists ) {
        	if (logger.isDebugEnabled()) {
				logger.debug(USER_EXISTS);
			}
        	return USER_EXISTS;
        }
    	
    	final CreateMembership membership = new CreateMembership();
		final Document memDoc = membership.createMember(contactNameNew,loginNew, passwordNew);

		if (logger.isDebugEnabled()) {
			logger.debug("membership doc: " + memDoc.asXML());
		}

		contactStatement.setLogin( loginNew );
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
				"New Membership: " + contactNameNew + " : "
						+ loginNew);
		root.addElement("giver").addAttribute("value", contactNameNew);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);

		final String supraSphere = (String) getSession().get("supra_sphere");
		final String inviteUsername = (String) getSession().get("username");
		final String inviteContact = (String) getSession().get("real_name");

		final String sphereId = (String) getSession().get("sphere_id");
		final String sphereName = getClient().getVerifyAuth().getDisplayName((sphereId));
		
		if (contactDocument.getRootElement().element("locations") == null) {
			contactDocument.getRootElement().addElement("locations");
		}

		final String sphereType = getClient().getVerifyAuth().getSphereType(sphereId);
		XMLSchemaTransform.setThreadAndOriginalAsMessage(contactDocument);
		XMLSchemaTransform.addOneLocationToDoc(contactDocument,
				(String) getSession().get("sphereURL"),
				sphereId, sphereName);
		getClient().replaceDoc(getSession(), contactDocument);
		//client.publishTerse(session, contactDocument);
		getClient().publishTerse(getSession(), memDoc);
		getClient().registerMember(getSession(), supraSphere, contactDocument,
				inviteUsername, inviteContact, sphereName, sphereId,
				contactNameNew, loginNew, sphereType);
		return null;
    }

	/**
	 * @return the client
	 */
	public DialogsMainCli getClient() {
		return this.client;
	}

	/**
	 * @return the session
	 */
	public Hashtable getSession() {
		return this.session;
	}
}
