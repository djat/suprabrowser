package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.protocol.getters.GetMachinePassCommand;
import ss.domainmodel.LoginSphere;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetMachinePassHandler extends AbstractGetterCommandHandler<GetMachinePassCommand, String> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetMachinePassHandler.class);
	
	private static final String MACHINE_PASS = "machine_pass";

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetMachinePassHandler(DialogsMainPeer peer) {
		super(GetMachinePassCommand.class, peer);
	}
		
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected String evaluate(GetMachinePassCommand command) throws CommandHandleException {
		logger.warn("got machine pass request");
		Hashtable session = command.getSessionArg();		
		String username = (String) session.get(SC.USERNAME);
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		final LoginSphere loginSphere = this.peer.getXmldb().getUtils().findLoginSphereElement(username);
		logger.warn("loginSphere: " + loginSphere );
		String loginSphereId = null;
		if (loginSphere != null) {
			loginSphereId = loginSphere.getSystemName();
		}
		logger.warn("here in getmachineverifier: " + loginSphereId);
		Document membershipDoc = null;
		if (loginSphereId != null && loginSphereId.length() > 0) {
			membershipDoc = this.peer.getXmldb().getMembershipDoc(
					loginSphereId, username);
		} else {
			membershipDoc = this.peer.getXmldb().getMembershipDoc(supraSphere,
					username);
			loginSphereId = supraSphere;
		}
		return membershipDoc.getRootElement().element(MACHINE_PASS).getText();		
	}

}
