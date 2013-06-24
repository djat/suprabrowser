/**
 * 
 */
package ss.framework.networking2.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 
 */
public final class PacketHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1934687045264895835L;

	/**
	 * 
	 */
	private static final int FLAG_SHOULD_SEND_NOTIFICATION = 1;

	private static final int FLAG_ZIP = 2;	

	private final int id;

	private final int dataSize;

	private final int flags;

	/**
	 * @param id
	 * @param dataSize
	 * @param shouldUseZip 
	 * @param flags
	 */
	public PacketHeader(int id, int dataSize, boolean shouldSendNotification, boolean shouldUseZip) {
		this(id, dataSize,
				(shouldSendNotification ? FLAG_SHOULD_SEND_NOTIFICATION : 0)
						| (shouldUseZip ? FLAG_ZIP : 0));
	}

	/**
	 * @param id
	 * @param dataSize
	 * @param shouldSendNotification
	 */
	private PacketHeader(int id, int dataSize, int flag) {
		super();
		this.id = id;
		this.dataSize = dataSize;
		this.flags = flag;
	}

	/**
	 * @return
	 */
	public boolean isShouldSendNotification() {
		return (this.flags & FLAG_SHOULD_SEND_NOTIFICATION) == FLAG_SHOULD_SEND_NOTIFICATION;
	}

	/**
	 * @return
	 */
	public boolean isZipPacket() {
		return (this.flags & FLAG_ZIP) == FLAG_ZIP;
	}

	/**
	 * @return
	 */
	public int getDataSize() {
		return this.dataSize;
	}

	/**
	 * @param dataout
	 * @return
	 * @throws IOException
	 */
	public void save(DataOutputStream dataout) throws IOException {
		dataout.writeInt(this.id);
		dataout.writeInt(this.dataSize);
		dataout.writeInt(this.flags);
	}

	/**
	 * @param datain
	 * @return
	 * @throws IOException
	 */
	public static PacketHeader load(DataInputStream datain) throws IOException {
		final int id = datain.readInt();
		final int dataSize = datain.readInt();
		final int flags = datain.readInt();
		return new PacketHeader(id, dataSize, flags);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Header[#" + this.id + ", size " + this.dataSize + ", flag "
				+ this.flags + "]";
	}

	public int getId() {
		return this.id;
	}
}
