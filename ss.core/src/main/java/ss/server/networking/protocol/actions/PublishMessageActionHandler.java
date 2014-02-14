/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.PublishMessageAction;
import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.client.ui.email.IAttachedFile;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.converter.SimpleFileDocumentConverter;
import ss.common.file.DefaultDataForSpecificFileProcessingProvider;
import ss.common.file.ParentStatementData;
import ss.common.file.SpecificFileProcessor;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.server.SystemSpeaker;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.server.networking.processing.FileProcessor;
import ss.server.networking.protocol.PublishHandler;
import ss.smtp.defaultforwarding.EmailAddressesCreator;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.defaultforwarding.ForcedForwardingInfo;
import ss.smtp.defaultforwarding.ForsedForwardingData;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;
import ss.util.SessionConstants;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class PublishMessageActionHandler extends AbstractActionHandler<PublishMessageAction> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PublishMessageActionHandler.class);

	@SuppressWarnings("unused")
	private static final String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private static final String fsep = System.getProperty("file.separator");
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public PublishMessageActionHandler(DialogsMainPeer peer) {
		super(PublishMessageAction.class, peer);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void execute(PublishMessageAction action) {
		if ( action.getForcedForwardingInfo() != null ) {
			processEmailMessage( action );
		} else {
			processOrdinaryMessage( action );
		}
	}
	
	private void processOrdinaryMessage( final PublishMessageAction action ){
		final Hashtable session = action.getSessionArg();
		final Document message = action.getDocumentArg(SessionConstants.DOCUMENT);
		final AttachedFileCollection files = (AttachedFileCollection) action.getObjectArg(SessionConstants.TO_EMAIL_ATTACHED_FILES);

		final Statement st = Statement.wrap(message);
		if(StringUtils.isBlank(st.getMessageId())) {
			final String message_id = VariousUtils.createMessageId();
			st.setMessageId(message_id);
		}
		String sphereId = st.getCurrentSphere();
		if (sphereId == null) {
			sphereId = (String)session.get(SC.SPHERE_ID);
			st.setCurrentSphere(sphereId);
		}
		PublishHandler.fillRequeredFileds(st.getBindedDocument(),
				this.peer.getUserContactName(), DialogsMainPeer.getCurrentMoment(), sphereId);
		this.peer.getXmldb().insertDoc(st.getBindedDocument(), sphereId);
		
		final List<Document> fileDocs = saveAndInsertFiles(action, session, files, st, sphereId);
		
		Hashtable update = new Hashtable();
		update.put(SC.SESSION, session);
		update.put(SC.DOCUMENT, st.getBindedDocument());
		update.put(SC.IS_SKIP_INSERTDOC_IN_PUBLISHHANDLER, new Boolean(true));
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH).handle( update );
		
		processFiles(action, session, fileDocs, st, sphereId);
	}

	/**
	 * @param action
	 * @param session
	 * @param files
	 * @param st
	 * @param sphereId
	 */
	
	private List<Document> saveAndInsertFiles(final PublishMessageAction action,
			final Hashtable session, final AttachedFileCollection files,
			final Statement st, final String sphereId){
		List<Document> fileDocs = new ArrayList<Document>();
		if ((files != null)&&(files.getCount() > 0)){
			
			final boolean isProcessFiles = action.isProcessFiles();
			if (logger.isDebugEnabled()) {
				logger.debug("With message processing files in number of: " + files.getCount());
			}
			String giver = st.getGiver();
			String threadType = st.getThreadType();
			if (StringUtils.isBlank(threadType)){
				threadType = "message";
			}
			final String supraSphereName = (String)this.peer.getSession().get(SC.SUPRA_SPHERE);
			for (IAttachedFile file : files){
				try {
					Document fileDoc = FileProcessor.INSTANCE.processFile(st.getMessageId() , threadType, (AttachedFile)file, session, giver);
					
					if ( isProcessFiles ) {
						SpecificFileProcessor.INSTANCE.process( getProvider(giver, supraSphereName, 
								file, fileDoc, sphereId, 
								new ParentStatementData( StringUtils.getNotNullString(st.getBody()), StringUtils.getNotNullString(st.getSubject()))));
					}
					
					this.peer.getXmldb().insertDoc(fileDoc, sphereId);
					
					fileDocs.add( fileDoc );
					
				} catch (Throwable ex) {
					logger.error( "Error processing attached file to message: " + file.getName() ,ex);
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No files for message");
			}
		}
		return fileDocs;
	}
	
	private void processFiles(final PublishMessageAction action,
			final Hashtable session, final List<Document> fileDocs,
			final Statement st, final String sphereId) {
		if ((fileDocs != null)&&(!fileDocs.isEmpty())){
			String giver = st.getGiver();
			final String supraSphereName = (String)this.peer.getSession().get(SC.SUPRA_SPHERE);
			for (Document fileDoc : fileDocs){
				try {
					SimpleFileDocumentConverter.convert(this.peer.getSession(), supraSphereName, giver, sphereId, fileDoc);
				} catch (Throwable ex) {
					logger.error( "Error convert file",ex );
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No files for message");
			}
		}
	}
	
	private void processEmailMessage( final PublishMessageAction action ){
		final ForcedForwardingInfo info = action.getForcedForwardingInfo();
		final Hashtable session = action.getSessionArg();
		final Document doc = action.getDocumentArg(SessionConstants.DOCUMENT);
		final AttachedFileCollection files = (AttachedFileCollection) action.getObjectArg(SessionConstants.TO_EMAIL_ATTACHED_FILES);

		final ExternalEmailStatement email = ExternalEmailStatement.wrap( doc );
		email.setType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		email.setThreadType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		
		if ( info.isStartThreadMailingConversation() ) {
			email.setMailingThread( true );
			info.setAllAddressesAs_BCC_insteadOf_CC(false);
		}
		
		if(StringUtils.isBlank(email.getMessageId())) {
			final String message_id = VariousUtils.createMessageId();
			email.setMessageId(message_id);
			email.setThreadId(message_id);
		}
		String emailmessageId = SendingElementFactory.generateMessageHeader(email.getMessageId());
		email.setEmailmessageId(emailmessageId);
		
		String sphereId = email.getCurrentSphere();
		if (sphereId == null) {
			sphereId = (String)session.get(SC.SPHERE_ID);
			email.setCurrentSphere(sphereId);
		}
		
		String real_name = (String) session.get(SC.REAL_NAME);
		if (StringUtils.isBlank(real_name)) {
			real_name = this.peer.getUserContactName();
		}
		final String sphereDisplayName = StringUtils.isNotBlank(real_name) ? 
				this.peer.getVerifyAuth().getSupraSphere().getSphereDisplayName(real_name, sphereId) :
					this.peer.getVerifyAuth().getSupraSphere().getDisplayNameWithoutRealName(sphereId);
		if ( StringUtils.isNotBlank(sphereDisplayName) ){
			email.setSubject(sphereDisplayName + ": " + StringUtils.getNotNullString(email.getSubject()));
		}
		
		PublishHandler.fillRequeredFileds(email.getBindedDocument(),
				this.peer.getUserContactName(), DialogsMainPeer.getCurrentMoment(), sphereId);
		
		final ForsedForwardingData forcedData = new ForsedForwardingData(info, files);
		EmailAddressesContainer addressesContainer = EmailAddressesCreator.
			create(sphereId, email.getMessageId(), email.getGiver(), this.peer, forcedData, email);
		if ( addressesContainer == null ) {
			Hashtable update = new Hashtable();
			update.put(SC.SESSION, session);
			update.put(SC.DOCUMENT, email.getBindedDocument());
			this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH).handle( update );
			logger.error("No addressesContainer, can not send anywere");
			SystemSpeaker.speakMessageError("Email sending failed", "Wrong addresses specified", email.getCurrentSphere(), email.getMessageId());
			return;
		}
		email.setInput(false);
		email.setReciever(addressesContainer.getSendTo());
		email.setCcrecievers( addressesContainer.getOriginalCC() );
		email.setBccrecievers("");

		this.peer.getXmldb().insertDoc(email.getBindedDocument(), sphereId);
		
		final List<Document> fileDocs = saveAndInsertFiles(action, session, files, email, sphereId);

		Hashtable update = new Hashtable();
		update.put(SC.SESSION, session);
		update.put(SC.DOCUMENT, email.getBindedDocument());
		update.put(SC.IS_SKIP_INSERTDOC_IN_PUBLISHHANDLER, new Boolean(true));
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH).handle( update );
		
		processFiles(action, session, fileDocs, email, sphereId);

		try {
			List<SendingElement> sendingElements = SendingElementFactory
					.createCreated(addressesContainer, files,
							new EmailBody(email.getSubject(), email.getBody()), email
									.getMessageId(), email
									.getCurrentSphere(), email.getEmailmessageId());
			Responcer.INSTANCE.initiateResponceElement(email
					.getMessageId(), email.getCurrentSphere(),
					sendingElements.size());
			for (SendingElement sendingElement : sendingElements) {
				Mailer.INSTANCE.send(sendingElement);
			}

		} catch (Exception ex) {
			logger.error(ex);
			SystemSpeaker.speakMessageError("Email sending failed", "Wrong addresses specified", email.getCurrentSphere(), email.getMessageId());
		}
	}

	private DefaultDataForSpecificFileProcessingProvider getProvider( final String giver, 
			final String supraSphereName, final IAttachedFile file, final Document fileDoc,
			final String sphereId, final ParentStatementData parentData){
		final DefaultDataForSpecificFileProcessingProvider provider = new DefaultDataForSpecificFileProcessingProvider();
		provider.setGiver( giver );
		final String systemFullName = bdir + fsep + "roots" + fsep	+ supraSphereName
			+ fsep + "File"	+ fsep + FileStatement.wrap(fileDoc).getDataId();
		provider.setSystemFullPath( systemFullName );
		provider.setFileName( file.getName() );
		provider.setPeer(this.peer);
		final List<String> sphereIds = new ArrayList<String>();
		sphereIds.add( sphereId );
		provider.setSphereIds( sphereIds );
		provider.setParentData( parentData );	
		return provider;
	}
}
