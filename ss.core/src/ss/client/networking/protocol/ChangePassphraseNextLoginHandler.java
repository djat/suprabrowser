/**
 * Jul 5, 2006 : 1:59:10 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.WelcomeScreen;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * Callback handler that used by server to reset client welcome screen.
 *   
 */
public class ChangePassphraseNextLoginHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangePassphraseNextLoginHandler.class);
	
	private final DialogsMainCli cli;

	public ChangePassphraseNextLoginHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleChangePassphraseNextLogin(final Hashtable update) {
		logger.info("Before that...changepassphrasenextlogin");
		
		boolean dontChangeOriginal = false;
		try {
			String dontChange = (String) update
					.get(SessionConstants.DONT_CHANGE_ORIGINAL);

			if (dontChange.equals("true")) {
				dontChangeOriginal = true;
			}

		} catch (NullPointerException npe) {
		}
		
		if(this.cli.getSF()==null) {
			return;
		}

		final WelcomeScreen welcomeScreen = this.cli.getSF().getWelcomeScreen();
		welcomeScreen.disposeInvitation();
		welcomeScreen.setFirstSessionId(
				(String) this.cli.session.get(SessionConstants.SESSION));

		welcomeScreen.setChangePassphraseNextLogin(true);

		if (dontChangeOriginal == false) {

			welcomeScreen
					.setOriginalUsernameBeforeChange(
							(String) this.cli.session
									.get(SessionConstants.USERNAME));
			welcomeScreen
					.setOriginalPassphraseBeforeChange(
							this.cli
									.getSF()
									.getTempPasswords()
									.getTempPW(
											((String) this.cli.session
													.get(SessionConstants.SUPRA_SPHERE))));
		}

		welcomeScreen.setSupraSphereFrame(
				this.cli.getSF());
		welcomeScreen.setPromptPassphrase(
				"Please choose a *different* username and passphrase");

		welcomeScreen.layoutGUI();
	}

	public String getProtocol() {
		return SSProtocolConstants.CHANGE_PASSPHRASE_NEXT_LOGIN;
	}

	public void handle(Hashtable update) {
		handleChangePassphraseNextLogin(update);
	}

}
