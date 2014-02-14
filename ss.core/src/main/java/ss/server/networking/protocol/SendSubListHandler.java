package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.DmpFilter;
import ss.common.GenericXMLDocument;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.util.VariousUtils;

public class SendSubListHandler implements ProtocolHandler {

	private static final String SYSTEMFILE = "systemfile";

	private static final String SUBJECT = "subject";

	private static final String RELATIVE_TO = "relative_to";

	private static final String RESPONSE_ID = "response_id";

	private static final String LAST_UPDATED = "last_updated";

	private static final String THREAD_ID = "thread_id";

	private static final String MESSAGE_ID = "message_id";

	private static final String CONFIRMED = "confirmed";

	private static final String ORIGINAL_ID = "original_id";

	private static final String FILE_SEPARATOR = "file_separator";

	private static final String PHYSICAL_LOCATION = "physical_location";

	private static final String STATUS = "status";

	private static final String THREAD_TYPE = "thread_type";

	private static final String FILESYSTEM = "filesystem";

	private static final String VALUE = "value";

	private static final String TYPE = "type";

	private static final String MOMENT = "moment";

	// TODO SC.MESSAGE_ID_SERVER relative
	public static final String MIS_MOMENT = "moment";

	// TODO SC.MESSAGE_ID_SERVER relative
	public static final String MIS_MESSAGE_ID = "messageId";

	private static final Logger logger = SSLogger.getLogger(SendSubListHandler.class);

	public SendSubListHandler() {
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_SUB_LIST;
	}

	public void handle(Hashtable update) {
		handleSendSubList(update);
	}

	@SuppressWarnings("unchecked")
	public void handleSendSubList(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);		
		Document doc = (Document) update.get(SC.DOC);		
		Vector dirs = (Vector) update.get(SC.DIRS);
		Vector files = (Vector) update.get(SC.FILES);
		
		Hashtable finalSession = (Hashtable) session.clone();
		String realName = (String) session.get(SC.REAL_NAME);

		Vector sendDirs = new Vector();
		Vector sendFiles = new Vector();

		for (int i = 0; i < dirs.size(); i++) {

			String one = (String) dirs.get(i);
			
			Document genericDoc = null;
			genericDoc = GenericXMLDocument.XMLDoc(one, "", realName);
			genericDoc.getRootElement().addElement(TYPE).addAttribute(VALUE,
					FILESYSTEM);
			genericDoc.getRootElement().addElement(THREAD_TYPE).addAttribute(
					VALUE, FILESYSTEM);

			genericDoc.getRootElement().addElement(STATUS).addAttribute(VALUE,
					CONFIRMED);

			Element physicalLocation = (Element) (doc.getRootElement().element(
					PHYSICAL_LOCATION).clone());
			Element fileSep = (Element) (doc.getRootElement().element(
					FILE_SEPARATOR).clone());
			genericDoc.getRootElement().add(physicalLocation);
			genericDoc.getRootElement().add(fileSep);

			String moment = DialogsMainPeer.getCurrentMoment();

			String messageId = new Long(GenericXMLDocument.getNextTableId()).toString();

			genericDoc.getRootElement().addElement(ORIGINAL_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(MESSAGE_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(THREAD_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(MOMENT).addAttribute(VALUE,
					moment);

			genericDoc.getRootElement().addElement(LAST_UPDATED).addAttribute(
					VALUE, moment);
			genericDoc.getRootElement().addElement(RESPONSE_ID).addAttribute(
					VALUE,
					doc.getRootElement().element(MESSAGE_ID).attributeValue(
							VALUE));

			if (doc.getRootElement().element(RELATIVE_TO) == null) {
				genericDoc.getRootElement().addElement(RELATIVE_TO)
						.addAttribute(
								VALUE,
								doc.getRootElement().element(SUBJECT)
										.attributeValue(VALUE));
			} else {
				genericDoc.getRootElement().add(
						(Element) doc.getRootElement().element(RELATIVE_TO)
								.clone());

			}

			sendDirs.add(genericDoc);

		}

		for (int i = 0; i < files.size(); i++) {
			Document genericDoc = null;
			try {
				String one = (String) files.get(i);

				genericDoc = GenericXMLDocument.XMLDoc(one, "", realName);
				genericDoc.getRootElement().addElement(TYPE).addAttribute(
						VALUE, SYSTEMFILE);
				genericDoc.getRootElement().addElement(THREAD_TYPE)
						.addAttribute(VALUE, FILESYSTEM);

				genericDoc.getRootElement().addElement(STATUS).addAttribute(
						VALUE, CONFIRMED);
			} catch (ClassCastException e) {
				logger.error(e.getMessage(), e);
				genericDoc = (Document) files.get(i);

			}

			Element physicalLocation = (Element) (doc.getRootElement().element(
					PHYSICAL_LOCATION).clone());
			genericDoc.getRootElement().add(physicalLocation);
			Element fileSep = (Element) (doc.getRootElement().element(
					FILE_SEPARATOR).clone());
			genericDoc.getRootElement().add(fileSep);

			String moment = DialogsMainPeer.getCurrentMoment();

			String messageId = new Long(GenericXMLDocument.getNextTableId()).toString();

			genericDoc.getRootElement().addElement(ORIGINAL_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(MESSAGE_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(THREAD_ID).addAttribute(
					VALUE, messageId);
			genericDoc.getRootElement().addElement(MOMENT).addAttribute(VALUE,
					moment);
			genericDoc.getRootElement().addElement(LAST_UPDATED).addAttribute(
					VALUE, moment);
			genericDoc.getRootElement().addElement(RESPONSE_ID).addAttribute(
					VALUE,
					doc.getRootElement().element(MESSAGE_ID).attributeValue(
							VALUE));

			if (doc.getRootElement().element(RELATIVE_TO) == null) {
				genericDoc.getRootElement().addElement(RELATIVE_TO)
						.addAttribute(
								VALUE,
								doc.getRootElement().element(SUBJECT)
										.attributeValue(VALUE));
			} else {
				genericDoc.getRootElement().add(
						(Element) doc.getRootElement().element(RELATIVE_TO)
								.clone());

			}

			sendFiles.add(genericDoc);

		}
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers, finalSession)) {
			final DmpResponse dmpResponse = new DmpResponse();
			String moment = DialogsMainPeer.getCurrentMoment();
			String messageId = VariousUtils.createMessageId();
			Hashtable messageIdServer = new Hashtable();
			messageIdServer.put(MIS_MESSAGE_ID, messageId);
			messageIdServer.put(MIS_MOMENT, moment);
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.GET_SUB_LIST);
			dmpResponse.setMapValue(SC.SESSION, finalSession);
			dmpResponse.setMapValue(SC.MESSAGE_ID_SERVER, messageIdServer);
			dmpResponse.setDocumentValue(SC.DOC, doc);
			dmpResponse.setVectorValue(SC.DIRS, sendDirs);
			dmpResponse.setVectorValue(SC.FILES, sendFiles);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "false");
			handler.sendFromQueue(dmpResponse);			
		}
	}

}
