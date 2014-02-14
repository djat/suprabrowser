/**
 * 
 */
package ss.client.networking;

import java.util.Hashtable;

import ss.client.ui.SupraSphereFrame;
import ss.common.ArgumentNullPointerException;
import ss.framework.exceptions.ObjectIsNotInitializedException;

/**
 *
 */
public class NetworkConnectionFactory {


	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NetworkConnectionFactory.class);
	
	/**
	 * Singleton instance
	 */
	public final static NetworkConnectionFactory INSTANCE = new NetworkConnectionFactory();

	private IStartUpSessionFactory defaultStartUpSessionFactory = new SupraSphereFrameBasedStartUpSessionProvider();
	
	private SupraSphereFrame supraSphereFrame;	
	
	private NetworkConnectionFactory() {
	}
	
	public NetworkConnectionProvider createProvider( String networkingClass ) {
		return createProvider( networkingClass, (IStartUpSessionFactory) null );
	}

	public NetworkConnectionProvider createProvider( String networkingClass, IStartUpSessionFactory sessionFactory ) {
		if (sessionFactory == null) {
			sessionFactory = this.defaultStartUpSessionFactory;
		}
		return createProvider(sessionFactory.createStartUpSession(), networkingClass );
	}
	
	/**
	 * @param networkingClass
	 * @param startUpSession
	 * @return
	 */
	private NetworkConnectionProvider createProvider(Hashtable startUpSession, String networkingClass) {
		// [dg] lately session will comes to startZKA 
		// startZKA modify given session.
		// Make a copy of startUpSession to avoid problems with session id (SC.SESSION)
		startUpSession = (Hashtable) startUpSession.clone();
		if (networkingClass == null) {
			throw new ArgumentNullPointerException("networkingClass");
		}
		SupraClient supraClient = new SupraClient( (String)startUpSession.get( "address"), (String)startUpSession.get("port") );
		supraClient.setSupraSphereFrame( getSupraSphereFrame() );
		Object connectorObject = supraClient.startZeroKnowledgeAuth(startUpSession, networkingClass );
		if ( connectorObject == null  ||  !(connectorObject instanceof NetworkConnectionProvider)) {
			throw new CantCreateConnectorException();
		}
		NetworkConnectionProvider connector = (NetworkConnectionProvider)connectorObject;
		return connector;
	}

	/**
	 * @param supraSphereFrame
	 * @return
	 */
	public synchronized boolean isInitializedByDefault() {
		return this.supraSphereFrame != null && this.supraSphereFrame.client != null;
	}
	
	/**
	 * @return the supraSphereFrame
	 */
	public synchronized SupraSphereFrame getSupraSphereFrame() {
		return this.supraSphereFrame;
	}

	/**
	 * @param supraSphereFrame the supraSphereFrame to set
	 */
	public synchronized void setSupraSphereFrame(SupraSphereFrame supraSphereFrame) {
		this.supraSphereFrame = supraSphereFrame;
	}
	

	/**
	 *
	 */
	public class SupraSphereFrameBasedStartUpSessionProvider implements
			IStartUpSessionFactory {

		/* (non-Javadoc)
		 * @see ss.client.networking.IStartUpSessionFactory#createStartUpSession()
		 */
		@SuppressWarnings("unchecked")
		public Hashtable createStartUpSession() {
			final SupraSphereFrame supraSphereFrame = getSupraSphereFrame();
			if ( supraSphereFrame == null ) {
				throw new ObjectIsNotInitializedException( this, "SupraSphereFrame"  );
			}
			final DialogsMainCli client = supraSphereFrame.client;
			if ( client == null ) {
				throw new ObjectIsNotInitializedException( this, "DialogsMainCli " );
			}
			Hashtable dmcSession = client.getSession();
			// [dg] lately session will comes to startZKA 
			// startZKA modify given session.
			// We should always copy it before send it to ZKA
			Hashtable<String,String> setUpSession = (Hashtable<String,String>) (dmcSession.clone());
			setUpSession.put("changePw", "false");
			if ( !setUpSession.contains( "passphrase" ) ) {
				setUpSession.put("passphrase", supraSphereFrame.getTempPasswords().getTempPW(
						((String) dmcSession.get("supra_sphere"))));
			}
			return setUpSession;	
		}
	}


	/**
	 * @param networkingClass
	 * @param sessionForLogin
	 * @return
	 */
	public NetworkConnectionProvider createProvider(String networkingClass, final Hashtable<String, String> sessionForLogin) {
		return createProvider( networkingClass, new IStartUpSessionFactory() {
			public Hashtable<?,?> createStartUpSession() {
				return (Hashtable<?,?>) sessionForLogin.clone();
			}
		} );
	}

}
