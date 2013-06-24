/**
 * Jul 5, 2006 : 12:19:20 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class GetSubListHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetSubListHandler.class);

	private final DialogsMainCli cli;

	public GetSubListHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleGetSubList(final Hashtable update) {
		logger.info("got sub list");
		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);
		Vector dirs = (Vector) update.get(SessionConstants.DIRS);
		Vector files = (Vector) update.get(SessionConstants.FILES);

		String sphereId = (String) this.cli.session.get(SessionConstants.SPHERE_ID);
//		MessagesPane mp = this.cli.getSF().getMessagesPaneFromSphereId(sphereId,
//				(String) this.cli.session.get(SessionConstants.UNIQUE_ID));

		for (int i = 0; i < dirs.size(); i++) {

			Document one = (Document) dirs.get(i);
			//Statement st = Statement.wrap(one);

			//mp.addToAllMessages(st.getMessageId(), st);

			//mp.insertUpdate(one, true, true, false);
			DeliverersManager.INSTANCE.insert(
	    			DeliverersManager.FACTORY.createSimple(one, false, false, sphereId));

		}
		for (int i = 0; i < files.size(); i++) {

			Document one = (Document) files.get(i);
			//Statement st = Statement.wrap(one);

			//mp.addToAllMessages(st.getMessageId(), st);

			//mp.insertUpdate(one, true, true, false);
			DeliverersManager.INSTANCE.insert(
	    			DeliverersManager.FACTORY.createSimple(one, false, false, sphereId));
		}
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_SUB_LIST;
	}

	public void handle(Hashtable update) {
		handleGetSubList(update);
	}

	@SuppressWarnings("unchecked")
	public void getSubList(Hashtable session2, Document doc) {
		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL, SSProtocolConstants.GET_SUB_LIST);

		test.put(SessionConstants.DOCUMENT, doc);
		// test.put("dirName",dirName);

		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
