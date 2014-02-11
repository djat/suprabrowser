package ss.lab.dm3.blob.service;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import ss.lab.dm3.blob.backend.BlobInformation;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class PipeHeader implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3855287403588455043L;

	private final static AtomicLong counter = new AtomicLong();
	
	private final Long id;
	
	private final QualifiedObjectId<?> resourceId;
	
	private long size;
	
	// Not used yet
	// private long initialOffset;
	// private byte[] checkSum

	/**
	 * @param id
	 * @param resourceId
	 */
	public PipeHeader(Long id, BlobInformation blobInformation) {
		super();
		this.id = id;
		this.resourceId = blobInformation.getResourceId();
		this.size = blobInformation.getSize();
	}

	/**
	 * @param blobInformation
	 * @return
	 */
	public static PipeHeader create(BlobInformation blobInformation) {
		return new PipeHeader( counter.incrementAndGet(), blobInformation );
	}

	
	public Long getId() {
		return this.id;
	}

	public QualifiedObjectId<?> getResourceId() {
		return this.resourceId;
	}
		
	/**
	 * @return
	 */
	public long getSize() {
		return this.size;
	}

	
}
