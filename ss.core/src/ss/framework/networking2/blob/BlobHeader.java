/**
 * 
 */
package ss.framework.networking2.blob;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ss.common.ArgumentNullPointerException;

/**
 * 
 */
final class BlobHeader {

	private static int HEADER_MARK = -721093553; 
	
	/**
	 * 
	 */
	private static final int DEFAULT_FLAG = 0;

	private static final String EMPTY_DESCRIPTION = "";

	private static final long NO_CRC = 0;

	private static final int FLAG_ERROR = -1;

	private final String id;

	private final long blobLength;
	
	private final long blobCrc; 

	private final int flags;
	
	private final String description;
	
	/**
	 * 
	 */
	public BlobHeader(int blobLength) {
		this(BlobIdGenerator.INSTANCE.nextId(), blobLength,	NO_CRC, DEFAULT_FLAG,
				EMPTY_DESCRIPTION );
	}
	
	public static BlobHeader createBlobError( String cause ) {
		return new BlobHeader( BlobIdGenerator.INSTANCE.nextId(), 0, NO_CRC, FLAG_ERROR,
				cause );
	}
	
	/**
	 * @param id
	 * @param blobLength
	 * @param blobCrc
	 * @param flags
	 * @param crc
	 * @param description
	 */
	private BlobHeader(String id, long blobLength, long blobCrc, int flags, String description) {
		super();
		if (id == null) {
			throw new ArgumentNullPointerException("id");
		}
		if ( blobLength < 0 ) {
			throw new IllegalArgumentException( "blobLength" );
		}
		this.id = id;
		this.blobLength = blobLength;
		this.blobCrc = blobCrc;
		this.flags = flags;
		this.description = description != null ? description
				: EMPTY_DESCRIPTION;
	}

	public void save(DataOutputStream out) throws IOException {
		out.writeInt(HEADER_MARK);
		out.writeUTF(this.id);
		out.writeLong(this.blobLength);
		out.writeLong(this.blobCrc);
		out.writeInt(this.flags);
		out.writeUTF(this.description);
		// TODO add crc evaluation
		out.writeLong( NO_CRC );
		out.flush();
	}

	public static BlobHeader load(DataInputStream in) throws IOException {
		int mark = in.readInt();
		if ( mark != HEADER_MARK ) {
			throw new IOException( "Unexpected data. Expected mark " + HEADER_MARK + " but actual is " + mark );
		}
		final String id = in.readUTF();
		final long blobLength = in.readLong();
		final long blobCrc = in.readLong();
		final int flag = in.readInt();
		final String description = in.readUTF();
		// TODO add crc check
		// @SuppressWarnings("unused")
		final long crc = in.readLong(); 
		return new BlobHeader(id, blobLength, blobCrc, flag, description );
	}

	/**
	 * @return the blobLength
	 */
	public long getBlobLength() {
		return this.blobLength;
	}
	
	/**
	 * @return the blobCrc
	 */
	public long getBlobCrc() {
		return this.blobCrc;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the flag
	 */
	public int getFlag() {
		return this.flags;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Blob [");
		sb.append("id: ").append(this.id);
		sb.append(", blobLength: ").append(this.blobLength);
		sb.append(", blobCrc: ").append(this.blobCrc);
		sb.append(", flag: ").append(this.flags);
		sb.append(", description: ").append(this.description);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return
	 */
	public boolean isError() {
		return (this.flags & FLAG_ERROR) == FLAG_ERROR;
	}

}
