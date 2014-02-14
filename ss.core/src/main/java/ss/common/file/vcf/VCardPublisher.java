/**
 * 
 */
package ss.common.file.vcf;

import java.util.List;

import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MessageStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class VCardPublisher {
	
	private static final String EXISTED_DOC_IS_NULL_EXCEPTION = "existedDoc is null";

	private static final String PEER_IS_NULL_EXCEPTION = "peer is null";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardPublisher.class);
	
	public static final VCardPublisher INSTANCE = new VCardPublisher(); 
	
	private VCardPublisher(){
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void publishAllContacts( final ContactStatement contact, final List<String> sphereIds, final NoteInfo note, final XMLDB xmldb) {
		if ((sphereIds == null)||(sphereIds.isEmpty())) {
			if (logger.isDebugEnabled()) {
				logger.debug("No sphere ids to publish");
			}
			return;
		}
		if ( (note != null) && (StringUtils.isNotBlank( note.getNewType() ) ) ) {
			contact.setRole( note.getNewType() );
		}
		for (String sphereId : sphereIds) {
			String message_id = VariousUtils.createMessageId();
			contact.setOriginalId(message_id);
			contact.setMessageId(message_id);
			contact.setThreadId(message_id);
			contact.setCurrentSphere( sphereId );
			saveAndPublish((Document) contact.getBindedDocument().clone(),sphereId, xmldb);
			if ( (note != null) && (note.isNotEmpty()) ) {
				saveAndPublish( createNote(note, contact.getGiver(), contact) ,contact.getCurrentSphere(), xmldb);
			}
		}
	}
	
	public void publishNoteForExistingContact(final NoteInfo note, final String giver, final Document existedDoc, final DialogsMainPeer peer){
		if ( (note == null) || (!note.isNotEmpty()) ) {
			logger.warn( "Note is blank, no publishing" );
			return;
		}
		if (existedDoc == null) {
			logger.error(EXISTED_DOC_IS_NULL_EXCEPTION);
			throw new NullPointerException(EXISTED_DOC_IS_NULL_EXCEPTION);
		}
		if (peer == null) {
			logger.error(PEER_IS_NULL_EXCEPTION);
			throw new NullPointerException(PEER_IS_NULL_EXCEPTION);
		}
		final ContactStatement contact = ContactStatement.wrap(existedDoc);
		saveAndPublish( createNote(note, giver, contact) ,contact.getCurrentSphere(), peer.getXmldb());
	}
	
	private Document createNote( final NoteInfo note, final String giver, final ContactStatement target ){
		if (!target.isContact()) {
			logger.error("Target is not contact");
			return null;
		}
		Statement message;
		if ( note.isMessage() ) {
			message = new MessageStatement();
			message.setType("message");
		} else {
			message = new TerseStatement();
			message.setType("terse");
		}
		
		String moment = DialogsMainPeer.getCurrentMoment();

		String message_id = VariousUtils.createMessageId();

		message.setMessageId(message_id);
		message.setThreadId(target.getThreadId());
		message.setResponseId(target.getMessageId());

		message.setThreadType( target.getThreadType() );
		
		message.setGiver( (giver == null) ? "" : giver );
		
		message.setMoment( moment );
		
		message.setSubject( note.getSubject() );
		if ( message.isMessage() ) {
			message.setBody( note.getBody() );
		}
		
		addVotingTemplate( message );
		
		return message.getBindedDocument();
	}

	/**
	 * @param message
	 */
	private void addVotingTemplate( final Statement message ) {
		message.setTallyNumber( "0.0" );
		message.setTallyValue( "0.0" );
		message.setVotingModelType( "absolute" );
		message.setVotingModelDesc( "Absolute without qualification" );
	}
	
	private void saveAndPublish(final Document doc, final String sphereId,
			final XMLDB xmlDB) {
		if (doc == null) {
			logger.error("Document is null");
			return;
		}
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
		dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "false");
		xmlDB.insertDoc(doc, sphereId);

		for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
			handler.sendFromQueue(dmpResponse);
		}
	}
	
	public void changeTypeForContact(final Document doc, final String sphereId, final String newType,
			final XMLDB xmlDB) {
		if (doc == null) {
			logger.error("Document is null");
			return;
		}
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
		dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "false");
		final ContactStatement st = ContactStatement.wrap( doc );
		if (logger.isDebugEnabled()) {
			logger.debug("Changing type for contact: " + st.getContactNameByFirstAndLastNames());
			logger.debug("Previous type was : " + st.getRole());
			logger.debug("New type is : " + newType );
		}
		st.setRole( newType );
		xmlDB.replaceDoc( st.getBindedDocument() , sphereId );

		for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
			handler.sendFromQueue(dmpResponse);
		}
	}
}
