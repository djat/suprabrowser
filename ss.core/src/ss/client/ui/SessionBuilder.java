/**
 * 
 */
package ss.client.ui;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import ss.client.networking.CustomStartUpSessionFactory;
import ss.common.MapUtils;
import ss.common.debug.DebugUtils;

final class SessionBuilder {

	private final WelcomeScreen welcome;

	private final Hashtable<String, String> session = new Hashtable<String, String>();

	private String address;

	private String port;

	private String sphereID;

	/**
	 * @param welcome
	 */
	SessionBuilder(final WelcomeScreen welcome) {
		super();
		this.welcome = welcome;
	}

	public Hashtable<String, String> getResult() {
		Logger logger = WelcomeScreen.logger;
		logger.warn("is change : " + this.welcome.isChangePw);
		logger.warn("change pass : "
				+ this.welcome.changePassphraseNextLogin);
		try {
			if (this.welcome.sphereValue == null) {
				this.welcome.sphereValue = this.welcome.getInitialSphereUrl();
			}
			
			logger.warn("Sphere value: " + this.welcome.sphereValue);

			final String sphereUrl = this.welcome.sphereValue;
			if (this.welcome.getInitialSphereUrl() != null) {
				if (!sphereUrl.equals(this.welcome.getInitialSphereUrl())) {
					this.welcome.saveNewUrl(this.welcome.sphereValue);
				}
			}
			this.session.put("sphereURL", sphereUrl);
			final String urlWithoutSpherePrefix = sphereUrl.substring(8,
					sphereUrl.length());

			logger.info("FIRST: " + urlWithoutSpherePrefix);

			StringTokenizer st = new StringTokenizer(
					urlWithoutSpherePrefix, ":");
			logger.info("here....:" + sphereUrl);
			this.address = st.nextToken();
			logger.info("address: " + this.address);
			String portST = st.nextToken();
			logger.info("port: " + portST);
			st = new StringTokenizer(portST, ",");
			this.port = st.nextToken();
			this.sphereID = st.nextToken();

			if (sphereUrl.startsWith("sphere")) {
				this.session.put("invite", "false");
			} else {
				this.session.put("invite", "true");
			}
			if (this.welcome.isChangePw == true) {
				logger.info("Change pw is true");
				this.session.put("changePw", "true");
				this.session.put("tempUsername", this.welcome.tempUsername);
				this.session.put("loginSphere", this.welcome.loginSphere);

			} else if (this.welcome.changePassphraseNextLogin == true) {

				logger.warn("Setting because :"
						+ this.welcome.originalUsername + " : "
						+ this.welcome.originalPassphrase + " : "
						+ this.welcome.getUser() + " : "
						+ this.welcome.getPass() );
				this.session.put("changePw", "changePassphraseNextLogin");
				this.session.put("username", this.welcome.originalUsername);
				this.session.put("passphrase",
						this.welcome.originalPassphrase);
				this.session.put("newUsername", this.welcome.getUser());
				this.session.put("newPassphrase", this.welcome.getPass());
				this.welcome.setPass( this.welcome.originalPassphrase );
				this.welcome.setUser(this.welcome.originalUsername);
				if (this.welcome.firstSessionId != null) {

					this.session.put("firstSessionId",
							this.welcome.firstSessionId);

				}

				// String profile = getProfile();

			} else {
				logger.info("change password false...");
			}
		} catch (Exception nse) {
			logger.error("", nse);
		}

		Integer portwrap = new Integer(this.port);
		// int port_int = portwrap.intValue();

		long long_num = System.currentTimeMillis();
		String session_id = (Long.toString(long_num));

		logger.info("suprasphere: " + this.sphereID);
		logger.info("invite? " + (String) this.session.get("invite"));

		this.session.put("address", this.address);
		this.session.put("sphere_id", this.sphereID);
		this.session.put("supra_sphere", this.sphereID);
		this.session.put("port", portwrap.toString());
		this.session.put("temp_session_id", session_id);

		if (this.welcome.isRememberedUserNameAndPass() == true ) {
			if ( this.welcome.getPass().length() > CustomStartUpSessionFactory.MACHINE_VERIFIER_THRESHOLD) {
				this.session.put("use_machine_verifier", "profile");
				this.session.put("profile_id", this.welcome.getProfileId() );
				this.session.put("passphrase", this.welcome.getPass());

			} else {
				this.session.put("use_machine_verifier", "false");
				this.session.put("passphrase", this.welcome.getPass());
			}
		} else {
			this.welcome.startUpArgs.setAutoLogin( false );
			this.session.put("use_machine_verifier", "false");
			this.session.put("passphrase", this.welcome.getPass());
		}
		this.session.put("username", this.welcome.getUser());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Session is " + MapUtils.allValuesToString(this.session) );
		}
		return this.session;
	}

}