/**
 * 
 */
package ss.framework.networking2.blob;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ss.common.ArgumentNullPointerException;

/**
 * 
 */
public class FileUploader extends AbstractBlobLoader {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileUploader.class);

	private final DataOutputStream dataOut;

	/**
	 * @param dataOut
	 */
	public FileUploader(final DataOutputStream dataOut) {
		super();
		if (dataOut == null) {
			throw new ArgumentNullPointerException("dataOut");
		}
		this.dataOut = dataOut;
	}

	/**
	 * @param sourcePath
	 * @throws CantTransferBlobException 
	 */
	public void upload(String sourcePath) throws CantTransferBlobException {
		if (sourcePath == null) {
			throw new ArgumentNullPointerException("sourcePath");
		}
		upload( new File( sourcePath ) );
	}
	
	public void upload( File file)
			throws CantTransferBlobException {
		final InputStream in;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			throw new CantTransferBlobException("Can't open file " + file, ex);
		}
		try {
			transfer(in);
		} catch (IOException ex) {
			throw new CantTransferBlobException("File tranfer failed " + file,
					ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				logger.error("Can't close input file " + file, ex);
			}
		}
	}

	public boolean safeUpload(File file) {
		try {
			upload(file);
			return true;
		} catch (CantTransferBlobException ex) {
			logger.error("Can't transfer file: " + file, ex);
		} catch (Throwable ex) {
			logger.error("Unexpected transfer file exception for: " + file, ex);
		}
		return false;
	}

	public boolean safeUpload(final String fileName) {
		if (fileName == null || fileName.length() == 0) {
			logger.error("File name is empty");
			return false;
		}
		final File file;
		try {
			file = new File(fileName);
		} catch (Throwable ex) {
			logger.error("Invalid file name: " + fileName, ex);
			return false;
		}
		return safeUpload(file);
	}

	private void transfer(final InputStream in)
			throws IOException {
		final BlobHeader header = new BlobHeader(in.available());
		if (logger.isDebugEnabled()) {
			logger.debug("Begin transfer: " + header);
		}
		header.save(this.dataOut);
		transfer(in, this.dataOut, header);
	}

	/**
	 * @throws IOException
	 */
	public void cantUpload( String cause ) throws IOException {
		cause = cause != null ? cause : "Unknown error";    
		final BlobHeader error = BlobHeader.createBlobError(cause);
		logger.warn("Notify upload error: " + error );
		error.save(this.dataOut);
	}
	
}
