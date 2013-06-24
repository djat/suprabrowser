/**
 * 
 */
package ss.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import ss.client.ui.SupraSphereFrame;
import ss.common.IoUtils;
import ss.common.MapUtils;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.simple.SimpleProtocol;
import ss.server.networking.SC;

/**
 *
 */
public final class NetworkConnectionProvider {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NetworkConnectionProvider.class);
	
	private final SupraSphereFrame supraSphereFrame; 
	
	private final Hashtable session;
	
	private final DataInputStream dataIn;
	
	private final DataOutputStream dataOut;
	
	private final Hashtable startUpMap;  
	
	private boolean connectionCreated = false;

	/**
	 * @param dataIn
	 * @param dataOut
	 * @param startUpMap
	 */
	public NetworkConnectionProvider(SupraSphereFrame supraSphereFrame, Hashtable session, DataInputStream dataIn, DataOutputStream dataOut, Hashtable startUpMap) {
		super();
		this.supraSphereFrame = supraSphereFrame;
		this.session = session;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.startUpMap = startUpMap;
	}

	/**
	 * @return the session
	 */
	public Hashtable getSession() {
		return this.session;
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void sendRequestToStartUpServer() throws IOException {
		IoUtils.sendObject(this.dataOut, this.startUpMap);		
	}
	
	public Protocol openProtocol( String protocolDisplayName ) throws IOException {
		NetworkConnection connection = openConnection();
		return new Protocol(connection.getDataIn(), connection.getDataOut(), protocolDisplayName );
	}
	
	public SimpleProtocol openSimpleProtocol() throws IOException {
		NetworkConnection connection = openConnection();
		return new SimpleProtocol( connection.getDataIn(), connection.getDataOut() );
	}

	public synchronized NetworkConnection openConnection() throws IOException {
		if ( this.connectionCreated ) {
			throw new IllegalStateException( "Can't open the same connection twice " + this );
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "sending request to start up server side " + 
					MapUtils.allValuesToString( this.startUpMap ) );
		}
		sendRequestToStartUpServer();
		if (logger.isDebugEnabled()) {
			logger.debug( "creating protocol"  );
		}
		this.connectionCreated = true;
		return new NetworkConnection( this.session, this.dataIn, this.dataOut );
	}
		
	/**
	 * @return
	 */
	public String getUserLogin() {
		return (String)this.session.get(SC.USERNAME);
	}

	/**
	 * @return
	 */
	public SupraSphereFrame getSupraSphereFrame() {
		return this.supraSphereFrame;
	}

	/**
	 * 
	 */
	public void forceClose() {
		try {
			this.dataIn.close();
		} catch (IOException ex) {
			logger.error( "Can't close datain", ex );
		}
		try {
			this.dataOut.close();
		} catch (IOException ex) {
			logger.error( "Can't close dataout", ex );
		}
	}

	/**
	 * @return
	 */
	public String getSessionKey() {
		Hashtable session = getSession();
		return session != null ? (String)session.get( SC.SESSION ) : null;
	}

}
