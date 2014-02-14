package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class SaveMarkForSphereHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveMarkForSphereHandler.class);
	
	private final DialogsMainCli cli;
	
	public SaveMarkForSphereHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_MARK_FOR_SPHERE;
	}

	@SuppressWarnings("unchecked")
	public void saveMarkForSphere(Hashtable session2, String localOrGlobal) {

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SAVE_MARK_FOR_SPHERE);

		test.put(SessionConstants.LOCAL_OR_GLOBAL, localOrGlobal);
		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
