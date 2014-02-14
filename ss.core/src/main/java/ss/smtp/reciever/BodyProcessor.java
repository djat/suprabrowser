/**
 * 
 */
package ss.smtp.reciever;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;

import org.dom4j.Document;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.domainmodel.ExternalEmailStatement;
import ss.smtp.Mail;
import ss.util.StringProcessor;
import ss.util.SupraXMLConstants;

/**
 * @author zobo
 *
 */
public class BodyProcessor {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BodyProcessor.class);

	@SuppressWarnings("unchecked")
	public static Document createBodyDocument(Mail mail, String body, String giverText){
	        try {
	        	Message message = mail.getMessage();
	            String subject = message.getSubject();
	            if (subject == null) {
	                subject = " [ NO SUBJECT ] ";
	            }
	            if (subject.lastIndexOf("\\") != -1) {
	            	subject = subject.replace("\\", "/");
	            }

	            ExternalEmailStatement email = new ExternalEmailStatement();
	            
	            email.setGiver(giverText);
	            email.setSubject(subject);
	            email.setLastUpdatedBy(giverText);

	            email.setOrigBody(body);

	            try {
	            	supplyWithRecievers(email, message);
	            	/*message.getRecipients(RecipientType.TO);
	                List recipients = (List) mail.getRecipients();
	                email.setReciever(StringProcessor.removeToInEmail((String) recipients.get(0)));
	                List ccrecipients = new ArrayList(recipients);                
	                ccrecipients.remove(0);
	                List<String> ccrecivers = new ArrayList<String>();
	                for (Iterator iter = ccrecipients.iterator(); iter.hasNext();) {
	                    String element = (String) iter.next();
	                    ccrecivers.add(StringProcessor.removeToInEmail(element));
	                }
	                String cc = (new SpherePossibleEmailsSet(ccrecivers))
	                        .getSingleStringEmails();
	                email.setCcrecievers(cc);
	                email.setBccrecievers("");*/
	            } catch (Exception e) {
	                logger.error("Could not set reciever and CC recievers", e);
	            }

	            email.setType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
	            email.setThreadType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
	            email.setInput(true);

	            Date current = new Date();
	            String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
	                    current)
	                    + " "
	                    + DateFormat.getDateInstance(DateFormat.MEDIUM).format(
	                            current);

	            email.setMoment(moment);
	            email.setLastUpdated(moment);
	            
		        logger.info("Document of email generated: " + email.getBindedDocument().asXML());
		        return email.getBindedDocument();

	        } catch (Exception ex) {

	            logger.error("Error creating body of email", ex);
	            return null;
	        }
	}

	/**
	 * @param email
	 * @param message
	 * @throws MessagingException 
	 */
	private static void supplyWithRecievers(ExternalEmailStatement email, Message message) throws MessagingException {
		Address[] addressesTo = message.getRecipients(RecipientType.TO);
		Address[] addressesCC = message.getRecipients(RecipientType.CC);
		Address[] addressesBCC = message.getRecipients(RecipientType.BCC);
        
	    List<String> ccrecivers = new ArrayList<String>();
	    List<String> bccrecievers = new ArrayList<String>();
	        
		if ((addressesTo != null) && (addressesTo.length > 0)){
			email.setReciever(StringProcessor.removeToAndRoutingNumberInEmails(addressesTo[0].toString()));
			for (int i = 1; i < addressesTo.length; i++){
				ccrecivers.add(StringProcessor.removeToAndRoutingNumberInEmails(addressesTo[i].toString()));
			}
		}
		if ((addressesCC != null) && (addressesCC.length > 0)){
			for (int i = 0; i < addressesCC.length; i++){
				ccrecivers.add(StringProcessor.removeToAndRoutingNumberInEmails(addressesCC[i].toString()));
			}
		}
		if ((addressesBCC != null) && (addressesBCC.length > 0)){
			for (int i = 0; i < addressesBCC.length; i++){
				bccrecievers.add(StringProcessor.removeToAndRoutingNumberInEmails(addressesBCC[i].toString()));
			}
		}

        String cc = (new SpherePossibleEmailsSet(ccrecivers))
                .getSingleStringEmails();
        String bcc = (new SpherePossibleEmailsSet(bccrecievers))
        		.getSingleStringEmails();
        email.setCcrecievers(cc);
        email.setBccrecievers(bcc);
		
	}
}
