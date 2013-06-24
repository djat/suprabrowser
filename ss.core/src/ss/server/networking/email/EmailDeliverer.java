/**
 * 
 */
package ss.server.networking.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.DmpFilter;
import ss.common.GenericXMLDocument;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.common.converter.ConvertingElementFactory;
import ss.common.converter.DocumentConverterAndIndexer;
import ss.common.file.DefaultDataForSpecificFileProcessingProvider;
import ss.common.file.ParentStatementData;
import ss.common.file.ReturnData;
import ss.common.file.SpecificFileProcessor;
import ss.common.file.vcf.NoteInfo;
import ss.common.file.vcf.VCardPublisher;
import ss.common.textformatting.simple.ParsingResult;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.ExternalEmailWithContactStatement;
import ss.domainmodel.Statement;
import ss.rss.RSSParser;
import ss.search.URLParser;
import ss.server.MethodProcessing;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.processing.FileProcessor;
import ss.smtp.defaultforwarding.EmailForwarder;
import ss.smtp.defaultforwarding.ForwardingElement;
import ss.smtp.reciever.MailData;
import ss.smtp.reciever.RecieveList;
import ss.smtp.reciever.Reciever;
import ss.smtp.reciever.file.CreatedFileInfo;
import ss.util.EmailUtils;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class EmailDeliverer {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailDeliverer.class);
	
	private final DialogsMainPeer peer;
	
	public EmailDeliverer( final DialogsMainPeer peer ){
		if ( peer == null ) {
			throw new NullPointerException("DialogsMainPeer can not be null");
		}
		this.peer = peer;
	}

	public void deliverMail(final Hashtable session) {
		final MailData data = (MailData) session
				.get(SessionConstants.MAIL_DATA_CONTAINER);

		final List<CreatedFileInfo> originalFiles = data.getFiles();
		final RecieveList recievers = data.getRecieveList();

		final Statement st = Statement.wrap(data.getBody());
		try {
			st.setSubject(new ParsingResult(st.getSubject()).getRemaindedSubject());
		} catch (Exception ex) {
			logger.error("Exception in renewing subject",ex);
		}
		
		ReturnData returnData = null;
		
		if (originalFiles != null) {
			final List<String> sphereIds = new ArrayList<String>();
			for (Reciever reciever : recievers.getRecievers()) {
				sphereIds.add(reciever.getRecipientsSphere());
			}
			for (CreatedFileInfo info : originalFiles) {
				try {
					final DefaultDataForSpecificFileProcessingProvider dataProvider = new DefaultDataForSpecificFileProcessingProvider(
							info.getSystemFileName(), info.getOriginalFileName(), st.getGiver(), getPeer(), 
							sphereIds, new ParentStatementData( st.getOrigBody(), st.getSubject() ));
					returnData = SpecificFileProcessor.INSTANCE.process( dataProvider );
				} catch (Exception ex) {
					logger
							.error(
									"Exception processing attached to email file into specific way",
									ex);
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.info("Mail Body:" + data.getBody().asXML());
			logger.info("Recievers number: " + recievers.getRecievers().size());
		}

		for (Reciever reciever : recievers.getRecievers()) {
			logger
					.info("Reciever sphere_id: "
							+ reciever.getRecipientsSphere());
			String sphereId = reciever.getRecipientsSphere();
			if ((returnData != null) && ( returnData.getSpheresNotToPublish() != null ) && ( returnData.getSpheresNotToPublish().contains( sphereId ) )) {
				if (logger.isDebugEnabled()) {
					logger.debug( "Not need to publish in sphere with id: " + sphereId );
				}
				continue;
			}
			String responceId = reciever.getResponseId();
			String messageId = new Long(GenericXMLDocument.getNextTableId())
					.toString();
			String threadId = messageId;
			
			boolean isToIndex = true;
			if ((returnData != null) && (returnData.getSpheresNotToIndex()!=null) && (returnData.getSpheresNotToIndex().contains(sphereId))) {
				isToIndex = false;
			}
			
			if (StringUtils.isNotBlank(responceId)) {
				Document messageToResponce = getXmldb().getSpecificMessage(
						responceId, reciever.getRecipientsSphere());
				if ( messageToResponce != null ) {
					threadId = Statement.wrap(messageToResponce).getThreadId();
				
					final ContactStatement specificContactStatement = getSpecificContactStatement( messageToResponce );
					if ( specificContactStatement != null ) {
						if (logger.isDebugEnabled()) {
							logger.debug("processing specific responce to contact");
						}
						processSpecificResponceToContact(specificContactStatement, 
								data.getBody(), originalFiles, session, isToIndex);
						return;
					}
				}
			}
			Document doc = EmailUtils.supplyWithIDs(((Document) data
					.getBody().clone()), messageId, threadId, responceId,
					sphereId);
			if (logger.isDebugEnabled()) {
				logger.debug("Mail Body after:" + doc.asXML());
			}
			final List<Document> files = createFilesList(originalFiles, sphereId, messageId, threadId);

			DeliverMailToSphere(reciever, doc, files, session, isToIndex);
		}
	}
	
	/**
	 * 
	 */
	private void processSpecificResponceToContact( final ContactStatement contactSt,
			final Document emailDocument,
			final List<CreatedFileInfo> originalFiles,
			final Hashtable session, final boolean isToIndex) {
		
		final String baseSphereId = contactSt.getCurrentSphere();
		final String baseMessageId = contactSt.getMessageId();
		String baseThreadId = baseMessageId;
		if ( StringUtils.isNotBlank( contactSt.getThreadId() )) {
			baseThreadId = contactSt.getThreadId();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("baseSphereId: " + baseSphereId);
			logger.debug("baseMessageId: " + baseMessageId);
			logger.debug("baseThreadId: " + baseThreadId);
		}
		
		final ExternalEmailStatement emailSt = ExternalEmailStatement.wrap(emailDocument);
		if (logger.isDebugEnabled()) {
			logger.debug("emailDocument: " + emailDocument.asXML());
		}
		final String sbj = emailSt.getSubject();
		final String bd = emailSt.getOrigBody();
		if (logger.isDebugEnabled()) {
			logger.debug("sbj: " + sbj);
			logger.debug("bd: " + bd);
		}
		final NoteInfo noteInfo = new NoteInfo( sbj, bd );
		noteInfo.setIsMessage(true);
		noteInfo.setSubject( sbj );
		noteInfo.setBody( bd );
		if (logger.isDebugEnabled()) {
			logger.debug("noteInfo: " + noteInfo.toString());
		}
		VCardPublisher.INSTANCE.publishNoteForExistingContact(
				noteInfo, emailSt.getGiver(), contactSt.getBindedDocument(), getPeer());
		
		final String sphereDisplayName = getVerifyAuth().getDisplayNameWithoutRealName(
				baseSphereId);
		final List<Document> files = createFilesList(originalFiles, baseSphereId, baseMessageId, baseThreadId);
		
		final XMLDB commit = new XMLDB();
		if ((files != null) && (!files.isEmpty())) {
			for ( Document file : files ) {
				commit.insertDoc(file, baseSphereId, isToIndex);
			}
		}
		
		String supraSphere = "";
		try {
			supraSphere = commit.getSupraSphere().getName();
		} catch (NullPointerException ex1) {
			logger.error(ex1);
		}
		
		if ((files != null) && (!files.isEmpty())) {
			for (final Document file : files) {
				String dataId = file.getRootElement().element("data_id")
									.attributeValue("value");
				
				final String name = FileProcessor.getFullPathName(dataId, supraSphere);
				final String messageId = file.getRootElement().element(
						"message_id").attributeValue("value");
				final String threadId = file.getRootElement().element(
						"thread_id").attributeValue("value");

				logger.warn("Name: " + name);
				logger.warn("Filename: " + file.getPath());
				File f = new File(name);
				logger.warn("Does it exist? :" + f);

				logger.info("Committing file: " + name);

				DocumentConverterAndIndexer.INSTANCE
						.convert(ConvertingElementFactory
								.createConvertAndPublish(session, threadId,
										messageId, name, baseSphereId, file,
										sphereDisplayName, isToIndex));
			}
		}
	}

	/**
	 * @param messageToResponce
	 * @return
	 */
	private ContactStatement getSpecificContactStatement(
			final Document messageToResponce) {
		if ( messageToResponce == null ) {
			logger.error("messageToResponce is null");
			return null;
		}
		final ExternalEmailWithContactStatement st = ExternalEmailWithContactStatement.wrap( messageToResponce );
		if ( st.isEmail() ) {
			final String contactMessageId = st.getContactMessageId();
			final String contactSphereId = st.getContactSphereId();
			if ( StringUtils.isNotBlank(contactMessageId) &&
					 StringUtils.isNotBlank(contactSphereId) ) {
				if (logger.isDebugEnabled()) {
					logger.debug("It is responce to sended out contact vcf");
				}
				final Document contact = getXmldb().getSpecificID(contactSphereId, contactMessageId);
				if ( contact != null ) {
					final ContactStatement contactSt = ContactStatement.wrap( contact );
					if ( contactSt.isContact() ) {
						if (logger.isDebugEnabled()) {
							logger.debug("ContactStatement found: " + contactSt.getContactNameByFirstAndLastNames() );
						}
						return contactSt;
					} else {
						logger.error("Doc found, but not contact for messageId: " + contactMessageId + " and sphereId: " + contactSphereId);
					}
				} else {
					logger.error("Contact doc not found for messageId: " + contactMessageId + " and sphereId: " + contactSphereId);
				}
			}
		}
		return null;
	}

	private List<Document> createFilesList( List<CreatedFileInfo> originalFiles, String sphereId, 
			String messageId, String threadId ){
		final List<Document> files = new ArrayList<Document>();
		if ((originalFiles != null) && (!originalFiles.isEmpty())) {
			for (CreatedFileInfo file : originalFiles) {
				String fileMessageId = new Long(GenericXMLDocument
						.getNextTableId()).toString();
				files.add(EmailUtils.supplyWithIDs(((Document) file
						.getFileDocument().clone()), fileMessageId,
						threadId, messageId, sphereId));
			}
		}
		return files;
	}

	private void DeliverMailToSphere(final Reciever reciever,
			final Document emailDoc, final List<Document> files,
			final Hashtable session, final boolean isToIndex) {

		logger.info("In Deliver Mail To Sphere");
		ExternalEmailStatement email = ExternalEmailStatement.wrap(emailDoc);

		if ( StringUtils.isBlank( email.getBody() ) && StringUtils.isNotBlank( email.getOrigBody() ) ) {
			email.setBody(email.getOrigBody());
		}
		final String sphereId = email.getCurrentSphere();

		final String moment = email.getMoment();
		logger.info("sphereID " + sphereId);

		final String contact = getVerifyAuth().getDisplayNameWithoutRealName(
				sphereId);

		logger.info("Contact : " + contact + " SupraSphere: " + sphereId);

		final XMLDB commit = new XMLDB();
	

		logger.info("Inserting doc : " + emailDoc.asXML());
		commit.insertDoc(emailDoc, sphereId, isToIndex);
		if ((files != null) && (!files.isEmpty())) {
			for ( Document file : files ) {
				commit.insertDoc(file, sphereId, isToIndex);
			}
		}
		
		for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
			dmpResponse.setDocumentValue(SC.DOCUMENT, emailDoc);
			logger.info("Delivering mail: " + emailDoc.asXML());
			dmpResponse.setStringValue(SC.DELIVERY_TYPE, "normal");
			dmpResponse.setStringValue(SC.SPHERE, sphereId);
			handler.sendFromQueue(dmpResponse.copy());

			try {
				String text = "";
				try {
					text = email.getSubject();
					text = text + " " + email.getOrigBody();

				} catch (Exception ex) {
				}
				Vector urls = URLParser.getURLSInsideString(text);

				processURLS(urls, handler, reciever, emailDoc, sphereId, moment);

			} catch (Exception ex) {
				logger.error("Exception in delivering mail body", ex);
			}
		}

		String supraSphere = "";
		try {
			supraSphere = commit.getSupraSphere().getName();
		} catch (NullPointerException ex1) {
			logger.error(ex1);
		}

		if ((files != null) && (!files.isEmpty())) {
			for (final Document file : files) {
				String dataId = file.getRootElement().element("data_id")
									.attributeValue("value");
				
				final String name = FileProcessor.getFullPathName(dataId, supraSphere);
				final String messageId = file.getRootElement().element(
						"message_id").attributeValue("value");
				final String threadId = file.getRootElement().element(
						"thread_id").attributeValue("value");

				logger.warn("Name: " + name);
				logger.warn("Filename: " + file.getPath());
				File f = new File(name);
				logger.warn("Does it exist? :" + f);

				logger.info("Committing file: " + name);

				DocumentConverterAndIndexer.INSTANCE
						.convert(ConvertingElementFactory
								.createConvertAndPublish(session, threadId,
										messageId, name, sphereId, file,
										contact, isToIndex));
			}
		}

		EmailForwarder.INSTANCE.send(new ForwardingElement(emailDoc, sphereId));
	}

	private void processURLS(Vector urls, DialogsMainPeer handler,
			final Reciever reciever, Document doc, final String sphereId,
			String moment) {

		ExternalEmailStatement emailStatement = ExternalEmailStatement
				.wrap(doc);
		Element email;
		for (int i = 0; i < urls.size(); i++) {
			String URL = (String) urls.get(i);

			Document exists = getXmldb().checkIfSeen(sphereId, URL);

			if (exists == null) {

				final String title = RSSParser.getTitleFromURL(URL);

				if (StringUtils.isBlank(title)) {
					logger.warn("Recieved blank title for URL: " + URL);
					continue;
				}

				Document createDoc = DocumentHelper.createDocument();
				email = createDoc.addElement(SC.EMAIL);

				String messageId = VariousUtils.createMessageId();
				// String response_id = null;

				email.addElement("original_id")
						.addAttribute("value", messageId);
				email.addElement("message_id").addAttribute("value", messageId);

				email.addElement("moment").addAttribute("value", moment);

				email.addElement("last_updated").addAttribute("value", moment);

				email.addElement("giver").addAttribute(
						"value",
						doc.getRootElement().element("giver").attributeValue(
								"value"));

				email.addElement("subject").addAttribute("value", title);
				email.addElement("address").addAttribute("value", URL);

				email.addElement("last_updated_by").addAttribute("value",
						emailStatement.getGiver());
				email.addElement("type").addAttribute("value", "bookmark");

				email.addElement("voting_model").addElement("tally")
						.addElement("member").addAttribute("value",
								"David Thomson").addAttribute("vote_moment",
								moment);

				final String parentMessageID = doc.getRootElement().element(
						"message_id").attributeValue("value");
				String threadID = null;
				try {
					threadID = doc.getRootElement().element("thread_id")
							.attributeValue("value");
				} catch (Throwable ex) {
					logger
							.error(
									"Cannot optain thread_id, use parent message_id instead",
									ex);
				}
				if (StringUtils.isBlank(threadID)) {
					threadID = parentMessageID;
				}

				email.addElement("response_id").addAttribute("value",
						parentMessageID);
				email.addElement("thread_id").addAttribute("value", threadID);

				email.addElement("thread_type").addAttribute(
						"value",
						doc.getRootElement().element("thread_type")
								.attributeValue("value"));
				email.addElement("status").addAttribute("value", "confirmed");
				email.addElement("confirmed").addAttribute("value", "true");

				Element body = new DefaultElement("body");

				body.addElement("version").addAttribute("value", "3000");

				body.addElement("orig_body");

				email.add(body);

				MethodProcessing mp = new MethodProcessing();
				Hashtable<String, String> toBookmark = new Hashtable<String, String>();
				toBookmark.put("sphere_id", emailStatement.getCurrentSphere());
				toBookmark.put("supra_sphere", emailStatement
						.getCurrentSphere());
				mp.processPublishedBookmark(toBookmark, createDoc,
						DialogsMainPeerManager.INSTANCE.getHandlers(),
						getXmldb());

				logger.info("Inserting Bookmark: " + createDoc.asXML());
				getXmldb().insertDoc(createDoc, sphereId);
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL,
						SSProtocolConstants.UPDATE);
				dmpResponse.setDocumentValue(SC.DOCUMENT, createDoc);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);

				handler.sendFromQueue(dmpResponse);
			}

		}
	}

	private DialogsMainPeer getPeer(){
		return this.peer;
	}
	
	private XMLDB getXmldb() {	
		return getPeer().getXmldb();
	}
	
	private VerifyAuth getVerifyAuth() {
		return getPeer().getVerifyAuth();
	}
}
