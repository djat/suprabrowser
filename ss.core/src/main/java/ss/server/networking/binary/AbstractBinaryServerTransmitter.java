/**
 * 
 */
package ss.server.networking.binary;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import ss.client.networking.binary.AbstractBinaryClientTransmitter;
import ss.common.ThreadUtils;

/**
 * @author zobo
 *
 */
public abstract class AbstractBinaryServerTransmitter {
	
	protected static String bdir = System.getProperty("user.dir");
	
	protected static String fsep = File.separator;
	
	private final DataOutputStream cdataout;

	private final DataInputStream cdatain;
	
	private final Hashtable session;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractBinaryClientTransmitter.class);
	
	public AbstractBinaryServerTransmitter(DataOutputStream cdataout, DataInputStream cdatain, Hashtable session) {
		super();
		this.cdataout = cdataout;
		this.cdatain = cdatain;
		this.session = session;
	}

	protected abstract void operation();
	
	public void transmit(){
		Thread thread = new Thread(){
			public void run() {
				operation();
				closeStreams();
			}
		};
		ThreadUtils.startDemon( thread, "Binary transmitter" );
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
}
