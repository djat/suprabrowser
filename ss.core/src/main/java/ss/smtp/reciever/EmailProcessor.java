/**
 * 
 */
package ss.smtp.reciever;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.dom4j.Document;

import ss.client.networking.SupraClient;
import ss.common.StringUtils;
import ss.domainmodel.ExternalEmailStatement;
import ss.server.db.XMLDB;
import ss.smtp.Mail;
import ss.smtp.MailAddress;
import ss.smtp.custom.clubdealinsubject.ClubDealInEmailSubjectStrategy;
import ss.smtp.reciever.file.CreatedFileInfo;
import ss.smtp.reciever.file.FileProcessor;
import ss.smtp.sender.SendingElementFactory;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class EmailProcessor {

	private static final String DELIVER_MAIL = "DeliverMail";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailProcessor.class);

	public final static String SENDER = "SENDER_ADDRESS";

	public final static String REPLY_TO = "Reply-To";
	
	public final static String FROM = "From";
	
	private Hashtable loginSession;
	
	private String sender;
		
	private List<String> notInSupraSphere;
	
	static {
		System.setProperty("mail.mime.decodefilename", "true");
	}

	public EmailProcessor(HashMap state) {
		super();
		this.sender = obtainSender(state);
	}

	@SuppressWarnings("unchecked")
	public void processEmail(Mail mail) throws IOException, MessagingException {

		if (logger.isDebugEnabled()) {
			logger.debug("processEmail performed");
		}
		final Message message = mail.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug("message: " + message.toString());
		}
		
		tryRenewSender( message );
		
		final RecipientsProcessor recipientsProcessor = new RecipientsProcessor();
		final RecieveList recieveList = recipientsProcessor.process(mail);
		this.notInSupraSphere = recipientsProcessor.getNotRecieved();

		final SupraClient sc = new SupraClient();
		
		if (recieveList.getRecievers().isEmpty()){
			return;
		}
		final XMLDB xmldb = new XMLDB();
		
		String supraSphere = "";
		try {
			supraSphere = xmldb.getSupraSphere().getName();
		} catch (NullPointerException ex1) {
			logger.error( "Error obtaining supraSphere name",ex1 );
		}

		this.loginSession = sc.loadMachineServerAuthProperties();
		MailData data = null;

		if (message.isMimeType("text/plain")) {
			if (logger.isDebugEnabled()) {
				logger.debug("text/plain type of message");
			}
			data = processTextPlainMail(mail);

		} else if (message.isMimeType("text/html")) {
			if (logger.isDebugEnabled()) {
				logger.debug("text/html type of message");
			}
			data = processTextHTMLMail(mail);

		} else if ((message.isMimeType("multipart/*"))
				|| (message.isMimeType("mixed"))) {
			if (logger.isDebugEnabled()) {
				logger.debug("multipart type of message");
			}
			data = processMultipartMail(mail, recieveList, supraSphere);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("other type of message");
			}
			data = processMultipartMail(mail, recieveList, supraSphere);
		}
		final String msg[] = message.getHeader("Message-ID");
		final String emailMessageId = ((msg != null)&&(msg.length > 0)) ? msg[0] : SendingElementFactory.generateMessageHeader();
		ExternalEmailStatement.wrap( data.getBody() ).setEmailmessageId( emailMessageId );
		data.setRecieveList(recieveList);
		
		try {
			ClubDealInEmailSubjectStrategy.INSTANCE.applyStrategy( data, xmldb );
		} catch ( Throwable ex ){
			logger.error( "Error in applying clubdeal strategy", ex );
		}
		
		this.loginSession.put(SessionConstants.MAIL_DATA_CONTAINER, data);
		sc.startZeroKnowledgeAuth(this.loginSession, DELIVER_MAIL);
	}

	/**
	 * Setting sender to FROM (or REPLY_TO) in email so trying not to publish bad looking sender addresses
	 */
	private void tryRenewSender( final Message message ) {
		try {
			final String msg[] = message.getHeader( FROM );
			if ( (msg != null)&&(msg.length > 0) ) {
				String from = msg[0];
				if ( StringUtils.isNotBlank( from ) ){
					if ( !from.contains("=") ) {
						this.sender = new String( from );
						return;
					}
				}
			}
		} catch (MessagingException ex) {
			logger.error( "Exception in trying to obtain FROM header in email", ex );
		}

		try {
			final String msg[] = message.getHeader( REPLY_TO );
			if ( (msg != null)&&(msg.length > 0) ) {
				if ( StringUtils.isNotBlank( msg[0] ) ){
					this.sender = new String( msg[0] );
				}
			}
		} catch (MessagingException ex) {
			logger.error( "Exception in trying to obtain REPLY_TO header in email", ex );
		}
	}

	/**
	 * @param mail
	 * @param result
	 * @param loginSession
	 * @throws MessagingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private MailData processMultipartMail(Mail mail, RecieveList recieveList, String supraSphere) throws MessagingException,
			IOException, FileNotFoundException {
		logger.info("ITS MULTIPART");

		Message msg = mail.getMessage();
		Vector<CreatedFileInfo> files = new Vector<CreatedFileInfo>();
		Multipart mp = (Multipart) msg.getContent();
		Document emailBody = null;

		for (int i = 0; i < mp.getCount(); i++) {

			BodyPart bodyPart = mp.getBodyPart(i);
			if (bodyPart.getFileName() != null) {

				if ( FileProcessor.INSTANCE.isAllowedFileName( bodyPart.getFileName() ) ) {
					files.add(FileProcessor.INSTANCE.createFileDocument(recieveList, bodyPart, this.sender, supraSphere));
				} else {
					logger.warn( "Not allowed filename: " + bodyPart.getFileName() );
				}

			} else {
				logger.info("how many times? " + i);

				emailBody = processBodyOfMultipart(mail, bodyPart);
			}
		}

		MailData data = new MailData();
		data.setBody(emailBody);
		data.setFiles(files);

		return data;
	}

	/**
	 * @param mail
	 * @param result
	 * @param loginSession
	 */
	private MailData processTextHTMLMail(Mail mail) {
		Document document;
		try {
			document = BodyProcessor.createBodyDocument(mail, mail.getMessage()
						.getContent().toString(), this.sender);
			MailData data = new MailData();
			data.setBody(document);
			return data;
		} catch (Exception ex) {
			logger.error("Exeption processing HTML Body", ex);
		} 
		return null;
	}

	/**
	 * @param mail
	 * @param result
	 * @param loginSession
	 */
	private MailData processTextPlainMail(Mail mail) {
		Document document;
		try {
			document = BodyProcessor.createBodyDocument(mail, mail.getMessage()
						.getContent().toString(), this.sender);
			MailData data = new MailData();
			data.setBody(document);
			return data;
		} catch (Exception ex) {
			logger.error("Exeption processing Text Plain Body", ex);
		} 
		return null;
	}

	private String obtainSender(HashMap state){
		String from = null;
		try {
			from = ((MailAddress) state.get(SENDER)).toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (from == null) {
			from = " NULL ";
		}
		return from;
	}

	/**
	 * @param mail
	 * @param result
	 * @param loginSession
	 * @param globalResponse
	 * @param bodyPart
	 */
	private Document processBodyOfMultipart(Mail mail, BodyPart bodyPart) {

		try {

			String content = "";
			try {
				content = bodyPart.getDataHandler().getContent().toString();
			} catch (UnsupportedEncodingException usee) {
				content = "unsupported encoding";
			}
			String ctype = bodyPart.getContentType();
			logger.info("Content type...?" + ctype);
			// String content2 = bodyPart.getContent()
			// .toString();
			// String content3 = mp.getBodyPart(i).

			if (ctype.toLowerCase().lastIndexOf("alternative") != -1
					|| ctype.toLowerCase().lastIndexOf("related") != -1) {

				logger.info("its alternative..or related");
				Multipart mp2 = (Multipart) bodyPart.getContent();
				for (int j = 0; j < mp2.getCount(); j++) {
					String innerContent = mp2.getBodyPart(j).getContent()
							.toString();

					if (VariousUtils.isTextHTML(innerContent)) {

						content = innerContent;

					}

				}

			}
			
			return BodyProcessor.createBodyDocument(mail, content, this.sender);
		} catch (Exception ex) {
			logger.error("Exeption processing MultiPart Body", ex);
		}
		return null;
	}

	public static String convertToNamingConvention(String name) {
		String ret = name.replace(' ', '_');
		ret = ret.toLowerCase();
		return ret;
	}

	/**
	 * @return the notInSupraSphere
	 */
	public List<String> getNotInSupraSphere() {
		return this.notInSupraSphere;
	}
}
