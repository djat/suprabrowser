package ss.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class IoUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(IoUtils.class);

	/**
	 * Converts a serializable object to a byte array.
	 */
	public static byte[] objectToBytes(final Serializable object)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		os.flush();
		return baos.toByteArray();
	}

	/**
	 * Converts a byte array to a serializable object.
	 */
	public static Serializable bytesToObject(final byte[] bytes)
			throws CantRestoreObjectFromByteArrayException {
		if (bytes == null) {
			throw new ArgumentNullPointerException("bytes");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream is = new ObjectInputStream(bais);
			return (Serializable) is.readObject();
		} catch (IOException ex) {
			throw prepareException(ex, bytes);
		} catch (ClassNotFoundException ex) {
			throw prepareException(ex, bytes);
		} catch (RuntimeException ex) {
			throw prepareException(ex, bytes);
		}
	}

	/**
	 * @param ex
	 * @return
	 */
	private static CantRestoreObjectFromByteArrayException prepareException(
			Exception ex, byte[] bytes) {
		logger.error("Failed to deserialize object."
				+ ListUtils.arrayToString(bytes));
		return new CantRestoreObjectFromByteArrayException(
				"Can't restore object from byte array. Array size "
						+ bytes.length, ex);
	}

	/**
	 * Writer object to stream
	 * 
	 * @param dataout
	 * @param object
	 * @throws IOException
	 */
	public static void sendObject(final DataOutputStream dataout,
			final Serializable object) throws IOException {
		byte[] objectBytes = IoUtils.objectToBytes(object);
		dataout.writeInt(objectBytes.length);
		dataout.write(objectBytes);
		dataout.flush();
	}

	/**
	 * Read object from stream
	 * 
	 * @param <T>
	 * @param datain
	 * @param expectedClass
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static <T extends Serializable> T receiveObject(
			final DataInputStream datain, Class<T> expectedClass)
			throws IOException, CantRestoreObjectFromByteArrayException {
		final int object_size = datain.readInt();
		final byte[] objectBytes = new byte[object_size];
		datain.readFully(objectBytes);
		Serializable rawPacket = null;
		rawPacket = bytesToObject(objectBytes);
		if (expectedClass.isInstance(rawPacket)) {
			return expectedClass.cast(rawPacket);
		} else {
			throw new RuntimeException("Wrong packet type: "
					+ rawPacket.getClass().getName() + " excpected "
					+ expectedClass.getName());
		}
	}

	public static byte[] zipBytes(byte[] in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(baos);
		try {
			dos.write(in);
			dos.finish();
			dos.close();
			byte[] out = baos.toByteArray();
			return out;
		} catch (IOException ex) {
			logger.error("failed to zip data",ex);
		}
		return null;
	}

	public static byte[] unzipBytes(byte[] in) {
		ByteArrayInputStream bais = new ByteArrayInputStream(in);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InflaterInputStream iis = new InflaterInputStream(bais);
		try {
			byte[] buffer = new byte[1024];
			while (iis.available() == 1) {
				int offset = iis.read(buffer, 0, buffer.length);
				if (offset != -1) {
					bos.write(buffer, 0, offset);
				}
			}
			return bos.toByteArray();
		} catch (IOException ex) {
			logger.error("failed to unzip data",ex);
		}
		return in;
	}
}
