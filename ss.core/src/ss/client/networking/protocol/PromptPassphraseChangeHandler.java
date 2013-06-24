/**
 * Jul 5, 2006 : 1:53:36 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * Callback that update WelcomeScreen in invitation process.
 * 
 */
public class PromptPassphraseChangeHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PromptPassphraseChangeHandler.class);
	
	private final DialogsMainCli cli;

	public PromptPassphraseChangeHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handlePromptPassphraseChange(final Hashtable update) {
		logger.info("Before that...");

		String tempUsername = (String) update
				.get(SessionConstants.TEMP_USERNAME);
		String loginSphere = (String) update.get(SessionConstants.LOGIN_SPHERE);
		String inviteURL = (String) update.get(SessionConstants.INVITE_URL);
		
		logger.info("change passphrase, temp username" + tempUsername
				+ " : " + loginSphere);

		logger.info("INVITE URL...." + inviteURL);

		SupraSphereFrame sf = this.cli.getSF();
		sf.getWelcomeScreen().saveNewUrl(inviteURL);
		sf.getWelcomeScreen().disposeInvitation();
		sf.getWelcomeScreen().setFirstSessionId(
				(String) this.cli.session.get(SessionConstants.SESSION));
		sf.getWelcomeScreen().setIsChangePw(true);
		sf.getWelcomeScreen().setTempUsername(tempUsername);
		// sF.getWelcomeScreen().setLoginSphere((String)update.get("inviteURL"));
		sf.getWelcomeScreen().setLoginSphere(loginSphere);
		sf.getWelcomeScreen().setInitialSphereUrl(inviteURL);

		sf.getWelcomeScreen().setSupraSphereFrame(sf);
		sf.getWelcomeScreen().setPromptPassphrase(
				"You now must choose a username and passphrase");

		sf.getWelcomeScreen().layoutGUI();
	}

	public String getProtocol() {
		return SSProtocolConstants.PROMPT_PASSPHRASE_CHANGE;
	}

	public void handle(Hashtable update) {
		handlePromptPassphraseChange(update);

	}

}
