/**
 * 
 */
package ss.server.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

import ss.common.converter.ConvertingElementFactory;
import ss.common.converter.DocumentConverterAndIndexer;
import ss.framework.networking2.blob.FileDownloader;
import ss.server.networking.processing.FileProcessor;

/**
 * @author zobo
 * 
 */
public class PutBinaryServerTransmitter extends AbstractBinaryServerTransmitter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PutBinaryServerTransmitter.class);

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public PutBinaryServerTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable session) {
		super(cdataout, cdatain, session);
	}

	@Override
	protected void operation() {
		logger.info("Start put binary...");

		final Hashtable session = getSession();
		final DataInputStream cdatain = getCdatain();

		try {
			final String fname = cdatain.readUTF();
			final String messageId = cdatain.readUTF();
			final String threadId = cdatain.readUTF();

			/* FIXME - hardcoded path reference */

			final String name = FileProcessor.getFullPathName(fname, (String) session.get("supra_sphere"));
			final FileDownloader transmitter = new FileDownloader(cdatain);
			final boolean succedded = transmitter.safeDownload(name);

			if (succedded) {
				DocumentConverterAndIndexer.INSTANCE.convert(ConvertingElementFactory.createConvert(
						session, threadId, messageId, name));
			}
		} catch (Throwable ex) {
			logger.error("Error during PutBinary, ZKA failed", ex);
		}
	}
}
