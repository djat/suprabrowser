/**
 * 
 */
package ss.client.networking.binary;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import ss.common.ThreadUtils;

/**
 * @author zobo
 *
 */
public abstract class AbstractBinaryClientTransmitter {
	
	protected final static String fsep = System.getProperty("file.separator");
	
	private DataOutputStream cdataout;

	private DataInputStream cdatain;
	
	private Hashtable update;
	
	private Hashtable session;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractBinaryClientTransmitter.class);
	
	public AbstractBinaryClientTransmitter(DataOutputStream cdataout, DataInputStream cdatain, Hashtable update, Hashtable session) {
		super();
		this.cdataout = cdataout;
		this.cdatain = cdatain;
		this.update = update;
		this.session = session;
	}

	protected abstract void performTransmit();
	
	public void transmit(){
		ThreadUtils.start( new Runnable(){
			public void run() {
				try {
					performTransmit();
				}
				finally {
					closeStreams();
				}				
			}
		}, getClass().getSimpleName() );
	}
	
	private void closeStreams(){
		try {
			this.cdataout.close();
			this.cdatain.close();
		} catch (IOException ex) {
			logger.error( "IO Exception while closing input and output streams", ex );
		} catch (Throwable ex) {
			logger.error( "Unknown Exception while closing input and output streams", ex );
		}
	}
	
	protected DataInputStream getCdatain() {
		return this.cdatain;
	}

	protected DataOutputStream getCdataout() {
		return this.cdataout;
	}
	
	protected byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		return baos.toByteArray();
	}

	public Hashtable getSession() {
		return this.session;
	}

	public Hashtable getUpdate() {
		return this.update;
	}
}
