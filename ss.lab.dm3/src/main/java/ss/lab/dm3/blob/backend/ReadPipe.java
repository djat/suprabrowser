package ss.lab.dm3.blob.backend;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class ReadPipe extends Pipe {

	private InputStream in;
	
	/**
	 * @param owner
	 * @param blobInformation
	 * @param in
	 */
	public ReadPipe(PipeSet owner, BlobInformation blobInformation, InputStream in) {
		super(owner, blobInformation);
		this.in = in;
	}


	/**
	 * @param buff
	 * @param offset
	 * @param length
	 */
	public synchronized int read(byte[] buff, int offset, int length) throws BlobException {
		checkRead();
		try {
			return this.in.read(buff, offset, length);
		}
		catch (IOException ex) {
			throw new BlobException( "Can't read from " + this, ex );
		}	
	}
	

	/**
	 * 
	 */
	private void checkRead() {
		if ( this.in == null ) {
			throw new IllegalStateException( "Can't read from " + this );
		}
	}
	
	/**
	 * 
	 */
	@Override
	protected synchronized void closing() {
		if ( this.in != null ) {
			try {
				this.in.close();
			}
			catch (IOException ex) {
				this.log.error( "Can't close in for " + this, ex );
			}
		}
		this.in = null;	
	}

}

