/**
 * 
 */
package ss.framework.networking2.blob;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ss.common.ArgumentNullPointerException;
import ss.common.FileUtils;

/**
 * 
 */
public class FileDownloader extends AbstractBlobLoader {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileDownloader.class);

	private final DataInputStream dataIn;

	/**
	 * @param datain
	 */
	public FileDownloader(DataInputStream dataIn) {
		super();
		if (dataIn == null) {
			throw new ArgumentNullPointerException("dataIn");
		}
		this.dataIn = dataIn;
	}
	
	public void download(String fileName) throws CantTransferBlobException {
		if (fileName == null) {
			throw new ArgumentNullPointerException("fileName");
		}
		download( new File( fileName ) );
	}
	
	public void download(final File file)
			throws CantTransferBlobException {
		if (file == null) {
			throw new ArgumentNullPointerException("file");
		}
		final FileOutputStream output;
		try {
			output = openFile(file);
		} catch (IOException ex) {
			throw new CantTransferBlobException("Can't open target file: "
					+ file, ex);
		}
		try {
			download(output);
		}
		finally {
			try {
				output.close();
			} catch (IOException ex) {
				logger.error( "Can't close output file " + file,  ex );
			}
		}		
	}

	/**
	 * @param output
	 * @throws DownloadRequestRefusedException
	 * @throws CantTransferBlobException
	 */
	public void download(final OutputStream output) throws CantTransferBlobException {
		try {
			transfer(output);
		} catch (IOException ex) {
			throw new CantTransferBlobException("Transmit failed", ex);
		}
	}

	public boolean safeDownload( File file) {
		try {
			download(file);
			return true;
		} catch (CantTransferBlobException ex) {
			logger.error("File transfer failed " + file, ex);
		} catch (Throwable ex) {
			logger.error("Unexpected exception while transfer file " + file,
							ex);
		}
		return false;
	}

	public boolean safeDownload(String fileName) {
		if ( fileName == null || fileName.length() == 0 ) {
			logger.error( "File name is empty");
			return false;
		}
		final File file;
		try {
			file = new File( fileName );
		} catch (Throwable ex) {
			logger.error( "Invalid file name: " + fileName, ex );
			return false;
		}
		return safeDownload( file );
	}

	private static FileOutputStream openFile(File file) throws IOException {
		FileUtils.ensureParentFolderExists(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}

	private void transfer(OutputStream stream) throws IOException, DownloadRequestRefusedException {
		final BlobHeader header = BlobHeader.load(this.dataIn);
		if (logger.isDebugEnabled()) {
			logger.debug("Transfer header received " + header );
		}
		if ( header.isError() ) {
			logger.warn( "Have transfer error header "+ header );
			throw new DownloadRequestRefusedException( header.getDescription() ); 
		}
		else {
			super.transfer(this.dataIn, stream, header );
		}
	}

}
