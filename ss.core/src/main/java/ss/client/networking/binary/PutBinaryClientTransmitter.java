/**
 * 
 */
package ss.client.networking.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.processing.TagActionProcessor;
import ss.framework.networking2.blob.FileUploader;

/**
 * @author zobo
 * 
 */
public class PutBinaryClientTransmitter extends AbstractBinaryClientTransmitter {

	/**
	 * @param cdataout
	 * @param cdatain
	 * @param update
	 * @param session
	 */
	public PutBinaryClientTransmitter(DataOutputStream cdataout,
			DataInputStream cdatain, Hashtable update, Hashtable session) {
		super(cdataout, cdatain, update, session);
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PutBinaryClientTransmitter.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void performTransmit() {
		final Hashtable update = getUpdate();
		final Hashtable session = getSession();
		final DataOutputStream cdataout = getCdataout();
		try {
			final byte[] objectBytes = objectToBytes(update);
			cdataout.writeInt(objectBytes.length);
			cdataout.write(objectBytes, 0, objectBytes.length);

			final Hashtable publishInfo = (Hashtable) session
					.get("publishInfo");
			session.remove("publishInfo");

			final String filename = (String) publishInfo.get("data_filename");
			final String messageId = (String) publishInfo.get("messageId");
			final String threadId = (String) publishInfo.get("threadId");

			final String transferBinaryOnly = (publishInfo
					.get("transferBinaryOnly") == null) ? "false"
					: (String) publishInfo.get("transferBinaryOnly");

			logger.warn("PUBLISH INFO>>>>>>: "
					+ ((Document) publishInfo.get("doc")).asXML());

			final File fname = (File) publishInfo.get("fname");
			final Document doc = (Document) publishInfo.get("doc");

			cdataout.writeUTF(filename);
			cdataout.writeUTF(messageId);
			cdataout.writeUTF(threadId);			

			final FileUploader transmitter = new FileUploader( cdataout );
			transmitter.addListener( new BlobLoaderObserver() );
			final boolean succesfull = transmitter.safeUpload(fname);
			
			if (succesfull) {

				final Hashtable cliSession = SupraSphereFrame.INSTANCE
						.getRegisteredSession((String) session
								.get("supra_sphere"), "DialogsMainCli");

				if (doc.getRootElement().element("current_sphere") == null) {
					cliSession.put("sphere_id", (String) session
							.get("sphere_id"));
				} else {

					cliSession.put("sphere_id", doc.getRootElement().element(
							"current_sphere").attributeValue("value"));
				}
				if (transferBinaryOnly.equals("false")) {

					logger.warn("Calling publish....."
							+ (String) cliSession.get("sphere_id"));
					String tagText = (String)publishInfo.get("tag");
					String sphereId = (String)cliSession.get("sphere_id");
					
					SupraSphereFrame.INSTANCE.client.publishTerse(cliSession,
							doc);
					
					Document document = SupraSphereFrame.INSTANCE.client.getSpecificId(cliSession, messageId);
					if(document!=null) {
						TagActionProcessor processor = new TagActionProcessor(SupraSphereFrame.INSTANCE.client, sphereId, document);
						processor.doTagAction(tagText);
					}
					
				}
			}
		} catch (Throwable ex) {
			logger.error("Error during Put Binary", ex);
		}
	}
}
