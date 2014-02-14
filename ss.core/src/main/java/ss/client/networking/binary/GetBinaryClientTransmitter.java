/**
 * 
 */
package ss.client.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;
import org.dom4j.Document;

import ss.common.FileMonitor;
import ss.common.OsUtils;
import ss.common.ThreadUtils;
import ss.framework.networking2.blob.FileDownloader;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class GetBinaryClientTransmitter extends AbstractBinaryClientTransmitter {

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public GetBinaryClientTransmitter(DataOutputStream cdataout, DataInputStream cdatain, Hashtable update, Hashtable session) {
		super(cdataout, cdatain, update, session);
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetBinaryClientTransmitter.class);

	@Override
	public void performTransmit() {
		final Hashtable update = getUpdate();
		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		final DataInputStream cdatain = getCdatain();
		try {

			byte[] objectBytes = objectToBytes(update);

			cdataout.writeInt(objectBytes.length);

			cdataout.write(objectBytes, 0, objectBytes.length);

			final Hashtable getInfo = (Hashtable) session.get("getInfo");

			session.remove("getInfo");

			final String filename = (String) getInfo.get("data_filename");

			Document doc = (Document) getInfo.get("document");

			String threadId = doc.getRootElement().element("thread_id")
					.attributeValue("value");
			String messageId = doc.getRootElement().element("message_id")
					.attributeValue("value");

			final File fname = VariousUtils.getSupraFile("Assets" + fsep
					+ "File", filename);

//			boolean music = false;
//
//			if (filename.toLowerCase().endsWith("mp3")
//					|| filename.toLowerCase().endsWith("wav")) {
//				music = true;
//			}

			cdataout.writeUTF(filename);
			cdataout.writeUTF(messageId);
			cdataout.writeUTF(threadId);

			final FileDownloader transmitter = new FileDownloader( cdatain );
			transmitter.addListener( new BlobLoaderObserver() );
			final boolean succedded = transmitter.safeDownload(fname);
			
			if (succedded) {
				final Thread t = new Thread() {
					@SuppressWarnings("deprecation")
					public void run() {

						String sname = null;

						try {
							sname = fname.toURL().toString();
						} catch (Exception e) {
						}

						Perl5Util perl = new Perl5Util();

						String newname = perl.substitute("s/\\s/%20/g",
								sname);

						String isForEditing = null;

						File forList = VariousUtils.getSupraFile(
								"Assets" + fsep + "File", filename);

						try {
							isForEditing = (String) getInfo
									.get("isForEditing");

							if (isForEditing.equals("true")) {

								logger.warn("It's for editing...."
										+ filename);

								if (forList.exists()) {
									logger.warn("File exists");
								} else {
									logger.warn("File does not exist");
								}

								FileMonitor fm = null;
								Document doc = (Document) getInfo
										.get("document");

								fm = new FileMonitor(session, doc);

								logger.info("adding file there: "
										+ forList.getAbsolutePath());

								if (fm.isAlreadyEditing(doc
										.getRootElement().element(
												"data_id")
										.attributeValue("value"))) {
									logger
											.warn("BUT ITS ALReADY editing");

								} else {
									logger
											.warn("IT is not editing this");
								}

								fm.addListener(
										fm.new FileChangeListener(doc),
										forList.getPath());
								fm.addToFilesDocs(forList.getPath(),
										doc);

								fm.addFile(forList);

							}

						} catch (NullPointerException npe) {
							logger.error("Null pointer exception in GetBinary", npe);
						}

						String execName = null;
						
						OsUtils.startFile(forList, execName, newname, filename);
						
//						String os = System.getProperty("os.name");
//
//						if (os.startsWith("Win")) {
//							execName = ("cmd /c \"start " + newname + "\"");
//
//							if (execName != null) {
//								try {
//									Process p = Runtime.getRuntime()
//											.exec(execName);
//									if (p == null) {
//
//									}
//
//								} catch (IOException ioe) {
//									logger.error("IOException in GetBinary", ioe);
//								}
//							}
//						} else if (os.startsWith("Linux")) {
//
//							if (CheckFileExtension
//									.isKnownFile(filename)) {
//
//								String[] command = {
//										OSCommandRegistry
//												.getProgramForFileExtension(filename),
//										forList.getAbsolutePath() };
//
//								logger.warn("New name: " + execName);
//
//								try {
//									Process p = Runtime.getRuntime()
//											.exec(command);
//									if (p == null) {
//
//									}
//
//								} catch (IOException ioe) {
//									logger.error("IOException in GetBinary", ioe);
//								}
//
//							}
//
//						}

					}
				};
				ThreadUtils.startDemon( t, "recieved file processing" );
			}
		} catch (Throwable ex) {
			logger.error("Error during GetBinary", ex);
		}
	}
}