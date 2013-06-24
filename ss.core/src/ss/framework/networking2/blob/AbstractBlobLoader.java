/**
 * 
 */
package ss.framework.networking2.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ss.common.ArgumentNullPointerException;

/**
 * 
 */
public abstract class AbstractBlobLoader {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractBlobLoader.class);

	protected static final int BUFFER_SIZE = 8192;

	private final List<BlobLoaderListener> listeners = new ArrayList<BlobLoaderListener>();

	public AbstractBlobLoader() {
	}

	public final void addListener(final BlobLoaderListener listener) {
		if (listener == null) {
			throw new ArgumentNullPointerException("listener");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("IBinaryTransmittionListener added " + listener);
		}
		this.listeners.add(listener);
	}

	public final void removeListener(final BlobLoaderListener listener) {
		if (logger.isDebugEnabled()) {
			logger.debug("IBinaryTransmittionListener removed " + listener);
		}
		this.listeners.remove(listener);
	}

	private final void finished() {
		for (BlobLoaderListener listener : this.listeners) {
			try {
				listener.finished();
			} catch (Throwable ex) {
				logger.error("Listener failed " + listener, ex);
			}
		}
	}

	private final void start(String message, int bytesToTransmit) {
		for (BlobLoaderListener listener : this.listeners) {
			try {
				listener.started(message, bytesToTransmit);
			} catch (Throwable ex) {
				logger.error("Listener failed " + listener, ex);
			}
		}
	}

	private final void update(int bytesTransmitted) {
		for (BlobLoaderListener listener : this.listeners) {
			try {
				listener.bytesLoaded(bytesTransmitted);
			} catch (Throwable ex) {
				logger.error("Listener failed " + listener, ex);
			}
		}
	}

	/**
	 * @param in
	 * @param out
	 * @param length
	 * @throws IOException
	 */
	public void transfer(InputStream in, OutputStream out, BlobHeader header) throws IOException {
		try {
			final int length = (int) header.getBlobLength();
			start( header.getDescription(), length );
			int transmittedLength = 0;
			final byte[] buff = new byte[BUFFER_SIZE];
			while (transmittedLength < length) {
				final int bytesread = in.read(buff);
				if (bytesread < 0) {
					if (logger.isDebugEnabled()) {
						logger.debug("Blob input ended, transmitted length: "
								+ transmittedLength);
					}
					out.flush();
					break;
				} else {
					out.write(buff, 0, bytesread);
					transmittedLength += bytesread;
					update(transmittedLength);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Blog transmitted successfully" );
			}
		} finally {
			finished();
		}
	}

}