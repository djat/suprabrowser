package ss.server.networking.protocol;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.dom4j.Document;

import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.IAttachedFile;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.converter.SimpleFileDocumentConverter;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;
import ss.server.SystemSpeaker;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.processing.FileProcessor;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;
import ss.util.SessionConstants;
import ss.util.SupraXMLConstants;

public class SendEmailFromServerHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendEmailFromServerHandler.class);

	public SendEmailFromServerHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_EMAIL_FROM_SERVER;
	}

	public void handle(Hashtable update) {
		handleSendEmailFromServer(update);

	}

	@SuppressWarnings("unused")
	private static final String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private static final String fsep = System.getProperty("file.separator");

	public void handleSendEmailFromServer(final Hashtable update) {
		Thread t = new Thread() {
			public void run() {
				EmailAddressesContainer addressesContainer = (EmailAddressesContainer) update
						.get(SessionConstants.TO_EMAIL_CONTAINER);
				AttachedFileCollection files = (AttachedFileCollection) update
						.get(SessionConstants.TO_EMAIL_ATTACHED_FILES);
				String body = (String) update.get(SC.BODY);
				String subject = (String) update.get(SC.SUBJECT);
				//String replySphere = (String) update.get(SC.REPLY_SPHERE);

				Hashtable toPublish = (Hashtable) update
						.get(SessionConstants.SESSION);

				if (toPublish != null) {

					Document doc = (Document) toPublish
							.get(SessionConstants.DOCUMENT);
					ExternalEmailStatement email = ExternalEmailStatement.wrap(doc);
					if (logger.isDebugEnabled()) {
						logger.debug("MessageId = " + email.getMessageId());
						logger.debug("CurrentSphere = " + email.getCurrentSphere());
						logger.debug("Address = " + email.getAddress());
					}
					String emailmessageId = SendingElementFactory.generateMessageHeader(email.getMessageId());
					email.setEmailmessageId(emailmessageId);
					

					String sphereId = email.getCurrentSphere();
					if (sphereId == null) {
						sphereId = (String)toPublish.get(SC.SPHERE_ID);
						email.setCurrentSphere(sphereId);
					}

					PublishHandler.fillRequeredFileds(email.getBindedDocument(),
							SendEmailFromServerHandler.this.peer.getUserContactName(), DialogsMainPeer.getCurrentMoment(), sphereId);
					SendEmailFromServerHandler.this.peer.getXmldb().insertDoc(email.getBindedDocument(), sphereId);
					
					final List<Document> fileDocs = createFileDocs(toPublish, files);

					publish(toPublish, doc);

					PublishFiles(toPublish, fileDocs);
					
					Statement st = Statement.wrap(doc);
					try {
						List<SendingElement> sendingElements = SendingElementFactory
								.createCreated(addressesContainer, files,
										new EmailBody(subject, body), st
												.getMessageId(), st
												.getCurrentSphere(), email.getEmailmessageId());
						Responcer.INSTANCE.initiateResponceElement(st
								.getMessageId(), st.getCurrentSphere(),
								sendingElements.size());
						for (SendingElement sendingElement : sendingElements) {
							Mailer.INSTANCE.send(sendingElement);
						}
						// List<SendList> notSent = (new eMailer()).send(
						// addressesContainer, files, new StringBuffer(body),
						// subject, replySphere);
						// Statement st = Statement.wrap(doc);
						// SystemSpeaker.speakNotSent(notSent,
						// st.getCurrentSphere(),
						// st.getMessageId());

					} catch (Exception ex) {
						logger.error(ex);
						SystemSpeaker.speakMessageError("Email sending failed", "Wrong addresses specified", st.getCurrentSphere(), st.getMessageId());
					}
				}
			}
		};
		t.start();
	}

	@SuppressWarnings("unchecked")
	private void publish(final Hashtable toSend, final Document doc) {

		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.PUBLISH);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, doc);

		Hashtable cliSession = toSend;// this.cli.getSession();
		logger.warn("publishing in this sphere: !!!"
				+ (String) toSend.get(SessionConstants.SPHERE_ID));

		if (cliSession.get(SessionConstants.EXTERNAL_CONNECTION) != null) {

			update.put(SessionConstants.EXTERNAL_CONNECTION, "true");

			logger.warn("it has an external connection...good");
			String localSphereURL = (String) cliSession
					.get(SessionConstants.LOCAL_SPHERE);
			if (localSphereURL == null) {
				localSphereURL = (String) cliSession
						.get(SessionConstants.SPHERE_URL);
			}

			logger.warn("it was not null " + localSphereURL);

			try {
				// update.put("remoteSphereURL",localSphereURL);
				update.put(SessionConstants.REMOTE_USERNAME,
						(String) cliSession.get(SessionConstants.USERNAME));
				// update.put("remoteSphereId",localSphereId);
			} catch (Exception e) {

			}

			Hashtable sendSession = (Hashtable) toSend.clone();
			String before = (String) sendSession
					.get(SessionConstants.SPHERE_ID);

			String localSphereId = (String) cliSession
					.get(SessionConstants.LOCAL_SPHERE_ID);

			sendSession.put(SessionConstants.SPHERE_ID, localSphereId);

			Hashtable localUpdate = (Hashtable) update.clone();

			logger
					.warn("Will send local update to this sphere, this is the critical part: "
							+ localSphereId);

			if (!localSphereId.equals(before)) {
				localUpdate.put(SessionConstants.SESSION, sendSession);
				localUpdate.put(SessionConstants.SPHERE, localSphereId);
				localUpdate.put(SessionConstants.REPRESS_NOTIFICATION, "true");

				logger.warn("sending also to this sphere: " + localSphereId);
			}

		} else {
			logger
					.warn("it does not have an external connection variable...boo");
		}
		update.put(SC.IS_SKIP_INSERTDOC_IN_PUBLISHHANDLER, new Boolean(true));
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH)
			.handle(update);
	}
	
	private List<Document> createFileDocs(Hashtable toPublish, AttachedFileCollection files){
		final List<Document> fileDocs = new ArrayList<Document>();
		try {
		if ( (files == null) || (files.getCount() <= 0) ) {
			return fileDocs;
		}
		Document doc = (Document) toPublish.get(SessionConstants.DOCUMENT);
		ExternalEmailStatement email = ExternalEmailStatement.wrap(doc);
		String messageId = email.getMessageId();
		String giver = email.getGiver();
		String sphereId = (String) toPublish.get(SC.SPHERE_ID);
		logger.error("sphereId: " + sphereId);
		for (IAttachedFile file : files) {
			try {
				final Document fileDoc = FileProcessor.INSTANCE.processFile(messageId, SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL, file, toPublish, giver);

				if (fileDoc == null) {
					logger.error("Skipping publishing file: " + file.getName() + ", due to error during processing occured");
					continue;
				}
				
				this.peer.getXmldb().insertDoc(fileDoc, sphereId);
				
				fileDocs.add( fileDoc );
			} catch (Exception ex) {
				logger.error("error processing file: " + file.getName(), ex);
			}
		}
		} catch (Exception ex) {
			logger.error("Error in saving files",ex);
		}
		return fileDocs;
	}

	private void PublishFiles(Hashtable toPublish, List<Document> fileDocs) {
		if ( (fileDocs == null) || (fileDocs.isEmpty()) ) {
			return;
		}
		Document doc = (Document) toPublish.get(SessionConstants.DOCUMENT);
		ExternalEmailStatement email = ExternalEmailStatement.wrap(doc);
		String giver = email.getGiver();
		String sphereId = email.getCurrentSphere();
		
		for (Document fileDoc : fileDocs) {
			try {
				String supraSphereName = (String)this.peer.getSession().get(SC.SUPRA_SPHERE);

				SimpleFileDocumentConverter.convert(this.peer.getSession(), supraSphereName, giver, sphereId, fileDoc);
			} catch (Exception ex) {
				logger.error("error processing file", ex);
			}
		}
	}

