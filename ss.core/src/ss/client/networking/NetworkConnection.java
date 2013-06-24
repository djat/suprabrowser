/**
 * 
 */
package ss.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;


/**
 *
 */
public final class NetworkConnection {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NetworkConnection.class);
	
	private final Hashtable session;
	
	private final DataInputStream dataIn;
	
	private final DataOutputStream dataOut;

	/**
	 * @param session
	 * @param dataIn
	 * @param dataOut
	 */
	public NetworkConnection(final Hashtable session, final DataInputStream dataIn, final DataOutputStream dataOut) {
		super();
		this.session = session;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
	}

	/**
	 * @return the dataIn
	 */
	public DataInputStream getDataIn() {
		return this.dataIn;
	}

	/**
	 * @return the dataOut
	 */
	public DataOutputStream getDataOut() {
		return this.dataOut;
	}

	/**
	 * @return the session
	 */
	public Hashtable getSession() {
		return this.session;
	}

	/**
	 * 
	 */
	public void close() {
		try {
			this.dataIn.close();
		} catch (IOException ex) {
			logger.error( "Can't close input stream",  ex );
		}
		try {
			this.dataOut.close();
		} catch (IOException ex) {
			logger.error( "Can't close output stream",  ex );
		}
		
	}
	
	
	

}
