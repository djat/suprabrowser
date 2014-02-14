package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.protocol.getters.GetMachineVerifierForProfileCommand;
import ss.domainmodel.LoginSphere;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetMachineVerifierForProfileHandler extends AbstractGetterCommandHandler<GetMachineVerifierForProfileCommand, Hashtable<String, String>> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetMachineVerifierForProfileHandler.class);
	
	private static final String MACHINE_VERIFIER = "machine_verifier";

	private static final String REMOTE = "remote";

	private static final String MACHINE_SALT = "machineSalt";

	private static final String SALT = "salt";

	private static final String MACHINE_PROFILE = "machineProfile";

	private static final String PROFILE_ID = "profile_id";

	private static final String APATH = "//membership/machine_verifier";

	private static final String FPATH = "//membership/machine_verifier[@remote=\"true\"]";

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetMachineVerifierForProfileHandler(DialogsMainPeer peer) {
		super(GetMachineVerifierForProfileCommand.class, peer);
	}

	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String, String> evaluate(GetMachineVerifierForProfileCommand command) throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String profileId = (String) session.get(SC.PROFILE_ID);
		String username = (String) session.get(SC.USERNAME);
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		Element machineVerifierElem = null;
		LoginSphere loginSphere = this.peer.getXmldb().getUtils().findLoginSphereElement(username);
		String loginSphereId = null;
		if (loginSphere != null) {
			loginSphereId = loginSphere.getSystemName();
		}

		logger.info("here in getmachineverifier: " + loginSphereId);

		Document membershipDoc = null;
		if (loginSphereId != null && loginSphereId.length() > 0) {
			membershipDoc = this.peer.getXmldb().getMembershipDoc(
					loginSphereId, username);

		} else {
			membershipDoc = this.peer.getXmldb().getMembershipDoc(supraSphere,
					username);
			loginSphereId = supraSphere;
		}

		logger.info("fpath: " + FPATH);
		try {
			Element one = (Element) membershipDoc.selectObject(FPATH);
			machineVerifierElem = one;
		} catch (Exception e) {
			logger.error( "Cant select machineVerifierElem by " + FPATH , e);
		}

		if (machineVerifierElem == null) {
			if (membershipDoc.getRootElement().element(MACHINE_VERIFIER) != null) {
				List machineVerifyers = getMachineVerifyers(membershipDoc);
				for (Object o : machineVerifyers) {
					Element one = (Element) o;
					if (one.attributeValue(PROFILE_ID).equals(profileId)) {
						machineVerifierElem = one;
						logger.info("got server membership doc: "
								+ machineVerifierElem.asXML());
					}
				}
			}
			machineVerifierElem.addAttribute(REMOTE, "true");
			this.peer.getXmldb().replaceDoc(membershipDoc, loginSphereId);
		}
		Hashtable<String,String> machineVerifier = new Hashtable<String,String>();
		machineVerifier.put(SC.MACHINE_VERIFIER, machineVerifierElem.getText());
		machineVerifier.put(MACHINE_SALT, machineVerifierElem
				.attributeValue(SALT));
		machineVerifier.put(MACHINE_PROFILE, machineVerifierElem
				.attributeValue(PROFILE_ID));
		return machineVerifier;
	}

	@SuppressWarnings("unchecked")
	private List getMachineVerifyers(Document membershipDoc) {
		List list = new ArrayList();
		Object o = membershipDoc.selectObject(APATH);
		if (o != null) {
			if (o instanceof Element) {
				list.add(o);
			} else {
				list = (List) o;
			}
		}
		return list;
	}


}
