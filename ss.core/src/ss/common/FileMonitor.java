package ss.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.MethodProcessing;
import ss.client.networking.SupraClient;
import ss.client.ui.SupraSphereFrame;
import ss.util.VariousUtils;

public class FileMonitor extends Thread {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileMonitor.class);

	String bdir = System.getProperty("user.dir");

	String fsep = System.getProperty("file.separator");

	static private Hashtable files_ = new Hashtable(); // File -> Long

	static private Vector listeners_ = new Vector(); // of
														// WeakReference(FileListener)

	static Hashtable directories = new Hashtable();

	static private Hashtable filesDocs = new Hashtable();

	private Hashtable session = null;

	// String editedThreadId = null;
	// String editedMessageId = null;

	static HashMap publishedFiles = new HashMap();

	static HashMap editedMessageIds = new HashMap();

	/**
	 * Create a file monitor instance with specified polling interval.
	 * 
	 * @param pollingInterval
	 *            Polling interval in milli seconds.
	 */
	public FileMonitor(Hashtable session, Document rootDoc) {

		this.session = session;

		// timer_ = new Timer (true);
		FileMonitorNotifier fmn = new FileMonitorNotifier(rootDoc);
		fmn.setPriority(1);
		fmn.start();

	}

	public FileMonitor() {

	}

	/**
	 * Stop the file monitor polling.
	 */

	/**
	 * Add file to listen for. File may be any java.io.File (including a
	 * directory) and may well be a non-existing file in the case where the
	 * creating of the file is to be trepped.
	 * <p>
	 * More than one file can be listened for. When the specified file is
	 * created, modified or deleted, listeners are notified.
	 * 
	 * @param file
	 *            File to listen for.
	 */
	@SuppressWarnings("unchecked")
	public void addFile(File file) {
		if (!files_.containsKey(file)) {
			logger.warn("Adding new file: " + file.getAbsolutePath());
			long modifiedTime = file.exists() ? file.lastModified() : -1;
			files_.put(file, new Long(modifiedTime));
		} else {
			logger
					.warn("Already has it...don't do that because it will kill it");
		}

	}

	@SuppressWarnings("unchecked")
	public void addToFilesDocs(String pathName, Document doc) {

		filesDocs.put(pathName, doc);

	}

	public Document getFromFilesDocs(String pathName) {
		return (Document) filesDocs.get(pathName);

	}

	public boolean isAlreadyEditing(String dataFilename) {
		logger.warn("Check is already: " + dataFilename);
		boolean isAlreadyEditing = false;

		File forList = VariousUtils
		.getSupraFile(
				"Assets"
						+ this.fsep
						+ "File",
				dataFilename);

		if (files_ == null) {
			logger.warn("Files was now null");
			return false;
		}
		
		
		if (files_.containsKey(forList)) {

			logger.warn("It was editing this one: " + dataFilename);
			isAlreadyEditing = true;
		} else {
			
			for (Enumeration enumer = files_.keys();enumer.hasMoreElements();) {
				
				File f = (File)enumer.nextElement();
				logger.warn("F: "+f.getAbsolutePath());
				
			}

			
						logger.warn("FIles simply no longer contains that key");
		}
		/*
		 * if (publishedFiles.containsKey(dataFilename)) { isAlreadyEditing =
		 * true; }
		 */

		logger.warn("Returning from is already: " + dataFilename);
		return isAlreadyEditing;

	}

	public void removeFromEditing(String dataFilename) {

		logger.warn("Remove this from editing: " + dataFilename);
		publishedFiles.remove(dataFilename);

		File forList = VariousUtils
		.getSupraFile(
				"Assets"
						+ this.fsep
						+ "File",
				dataFilename);
		removeFile(forList);

		filesDocs.remove(dataFilename);

	}

	/**
	 * Remove specified file for listening.
	 * 
	 * @param file
	 *            File to remove.
	 */
	public void removeFile(File file) {
		files_.remove(file);
	}

	/**
	 * Add listener to this file monitor.
	 * 
	 * @param fileListener
	 *            Listener to add.
	 */

	@SuppressWarnings("unchecked")
	public void addSingleListener(FileListener fileListener, String path) {
		// Don't add if its already there

		logger.info("adding single file ");

		// logger.info("Adding directory: "+dir+ " : "+directories.size());

		/*
		 * for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
		 * WeakReference reference = (WeakReference) i.next(); FileListener
		 * listener = (FileListener) reference.get(); if (listener ==
		 * fileListener) return; }
		 */

		// Use WeakReference to avoid memory leak if this becomes the
		// sole reference to the object.
		listeners_.add(fileListener);

	}

	@SuppressWarnings("unchecked")
	public void addListener(FileListener fileListener, String dir) {

		// Don't add if its already there
		/*
		 * if (directories.containsKey(dir)) { logger.info("not adding!! :
		 * "+dir); return; } else {
		 * 
		 * logger.info("Adding directory: "+dir+ " : "+directories.size());
		 * directories.put(dir,fileListener);
		 *  }
		 * 
		 * 
		 * for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
		 * 
		 * FileListener listener = (FileChangeListener) i.next(); if (listener ==
		 * fileListener) return; }
		 *  // Use WeakReference to avoid memory leak if this becomes the //
		 * sole reference to the object.
		 */

		if (listeners_.size() == 0) {
			listeners_.add(fileListener);
		}
	}

	/**
	 * Remove listener from this file monitor.
	 * 
	 * @param fileListener
	 *            Listener to remove.
	 */
	public void removeListener(FileListener fileListener, String dir) {
		directories.remove(dir);
		for (Iterator i = listeners_.iterator(); i.hasNext();) {

			FileListener listener = (FileListener) i.next();
			if (listener == fileListener) {
				i.remove();
				break;
			}
		}

	}

	/**
	 * This is the timer thread which is executed every n milliseconds according
	 * to the setting of the file monitor. It investigates the file in question
	 * and notify listeners if changed.
	 */
	private class FileMonitorNotifier extends Thread {
		long polling = 500;

		Document rootDoc = null;

		public FileMonitorNotifier(Document rootDoc) {
			this.rootDoc = rootDoc;

		}

		@SuppressWarnings("unchecked")
		public void run() {
			while (true) {
				// Loop over the registered files and see which have changed.
				// Use a copy of the list in case listener wants to alter the
				// list within its fileChanged method.
				Collection files = new ArrayList(files_.keySet());

				for (Iterator i = files.iterator(); i.hasNext();) {

					File file = (File) i.next();

					long lastModifiedTime = ((Long) files_.get(file))
							.longValue();

					long newModifiedTime = file.exists() ? file.lastModified()
							: -1;

					if (newModifiedTime != lastModifiedTime) {
						logger.warn("it did not equal...");

						// Register new modified time
						files_.put(file, new Long(newModifiedTime));

						// Notify listeners
						for (Iterator j = listeners_.iterator(); j.hasNext();) {

							// WeakReference reference = (WeakReference)
							// j.next();

							FileChangeListener listener = (FileChangeListener) j
									.next();

							// Remove from list if the back-end object has been
							// GC'd

							if (listener == null) {
								logger.warn("listener was null..gc'd");
								// j.remove();

								// listener.fileChanged (file);
								// FileChangeListener newLis = new
								// FileChangeListener(rootDoc);
								// newLis.fileChanged(file);
								// addListener(newLis,file.getPath());

							}

							else {

								listener.fileChanged(file);
							}
						}

						logger.warn("After..clearly no listeners");
					}
				}
				try {
					sleep(this.polling);

				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public Vector getCurrentFiles() {

		Vector returnFiles = new Vector();

		Collection files = new ArrayList(files_.keySet());

		for (Iterator i = files.iterator(); i.hasNext();) {

			File file = (File) i.next();
			// if (!file.isDirectory()) {
			returnFiles.add(file);
			// }

		}
		return returnFiles;
	}

	@SuppressWarnings("unchecked")
	public Vector getFileNames(String basePath) {

		Vector returnFiles = new Vector();

		Collection files = new ArrayList(files_.keySet());

		for (Iterator i = files.iterator(); i.hasNext();) {

			File file = (File) i.next();
			if (!file.isDirectory()) {

				String dirPath = getDifferentialDirectory(basePath, file
						.getPath(), file.getName());
//				String dirOnly = dirPath.substring(0, dirPath.length()
//						- file.getName().length());
				returnFiles.add(dirPath);
			}
		}
		return returnFiles;

	}

	public String getDifferentialDirectory(String basePath, String filePath,
			String filename) {

//		String fsep = System.getProperty("file.separator");

//		int pos = filePath.lastIndexOf(basePath);
//		int fsepPos = filePath.lastIndexOf(fsep);

		// logger.info("fsep: "+fsep);
		// logger.info("fseppos: "+fsepPos);
		// logger.info("base: "+basePath+ " : "+filePath+ " : "+pos);

		String remainder = filePath.substring(basePath.length(), filePath
				.length());

		return remainder;

	}

	/**
	 * Test this class.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String args[]) {

		/*
		 * Create the monitor String bdir = System.getProperty("user.dir");
		 * 
		 * File f = new File(bdir);
		 * 
		 * RecursiveList rl = new RecursiveList();
		 * 
		 * FileMonitor monitor = new FileMonitor (1000);
		 * 
		 * rl.listDirContents(f,monitor);
		 * 
		 * monitor.addListener (monitor.new TestListener());
		 * 
		 */

	}

	public void run() {

		while (true) {

			try {
				sleep(5000);
				logger.info("Slept");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}

		}

	}

	public void init() {

	}

	// Avoid program exit

	public class TestListener implements FileListener

	{

		Document rootDoc = null;

		public TestListener(Document rootDoc) {
			this.rootDoc = rootDoc;

		}

		String fsep = System.getProperty("file.separator");

		public void fileChanged(File file) {

			if (!file.isDirectory()) {
				logger.info("File changed: " + file);

				String fullPath = file.getAbsolutePath();

				int pos = fullPath.lastIndexOf(this.fsep);

				logger.info("POS: " + pos);

				if (pos == -1) {
					pos = fullPath.lastIndexOf(this.fsep);

				}

				String remain = fullPath.substring(pos, fullPath.length());
				String fileOnly = remain;
				fileOnly = fileOnly.replace(this.fsep, "");

				logger.info("remain..file only: " + remain);

				String rest = fullPath.replace(remain, "");

				String directoryRemaining = rest;

				String converted = convertFseps(this.fsep, "/", directoryRemaining);

				String one = converted;

				String two = this.rootDoc.getRootElement().element("subject")
						.attributeValue("value");

				logger.info("INDEX " + one.replaceAll(two, ""));
				String preDir = one.replaceAll(two, "");

				// Now get the directory in front to be used as the directory
				// after Filesystem
				String rootPath = this.rootDoc.getRootElement().element("subject")
						.attributeValue("value");

				pos = rootPath.lastIndexOf("/");

				logger.info("POS: " + pos);

				if (pos == -1) {
					pos = rootPath.lastIndexOf(this.fsep);

				}
				String prePreDir = rootPath.substring(pos, rootPath.length());
				// logger.info("last directory before: "+preDir);

				preDir = prePreDir + preDir;

				logger.info("session..rd...fp..pd..fo.."
						+ (String) FileMonitor.this.session.get("sphere_id") + " ..."
						+ (String) FileMonitor.this.session.get("profile_id") + "...."
						+ this.rootDoc.asXML() + "..." + fullPath + "..." + preDir
						+ "..." + fileOnly);

				MethodProcessing.initBRForTransferOnly(FileMonitor.this.session, this.rootDoc,
						fullPath, preDir, fileOnly, SupraSphereFrame.INSTANCE, "transferOnly");

			}
		}

		public String convertFseps(String remoteFsep, String localFsep,
				String name) {
			String result = null;

			if (name.lastIndexOf(remoteFsep) == -1) {
				return name;
			} else {

				result = name.replace(remoteFsep, localFsep);
				return result;

			}

		}

	}

	public class FileChangeListener implements FileListener

	{

		Document rootDoc = null;

		public FileChangeListener(Document rootDoc) {
			this.rootDoc = rootDoc;

		}

		String fsep = System.getProperty("file.separator");

		@SuppressWarnings("unchecked")
		public void fileChanged(File file) {

			logger.warn("File changed here!: " + file.getAbsolutePath());

			if (!file.isDirectory()) {

				String fullPath = file.getAbsolutePath();

				Hashtable sendSession = (Hashtable) FileMonitor.this.session.clone();
				// logger.warn("File changed: "+fullPath);
				// logger.warn("Data filename:
				// "+rootDoc.getRootElement().element("data_id").attributeValue("value"));

				GenericXMLDocument generic = new GenericXMLDocument();
				Document doc = generic.XMLDoc(this.rootDoc.getRootElement().element(
						"subject").attributeValue("value"));

				this.rootDoc = getFromFilesDocs(fullPath);

				doc.getRootElement().addElement("response_id").addAttribute(
						"value",
						this.rootDoc.getRootElement().element("message_id")
								.attributeValue("value"));
				doc.getRootElement().addElement("type").addAttribute("value",
						"file");
				doc.getRootElement().addElement("thread_type").addAttribute(
						"value", "file");
				doc.getRootElement().addElement("current_sphere").addAttribute(
						"value",
						this.rootDoc.getRootElement().element("current_sphere")
								.attributeValue("value"));

				int inBytes = 0;
				try {
					FileInputStream fin = new FileInputStream(fullPath);

					inBytes = fin.available();

					fin.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
				Integer asdf = new Integer(inBytes);
				String bytes = asdf.toString();

				doc.getRootElement().addElement("bytes").addAttribute("value",
						bytes);

				String origDataFilename = this.rootDoc.getRootElement().element(
						"data_id").attributeValue("value");
				StringTokenizer st = new StringTokenizer(origDataFilename,
						"_____");
				st.nextToken();
				String fname = st.nextToken();

				String data_filename = (VariousUtils.getNextRandomLong()
						+ "_____" + fname);
				doc.getRootElement().addElement("original_data_id")
						.addAttribute(
								"value",
								this.rootDoc.getRootElement().element("data_id")
										.attributeValue("value"));
				doc.getRootElement().addElement("data_id").addAttribute(
						"value", data_filename);

				// doc.getRootElement().addElement("status").addAttribute("value","ratified");
				doc.getRootElement().addElement("confirmed").addAttribute(
						"value", "true");
				doc.getRootElement().element("giver").addAttribute("value",
						(String) FileMonitor.this.session.get("real_name"));

				doc.getRootElement().addElement("voting_model").addAttribute(
						"type", "absolute").addAttribute("desc",
						"Absolute without qualification");

				doc.getRootElement().element("voting_model")
						.addElement("tally").addAttribute("number", "0.0")
						.addAttribute("value", "0.0");

				// String sphere =
				// main_session.get("sphere_id");
				// session.put("supra_sphere",sphere);

				sendSession.put("delivery_type", "normal");

//				String leading_dir = "File";

				// Client bootSimplest = new
				// Client((String)session.get("address"),(String)session.get("port"));
				// SimplestClient client =
				// (SimplestClient)bootSimplest.startZeroKnowledgeAuth((String)session.get("supra_sphere"),"supra","user","SimplestClient");
				// client.setSupraSphereFrame(mP.sF);

				// client.publishFile(session,mP,fname,leading_dir,data_filename,mP,doc);

				Hashtable publishInfo = new Hashtable();

				sendSession.put("passphrase", SupraSphereFrame.INSTANCE.getTempPasswords().getTempPW(
						((String) sendSession.get("supra_sphere"))));

				// publishInfo.put("data_filename", data_filename);
				publishInfo.put("fname", file);
				publishInfo.put("doc", doc);

				logger.warn("Should publish...: " + doc.asXML());

				if (!publishedFiles.containsKey(origDataFilename)) {
					publishedFiles.put(origDataFilename, data_filename);
					publishInfo.put("data_filename", data_filename);
					publishInfo.put("transferBinaryOnly", "false");

					String editedMessageId = doc.getRootElement().element(
							"message_id").attributeValue("value");

					Hashtable ids = new Hashtable();
					ids.put("messageId", editedMessageId);

					publishInfo.put("messageId", editedMessageId);

					String editedThreadId = doc.getRootElement().element(
							"thread_id").attributeValue("value");

					ids.put("threadId", editedThreadId);

					editedMessageIds.put(origDataFilename, ids);

					publishInfo.put("threadId", editedThreadId);
				} else {

					String dataFilename = (String) publishedFiles
							.get(origDataFilename);

					Hashtable ids = (Hashtable) editedMessageIds
							.get(origDataFilename);
					String editedMessageId = (String) ids.get("messageId");
					String editedThreadId = (String) ids.get("threadId");

					publishInfo.put("data_filename", dataFilename);
					publishInfo.put("transferBinaryOnly", "true");
					publishInfo.put("messageId", editedMessageId);

					publishInfo.put("threadId", editedThreadId);

				}

				sendSession.put("publishInfo", publishInfo);

				SupraClient sClient = new SupraClient((String) sendSession
						.get("address"), (String) sendSession.get("port"));

				sClient.setSupraSphereFrame(SupraSphereFrame.INSTANCE);

				sClient.startZeroKnowledgeAuth(sendSession, "PutBinary");

			}
		}

		public String convertFseps(String remoteFsep, String localFsep,
				String name) {
			String result = null;

			if (name.lastIndexOf(remoteFsep) == -1) {
				return name;
			} else {

				result = name.replace(remoteFsep, localFsep);
				return result;

			}

		}

	}

	// MethodProcessing

}
