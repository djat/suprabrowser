package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * create duplicate of membership in db.
 * call Xmldb.makeCurrentSphereCore that change login_sphere in the
 * suprasphere document  
 * 
 */
public class MakeCurrentSphereCoreHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MakeCurrentSphereCoreHandler.class);

	private final DialogsMainCli cli;
	
	public MakeCurrentSphereCoreHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.MAKE_CURRENT_SPHERE_CORE;
	}

	@SuppressWarnings("unchecked")
	public void makeCurrentSphereCore(Hashtable session2, String login) {

		Hashtable toSend = (Hashtable) session2.clone();
		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.MAKE_CURRENT_SPHERE_CORE);
		//test.put(SessionConstants.DOCUMENT, document);
		test.put(SessionConstants.LOGIN, login);
		test.put(SessionConstants.CONTACT_NAME, this.cli.getVerifyAuth().getRealName(login));
		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
