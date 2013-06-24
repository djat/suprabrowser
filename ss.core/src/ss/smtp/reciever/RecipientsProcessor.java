	/**
 * 
 */
package ss.smtp.reciever;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.MessagingException;

import org.dom4j.Document;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.configuration.DomainProvider;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;
import ss.smtp.Mail;
import ss.util.StringProcessor;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class RecipientsProcessor {

	private static final String NO_SUBJECT = " [ NO SUBJECT ] ";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(RecipientsProcessor.class);
	
    private VerifyAuth verify;
    
    private XMLDB xmldb;

	private RecieveList result;
	
	private Vector<String> notInSupraSphere = new Vector<String>();
	
	public String RecId = null;
	
	@SuppressWarnings("deprecation")
	public RecipientsProcessor(){
		try {
			this.xmldb = new XMLDB();
			this.verify = SupraSphereProvider.INSTANCE.createVerifyAuth();
			this.result = new RecieveList();
		} catch (NullPointerException ex) {
			logger.error("Cannot get suprasphere Document", ex);
		}
	}
	
	public RecieveList process(Mail mail) throws MessagingException {
		if (this.verify == null) {
			return null;
		}

		List theRecipients = (List) mail.getRecipients();
		String subject = null;
		
		try {
			subject = mail.getMessage().getSubject();
			logger.info("Subject: " + subject);
			
			String [] headers = mail.getMessage().getHeader("In-Reply-To");
			if ( (headers != null) && ( headers.length > 0 )) {
				this.RecId = headers[0];
			}
			
		} catch (MessagingException ex) {
			logger.error("Cannot get Subject", ex);
		}
		
		for (Iterator iter = theRecipients.iterator(); iter.hasNext();) {
			String recipient = (String) iter.next();

			logger.info("Recipient: " + recipient);

			if (recipient != null) {
				this.result.addReciever(processReciever(recipient, subject));
			}
		}
		return this.result;
	}
	
	/**
	 * @param recipient
	 * @param subjectLabel
	 * @return
	 */
	private Reciever processReciever(String recipient, String globalSubject) {
		
		String fixedRecipient = recipient;
		if (recipient.lastIndexOf(":") != -1) {
			StringTokenizer st = new StringTokenizer(recipient, ":");
			st.nextToken();
			fixedRecipient = st.nextToken();
		}

		String recipientAddress = SpherePossibleEmailsSet.parseSingleAddress(fixedRecipient);
		
		String recipientDesciption = SpherePossibleEmailsSet.getDescriptionFromAddress(fixedRecipient);
		if (recipientDesciption.lastIndexOf(":") != -1) {
			StringTokenizer st = new StringTokenizer(recipientDesciption, ":");
			st.nextToken();
			recipientDesciption = st.nextToken();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("recipientAddress: " + recipientAddress + ", recipientDesciption: " + recipientDesciption);
		}
		if (recipientDesciption.equals(fixedRecipient)){
			recipientDesciption = "";
		}
		
		final String recipientDomain = SpherePossibleEmailsSet.getDomainFromSingleAddress(recipientAddress);
		if (logger.isDebugEnabled()) {
			logger.debug("recipientDomain: " + recipientDomain);
		}
		if (!isMatchCurrentDomain(recipientDomain)){
			if (logger.isDebugEnabled()) {
				logger.debug("Domain is not ours, returning");
			}
			return null;
		}
		final String recipientAddressWithoutRoutingNumber = getSphereEmailName(recipientAddress, recipientDomain);
		if (logger.isDebugEnabled()) {
			logger.debug("recipientAddressWithoutRoutingNumber: " + recipientAddressWithoutRoutingNumber);
		}
		SphereEmail sphere = processSphere(recipientAddressWithoutRoutingNumber);
		if (sphere == null){
			this.notInSupraSphere.add(fixedRecipient);
			if (logger.isDebugEnabled()) {
				logger.debug("Email address has not been found: " + recipientAddressWithoutRoutingNumber);
			}
			return null;
		}
		final String responceId = processResponceId(recipientAddress, sphere.getSphereId(), globalSubject);
		final String emailAddress = processEmailAddress(sphere, recipientAddressWithoutRoutingNumber, recipientDesciption, recipientDomain);
		
		final String subject = (globalSubject == null) ? NO_SUBJECT : globalSubject;
		
		if (logger.isDebugEnabled()) {
			logger.debug("--Processed-- responceId: " + responceId + ", emailAddress: " + emailAddress + ", subject: " + subject);
		}
		return new Reciever(sphere.getSphereId(), responceId, emailAddress);
	}

	/**
	 * @param sphere
	 * @param recipientName
	 * @param recipientDesciption
	 * @return
	 */
	private String processEmailAddress(SphereEmail sphere, String recipientAddress, String recipientDesciption, String domain) {
		String last = SpherePossibleEmailsSet.createAddressString(recipientDesciption, 
				SpherePossibleEmailsSet.parseSingleAddressEvenWithoutDomain(recipientAddress), domain);
		try {
			String recipientDesciptionWithOutTo = StringProcessor.removeToInEmail(recipientDesciption);
			SpherePossibleEmailsSet set =  sphere.getEmailNames();
			String desc = StringProcessor.unsuitFromLapki(recipientDesciptionWithOutTo);
			List<String> addresses = set.getParsedEmailAddresses();
			for (String s : addresses){
				if (SpherePossibleEmailsSet.parseSingleAddress(s).equals(recipientAddress)){
					if (desc.contains(StringProcessor.unsuitFromLapki(SpherePossibleEmailsSet.getDescriptionFromAddress(s)))){
						return s;
					}
					last = s;
				}
			}
		} catch (Throwable ex) {
			logger.error("Error generating proper description for email address", ex);
		}
		return last;
	}

	/**
	 * @param recipientName
	 * @return sphere_id or null
	 */
	private SphereEmail processSphere(String recipientAddressWithoutRoutingNumber) {
		final SphereEmail sphereEmail = this.verify.getSpheresEmails()
			.getSphereEmailByEmailName( recipientAddressWithoutRoutingNumber );

		if (sphereEmail == null) {
			logger.warn("Sphere not found for email: " + recipientAddressWithoutRoutingNumber);
			return null;
		}

		if (!(sphereEmail.getEnabled())) {
			// The sphere is not allowed to accept email
			// TODO: some action to have place.
			logger.warn("Sphere is not enabled: "
					+ sphereEmail.getSphereId());
			return null;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Recipient sphere resolved: " + sphereEmail.getSphereId());
		}
		
		return sphereEmail;
	}

	/**
	 * @param recipientName
	 * @return responce_id or null
	 */
	private String processResponceId(String recipientName, String sphereId, String subject) {
		if ( this.RecId != null ) {
			Vector<Document> emails = this.xmldb.getEmailsForSphereId(sphereId);
			if ( emails != null ) {
				for(Document doc : emails) {
					ExternalEmailStatement ext = ExternalEmailStatement.wrap(doc);
					if(ext.getEmailmessageId()!=null && ext.getEmailmessageId().equals(this.RecId)) {
						return ext.getMessageId();	
					}
				}
			}
		}
		final String emailName = SpherePossibleEmailsSet.parseSingleAddressEvenWithoutDomain(recipientName);
		final String responceName = getResponceName( emailName );
		if (StringUtils.isNotBlank(responceName)){
			if (isMessageIdExists(responceName, sphereId)) {
				return responceName;
			}
		}
//		try {
//			return processWithSubjectFinding(sphereId, subject);
//		} catch (Exception ex) {
//			logger.error("Error in existing emails processing", ex);
//			return null;
//		}
		return null;
	}
	
	private static String getSphereEmailName( final String recipientName, final String domain){
		final String emailName = SpherePossibleEmailsSet.parseSingleAddressEvenWithoutDomain(recipientName);
		String sphereName;
		if (emailName.lastIndexOf(".") != -1) {
			StringTokenizer st = new StringTokenizer(emailName, ".");
			sphereName = st.nextToken();
		} else {
			sphereName = emailName;
		}
		return sphereName + "@" + domain;
	}
	
	private static String getResponceName(String recipientName){
		if (recipientName.lastIndexOf(".") != -1) {
			StringTokenizer st = new StringTokenizer(recipientName, ".");
			st.nextToken();
			return st.nextToken();
		} else {
			return null;
		}
	}

	private String processWithSubjectFinding(String sphereId, String subject) {
		if (subject == null)
			return null;
		Vector<Document> emails = this.xmldb.getEmailsForSphereId(sphereId);
		for (Document doc : emails){
			ExternalEmailStatement email = ExternalEmailStatement.wrap(doc);
			if ( subject.contains( email.getSubject() ) ){
				return email.getMessageId();
			}
		}
		
		return null;
	}
	
	private boolean isMessageIdExists(String id, String sphereId) {
		return VariousUtils.isNumber(id);
	}

	/**
	 * @return
	 */
	public List<String> getNotRecieved() {
		return this.notInSupraSphere;
	}
	
	/**
	 * @param recipientDomain
	 * @return
	 */
	private boolean isMatchCurrentDomain(String recipientDomain) {
		return DomainProvider.contains(recipientDomain);
	}
}
