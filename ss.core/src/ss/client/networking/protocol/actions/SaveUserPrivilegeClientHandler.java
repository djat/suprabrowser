package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.common.protocolobjects.SaveUserPrivilegeProtocolObject;

public class SaveUserPrivilegeClientHandler extends AbstractDocumentClientHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveUserPrivilegeClientHandler.class);

	public SaveUserPrivilegeClientHandler(DialogsMainCli cli) {
		super(cli);
	}

	@Override
	public String getProtocol() {
		return SSProtocolConstants.SAVE_USER_PRIVILEGS;
	}

	/**
	 * Save user privileges
	 * 
	 * @param userLogin
	 *            user login name
	 * @param userPrivilege
	 *            user privilege
	 */
	@SuppressWarnings("unchecked")
	public void saveUserPrivileges(String userLogin, String userPrivilege) {
		logger.debug(String.format("saveUserPrivileges %s, %s", userLogin,
				userPrivilege));
		Hashtable update = new Hashtable();
		SaveUserPrivilegeProtocolObject saveUserPrivilegesProtocolObject = new SaveUserPrivilegeProtocolObject(
				update);
		saveUserPrivilegesProtocolObject.setUserLogin(userLogin);
		saveUserPrivilegesProtocolObject.setUserPrivileges(userPrivilege);
		super.sendUpdate(update);
	}
}
