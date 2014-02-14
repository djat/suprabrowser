/**
 * 
 */
package ss.client.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.progressbar.DownloadProgressBar;
import ss.common.build.AntBuilder;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class MultipleDowunloadBinaryClientTransmitter extends
		AbstractBinaryClientTransmitter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MultipleDowunloadBinaryClientTransmitter.class);

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public MultipleDowunloadBinaryClientTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable update, Hashtable session) {
		super(cdataout, cdatain, update, session);
	}

	@Override
	protected void performTransmit() {
		final Hashtable update = getUpdate();
		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		final DataInputStream cdatain = getCdatain();
		try {

			byte[] objectBytes = objectToBytes(update);
			cdataout.writeInt(objectBytes.length);

			cdataout.write(objectBytes, 0, objectBytes.length);

			Hashtable getInfo = (Hashtable) session.get("getInfo");
			session.remove("getInfo");
			Vector files = (Vector) getInfo.get("files");

			Document versionsDoc = null;
			File file = new File(System.getProperty("user.dir") + fsep
					+ "supraversions.xml");

			SAXReader reader1 = new SAXReader();

			try {

				versionsDoc = reader1.read(file);

			} catch (Exception e) {

				logger.error(e.getMessage(), e);

			}

			AntBuilder antBuilder = new AntBuilder();
			cdataout.writeInt(files.size());
			for (int j = 0; j < files.size(); j++) {

				// File fname = 
				// (File)publishInfo.get("fname");
				logger.info("Actual format: "
						+ ((Element) files.get(j)).asXML());
				String filename = ((Element) files.get(j))
						.attributeValue("name");

				String location = ((Element) files.get(j))
						.attributeValue("location");
				location = VariousUtils.convertFseps("/", fsep, location);
				logger.info("Location!!: " + location);

				File fname = new File(filename);

				cdataout.writeUTF(filename);

				byte[] inobjectBytes = null;
				//
				int object_size = cdatain.readInt();

				inobjectBytes = new byte[object_size];

				int bytesread = 0;
				int bytestotal = 0;
				final int inBytes = object_size;

				DownloadProgressBar dpb = null;

				dpb = new DownloadProgressBar(inBytes, null);

				FileOutputStream fout = new FileOutputStream(fname);

				while (true) {

					if (bytestotal == inBytes) {

						fout.close();

						if (dpb != null) {
							dpb.destroyDownloadBar();
						}

						break;

					}

					byte[] buff = new byte[4096];
					bytesread = cdatain.read(buff);

					for (int i = 0; i < bytesread; i++) {

						try {
							inobjectBytes[i + bytestotal] = buff[i];
						} catch (ArrayIndexOutOfBoundsException eie) {

							logger.error(eie);
						}

					}

					if (dpb != null) {
						dpb.updateDownloadBar(bytestotal);
					}

					if (bytestotal == inBytes) {

						fout.close();

						if (dpb != null) {
							dpb.destroyDownloadBar();
						}

						break;

					} else {

						fout.write(buff, 0, bytesread);
						bytestotal += bytesread;

					}

				}

				Thread.sleep(100);

				if (versionsDoc != null) {
					String version = ((Element) files.get(j))
							.attributeValue("current_version");
					String oldName = ((Element) files.get(j))
							.attributeValue("old_name");

					String path = "//versions/asset[@name=\"" + filename
							+ "\"]";
					try {

						Element assetElem = (Element) versionsDoc
								.selectObject(path);
						assetElem.addAttribute("current_version", version);

						String suprajar = ((Element) files.get(j))
								.attributeValue("suprajar");
						if (suprajar != null) {
							if (suprajar.equals("true")) {

								logger.info("replacing port: "
										+ suprajar
										+ ((Element) files.get(j))
												.attributeValue("port"));
								antBuilder.replacePort(((Element) files.get(j))
										.attributeValue("port"));
								antBuilder.replaceSupraJarname(filename);

							}

						} else {
							antBuilder.replaceLibraryInRunPath(oldName,
									filename);
						}

					} catch (ClassCastException cce) {
						logger.error(cce);
						Element newLib = ((Element) files.get(j));
						newLib.detach();
						versionsDoc.getRootElement().add(newLib);
						String suprajar = ((Element) files.get(j))
								.attributeValue("suprajar");
						if (suprajar != null) {
							if (suprajar.equals("true")) {

								logger.info("replacing port: "
										+ suprajar
										+ ((Element) files.get(j))
												.attributeValue("port"));
								antBuilder.replacePort(((Element) files.get(j))
										.attributeValue("port"));
								antBuilder.replaceSupraJarname(filename);

							} else {

								logger
										.info("suprajar not true...dont replace port: "
												+ ((Element) files.get(j))
														.asXML());
							}

						} else {
							logger
									.info("Adding to run path because suprajar was actually null."
											+ filename);
							antBuilder.addToRunPath(filename);
						}

					}

				}

			}

			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream fout = new FileOutputStream(file);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(versionsDoc);
			writer.close();
			fout.close();

			SupraSphereFrame.INSTANCE.client.restartOnly(true);

		} catch (Throwable ex) {
			logger.error("Error during Downloading multiple files", ex);
			SupraSphereFrame.INSTANCE.disposeButDontRemove();
		}
	}
}