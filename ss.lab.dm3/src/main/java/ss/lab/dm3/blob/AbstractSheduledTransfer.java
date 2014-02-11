package ss.lab.dm3.blob;

import java.io.IOException;

import ss.lab.dm3.blob.backend.Pipe;
import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public abstract class AbstractSheduledTransfer {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final BlobTransferService blobTransferService;
	
	private final QualifiedObjectId<?> targetId;	
	
	protected final IProgressListener listener;

	private Exception transferException = null;

	private volatile boolean canceled = false;
	
	private volatile boolean started = false; 
	/**
	 * @param listener
	 * @param targetId
	 */
	public AbstractSheduledTransfer(BlobTransferService blobTransferService, QualifiedObjectId<?> targetId, IProgressListener listener) {
		super();
		if ( blobTransferService == null ) {
			throw new NullPointerException( "blobTransferService" );
		}
		this.blobTransferService = blobTransferService;
		this.targetId = targetId;
		this.listener = listener;
	}
	
	public synchronized void begin() {
		if ( this.started ) {
			this.log.warn( "Transfer already began " + this );
			return;
		}
		this.started = true; 
		Thread uploader = new Thread( new RunableTransfer() );
		uploader.start();
	}

	
	protected abstract void doTransfer(PipeHeader pipeHeader, BlobTransferService blobTransferService) throws IOException;


	protected abstract BlobOpenKind getBlobOpenKind();
	
	/**
	 * @return
	 */
	protected BlobTransferService getTransferBlobService() {
		return this.blobTransferService;
	}
	
	protected PipeHeader open( BlobOpenKind blobOpenKind ) {
		return this.blobTransferService.open( this.targetId, blobOpenKind);
	}
	
	/**
	 * 
	 */
	private void transfer() {
		final BlobOpenKind blobOpenKind = getBlobOpenKind();
		if ( isCanceled() ) {
			this.listener.onFail( new TransferCanceledException( this ) );
			return;
		}
		final PipeHeader pipeHeader = this.blobTransferService.open(this.targetId, blobOpenKind);
		try {
			checkIsNotCanceled();
			doTransfer(pipeHeader, this.blobTransferService);
			checkIsNotCanceled();
		}
		catch (IOException ex) {
			this.log.error( "Can't transfer " + this, ex );
			this.listener.onFail( ex );
			this.transferException = ex;			
		}
		finally {
			try {
				this.blobTransferService.close(pipeHeader.getId(), this.transferException == null ? Pipe.TransferState.FINISHED : Pipe.TransferState.FAILED );				
			}
			catch (ServiceException ex) {
				this.log.error( "Can't close " + pipeHeader, ex );
				if ( this.transferException != null ) {
					this.transferException = ex;
				}
			}
			if ( this.transferException == null ) {
				this.listener.onSuccess( null );
			}
			else {
				this.listener.onFail(this.transferException);
			}
		}
	}

	/**
	 * 
	 */
	protected synchronized void checkIsNotCanceled() {
		if ( this.canceled ) {
			throw new TransferCanceledException( this );
		}
	}

	/**
	 * @return
	 */
	public synchronized boolean isCanceled() {
		return this.canceled;
	}
	
	public synchronized boolean isStarted() {
		return this.started;
	}

	/**
	 * 
	 */
	public synchronized void cancel() {
		this.canceled = true;
		if ( !isStarted() ) {
			begin();
		}
	}
	
	private final class RunableTransfer implements Runnable {
		public void run() {
			try {
				transfer();
			}
			catch(Exception ex) {
				AbstractSheduledTransfer.this.log.error( "Transfer " + AbstractSheduledTransfer.this + " failed ", ex );	
			}
		}
	}

}
