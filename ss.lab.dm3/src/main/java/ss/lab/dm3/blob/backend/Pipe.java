package ss.lab.dm3.blob.backend;

import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 */
public abstract class Pipe {

	public enum TransferState {
		FINISHED,
		INTERRUPED,
		FAILED,
		OPENED
	};
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private final PipeSet owner;
	
	protected final PipeHeader header;

	protected final BlobInformation blobInformation;
	
	private boolean alive = true;

	private TransferState closeState = TransferState.OPENED; 
	
	/**
	 * @param blobInformation
	 * @param openWrite
	 */
	public Pipe(PipeSet owner, BlobInformation blobInformation) {
		this.owner = owner;
		this.header = PipeHeader.create( blobInformation );
		this.blobInformation = blobInformation;
	}

	/**
	 * @return
	 */
	public final Long getId() {
		return this.header.getId();
	}

	/**
	 * @return
	 */
	public final QualifiedObjectId<?> getResourceId() {
		return this.header.getResourceId();
	}
	
	
	/**
	 * @return
	 */
	public final PipeHeader getHeader() {
		return this.header;
	}

	/**
	 * 
	 */
	public final synchronized void close( TransferState state ) {
		this.owner.remove( this );
		if ( this.alive ) {
			this.closeState = state;
			this.alive = false;
			// Do specific closing operations
			closing();
			// Close blob information 
			this.blobInformation.close(isSuccessfullyClosed());
		}
	}
	
	protected abstract void closing();

	public final boolean isAlive() {
		return this.alive;
	}

	public boolean isSuccessfullyClosed() {
		return this.closeState == TransferState.FINISHED;
	}

}