//	private Document processFile(String responseId, AttachedFile file,
//			Hashtable session, String giver) throws MessagingException,
//			FileNotFoundException {
//		String originalFname = file.getName();
//		logger.info("For this attachment, will write: "
//				+ originalFname.toString());
//
//		String fname = VariousUtils.getNextRandomLong() + "_____"
//				+ originalFname;
//		File out = new File(bdir + fsep + "roots" + fsep
//				+ (String) session.get("supra_sphere") + fsep + "File" + fsep
//				+ fname);
//		// File out = new File(fname);
//
//		// TODO: add file path creation
//
//		FileOutputStream fout = new FileOutputStream(out);
//		// mp.getBodyPart(i).writeTo(fout);
//		int bytes = -1;
//		try {
//			(new DataHandler(file.createDataSource())).writeTo(fout);
//			fout.close();
//			FileInputStream fin = new FileInputStream(bdir + fsep + "roots"
//					+ fsep + (String) session.get("supra_sphere") + fsep
//					+ "File" + fsep + fname);
//			bytes = fin.available();
//		} catch (Exception ex) {
//			logger.error("File read exception", ex);
//		}
//
//		Document createDoc = DocumentHelper.createDocument();
//
//		Element email = createDoc.addElement("email");
//
//		String from = giver;
//
//		if (from == null) {
//			from = " NULL ";
//		}
//		email.addElement("giver").addAttribute("value", from);
//
//		email.addElement("subject").addAttribute("value", originalFname);
//
//		// email.addElement("last_updated_by").addAttribute("value",
//		// (String) this.session.get("contact_name"));
//
//		DefaultElement body = new DefaultElement("body");
//		body.setText("");
//		body.addElement("version").addAttribute("value", "3000");
//		body.addElement("orig_body").setText("");
//		email.add(body);
//
//		Date current = new Date();
//		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
//				current)
//				+ " "
//				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
//
//		// String response_id = null;
//
//		// long longnum = GenericXMLDocument.getNextTableId();
//
//		String messageId = VariousUtils.createMessageId();
//		email.addElement("message_id").addAttribute("value", messageId);
//		email.addElement("original_id").addAttribute("value", messageId);
//		email.addElement("response_id").addAttribute("value", responseId);
//		email.addElement("thread_id").addAttribute("value", responseId);
//
//		email.addElement("moment").addAttribute("value", moment);
//		email.addElement("last_updated").addAttribute("value", moment);
//
//		email.addElement("voting_model").addElement("tally");
//
//		email.addElement("confirmed").addAttribute("value", "true");
//		email.element("voting_model").element("tally").addElement("member")
//				.addAttribute("value", from)
//				.addAttribute("vote_moment", moment);
//
//		email.addElement("type").addAttribute("value", "file");
//
//		email.addElement("thread_type").addAttribute("value",
//				SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
//
//		email.addElement("current_sphere").addAttribute("value",
//				(String) session.get(SessionConstants.SPHERE_ID));
//
//		email.addElement("bytes").addAttribute("value",
//				new Integer(bytes).toString());
//		email.addElement("original_data_id").addAttribute("value", fname);
//		email.addElement("data_id").addAttribute("value", fname);
//
//		logger.info("CREATE FILE DOC : " + createDoc.asXML());
//		return createDoc;
//	}
}
