/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.networking.protocol.getters.GetAllMessagesCommand;
import ss.domainmodel.Statement;
import ss.framework.networking2.CommandHandleException;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.util.DateTimeParser;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetAllMessagesCommandHandler extends AbstractGetterCommandHandler<GetAllMessagesCommand, Hashtable> {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(GetAllMessagesCommandHandler.class);
	
	public GetAllMessagesCommandHandler(final DialogsMainPeer peer) {
		super(GetAllMessagesCommand.class, peer);
	}
	
	@Override
	protected Hashtable evaluate(GetAllMessagesCommand command)
			throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String sphereId = command.getStringArg(SessionConstants.SPHERE_ID2);
		String supraSphere = this.peer.getVerifyAuth().getSupraSphereName();
		Document sphereDef = this.peer.getXmldb().getSphereDefinition(
				supraSphere, sphereId);
		
		if (sphereDef == null) {
			return getPrivateSphereDocs(sphereId);
		}

		Hashtable table = new Hashtable();
		try {
			table = this.peer.getXmldb().getForSphereLight(session, this.peer,
					sphereId, sphereDef, 0L, 1000L);
		} catch (DocumentException ex) {
			ex.printStackTrace();
		}
		return table;
	}

	/**
	 * @param sphereId
	 * @param table
	 * @return
	 */
	private Hashtable getPrivateSphereDocs(String sphereId) {
		Hashtable table = new Hashtable();
		List<Document> messageDocs = this.peer.getXmldb().getAllMessages(
				sphereId);
		List<Document> toExcept = new ArrayList<Document>();
		for(Document doc : messageDocs) {
			String messageType = Statement.wrap(doc).getType();
			if(messageType.equals("user_activity") || messageType.equals("stats")) {
				toExcept.add(doc);
			}
		}
		messageDocs.removeAll(toExcept);
		Collections.sort(messageDocs, getDocComparator());
		table.put("docs_in_order", messageDocs.toArray(new Document[]{}));
		return table;
	}

	/**
	 * @return
	 */
	private Comparator<Document> getDocComparator() {
		return new Comparator<Document>() {
			public int compare(Document o1, Document o2) {
				Statement first = Statement.wrap(o1);
				Statement second = Statement.wrap(o2);
				long firstTime = DateTimeParser.INSTANCE.parseToDate(
						first.getMoment()).getTime();
				long secondTime = DateTimeParser.INSTANCE.parseToDate(
						second.getMoment()).getTime();
				if (firstTime > secondTime) {
					return 1;
				} else if (firstTime < secondTime) {
					return -1;
				}
				return 0;
			}
		};
	}

}
