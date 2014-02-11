package ss.lab.dm3.blob;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import ss.lab.dm3.blob.backend.BlobException;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobManager {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private BlobTransferService blobTransferService; 
	
	private final HashMap<QualifiedObjectId<?>, SheduledUpload> sheduledUploads = new HashMap<QualifiedObjectId<?>,SheduledUpload>();
	
	/**
	 * @param blobTransferService
	 */
	public BlobManager(BlobTransferService blobTransferService) {
		super();
		this.blobTransferService = blobTransferService;
	}

	public synchronized void bind(QualifiedObjectId<?> resourceId, InputStream in,
			IProgressListener progressListener) {
		if ( this.sheduledUploads.containsKey( resourceId ) ) {
			throw new BlobException( "Already contains upload for " + resourceId );
		}
		this.sheduledUploads.put(resourceId, new SheduledUpload( this.blobTransferService, resourceId, progressListener, in ) );
	}

	public synchronized void beginDownload(QualifiedObjectId<?> resourceId, OutputStream out,
			IProgressListener progressListener) {
		final SheduledDownload sheduledDownload = new SheduledDownload( this.blobTransferService, resourceId, progressListener, out );
		sheduledDownload.begin();
	}
	
	public synchronized void beginBindedUploads() {
		for( SheduledUpload upload : this.sheduledUploads.values() ) {
			upload.begin();
		}
		this.sheduledUploads.clear();
	}

	/**
	 * 
	 */
	public synchronized void cancelBindedUploads() {
		for( SheduledUpload upload : this.sheduledUploads.values() ) {
			try {
				upload.cancel();
			}
			catch( RuntimeException ex ) {
				this.log.error( "Can't cancel " + upload, ex );
			}
		}
		this.sheduledUploads.clear();
	}

}
