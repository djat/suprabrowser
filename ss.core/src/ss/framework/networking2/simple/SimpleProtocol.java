/**
 * 
 */
package ss.framework.networking2.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

import ss.common.ArgumentNullPointerException;
import ss.common.CantRestoreObjectFromByteArrayException;
import ss.framework.networking2.blob.CantTransferBlobException;
import ss.framework.networking2.blob.FileDownloader;
import ss.framework.networking2.blob.FileUploader;
import ss.framework.networking2.io.Packet;
import ss.framework.networking2.io.PacketInputStream;
import ss.framework.networking2.io.PacketOutputStream;

/**
 * 
 */
public final class SimpleProtocol {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SimpleProtocol.class);
	
	private final PacketInputStream in;

	private final PacketOutputStream out;

	private final FileDownloader fileDownloader;

	private final FileUploader fileUploader;

	/**
	 * 
	 */
	public SimpleProtocol(DataInputStream dataIn, DataOutputStream dataOut) {
		super();
		this.in = new PacketInputStream(dataIn);
		this.out = new PacketOutputStream(dataOut);
		this.fileDownloader = new FileDownloader(dataIn);
		this.fileUploader = new FileUploader(dataOut);
	}

	public void send(Serializable object) throws SimpleProtocolException {
		if (object == null) {
			throw new ArgumentNullPointerException("object");
		}
		try {
			this.out.write(new Packet(object));
		} catch (IOException ex) {
			throw new SimpleProtocolException("Can't send " + object, ex);
		}
	}

	public <T extends Serializable> T receive(Class<T> expectedClazz)
			throws SimpleProtocolException {
		final T obj = tryReceive(expectedClazz);
		if ( obj == null ) {
			throw new SimpleProtocolException("Can't restore packet. Protocol closed unexpectedly." );
		} 
		return obj;
	}

	public <T extends Serializable> T tryReceive(Class<T> expectedClazz)
			throws SimpleProtocolException {
		try {
			Packet packet = this.in.read();
			Serializable data = packet.getData();
			if (!expectedClazz.isInstance(data)) {
				throw new SimpleProtocolException("Receive unexpected object "
						+ data + " instead of " + expectedClazz);
			} else {
				return expectedClazz.cast(data);
			}
		} catch (EOFException ex) {
			return null;
		} catch (IOException ex) {
			throw new SimpleProtocolException("Can't restore packet", ex);
		} catch (CantRestoreObjectFromByteArrayException ex) {
			throw new SimpleProtocolException("Can't restore packet data", ex);
		}
	}

	public void uploadFile(String sourcePath) throws SimpleProtocolException {
		try {
			this.fileUploader.upload(sourcePath);
		} catch (CantTransferBlobException ex) {
			throw new SimpleProtocolException(
					"Can't upload file " + sourcePath, ex);
		}
	}

	/**
	 * @param destinationPath
	 */
	public void downloadFile(String destinationPath)
			throws SimpleProtocolException {
		try {
			this.fileDownloader.download(destinationPath);
		} catch (CantTransferBlobException ex) {
			throw new SimpleProtocolException("Can't download file to "
					+ destinationPath, ex);
		}

	}

	/**
	 * @return the fileDownloader
	 */
	public FileDownloader getFileDownloader() {
		return this.fileDownloader;
	}

	/**
	 * @return the fileUploader
	 */
	public FileUploader getFileUploader() {
		return this.fileUploader;
	}
	
	public void close() {
		try {
			this.in.close();
		} catch (IOException ex) {
			logger.error( "Can't close input stream",  ex );
		}
		try {
			this.out.close();
		} catch (IOException ex) {
			logger.error( "Can't close output stream",  ex );
		}
	}

	/**
	 * @throws SimpleProtocolException
	 * 
	 */
	public void cantUpload( String cause ) throws SimpleProtocolException {
		try {
			this.fileUploader.cantUpload( cause );
		}
		catch (IOException ex) {
			throw new SimpleProtocolException( "Can't notify about upload problem", ex );
		}
	}

}
