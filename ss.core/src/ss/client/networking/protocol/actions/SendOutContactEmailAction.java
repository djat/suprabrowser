/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.ArrayList;

/**
 * @author zobo
 *
 */
public class SendOutContactEmailAction extends AbstractAction {

	private static final String EMAIL_ADDRESSES = "emailAddresses";

	private static final String EMAIL_BODY = "emailBody";

	private static final String EMAIL_SUBJECT = "emailSubject";

	private static final String CONTACT_MESSAGE_ID = "contactMessageId";

	private static final long serialVersionUID = -7482407842239058151L;

	private static final String CONTACT_SPHERE_ID = "contactSphereId";

	public void setContactSphereId( final String sphereId ){
		putArg(CONTACT_SPHERE_ID, sphereId);
	}
	
	public String getContactSphereId() {
		return getStringArg( CONTACT_SPHERE_ID );
	}

	public void setContactMessageId( final String messageId ){
		putArg(CONTACT_MESSAGE_ID, messageId);
	}
	
	public String getContactMessageId(){
		return getStringArg( CONTACT_MESSAGE_ID );
	}
	
	public void setEmailSubject( final String emailSubject ){
		putArg(EMAIL_SUBJECT, emailSubject);
	}
	
	public String getEmailSubject(){
		return getStringArg( EMAIL_SUBJECT );
	}
	
	public void setEmailBody( final String emailBody ){
		putArg(EMAIL_BODY, emailBody);
	}
	
	public String getEmailBody(){
		return getStringArg( EMAIL_BODY );
	}
	
	public void setEmailAddresses( final ArrayList<String> addresses ){
		putArg(EMAIL_ADDRESSES, addresses);
	}
	
	public ArrayList<String> getEmailAddresses(){
		return (ArrayList<String>) getObjectArg(EMAIL_ADDRESSES);
	}
}
