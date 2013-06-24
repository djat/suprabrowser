package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.protocol.getters.CreateNewProfileCommand;
import ss.common.CreateMembership;
import ss.common.GenericXMLDocument;
import ss.domainmodel.LoginSphere;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.VariousUtils;

public class CreateNewProfileHandler extends AbstractGetterCommandHandler<CreateNewProfileCommand, Hashtable<String, String>> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateNewProfileHandler.class);
	
	private static final String MACHINE_VERIFIER = "machine_verifier";

	private static final String SYSTEM_NAME = "system_name";

	private static final String SALT = "salt";

	private static final String VERIFIER = "verifier";

	private static final String _0000000000000000000 = "0000000000000000000";

	private static final String APATH = "//membership/machine_verifier";

	private static final String PROFILE_ID = "profile_id";

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public CreateNewProfileHandler(DialogsMainPeer peer) {
		super(CreateNewProfileCommand.class, peer);
	}


	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String, String> evaluate(CreateNewProfileCommand command) throws CommandHandleException {
		logger.info("in create new profile....");
		Hashtable session = command.getSessionArg();
		String profileId = (String) session.get(SC.PROFILE_ID);
		String realName = (String) session.get(SC.REAL_NAME);
		String username = (String) session.get(SC.USERNAME);
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);

		if (profileId == null || profileId.equals(_0000000000000000000)) {
			logger.info("profile was null, create it");
			profileId = new Long(GenericXMLDocument.getNextTableId()).toString();
			logger.info("new blank profile id: !" + profileId);
		} else {
			logger.info("profile id was not null in save!!: " + profileId);
		}

		CreateMembership membership = new CreateMembership();
		Document throwAway = membership.createMember("asdf", "asdf", "asdf");
		String verifier = throwAway.getRootElement().element(VERIFIER)
				.getText();
		Document forMachine = membership.createMember(realName, username,
				verifier);

		String machineVerifier = forMachine.getRootElement().element(VERIFIER)
				.getText();
		String machineSalt = forMachine.getRootElement().element(VERIFIER)
				.attributeValue(SALT);
		String machinePassphrase = verifier;

		LoginSphere loginSphere = this.peer.getXmldb().getUtils().findLoginSphereElement(username);

		String loginSphereId = null;

		if (loginSphere != null) {
			loginSphereId = loginSphere.getSystemName();
		}
		Document membershipDoc = null;
		if (loginSphereId != null && loginSphereId.length() > 0) {

			membershipDoc = this.peer.getXmldb().getMembershipDoc(
					loginSphereId, username);

		} else {
			membershipDoc = this.peer.getXmldb().getMembershipDoc(supraSphere,
					username);
			loginSphereId = supraSphere;

		}

		if (membershipDoc.getRootElement().element(MACHINE_VERIFIER) != null) {
			try {
				Element one = (Element) membershipDoc.selectObject(APATH);
				if (one != null) {
					removeVerifyer(one, membershipDoc, profileId);
				}
			} catch (ClassCastException cce) {
				ArrayList mach = (ArrayList) membershipDoc.selectObject(APATH);
				for (Object one : mach) {
					removeVerifyer((Element) one, membershipDoc, profileId);
				}
			}
		}
		membershipDoc.getRootElement().addElement(MACHINE_VERIFIER)
				.addAttribute(SALT, machineSalt).addAttribute(PROFILE_ID,
						profileId).setText(machineVerifier);

		logger.info("MEMBERSHIP DOC IN REPLAYCE after add machine: :"
				+ membershipDoc.asXML());

		this.peer.getXmldb().replaceDoc(membershipDoc, loginSphereId);

		// <machine_verifier
		// salt="918995918">82628753177059243047584184012107280066490176339680200088352430756909343473878774165817571979462226000114879899939337443946316876061956177337692694004735956898944587616749931173804683722234692165481019659918213202631200711654873221776019999884229699725612343137408841603079285668871575969283536239323797264943</machine_verifier><machine_pass>85043682359592560430903532470549758044991013835601998726842272409079009613894539248466423369274910036952186582931179638391711811021769098670883855931023197443945237971547969820824457602162053568519804940806674867955413647877818772335235640139504755466030994515732562571943417348732839698858061983013495637514</machine_pass>

		String moment = DialogsMainPeer.getCurrentMoment();
		String messageId = VariousUtils.createMessageId();
		Hashtable<String,String> machineLogin = new Hashtable<String,String>();
		machineLogin.put(SC.MESSAGE_ID, messageId);
		machineLogin.put(SC.MOMENT, moment);
		machineLogin.put(SC.MACHINE_PASSPHRASE, machinePassphrase);
		machineLogin.put(SC.PROFILE_ID2, profileId);
		return machineLogin;
	}

	private void removeVerifyer(Element one, Document membershipDoc,
			String profileId) {
		String oneProfieId = one.attributeValue(PROFILE_ID);
		if (oneProfieId.equals(profileId)) {
			membershipDoc.getRootElement().remove(one);
		} else {
			logger.info("not equal: " + oneProfieId + " : " + profileId);
		}
	}

}
