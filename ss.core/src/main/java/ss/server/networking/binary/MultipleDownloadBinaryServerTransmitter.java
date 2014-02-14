/**
 * 
 */
package ss.server.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @author zobo
 * 
 */
public class MultipleDownloadBinaryServerTransmitter extends
		AbstractBinaryServerTransmitter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MultipleDownloadBinaryServerTransmitter.class);

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param session
	 */
	public MultipleDownloadBinaryServerTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable session) {
		super(cdataout, cdatain, session);
	}

	@Override
	protected void operation() {

		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		final DataInputStream cdatain = getCdatain();

		try {
			int numberOfFiles = cdatain.readInt();

			logger.info("GET THIS MANY FILES: " + numberOfFiles);

			for (int j = 0; j < numberOfFiles; j++) {

				String fname = cdatain.readUTF();

				logger.info("FNAME: " + fname);
				/* FIXME - hardcoded path */
				String name = bdir + fsep + "roots" + fsep
						+ (String) session.get("supra_sphere") + fsep
						+ "Assets" + fsep + "Library" + fsep + fname;
				FileInputStream fin = new FileInputStream(name);

				int inBytes = fin.available();

				try {

					cdataout.writeInt(inBytes);

				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}

				byte[] buff = new byte[2048];

				int bytesread = 0;

				int bytestotal = 0;

				while (true) {

					bytesread = fin.read(buff);

					if (bytesread == -1 || bytestotal == inBytes) {
						logger.info("bytes read -1 or bytes total inbytes");
						// cdataout.close();
						// cdataout.writeInt(-1);

						break;
					} else {

						cdataout.write(buff, 0, bytesread);
						bytestotal += bytesread;
						// logger.info("writing: "+bytestotal+ "
						// : "+bytesread+" : "+inBytes);

					}

					if (bytestotal == inBytes) {
						// logger.info("bytes total
						// inbytes...break");
						break;
					}

				}

			}
		} catch (Throwable ex) {
			logger.error("Error during MultipleDownloadBinary, ZKA failed", ex);
		}
	}

}
