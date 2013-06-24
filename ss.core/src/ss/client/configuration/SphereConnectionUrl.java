/**
 * 
 */
package ss.client.configuration;

import ss.client.ui.IllegalSphereUrlException;


/**
 *
 */
public final class SphereConnectionUrl {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereConnectionUrl.class);
	
	/**
	 * 
	 */
	private static final String DEFAULT_SERVER = "suprasecure.com";
	
	/**
	 * 
	 */
	private static final int DEFAULT_PORT = 3000;
	
	/**
	 * 
	 */
	private static final String URL_PREFIX = "sphere::";
	/**
	 * 
	 */
	private static final String SERVER_SPHERE_ID_SEPARATOR = ",";

	/**
	 * 
	 */
	private static final String SERVER_PORT_SEPARATOR = ":";

	
	private String server;

	private int port;

	private String sphereId;

	private boolean invite;
	
	public SphereConnectionUrl() {
		this.server = DEFAULT_SERVER;
		this.port = DEFAULT_PORT;
		this.sphereId = "";
		this.invite = false;
	}
	
	public SphereConnectionUrl( String sphereUrl ) throws IllegalSphereUrlException {
		if ( sphereUrl == null || sphereUrl.length() == 0 ) {
			throw new IllegalSphereUrlException( sphereUrl, "Can't parse empty url." );
		}
		this.invite = !sphereUrl.startsWith("sphere");			
		final String urlBody = sphereUrl.substring(URL_PREFIX.length());
		final String [] bodyParts = urlBody.split( SERVER_SPHERE_ID_SEPARATOR );
		if ( bodyParts.length < 1 ) {
			throw new IllegalSphereUrlException( sphereUrl, "Can't get server and port information." );
		}
		final String serverAndPort = bodyParts[ 0 ];
		final String[] serverAndPortParts = serverAndPort.split( SERVER_PORT_SEPARATOR );
		if ( serverAndPortParts.length != 2 ) {
			throw new IllegalSphereUrlException( sphereUrl, "Can't parse server and port information (" + serverAndPort +")" );		
		}
		this.server = serverAndPortParts[ 0 ];
		final String portStr = serverAndPortParts[ 1 ];
		try {
			this.port = Integer.parseInt( portStr );
		}
		catch( NumberFormatException ex ) {
			throw new IllegalSphereUrlException( sphereUrl, "Can't parse port (" + portStr +"), because " + ex.getMessage()  );
		}
		if ( bodyParts.length > 1 ) {
			this.sphereId = bodyParts[ 1 ];
		}
		else {
			this.sphereId = "";
		}
	}

	/**
	 * @return the address
	 */
	public String getServer() {
		return this.server;
	}

	/**
	 * @return the invite
	 */
	public boolean isInvite() {
		return this.invite;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @return the sphereID
	 */
	public String getSphereId() {
		return this.sphereId;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @param sphereId the sphereId to set
	 */
	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return URL_PREFIX + this.server + SERVER_PORT_SEPARATOR + this.port + SERVER_SPHERE_ID_SEPARATOR + this.sphereId;
	}

	
	
	
}
