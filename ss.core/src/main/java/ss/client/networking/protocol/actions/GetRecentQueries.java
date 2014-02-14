/**
 * Jul 5, 2006 : 4:03:33 PM
 */
package ss.client.networking.protocol.actions;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.client.networking.protocol.getters.GetRecentQueriesCommand;
import ss.common.GenericXMLDocument;
import ss.common.XmlDocumentUtils;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class GetRecentQueries  {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetRecentQueries.class);
	
	private static final String RESPONSE_ID = "response_id";

	private static final String LAST_UPDATED = "last_updated";

	private static final String MOMENT = "moment";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String CONFIRMED = "confirmed";

	private static final String STATUS = "status";

	private static final String MESSAGE_ID = "message_id";

	private static final String THREAD_TYPE = "thread_type";

	private static final String KEYWORDS = "keywords";

	private static final String TYPE = "type";

	private static final String GIVER = "giver";

	private static final String VALUE = "value";

	private final DialogsMainCli cli;

	public GetRecentQueries(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	private void preprocessQueries(Vector<Element> queries, String messageId, String localSphereId  ) {
		for (Element query : queries ) {
			String subject = query.attributeValue(VALUE);
			Document genericDoc = GenericXMLDocument.XMLDoc(subject, "",
					query.attributeValue(GIVER));
			genericDoc.getRootElement().addElement(TYPE).addAttribute(VALUE,
					KEYWORDS);
			genericDoc.getRootElement().addElement(THREAD_TYPE).addAttribute(
					VALUE, KEYWORDS);
			genericDoc.getRootElement().addElement(MESSAGE_ID).addAttribute(
					VALUE,
					new Long(GenericXMLDocument.getNextTableId()).toString());
			genericDoc.getRootElement().addElement(STATUS).addAttribute(VALUE,
					CONFIRMED);
			genericDoc.getRootElement().addElement(CONFIRMED).addAttribute(
					VALUE, "true");
			genericDoc.getRootElement().addElement(CURRENT_SPHERE)
					.addAttribute(
							VALUE,
							(String) this.cli.session
									.get(SessionConstants.SPHERE_ID2));
			String moment = query.attributeValue(MOMENT);
			if (moment == null) {
				Date current = new Date();
				moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
						current)
						+ " "
						+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(
								current);
			}
			genericDoc.getRootElement().addElement(MOMENT).addAttribute(VALUE,
					moment);
			genericDoc.getRootElement().addElement(LAST_UPDATED).addAttribute(
					VALUE, moment);
			genericDoc.getRootElement().addElement(RESPONSE_ID).addAttribute(
					VALUE, messageId);
			if (logger.isDebugEnabled()) {
				logger.debug("doc : "+XmlDocumentUtils.toPrettyString(genericDoc));
			}
			Hashtable newUpdate = new Hashtable();
			newUpdate.put(SessionConstants.DOCUMENT, genericDoc);
			newUpdate.put(SessionConstants.SPHERE, localSphereId);
			newUpdate.put(SessionConstants.IS_UPDATE, "true");
			this.cli.callInsert( new PostponedUpdate( newUpdate ) );
		}
	}



	@SuppressWarnings("unchecked")
	public Vector getRecentQueries(Hashtable session, String homeSphereId,
			String homeMessageId, String localSphereId) {
		logger.info("Start recentqueires: " + homeSphereId + " : "
				+ homeMessageId + " : " + localSphereId);
		GetRecentQueriesCommand command = new GetRecentQueriesCommand();
		command.putSessionArg( session );
		command.putArg(SessionConstants.HOME_SPHERE_ID, homeSphereId);
		command.putArg(SessionConstants.HOME_MESSAGE_ID, homeMessageId);
		Vector<Element> queries = command.execute(this.cli, Vector.class );
		preprocessQueries( queries, homeMessageId, localSphereId );
		return queries ;
	}

}
