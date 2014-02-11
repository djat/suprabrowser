package ss.lab.dm3.blob.backend;

import ss.lab.dm3.blob.IBlobObject;
import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobInformation {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
		.getLog(getClass());
		
	private final BlobInformationProvider owner;
	
	private QualifiedObjectId<?> resourceId;
	
	private BlobState blobState;
	
	private long size;

	
	/**
	 * @param obj
	 */
	public BlobInformation(BlobInformationProvider owner, IBlobObject obj) {
		super();
		if ( owner == null ) {
			throw new NullPointerException( "owner" );
		}
		this.resourceId = obj.getQualifiedId();
		this.blobState = obj.getBlobState();
		this.size = obj.getSize();		
		this.owner = owner;
	}

	/**
	 * @param blobOpenKind
	 * @return
	 */
	public void open(BlobOpenKind blobOpenKind) {
		if ( blobOpenKind == BlobOpenKind.CREATE_OVERWRITE ) {
			if ( this.blobState == BlobState.WRITING ) {
				throw new BlobException( "Can't write to " + this );
			}
			this.blobState = BlobState.WRITING;
		}
		else if ( blobOpenKind == BlobOpenKind.READ ) {
			if ( this.blobState != BlobState.READY ) {
				throw new BlobException( "Can't read from " + this );
			}
		}
		else {
			throw new IllegalArgumentException( "Unknown blobOpenKind " + blobOpenKind );
		}
	}

	/**
	 * @return
	 */
	public QualifiedObjectId<?> getResourceId() {
		return this.resourceId;
	}

	public BlobState getBlobState() {
		return this.blobState;
	}

	public long getSize() {
		return this.size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	public byte[] getCheckSum() {
		// TODO implement check sum
		return null;
	}

	/**
	 * @param successful
	 */
	public void close(boolean successful) {
		// Updates blob only if we are in writing blob state
		if ( this.blobState == BlobState.WRITING ) {
			this.blobState = successful ? BlobState.READY : BlobState.BROKEN;
			this.owner.updateBlobBy(this);
		}
		else if ( this.blobState != BlobState.READY ) {
			this.log.error( "Unexpected blob state " + this );
		}
	}

	/**
	 * @param obj
	 */
	public void writePropertiresTo(IBlobObject obj) {
		if ( obj == null ) {
			throw new NullPointerException( "obj" );
		}
		obj.setBlobState( this.blobState );
		obj.setSize( this.size );
	}
	
}
