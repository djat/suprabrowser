/**
 * 
 */
package ss.client.networking;

import java.util.Hashtable;

import ss.client.configuration.SphereConnectionUrl;
import ss.common.ArgumentNullPointerException;
import ss.common.MapUtils;

/**
 * 
 */
public class CustomStartUpSessionFactory implements IStartUpSessionFactory {

	/**
	 * 
	 */
	public static final int MACHINE_VERIFIER_THRESHOLD = 150;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CustomStartUpSessionFactory.class);

	private final String login;

	private final String password;

	private final SphereConnectionUrl connectionUrl;

	private final String profileId;

	/*
	 * @param login
	 * @param password
	 * @param loginUrl
	 */
	public CustomStartUpSessionFactory(String login,
			String password, SphereConnectionUrl connectionUrl) {
		 this( login, password, null, connectionUrl );
	 }
	
	/**
	 * @param login
	 * @param password
	 * @param loginUrl
	 */
	public CustomStartUpSessionFactory(String login,
			String password, String profileId, SphereConnectionUrl connectionUrl) {
		super();
		if (login == null) {
			throw new ArgumentNullPointerException("login");
		}
		if (password == null) {
			throw new ArgumentNullPointerException("password");
		}
		if (connectionUrl == null) {
			throw new ArgumentNullPointerException("connectionUrl");
		}
		if (password.length() > MACHINE_VERIFIER_THRESHOLD &&
			profileId == null ) {
			throw new IllegalArgumentException("profileId should be not null if password has machine verifier form");
		}
		this.login = login;
		this.password = password;
		this.profileId = profileId;
		this.connectionUrl = connectionUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.networking.IStartUpSessionFactory#createStartUpSession()
	 */
	public Hashtable<?,?> createStartUpSession() {
		final Hashtable<String, String> startUpSession = new Hashtable<String, String>();
		startUpSession.put("sphereURL", this.connectionUrl.toString());
		startUpSession.put("invite", this.connectionUrl.isInvite() ? "true"
				: "false");
		final String session_id = String.valueOf(System.currentTimeMillis());
		startUpSession.put("address", this.connectionUrl.getServer());
		startUpSession.put("sphere_id", this.connectionUrl.getSphereId());
		startUpSession.put("supra_sphere", this.connectionUrl.getSphereId());
		startUpSession
				.put("port", String.valueOf(this.connectionUrl.getPort()));
		startUpSession.put("temp_session_id", session_id);
		
		if (this.password.length() > MACHINE_VERIFIER_THRESHOLD) {
			startUpSession.put("use_machine_verifier", "profile");
			startUpSession.put("profile_id", this.profileId);
		} else {
			startUpSession.put("use_machine_verifier", "false");
		}
		startUpSession.put("passphrase", this.password );
		startUpSession.put("username", this.login);
		if (logger.isDebugEnabled()) {
			logger.debug( "Start up session is " + MapUtils.allValuesToString( startUpSession ) );
		}
		return startUpSession;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() 
		+ " login: " + this.login
		+ ", password: " + this.password
		+ ", profileId: " + this.profileId 
		+ ", connectionUrl: " + this.connectionUrl;
	}

	
}
