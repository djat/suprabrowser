/**
 * 
 */
package ss.framework.networking2.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import ss.common.ArgumentNullPointerException;
import ss.common.CantRestoreObjectFromByteArrayException;
import ss.common.IoUtils;
import ss.framework.networking2.io.zip.IZipPolicy;
import ss.framework.networking2.io.zip.ZipPolicyFactory;

/**
 * 
 */
public final class Packet {
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Packet.class);

	private static IZipPolicy POLICY = ZipPolicyFactory.INCTANCE.getZipPolicy();

	private final PacketHeader header;

	private final Serializable data;

	private final byte[] rawData;
	
	private int dataLength;

	/**
	 * @param header
	 * @param data
	 * @throws IOException
	 */
	public Packet(Serializable data) throws IOException {
		if (data == null) {
			throw new ArgumentNullPointerException("data");
		}
		this.data = data;
		this.rawData = checkAndPackRawData(data);
		this.header = createHeader();
		PacketDetailsTracer.INSTANCE.packetCreated(this);
	}

	/**
	 * @param data
	 * @return
	 */
	private PacketHeader createHeader() {
		boolean shouldSendNotification;
		int id;
		if (this.data instanceof IPacketInformationProvider) {
			final IPacketInformationProvider packetInformationProvider = (IPacketInformationProvider) this.data;
			shouldSendNotification = packetInformationProvider
					.isShouldSendNotification();
			id = packetInformationProvider.getDesiredPacketId();
			if (id <= 0) {
				id = PacketIdGenerator.INSTANCE.nextId();
			}
		} else {
			shouldSendNotification = false;
			id = PacketIdGenerator.INSTANCE.nextId();
		}
		return new PacketHeader(id, this.rawData.length,
				shouldSendNotification, POLICY.isMustBeZipped(this));
	}

	/**
	 * @param header
	 * @param rawData
	 * @throws CantRestoreObjectFromByteArrayException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Packet(PacketHeader header, byte[] rawData)
			throws CantRestoreObjectFromByteArrayException {
		if (header == null) {
			throw new ArgumentNullPointerException("header");
		}
		if (rawData == null) {
			throw new ArgumentNullPointerException("rawData");
		}
		this.header = header;
		if (header.getDataSize() != rawData.length) {
			logger.error("illegal raw data size " + header.getDataSize() + ", "
					+ rawData.length);
		}
		this.rawData = checkAndUnpackRawData(header, rawData);
		this.data = IoUtils.bytesToObject(this.rawData);
		PacketDetailsTracer.INSTANCE.packetRestored(this);
	}

	/**
	 * @return the header
	 */
	public PacketHeader getHeader() {
		return this.header;
	}

	/**
	 * @return the data
	 */
	public Serializable getData() {
		return this.data;
	}

	/**
	 * @return
	 */
	public byte[] getRawData() {
		return this.rawData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.header.toString();
	}

	private byte[] checkAndPackRawData(Serializable data) throws IOException {
		byte[] objectBytes = IoUtils.objectToBytes(data);
		this.dataLength = objectBytes.length;
		if (POLICY.isZippingEnabled()) {
			byte[] zipBytes = objectBytes;
			if (POLICY.isMustBeZipped(this)) {
				ZipLogObject zipLog = null;
				if (logger.isDebugEnabled()) {
					zipLog = new ZipLogObject();
					zipLog.before();
				}
				zipBytes = IoUtils.zipBytes(objectBytes);
				if (logger.isDebugEnabled()) {
					zipLog.after();
					zipLog.log(true, this.dataLength, zipBytes.length);
				}
			}
			return zipBytes;
		} else {
			return objectBytes;
		}
	}

	private byte[] checkAndUnpackRawData(PacketHeader header, byte[] rawData) {
		if (header.isZipPacket()) {
			ZipLogObject zipLog = null;
			if (logger.isDebugEnabled()) {
				zipLog = new ZipLogObject();
				zipLog.before();
			}
			byte[] unzipBytes = IoUtils.unzipBytes(rawData);
			if (logger.isDebugEnabled()) {
				zipLog.after();
				zipLog.log(false, rawData.length, unzipBytes.length);
			}
			this.dataLength = unzipBytes.length;
			return unzipBytes;
		} else {
			return rawData;
		}
	}

	private class ZipLogObject {

		private Date start;

		private Date end;

		private int in;

		private int out;

		private boolean zip;

		/**
		 * 
		 */
		public void before() {
			this.start = new Date();
		}

		/**
		 * @param zip
		 * @param in
		 * @param out
		 */
		public void log(boolean zip, int in, int out) {
			this.zip = zip;
			this.in = in;
			this.out = out;
			if (logger.isDebugEnabled()) {
				logger.debug(this.zip + "|" + this.in + "|" + this.out + "|"
						+ (this.end.getTime() - this.start.getTime()));
			}
		}

		/**
		 * 
		 */
		public void after() {
			this.end = new Date();
		}

	}

	public int getDataLength() {
		return this.dataLength;
	}

}
