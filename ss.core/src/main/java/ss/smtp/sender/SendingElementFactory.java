/**
 * 
 */
package ss.smtp.sender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import ss.client.ui.PreviewHtmlTextCreator;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.IAttachedFile;
import ss.client.ui.email.SendList;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.domainmodel.configuration.DomainProvider;
import ss.smtp.SMailMessage;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.sender.SendingElement.SendingElementMode;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class SendingElementFactory {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendingElementFactory.class);
	private static String localHost = null;
	
	private SendingElementFactory(){
		
	}
	
	public static List<SendingElement> createForwarded(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, EmailBody emailBody, String messageId, String sphereId){
		return create(addressesContainer, files, emailBody, messageId, sphereId, SendingElementMode.FORWARDED, null);
	}
	
	public static List<SendingElement> createCreated(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, EmailBody emailBody, String messageId, String sphereId, String emailmessageId){
		return create(addressesContainer, files, emailBody, messageId, sphereId, SendingElementMode.CREATED, emailmessageId);
	}
	
	private static List<SendingElement> create(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, EmailBody emailBody, String messageId, String sphereId, SendingElementMode mode, String emailmessageId){
		List<SendingElement> elements = new ArrayList<SendingElement>(); 
		
		for ( SendList sendList : addressesContainer.getSendLists() ) {
			final String sender = addressesContainer.getFrom();
			final String recipient = addressesContainer.getSendTo();
			final String recipientHost = SpherePossibleEmailsSet
					.getDomainFromSingleAddress(recipient);

			if (logger.isDebugEnabled()) {
				logger.debug("SENDING EMAIL : " + recipientHost + " , " + sender
						+ " , " + recipient + " , " + emailBody.getBody() + " , " + emailBody.getSubject());
			}

			try {
				final List<String> mxHosts = HostResolver.resolver.getHostAddress(sendList.getHost());
				final Session sendsession = createSession(mxHosts.get(0));
				final String newEmailMessageId = (emailmessageId != null) ? emailmessageId : generateMessageHeader( messageId );
				final SendingElementRecreateInfo recreateInfo = new SendingElementRecreateInfo(addressesContainer, files, emailBody, newEmailMessageId );
				final SMailMessage smessage = create(addressesContainer, files, emailBody, sendsession, newEmailMessageId );
				elements.add(new SendingElement(smessage, sendsession, sendList, mxHosts, messageId, sphereId, mode, recreateInfo));
			} catch (Exception ex) {
				logger.error("Error in email composing on server" ,ex);
			}
		}
	
		return elements;
	}
	
	public static SendingElement recreate(SendingElement element, String mxHost) throws Exception{

		try {
			SendingElementRecreateInfo recreateInfo = element.getRecreateInfo();
			Session sendsession = createSession(mxHost);
			SMailMessage smessage = create(recreateInfo.getAddressesContainer(), recreateInfo.getFiles(), recreateInfo.getEmailBody(), sendsession, recreateInfo.getHeader());
			
			return new SendingElement(smessage, sendsession, element.getSendList(), element.getMXHosts(), element.getMessageId(),
					element.getSphereId(), element.getMode(), recreateInfo);
		} catch (Exception ex) {
			logger.error(ex);
			throw new Exception("Error recreating element");
		}
	}
	
	private static Session createSession(String mxHost){
		final Properties props = System.getProperties();
		
		props.setProperty("mail.smtp.socketFactory.fallback", "true");
		props.setProperty("mail.smtp.socketFactory.port", "25");
		props.put("mail.smtp.host", mxHost);
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.localhost", getLocalHost());
		
		return Session.getDefaultInstance(props, null);
	}
	
	private static Object getLocalHost() {
		if ( localHost == null ) {
			localHost = DomainProvider.getDefaultDomain();
		}
		return localHost;
	}

	private static SMailMessage create(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, EmailBody subjBody, Session sendsession, String emailMessageId) throws Exception{
		SMailMessage smessage = null;

		final String body = subjBody.getBody();

		if (body.length() > 0) {
			logger.info("will send body too");
		}
		
		smessage = new SMailMessage(sendsession);

		smessage.setFrom(new InternetAddress(addressesContainer.getFrom()));

		smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(
				addressesContainer.getSendTo()));
		if (addressesContainer.isCCExists()) {
			smessage.addRecipients(RecipientType.CC, addressesContainer
					.getCCAdresses());
		}
		if (addressesContainer.isBCCExists()) {
			smessage.addRecipients(RecipientType.BCC, addressesContainer
					.getBCCAdresses());
		}
		if (addressesContainer.isReplyToExists()) {
			smessage.setReplyTo(addressesContainer.getReplyTo());
		}

		smessage.setSubject(subjBody.getSubject());
		createMessageContent(smessage, body, files);
		if ( emailMessageId != null ) {
			smessage.setHeader(emailMessageId);
		}
		smessage.setSentDate(new Date());

		smessage.updateHeaders();
		smessage.saveChanges();

		return smessage;
	}
	
	/**
	 * @param sphereId 
	 * @param messageId 
	 * @return
	 */
	public static String generateMessageHeader( final String messageId ) {
		final String unificator1 = VariousUtils.createMessageId();
		final String unificator2 = VariousUtils.createMessageId();
		String id = messageId + "-" + unificator1 + "-" + unificator2 + "@";
		final String domain = DomainProvider.getDefaultDomain();
		if (StringUtils.isNotBlank(domain)){
			id += domain;
		} else {
			id += "supra";
		}
		id += ".on.suprasphere";
		if (logger.isDebugEnabled()) {
			logger.debug("Result id = " + id);
		}
		return id;
	}
	
	public static String generateMessageHeader(){
		return generateMessageHeader( VariousUtils.createMessageId() );
	}

	private static String applyHTMPTags( final String body ) {
		String htmlBody = body;
		if (!PreviewHtmlTextCreator.isHtml(htmlBody)){
			htmlBody = convert(htmlBody);
		}
		final String resString = "<html><head></head><body> " + htmlBody + "</body></html>";
		return resString;
	}
	
	/**
	 * @param htmlBody
	 * @return
	 */
	private static String convert( final String str ) {
		String text = str.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
		StringBuilder builder = new StringBuilder();
		final String[] parts = text.split("\n");
		builder.append("<p>");
		boolean insert = false;
		for (String s : parts){
			if (insert){
				builder.append("</p><p>");
			} else {
				insert = true;
			}
			builder.append(s);
		}
		builder.append("</p>");
		return builder.toString();
	}

	private static  void createMessageContent(SMailMessage smessage, final String body,
			final AttachedFileCollection files) throws MessagingException {
		
		if ((files != null) && (files.getCount() != 0)) {
			createMessageContentMultipart(smessage, body, files);
			return;
		}
		if (isTextHTML(body)) {
			createMessageContentHTML(smessage, body);
			return;
		} else {
			createMessageContentPlain(smessage, body);
			return;
		}
	}
	
	/**
	 * @param body
	 * @return
	 */
	private static boolean isTextHTML(String body) {
		// TODO: implement for possible use of text/plain messages 
		return true;
	}
	
	/**
	 * @param smessage
	 * @param body
	 * @throws MessagingException 
	 */
	private static void createMessageContentPlain(SMailMessage smessage, String body) throws MessagingException {
		smessage.setText(applyHTMPTags(body),"ISO-8859-1");
	}

	/**
	 * @param body
	 * @return
	 * @throws MessagingException 
	 */
	private static void createMessageContentHTML(final SMailMessage smessage, final String body) throws MessagingException {
		smessage.setContent(applyHTMPTags(body),"text/html; charset=\"ISO-8859-1\"");
	}

	/**
	 * @param body
	 * @param files
	 * @return
	 */
	private static void createMessageContentMultipart(final SMailMessage smessage, final String body, final AttachedFileCollection files) throws MessagingException {
		MimeMultipart mimeMultipart;
		mimeMultipart = new MimeMultipart("mixed");
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		messageBodyPart.setText(applyHTMPTags(body));
		messageBodyPart.setHeader("Content-Type", "text/html; charset=\"ISO-8859-1\"");
		mimeMultipart.addBodyPart(messageBodyPart);
		MimeBodyPart messageFileBodyPart = null;
		if ((files != null) && (files.getCount() != 0)) {
			for (final IAttachedFile singleFile : files) {
				messageFileBodyPart = new MimeBodyPart();
				messageFileBodyPart.setFileName(singleFile.getName());
				messageFileBodyPart.setDataHandler(new DataHandler(singleFile
						.createDataSource()));
				mimeMultipart.addBodyPart(messageFileBodyPart);
			}
		}
		smessage.setContent(mimeMultipart);
	}
}
