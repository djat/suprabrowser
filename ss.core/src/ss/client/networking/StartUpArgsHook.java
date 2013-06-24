package ss.client.networking;

import java.util.Hashtable;

import ss.client.configuration.SphereConnectionUrl;

public class StartUpArgsHook {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(StartUpArgsHook.class);
	
	public final static StartUpArgsHook INSTANCE = new StartUpArgsHook();
	
	private CustomStartUpSessionFactory lastGoodStartUpSessionFactory;

	private StartUpArgsHook() {
	}
	

	/**
	 * @param session
	 * @param newPassphrase
	 */
	public synchronized void notifyValidSetUpArgs(Hashtable setUpArgs, String passphrase, String profileId ) {
		String login = (String) setUpArgs.get( "username" );
		String sphereUrl = (String) setUpArgs.get( "sphereURL" );
		if ( login != null &&
			 sphereUrl != null &&
			 passphrase != null ) {
			final SphereConnectionUrl sphereConnectionUrl;
			try {
				sphereConnectionUrl = new SphereConnectionUrl( sphereUrl );
				this.lastGoodStartUpSessionFactory = new CustomStartUpSessionFactory( login, passphrase, profileId, sphereConnectionUrl );			
			} catch (Exception ex) {
				logger.error( "Can't create lastGoodStartUpSessionFactory", ex );
				return;
			}
		}
	}


	public CustomStartUpSessionFactory getLastGoodStartUpSessionFactory() {
		return this.lastGoodStartUpSessionFactory;
	}
	
	
	
}
