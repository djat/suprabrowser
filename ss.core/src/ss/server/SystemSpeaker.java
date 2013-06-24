/**
 * 
 */
package ss.server;

import java.util.List;

import org.dom4j.Document;

import ss.client.ui.email.SendList;
import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.Statement;
import ss.domainmodel.SystemMessageStatement;
import ss.domainmodel.TerseStatement;
import ss.domainmodel.VotedMember;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.smtp.responcetosphere.ResponceStringInfo.ResponceType;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class SystemSpeaker {

	public static final String SERVER_GIVER = "S E R V E R";
	
	//private static final String CHANGES = "Changes in sphere workflow roles model";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SystemSpeaker.class);

	private static final XMLDB xmlDB = new XMLDB();

	public static void speakTerseError(String message, String sphereId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId);
		terse.setSystemType(SystemMessageStatement.SYSTEM_TYPE_ERROR);
		publish(terse);
	}

	public static void speakTerseError(String message, String sphereId, String responceId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_ERROR);
		publish(terse);
	}
	
	public static void speakTerseWarning(String message, String sphereId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_WARNING);
		publish(terse);
	}

	public static void speakTerseWarning(String message, String sphereId, String responceId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_WARNING);
		publish(terse);
	}
	
	public static void speakTerseInfo(String message, String sphereId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_INFO);
		publish(terse);
	}

	public static void speakTerseInfo(String message, String sphereId, String responceId) {
		SystemMessageStatement terse = createSimpleMessage(message, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_INFO);
		publish(terse);
	}
	
	public static void speakMessageError(String message, String body, String sphereId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_ERROR);
		publish(terse);
	}

	public static void speakMessageError(String message, String body, String sphereId, String responceId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_ERROR);
		publish(terse);
	}
	
	public static void speakMessageWarning(String message, String body, String sphereId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_WARNING);
		publish(terse);
	}

	public static void speakMessageWarning(String message, String body, String sphereId, String responceId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_WARNING);
		publish(terse);
	}
	
	public static void speakMessageInfo(String message, String body,  String sphereId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_INFO);
		publish(terse);
	}

	public static void speakMessageInfo(String message, String body, String sphereId, String responceId) {
		SystemMessageStatement terse = createCompleteMessage(message, body, sphereId, responceId);
		terse.setSystemType(TerseStatement.SYSTEM_TYPE_INFO);
		publish(terse);
	}

	private static Statement getPrototype() {

		Statement terse = new Statement();
		String message_id = VariousUtils.createMessageId();
		String moment = DialogsMainPeer.getCurrentMoment();
		terse.setGiver(SERVER_GIVER);
		terse.setLastUpdatedBy(SERVER_GIVER);
		terse.setLastUpdated(moment);
		terse.setVotingModelType("absolute");
		terse.setVotingModelDesc("Absolute without qualification");
		terse.setTallyNumber("0.0");
		terse.setTallyValue("0.0");
		terse.getVotedMembers().add(new VotedMember(SERVER_GIVER, moment));
		terse.setOrigBody("");
		terse.setConfirmed(true);
		terse.setMoment(moment);
		terse.setMessageId(message_id);
		terse.setOriginalId(message_id);

		return terse;
	}

	public static SystemMessageStatement createSimpleMessage(String message, String sphereId) {
		SystemMessageStatement terse = SystemMessageStatement.wrap(getPrototype().getBindedDocument());
		terse.setCurrentType();
		terse.setServerSystemMessage(true);
		terse.setSubject(message);
		terse.setCurrentSphere(sphereId);
		String message_id = terse.getMessageId();
		terse.setThreadId(message_id);
		terse.setThreadType(terse.getType());
		return terse;
	}

	public static SystemMessageStatement createSimpleMessage(String message, String sphereId,
			String responceId) {
		SystemMessageStatement terse = SystemMessageStatement.wrap(getPrototype().getBindedDocument());
		terse.setCurrentType();
		terse.setServerSystemMessage(true);
		terse.setSubject(message);
		terse.setCurrentSphere(sphereId);
		Statement st = Statement.wrap(xmlDB.getSpecificMessage(responceId,
				sphereId));
		terse.setResponseId(st.getMessageId());
		terse.setThreadId(st.getThreadId());
		terse.setThreadType(st.getThreadType());
		return terse;
	}
	
	public static SystemMessageStatement createCompleteMessage(String subject, String body, String sphereId) {
		SystemMessageStatement message = SystemMessageStatement.wrap(getPrototype().getBindedDocument());
		message.setCurrentType();
		message.setServerSystemMessage(true);
		message.setSubject(subject);
		message.setBody(body);
		message.setOrigBody(body);
		message.setCurrentSphere(sphereId);
		String message_id = message.getMessageId();
		message.setThreadId(message_id);
		message.setThreadType(message.getType());
		return message;
	}

	public static SystemMessageStatement createCompleteMessage(String subject, String body, String sphereId,
			String responceId) {
		SystemMessageStatement message = SystemMessageStatement.wrap(getPrototype().getBindedDocument());
		message.setCurrentType();
		message.setServerSystemMessage(true);
		message.setSubject(subject);
		message.setBody(body);
		message.setOrigBody(body);
		message.setCurrentSphere(sphereId);
		try {
			Statement st = Statement.wrap(xmlDB.getSpecificMessage(responceId,
					sphereId));
			message.setResponseId(st.getMessageId());
			message.setThreadId(st.getThreadId());
			message.setThreadType(st.getThreadType());
		} catch (Throwable ex) {
			logger.error("Error getting Document for messageId: " + responceId + ", sphereId: " + sphereId, ex);
			message.setResponseId(responceId);
			message.setThreadId(responceId);
			message.setThreadType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		}
		return message;
	}

	private static synchronized void publish(final Statement statement) {
		logger.info("Server responce: " + statement.getSubject());
		String sphereId = statement.getCurrentSphere();
		Document doc = statement.getBindedDocument();
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
		dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "false");
		xmlDB.insertDoc(doc, sphereId);

		for (DialogsMainPeer handler : DmpFilter.filter(sphereId) ) {
			handler.sendFromQueue(dmpResponse);
		}
	}
	

	public static void speakNotSent(List<SendList> notSent, String sphereId,
			String responceId) {
		if ((notSent == null) || (notSent.isEmpty())) {
			return;
		}
		String subj = "Email message not sent";
		String body = "Server did not manage to send email to following addresses: ";
		for (SendList sendList : notSent) {
			body += sendList.getSingleLineAddresses();
		}
		speakMessageError(subj, body, sphereId, responceId);
	}
	
	public static void speakEmailAddressableSphereStateChanged(SphereEmail sphereEmail){
		String body = sphereEmail.getEmailNames().getSingleStringEmails();
		String subject;
		if (sphereEmail.getEnabled()){
			subject = "Current sphere is email addressable";
		} else {
			subject = "Current sphere is not email addressable";
		}
		subject += ", Email aliases changed";
		speakMessageWarning(subject, body, sphereEmail.getSphereId());
	}
	
//	public static void speakChangeRolesModel(AbstractDelivery newModel) {
//		final String body = newModel.getHtmlDescription();
//		SystemSpeaker.speakMessageInfo(CHANGES, body, newModel.getSphereId());
//	}	
	
	public static void speakForardInfo(String body, String sphereId,
			String responceId){
		String subj = "Forwarding info";
		speakMessageInfo(subj, body, sphereId, responceId);
	}

	/**
	 * @param type 
	 * @param resp
	 * @param sphereId
	 * @param messageId
	 */
	public static void speakRorwardingPerformed(ResponceType type, String message, String body, String sphereId, String messageId) {
		if (type == ResponceType.INFO) {
			speakMessageInfo(message, body, sphereId, messageId);
		} else if (type == ResponceType.WARN) {
			speakMessageWarning(message, body, sphereId, messageId);
		} else if (type == ResponceType.ERROR) {
			speakMessageError(message, body, sphereId, messageId);
		}
	}
}
