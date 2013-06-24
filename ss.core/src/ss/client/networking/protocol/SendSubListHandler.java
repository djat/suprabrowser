/**
 * Jul 5, 2006 : 12:29:31 PM
 */
package ss.client.networking.protocol;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.GenericXMLDocument;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class SendSubListHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(SendSubListHandler.class);
	
	// TODO move
	private static final String CONFIRMED = "confirmed";

	private static final String STATUS = "status";

	private static final String FILESYSTEM = "filesystem";

	private static final String THREAD_TYPE = "thread_type";

	private static final String SYSTEMFILE = "systemfile";

	private static final String TYPE = "type";

	private static final String RELATIVE_TO = "relative_to";

	private static final String FILE_SEPARATOR = "file_separator";

	private static final String SUBJECT = "subject";

	private static final String VALUE = "value";

	private static final String PHYSICAL_LOCATION = "physical_location";

	private final DialogsMainCli cli;


	private static final String fsep = System.getProperty("file.separator");

	public SendSubListHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleSendSubList(final Hashtable update) {
		logger.info("got sendsublist");

		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);
		Document doc = (org.dom4j.Document) update.get(SessionConstants.DOC);

		Hashtable finalSession = (Hashtable) this.cli.session.clone();

		String currentProfileId = this.cli.getSF().getMainVerbosedSession().getProfileId();

		logger.info("current profile!!: " + currentProfileId);

		if (doc == null) {
			logger.info("thats why");

		} else {
			logger.info("so far: " + doc.asXML());
		}

		String fsProfileId = doc.getRootElement().element(PHYSICAL_LOCATION)
				.attributeValue(VALUE);

		if (fsProfileId.equals(currentProfileId)) {

			String dirName = doc.getRootElement().element(SUBJECT)
					.attributeValue(VALUE);

			// String fsep =
			// System.getProperty("file.separator");
			String docSep = doc.getRootElement().element(FILE_SEPARATOR)
					.attributeValue(VALUE);

			if (docSep.equals("backwards")) {

				dirName = dirName.replace("/", "\\");

			}

			Element relativeTo = doc.getRootElement().element(RELATIVE_TO);

			if (relativeTo != null) {

				String relativeName = relativeTo.attributeValue(VALUE);

				dirName = relativeName + "/" + dirName;

				relativeTo.setAttributeValue(VALUE, dirName);

			} else {

			}

			Vector dirs = new Vector();
			Vector files = new Vector();
			File dir = new File(dirName);

			String[] list = dir.list();

			for (int i = 0; i < list.length; i++) {

				String one = list[i];

				File test = new File(dirName + fsep + one);
				if (test.isDirectory()) {
					logger.info("DIRECTORY!!");
					dirs.add(one);

				} else {

					if (one.toLowerCase().endsWith("mp3")) {

						Document genericDoc = null;
						genericDoc = GenericXMLDocument.XMLDoc(one, "",
								(String) this.cli.session
										.get(SessionConstants.REAL_NAME));
						genericDoc.getRootElement().addElement(TYPE)
								.addAttribute(VALUE, SYSTEMFILE);
						genericDoc.getRootElement().addElement(THREAD_TYPE)
								.addAttribute(VALUE, FILESYSTEM);

						// ID3Reader id3 = new
						// ID3Reader(test.getAbsolutePath());
						// ID3Tag idr = new ID3Tag();
						// idr = id3.readExtendedTag(new
						// RandomAccessFile(test,"r"));

						// logger.info("idr.tostaring:
						// "+idr.toString());
						// V2Tag xtag = null;
						// xtag = x.getV2Tag();

						// if (v2tag != null) {
						// genericDoc.getRootElement().element("body").setText(idr.toString());
						// }

						genericDoc.getRootElement().addElement(STATUS)
								.addAttribute(VALUE, CONFIRMED);

						files.add(genericDoc);

					} else {
						files.add(one);

					}

				}

			}

			Hashtable toSend = new Hashtable();

			toSend.put(SessionConstants.SESSION, finalSession);

			toSend.put(SessionConstants.PROTOCOL,
					SSProtocolConstants.SEND_SUB_LIST);

			toSend.put(SessionConstants.DOC, doc);
			toSend.put(SessionConstants.DIRS, dirs);
			toSend.put(SessionConstants.FILES, files);

			toSend.put(SessionConstants.SHOW_PROGRESS, "false");
			this.cli.sendFromQueue(toSend);

		}
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_SUB_LIST;
	}

	public void handle(Hashtable update) {
		handleSendSubList(update);
	}

}
