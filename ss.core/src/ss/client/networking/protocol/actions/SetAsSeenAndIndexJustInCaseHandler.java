package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class SetAsSeenAndIndexJustInCaseHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetAsSeenAndIndexJustInCaseHandler.class);

	private final DialogsMainCli cli;
	
	public SetAsSeenAndIndexJustInCaseHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SET_AS_SEEN_AND_INDEX_JUST_IN_CASE;
	}

	@SuppressWarnings("unchecked")
	public void setAsSeenAndIndexJustInCase(Hashtable session2,
			Document createDoc) {

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SET_AS_SEEN_AND_INDEX_JUST_IN_CASE);

		test.put(SessionConstants.DOC, createDoc);
		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
