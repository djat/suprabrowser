/**
 * 
 */
package ss.server.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

import ss.framework.networking2.blob.FileUploader;

/**
 * @author zobo
 * 
 */
public class GetBinaryServerTransmitter extends AbstractBinaryServerTransmitter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetBinaryServerTransmitter.class);

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public GetBinaryServerTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable session) {
		super(cdataout, cdatain, session);
	}

	@Override
	protected void operation() {
		logger.info("start get binary");

		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		final DataInputStream cdatain = getCdatain();

		try {
			String fname = cdatain.readUTF();
			String messageId = cdatain.readUTF();
			String threadId = cdatain.readUTF();

			logger.warn("start get binary filename should be dataname: "
					+ fname);

			final String name = bdir + fsep + "roots" + fsep
					+ (String) session.get("supra_sphere") + fsep + "File"
					+ fsep + fname;

			(new FileUploader(cdataout)).safeUpload(name);
		} catch (Throwable ex) {
			logger.error("Error during GetBinary, ZKA failed", ex);
		} 
	}
}
