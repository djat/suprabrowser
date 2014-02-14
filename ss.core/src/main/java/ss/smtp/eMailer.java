package ss.smtp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.IAttachedFile;
import ss.client.ui.email.SendList;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.PathUtils;
import ss.util.VariousUtils;

/**
 * Demo app that exercises the Message interfaces. List information about
 * folders using connection to mail storage.
 * 
 * Based on folderlist JavaMail example by John Mani and Bill Shannon
 * 
 * @author Eugen Kuleshov
 */

public class eMailer {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(eMailer.class);

	private static final boolean DEBUG_MODE = false;

	private static final int RETRY_TIMES = 50;

	private final Vector<PostponedSend> reSendQueue = new Vector<PostponedSend>();

	private final Hashtable<String, String> retriedNumber = new Hashtable<String, String>();

	static boolean debug = true;
	
	private final Object addToResendQueueMutex = new Object();
	
	public eMailer() {

	}
	
	public List<SendList> send(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, StringBuffer text, String subject,
			String replySphere) {
		return send(addressesContainer, files, text, subject, replySphere, null);
	}

	/**
	 * @param recipientHost
	 * @param sender
	 * @param recipient
	 * @param addressesContainer
	 * @param files
	 * @param sb
	 * @param subject
	 * @param replySphere
	 * @param object
	 */
	private List<SendList> send(EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, StringBuffer text, String subject,
			String replySphere, String existingUnique) {
		List<SendList> brokenList = new ArrayList<SendList>();
		for (SendList sendList : addressesContainer.getSendLists()) {
			String sender = addressesContainer.getFrom();
			String recipient = addressesContainer.getSendTo();

			logger.info("REPLY SPHERE...." + replySphere + " : recip:  "
					+ recipient);

			String recipientHost = SpherePossibleEmailsSet
					.getDomainFromSingleAddress(recipient);

			logger.info("SENDING EMAIL : " + recipientHost + " , " + sender
					+ " , " + recipient + " , " + text + " , " + subject
					+ " , " + replySphere);
			try {
				send(sendList, sender, recipient, addressesContainer, files,
						text, subject, replySphere, existingUnique);
			} catch (Exception ex) {
				logger
						.error("Error sending email to " + sendList.getHost(),
								ex);
				brokenList.add(sendList);
			}
		}
		return brokenList;
	}

	public void reSend(final String uniqueId,
			final EmailAddressesContainer addressesContainer,
			final AttachedFileCollection files, final StringBuffer sb,
			final String subject, final String replySphere) {

		String number = null;
		try {
			number = (String) this.retriedNumber.get(uniqueId);
		} catch (Exception npe) {
			logger.error(npe);
			this.retriedNumber.put(uniqueId, "1");
		}
		if (number == null) {
			number = "0";
		}

		logger.info("Number: " + number);
		int times = new Integer(number).intValue() + 1;

		if (times <= RETRY_TIMES) {

			this.retriedNumber.put(uniqueId, new Integer(times).toString());

			Thread t = new Thread() {
				public void run() {
					send(addressesContainer, files, sb, subject, replySphere,
							uniqueId);
					try {
						sleep(30000);
					} catch (InterruptedException e) {
						logger.error(e);
					}

				}

			};
			t.start();

		}

	}

	public void send(SendList sendList, String sender, String recipient,
			EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, StringBuffer text, String subject,
			String replySphere, String existingUnique) throws Exception {

		SMailMessage smessage = null;
		String uniqueId = null;
		if (existingUnique != null) {
			uniqueId = existingUnique;
		} else {
			uniqueId = VariousUtils.getNextRandomLong();
		}

		final Properties props = System.getProperties();

		final String body = text.toString();

		if (body.length() > 0) {
			logger.info("will send body too");
		}

		props.setProperty("mail.smtp.socketFactory.fallback", "true");
		// props.setProperty( "mail.smtp.socketFactory.port", "25");
		props.setProperty("mail.smtp.socketFactory.port", "25");
		String mxHost = mxLookup(sendList.getHost());
		props.put("mail.smtp.host", mxHost);
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.localhost", "suprasecure.com");

		Session sendsession = Session.getDefaultInstance(props, null);

		smessage = new SMailMessage(sendsession);

		smessage.setFrom(new InternetAddress(sender));

		smessage.addRecipient(Message.RecipientType.TO, new InternetAddress(
				recipient));
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

		smessage.setSubject(subject);
		createMessageContent(smessage, body, files);
		smessage.setHeader(sender);
		smessage.setSentDate(new Date());

		smessage.updateHeaders();
		smessage.saveChanges();

		if (DEBUG_MODE) {
			final File outFile = new File(PathUtils.combinePath(PathUtils
					.getBaseDir(), "test-mail.txt"));
			logger.warn("DEBUG_MODE: Write email to local file "
					+ outFile.getCanonicalPath());
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			final FileOutputStream out = new FileOutputStream(outFile);
			try {
				smessage.writeTo(out);
			} finally {
				out.close();
			}
		} else {
			Transport transport = sendsession.getTransport("smtp");
			transport.connect(mxHost, 25, "", "");
			logger.info("Seinding message by " + sendList);
			transport.sendMessage(smessage, sendList.getAddresses());
			transport.close();
			logger.info("Sent to " + mxHost);
		}

		if (false) {
			if (this.retriedNumber.get(uniqueId) != null) {
				logger.info("Times for that unique: " + uniqueId + " : "
						+ (String) this.retriedNumber.get(uniqueId));
			} else {
				logger.info("Times for that unique: " + uniqueId + " : 0");
			}

			PostponedSend toResend = new PostponedSend();
			toResend.setUniqueId(uniqueId);
			toResend.setAddressesContainer(addressesContainer);
			toResend.setAttachedFiles(files);
			toResend.setSubject(subject);
			toResend.setReplySphere(replySphere);
			toResend.setText(text);
			addToResendQueue(toResend);
		}
	}

