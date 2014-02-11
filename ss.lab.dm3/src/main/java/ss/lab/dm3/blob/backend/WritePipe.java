package ss.lab.dm3.blob.backend;

import java.io.IOException;
import java.io.OutputStream;

import ss.lab.dm3.blob.IBlobObject;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class WritePipe extends Pipe {

	private OutputStream out;

	private int actualWritenLength = 0;
	
	/**
	 * @param owner
	 * @param blobInformation
	 * @param out
	 */
	public WritePipe(PipeSet owner, BlobInformation blobInformation, OutputStream out) {
		super(owner, blobInformation);
		this.out = out;
	}

	/**
	 * @param buff
	 * @param offset
	 * @param length
	 * @return
	 */
	public synchronized void write(byte[] buff, int offset, int length) throws BlobException {
		checkWrite();
		try {
			this.out.write(buff, offset, length);
			this.actualWritenLength += length;
		}
		catch (IOException ex) {
			throw new BlobException("Can't read from " + this, ex);
		}		
	}

	/**
	 * 
	 */
	private void checkWrite() {
		if (this.out == null) {
			throw new IllegalStateException("Can't write to " + this);
		}
	}

	/**
	 * 
	 */
	@Override
	protected void closing() {
		// Update blob information size
		this.blobInformation.setSize( this.actualWritenLength );
		// Close the out
		if (this.out != null) {
			try {
				this.out.close();
			}
			catch (IOException ex) {
				this.log.error("Can't close out for " + this, ex);
			}
		}
		this.out = null;
	}

	@Override
	public boolean isSuccessfullyClosed() {
		if ( super.isSuccessfullyClosed() ) {
			final long size = getHeader().getSize();
			return size == IBlobObject.UNKNOWN_BLOB_SIZE || this.actualWritenLength == size; 
		}
		else {
			return false;
		}
	}

	
}
