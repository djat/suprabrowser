/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.SendOutContactEmailAction;
import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.IAttachedFile;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.file.vcf.VCardCreater;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.ExternalEmailWithContactStatement;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.email.EmailDeliverer;
import ss.smtp.Mail;
import ss.smtp.MailAddress;
import ss.smtp.MailImpl;
import ss.smtp.defaultforwarding.EmailAddressesCreator;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.reciever.EmailProcessor;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class SendOutContactEmailActionHandler extends AbstractActionHandler<SendOutContactEmailAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendOutContactEmailActionHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SendOutContactEmailActionHandler(DialogsMainPeer peer) {
		super(SendOutContactEmailAction.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(SendOutContactEmailAction action) {
		if (logger.isDebugEnabled()) {
			logger.debug("SendOutContactEmailActionHandler execute started");
		}
		perform( action.getContactMessageId(), action.getContactSphereId(),
				new EmailBody( action.getEmailSubject(), action.getEmailBody() ),
				action.getEmailAddresses() );
		if (logger.isDebugEnabled()) {
			logger.debug("SendOutContactEmailActionHandler execute finished");
		}
	}

	/**
	 * @param contactMessageId
	 * @param contactSphereId
	 * @param emailBody
	 * @param emailAddresses
	 */
	private void perform(String contactMessageId, String contactSphereId,
			EmailBody emailBody, ArrayList<String> emailAddresses) {
		if ( StringUtils.isBlank(contactMessageId) ) {
			logger.error("contactMessageId is blank");
			return;
		}
		if ( StringUtils.isBlank(contactSphereId) ) {
			logger.error("contactSphereId is blank");
			return;
		}
		final Document contactDoc = this.peer.getXmldb().getSpecificID(contactSphereId, contactMessageId);
		if ( contactDoc == null ) {
			logger.error("No document for messageId: " + contactMessageId + ", sphereId: " + contactSphereId);
			return;
		}
		if ( (emailAddresses == null) || (emailAddresses.isEmpty()) ) {
			logger.error("emailAddresses is empty");
			return;
		}
		final ContactStatement contactSt = ContactStatement.wrap( contactDoc );
		if ( !contactSt.isContact() ) {
			logger.error("Doc is not contact for messageId: " + contactMessageId + ", sphereId: " + contactSphereId);
			return;
		}
		if ( StringUtils.isBlank(emailBody.getSubject()) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("subject was not specified, setting on contact name");
			}
			emailBody.setSubject("Contact: " + contactSt.getContactNameByFirstAndLastNames());
		}
		final AttachedFile contactFile = VCardCreater.INSTANCE.create(contactSt);
		if ( contactFile == null ) {
			logger.error("Could not create contact file");
			return;
		}
		final ExternalEmailStatement email = createEmail(contactSt, emailBody, emailAddresses);
		
		publishEmail( email );

		sendEmail(contactFile, emailBody, email, emailAddresses);
	}
	
	private void publishEmail( final ExternalEmailStatement email ){
		final Hashtable session = (Hashtable) this.peer.getSession().clone();
		session.put(SC.SPHERE_ID, email.getCurrentSphere());
		Hashtable update = new Hashtable();
		update.put(SC.SESSION, session);			
		update.put(SC.DOCUMENT, email.getBindedDocument());
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH)
			.handle( update );		
	}
	
	private ExternalEmailStatement createEmail( final ContactStatement contactSt, final EmailBody emailBody, ArrayList<String> emailAddresses ){
		final String messageId = VariousUtils.createMessageId();
		final String giver = this.peer.getUserContactName();

		ExternalEmailWithContactStatement email = new ExternalEmailWithContactStatement();
        
        email.setGiver(giver);
        email.setSubject(emailBody.getSubject());
        email.setLastUpdatedBy(giver);

		email.setMessageId( messageId );
		email.setThreadId( messageId );
		email.setCurrentSphere( contactSt.getCurrentSphere() );
        
        email.setOrigBody(emailBody.getBody());
        email.setType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
        email.setThreadType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
        email.setInput(false);

        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(
                        current);

        email.setMoment(moment);
        email.setLastUpdated(moment);
        
        email.setContactMessageId( contactSt.getMessageId() );
        email.setContactSphereId( contactSt.getCurrentSphere() );
        
        email.setEmailmessageId( SendingElementFactory.generateMessageHeader( messageId ) );
        
        final SpherePossibleEmailsSet set = new SpherePossibleEmailsSet();
    	for ( String address : emailAddresses ) {
   			set.addAddresses( address );
    	}
    	set.cleanUp();
    	final String sendTo = set.getAndDetach(0);
        email.setReciever( sendTo );
        final String cc = set.getSingleStringEmails();
        if ( StringUtils.isNotBlank(cc) ) {
        	email.setCcrecievers( cc );
        }
        
        return email;
	}
	
	private void sendEmail( IAttachedFile file, EmailBody emailBody, ExternalEmailStatement st, ArrayList<String> emailAddresses ){
        final SpherePossibleEmailsSet set = new SpherePossibleEmailsSet();
    	for ( String address : emailAddresses ) {
   			set.addAddresses( address );
    	}
    	set.cleanUp();
    	final String sendTo = set.getAndDetach(0);
        final String cc = set.getSingleStringEmails();
        
		String from = EmailAddressesCreator.getFromPerUser(this.peer.getUserContactName(), this.peer.getVerifyAuth(), this.peer.getSession());
		String fromForSphere = EmailAddressesCreator.getFrom(st.getCurrentSphere(), this.peer.getVerifyAuth(), this.peer.getSession());
		if (from == null) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("From for giver is null, setting from from current sphere email aliases");
			}
			from = fromForSphere;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("From is: " + from);
		}
		String replyTo = EmailAddressesCreator.getReplyTo(fromForSphere, st.getMessageId(), from);
		if (logger.isDebugEnabled()) {
			logger.debug("ReplyTo is: " + replyTo);
		}

		EmailAddressesContainer addressesContainer = new EmailAddressesContainer(sendTo, from, replyTo, cc, null);		
		AttachedFileCollection files = new AttachedFileCollection();
		files.add( file );
		List<SendingElement> elements = SendingElementFactory.createCreated(addressesContainer, files, emailBody, st.getMessageId() , st.getCurrentSphere(), st.getEmailmessageId());
		if ( (elements != null) && (!elements.isEmpty()) ) {
			for ( SendingElement element : elements ) {
				Mailer.INSTANCE.send( element );
			}
		} else {
			logger.error("Sending elements list is empty");
		}
	}
	
}