	private void createMessageContent(SMailMessage smessage, final String body,
			final AttachedFileCollection files) throws MessagingException {
		// TODO:see
		// org.springframework.mail.javamail.MimeMessageHelperMimeMessageHelper
		// TODO:compare gmail and current emails texts
		
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
	private boolean isTextHTML(String body) {
		// TODO: implement for possible use of text/plain messages 
		return true;
	}

	/**
	 * @param smessage
	 * @param body
	 * @throws MessagingException 
	 */
	private void createMessageContentPlain(SMailMessage smessage, String body) throws MessagingException {
		smessage.setText(applyHTMPTags(body),"ISO-8859-1");
	}

	/**
	 * @param body
	 * @return
	 * @throws MessagingException 
	 */
	private void createMessageContentHTML(final SMailMessage smessage, final String body) throws MessagingException {
		smessage.setContent(applyHTMPTags(body),"text/html; charset=\"ISO-8859-1\"");
		/*MimeMultipart mimeMultipart;
		mimeMultipart = new MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		//messageBodyPart.setText(body, "ISO-8859-1");
		messageBodyPart.setContent(applyHTMPTags(body),"text/html; charset=\"ISO-8859-1\"");
		//messageBodyPart.setHeader("Content-Type", "text/html; charset=\"ISO-8859-1\"");
		mimeMultipart.addBodyPart(messageBodyPart);
		
		return mimeMultipart;*/
	}

	/**
	 * @param body
	 * @param files
	 * @return
	 */
	private void createMessageContentMultipart(final SMailMessage smessage, final String body, final AttachedFileCollection files) throws MessagingException {
		MimeMultipart mimeMultipart;
		mimeMultipart = new MimeMultipart("mixed");
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		//messageBodyPart.setText(body, "ISO-8859-1");
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

	/**
	 * @param body
	 * @return
	 */
	private String applyHTMPTags(String body) {
		String retString = "<html><head></head><body> " + body + "</body></html>";
		return retString;
	}

	public void addToResendQueue(PostponedSend toResend) {
		this.reSendQueue.add(toResend);
		synchronized (this.addToResendQueueMutex) {
			PostponedSend tryAgain = this.reSendQueue.elementAt(0);
			while (this.reSendQueue.size() > 0) {
				reSend(tryAgain.getUniqueId(),
						tryAgain.getAddressesContainer(), tryAgain
								.getAttachedFiles(), tryAgain.getText(),
						tryAgain.getSubject(), tryAgain.getReplySphere());
				this.reSendQueue.removeElementAt(0);
			}
		}
	}

	/*
	 * public static void main( String args[] ) { if( args.length == 0 ) {
	 * System.err.println( "Usage: MXLookup host [...]" ); System.exit( 99 ); }
	 * for( int i = 0; i < args.length; i++ ) { try { logger.info( args[i] + "
	 * has " + doLookup( args[i] ) + " mail servers" ); } catch( Exception e ) {
	 * logger.info(args[i] + " : " + e.getMessage()); } } }
	 */

	public String mxLookup(String hostName) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = null;
		try {
			attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		} catch (NameNotFoundException nnfe) {
			return hostName;

		}
		Attribute attr = attrs.get("MX");

		if (attr != null) {
			String first = (String) attr.get(0);

			StringTokenizer st = new StringTokenizer(first, " ");
			st.nextToken();
			String remainder = st.nextToken();
			String trimmed = remainder.substring(0, remainder.length() - 1);
			logger.info("RETURNING LOOKUP OF MX RECORD: " + trimmed);
			return trimmed;

			// return( attr.size() );
		} else {

			return null;
		}

	}

}
