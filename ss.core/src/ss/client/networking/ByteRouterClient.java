/**
 *
 */

package ss.client.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.MethodProcessing;
import ss.client.ui.SupraSphereFrame;
import ss.common.FileMonitor;
import ss.common.RecursiveList;
import ss.util.VariousUtils;

public class ByteRouterClient extends Thread {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ByteRouterClient.class);

	static Hashtable byteRouters = new Hashtable();

	private DataInputStream cdatain = null;

	private DataOutputStream cdataout = null;

	private String routerName = null;

	private String clientSession = null;

	/* FIXME - platform-specific access goes to VariousUtils.getSupraFile */
	String fsep = System.getProperty("file.separator");

	// String bdir = System.getProperty("user.dir");

	private SupraSphereFrame sF;

	private boolean isReusable = false;

	private String location = null;

	private String executeAfter = null;

	private Document doc = null;

	private String extraInfo;

	@SuppressWarnings("unused")
	private Document rootDoc = null;

	private Hashtable session = null;

	public ByteRouterClient(SupraSphereFrame sF, Hashtable session,
			String clientSession, String routerName, DataInputStream cdatain,
			DataOutputStream cdataout, String senderOrReceiver, Document doc,
			Document rootDoc, String extraInfo) {

		logger.info("construct?");
		this.cdatain = cdatain;
		this.extraInfo = extraInfo;
		this.cdataout = cdataout;
		this.routerName = routerName;
		this.clientSession = clientSession;
		this.sF = sF;
		this.doc = doc;
		this.rootDoc = rootDoc;
		setLocation(doc);
		this.session = session;

		logger.info("ROUTER NAME: " + routerName + "sendor receiver : "
				+ senderOrReceiver + " extrainfo : " + extraInfo);

		start();

		// byteRouters.put(routerName+","+clientSession,this);

		if (senderOrReceiver.equals("sender") && extraInfo != null) {

			logger.info("Started write bytes to endpoint.....will use this!: "
					+ senderOrReceiver + " : " + routerName + " : "
					+ clientSession);

			if (!extraInfo.equals("transferOnly")) {
				writeInitBytesToEndpoint();
				writeBytesToEndpoint(doc, rootDoc);
			} else if (extraInfo.equals("transferOnly")) {

			}

		} else {

			if (senderOrReceiver.equals("sender")) {

				extraInfo = "none";

				if (!extraInfo.equals("transferOnly")) {
					writeInitBytesToEndpoint();
					writeBytesToEndpoint(doc, rootDoc);

				} else {

				}

			}

			logger.info("okay thats why");
		}

	}

	public ByteRouterClient(SupraSphereFrame sF, Hashtable session,
			Hashtable bootStrapInfo, String clientSession, String routerName,
			DataInputStream cdatain, DataOutputStream cdataout,
			String senderOrReceiver, Document doc, Document rootDoc,
			String extraInfo) {

		logger
				.debug("start ByteRouterClient(SupraSphereFrame sF, Hashtable session, "
						+ " Hashtable bootStrapInfo, String clientSession, String routerName, "
						+ " DataInputStream cdatain, DataOutputStream cdataout, "
						+ " String senderOrReceiver, Document doc,Document rootDoc,String extraInfo)");
		this.cdatain = cdatain;
		this.extraInfo = extraInfo;
		this.cdataout = cdataout;
		this.routerName = routerName;
		this.clientSession = clientSession;
		this.sF = sF;
		this.doc = doc;
		this.rootDoc = rootDoc;
		setLocation(doc);
		this.session = session;

		logger.info("ROUTER NAME: " + routerName + "sendor receiver : "
				+ senderOrReceiver + " extrainfo : " + extraInfo);

		start();

		// byteRouters.put(routerName+","+clientSession,this);

		String fullFileName = (String) bootStrapInfo.get("fullFileName");
		String preDir = (String) bootStrapInfo.get("preDir");
		String fileOnly = (String) bootStrapInfo.get("fileOnly");

		if (senderOrReceiver.equals("sender") && extraInfo != null) {

			logger.info("Started write bytes to endpoint.....will use this!: "
					+ senderOrReceiver + " : " + routerName + " : "
					+ clientSession);
			writeInitBytesToEndpoint();
			if (!extraInfo.equals("transferOnly")) {
				writeBytesToEndpoint(doc, rootDoc);
			} else if (extraInfo.equals("transferOnly")) {

				writeFileChangedBytesToEndpoint(doc, fullFileName, preDir,
						fileOnly);
			}

		} else {

			if (senderOrReceiver.equals("sender")) {

				extraInfo = "none";
				writeInitBytesToEndpoint();
				if (!extraInfo.equals("transferOnly")) {
					writeBytesToEndpoint(doc, rootDoc);
					writeBytesToEndpoint(doc, rootDoc);
				} else if (extraInfo.equals("transferOnly")) {

					writeFileChangedBytesToEndpoint(doc, fullFileName, preDir,
							fileOnly);

				}

			}

			logger.debug("okay thats why");
		}

	}

	public void setLocation(Document doc) {

		this.location = doc.getRootElement().element("physical_location")
				.attributeValue("value");

	}

	public String getLocation() {

		return this.location;

	}

	public void writeInitBytesToEndpoint() {

		logger.info("starting write init bytes");
		try {
			this.cdataout.writeUTF("start");

			this.cdataout.writeUTF(this.routerName);
			this.cdataout.writeUTF(this.clientSession);
		} catch (IOException ex) {
			logger.error(ex);
		}
		logger.info("init bytes already sent");

	}

	public void writeBytesToEndpoint(final Document doc, final Document rootDoc) {

		logger.info("Starting write bytes to endpoint in client...."
				+ doc.asXML());
		if (rootDoc == null) {

			logger.info("root doc is null");
		} else {
			logger.info("root doc is NOT null");

		}

		Thread t = new Thread() {
			public void run() {
				writeBytesToEndpointBody(doc, rootDoc);
			}
		};
		t.start();

	}

	public void writeFileChangedBytesToEndpoint(Document rootDoc,
			String fullFileName, String dir, String fileOnly) {

		this.isReusable = false;
		logger.info("starting writefilechangedbytes");

		try {

			logger.info("writing cdataout int: " + fullFileName);

			this.cdataout.writeInt(-400);
			logger.info("wrote int...now write dir: " + dir);
			this.cdataout.writeUTF(dir);

			this.cdataout.writeUTF(fileOnly);

			FileInputStream fin = new FileInputStream(fullFileName);

			int inBytes = fin.available();

			this.cdataout.writeInt(inBytes);

			logger.info("This many bytes available!!: " + inBytes);

			byte[] buff = new byte[4096];

			int bytesread = 0;

			int bytestotal = 0;

			while (true) {

				bytesread = fin.read(buff);

				if (bytesread == -1) {
					logger.info("SENT bytetotal!!: " + bytestotal);
					break;
				}
				this.cdataout.write(buff, 0, bytesread);

				bytestotal += bytesread;

			}
			logger.info("STOPPED WRITING FILE!!!!:");
			this.extraInfo = null;

		} catch (IOException ioe) {
		}

		this.isReusable = true;

	}

	public String getDifferentialDirectory(String basePath, String filePath,
			String filename) {

		// String fsep = System.getProperty("file.separator");

		// int pos = filePath.lastIndexOf(basePath);
		// int fsepPos = filePath.lastIndexOf(fsep);

		// logger.info("fsep: "+fsep);
		// logger.info("fseppos: "+fsepPos);
		// logger.info("base: "+basePath+ " : "+filePath+ " : "+pos);

		String remainder = filePath.substring(basePath.length(), filePath
				.length());
		return remainder;

	}

	public void startKeepAliveThread() {

		Thread t = new Thread() {
			public void run() {
				try {
					sleep(10000);
				} catch (InterruptedException ex) {
					logger.error(ex);
				}
				if (ByteRouterClient.this.isReusable == true) {
					try {
						ByteRouterClient.this.cdataout.writeUTF("keepAlive");
					} catch (IOException ex1) {
						logger.error(ex1);
					}
				}

			}

		};

	}

	public void run() {

		while (true) {

			try {

				String start = this.cdatain.readUTF();
				if (!start.equals("keepAlive")) {

					int protocol = this.cdatain.readInt();

					logger.info("okay..protocol was!: " + protocol);

					if (protocol == -200) {

						String filename = this.cdatain.readUTF();

						logger.info("FILENAME READ!: " + filename);
						int objectSize = this.cdatain.readInt();
						logger.info("object size about to read: " + objectSize);

						File file = VariousUtils.getSupraFile("Assets"
								+ File.separator + "File", filename);

						FileOutputStream fout = new FileOutputStream(file);

						int bytesread = 0;
						int bytestotal = 0;

						while (true) {

							byte[] buff = new byte[4096];

							bytesread = this.cdatain.read(buff);

							fout.write(buff, 0, bytesread);

							bytestotal += bytesread;

							if (bytestotal == objectSize) {

								logger
										.debug("breaking in byte router client...must have gotten total bytes");

								break;
							}

						}
						fout.close();

						MethodProcessing.doExec(this.session, this.doc,
								this.sF, filename);

					} else if (protocol == -100) {

						FileMonitor fm = new FileMonitor(this.session, this.doc);
						String remoteFsep = null;
						String remoteBeginDir = null;

						int objectSize = this.cdatain.readInt();
						remoteFsep = this.cdatain.readUTF();
						remoteBeginDir = this.cdatain.readUTF();

						File dir = VariousUtils
								.getSupraFile("Assets" + File.separator
										+ "Filesystem", remoteBeginDir);

						if (!dir.exists()) {
							dir.mkdirs();
						}

						byte[] inobjectBytes = new byte[objectSize];

						inobjectBytes = new byte[objectSize];

						inobjectBytes = new byte[objectSize];
						this.cdatain.readFully(inobjectBytes);

						Vector files = new Vector();
						try {
							files = (Vector) bytesToObject(inobjectBytes);
						} catch (ClassNotFoundException ex) {
							logger.error(ex);
						}

						for (int i = 0; i < files.size(); i++) {

							String file = (String) files.get(i);

							file = convertFseps(remoteFsep, this.fsep, file);

							logger.info("After conversion: " + file);

							int dirOrFile = this.cdatain.readInt();

							if (dirOrFile == -50) {

								try {
									File f = VariousUtils
											.getSupraFile("Assets" + this.fsep
													+ "Filesystem" + this.fsep
													+ remoteBeginDir + file);

									FileOutputStream fout = new FileOutputStream(
											f);

									int fileSize = this.cdatain.readInt();
									logger.info("file size: " + fileSize);
									inobjectBytes = new byte[fileSize];
									this.cdatain.readFully(inobjectBytes);

									fout.write(inobjectBytes, 0, fileSize);
									fout.close();
									fm.addFile(f);
								} catch (FileNotFoundException ex) {
									logger.error(ex);

								}

							} else {

								String dirName = this.cdatain.readUTF();

								logger.info("DIR NAME GOT : " + dirName);

								dirName = convertFseps(remoteFsep, this.fsep,
										dirName);

								File f = VariousUtils.getSupraFile("Assets"
										+ this.fsep + "Filesystem" + this.fsep
										+ remoteBeginDir + dirName);

								if (!f.exists()) {
									f.mkdirs();
								}
								i--;
								logger.info("must create this directory..");

							}
						}

						File f = VariousUtils.getSupraFile("Assets" + this.fsep
								+ "Filesystem" + this.fsep + remoteBeginDir);

						logger
								.info("adding file listener at the end....why add it twice? "
										+ f.getPath());

						fm.addListener(fm.new TestListener(this.doc), f
								.getPath());

						try {
							/* FIXME - must sleep be hardcoded into code? */
							sleep(1500);
							logger.info("still going....nothing stops me");
						} catch (InterruptedException ex) {
							logger.error(ex);
						}

						if (this.executeAfter != null) {
							logger.info("NOW I can execute somethign!!!!"
									+ this.doc.asXML());
						} else {
							logger
									.info("You cannot execute anything because executeafter not set");
						}

					}

					try {
						sleep(1000);
						logger
								.debug("here we are here in brc.....should keep going somehow...");

					} catch (InterruptedException ex) {
						logger.error(ex);
					}

				} else {

					startKeepAliveThread();
				}
			} catch (IOException ex) {

				logger.error(ex);
				break;
			}

			this.isReusable = true;

		}

	}

	public boolean isReusable() {
		return this.isReusable;
	}

	public String convertFseps(String remoteFsep, String localFsep, String name) {
		String result = null;

		if (name.lastIndexOf(remoteFsep) == -1) {
			return name;
		} else {

			result = name.replace(remoteFsep, localFsep);
			return result;

		}

	}

	private byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(object);
		return baos.toByteArray();
	}

	/**
	 * Converts a byte array to a serializable object.
	 * 
	 * @param bytes
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @exception IOException
	 *                Description of the Exception
	 * @exception ClassNotFoundException
	 *                Description of the Exception
	 */
	private Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream is = new ObjectInputStream(bais);
		return is.readObject();
	}

	public void setReusable(boolean b) {
		this.isReusable = false;
	}

	/**
	 * @param string
	 */
	public void setExtraInfo(String string) {
		this.extraInfo = string;
	}

	/**
	 * @param doc
	 * @param rootDoc
	 */
	private void writeBytesToEndpointBody(final Document doc,
			final Document rootDoc) {
		try {

			String dirName = doc.getRootElement().element("subject")
					.attributeValue("value");
			String fileOnly = null;

			Element relativeTo = doc.getRootElement().element("relative_to");

			if (relativeTo != null) {

				String relativeName = relativeTo.attributeValue("value");
				fileOnly = dirName;
				dirName = relativeName + this.fsep + dirName;

			} else {

				logger.info("DIRNAME: " + dirName + " : " + this.fsep);
				int pos = dirName.lastIndexOf(this.fsep);

				logger.info("POS: " + pos);

				if (pos == -1) {
					pos = dirName.lastIndexOf("/");

				}
				logger.info("dirname length: " + dirName.length());

				String remain = dirName.substring(pos, dirName.length());
				fileOnly = remain;

			}

			String fname = dirName;

			File f = new File(fname);

			if (!f.isDirectory()) {

				logger.info("argh, at least that workds");

				if (this.extraInfo == null) {

					this.cdataout.writeInt(-200);

				} else {
					if (this.extraInfo.equals("mirrorToServer")) {

						logger.info("mirroring file");

						logger.info("okay, now should have root doc: "
								+ rootDoc.asXML());

						this.cdataout.writeInt(-350);

						dirName = doc.getRootElement().element("subject")
								.attributeValue("value");
						fileOnly = null;

						relativeTo = doc.getRootElement()
								.element("relative_to");
						logger.info("relative to: " + relativeTo.asXML());

						if (relativeTo != null) {

							String relativeName = relativeTo
									.attributeValue("value");
							fileOnly = dirName;
							dirName = relativeName + "/" + dirName;

						} else {

							logger.info("DIRNAME: " + dirName + " : "
									+ this.fsep);
							int pos = dirName.lastIndexOf(this.fsep);

							logger.info("POS: " + pos);

							if (pos == -1) {
								pos = dirName.lastIndexOf("/");
							}
							logger.info("dirname length: " + dirName.length());

							String remain = dirName.substring(pos, dirName
									.length());
							fileOnly = remain;

						}

					}

					else {
						this.cdataout.writeInt(-200);
					}
				}
				if (rootDoc != null) {
					String rootBaseDir = rootDoc.getRootElement().element(
							"subject").attributeValue("value");
					int lastPos = rootBaseDir.lastIndexOf("/");
					String preDir = rootBaseDir.substring(lastPos, rootBaseDir
							.length());

					String rootWithoutDir = dirName.substring(dirName
							.indexOf(rootBaseDir), dirName.length());

					String dirWithoutFile = rootWithoutDir.substring(0,
							rootWithoutDir.indexOf(fileOnly));
					logger.info("dirwithoutfile:" + dirWithoutFile + " : "
							+ rootBaseDir);

					String dirOnly = dirWithoutFile.replaceAll(rootBaseDir, "");
					logger.info("Dir only: " + dirOnly);
					logger.info("FILE ONLY AFTER: " + fileOnly + " : "
							+ dirName);
					this.cdataout.writeUTF(preDir);
					this.cdataout.writeUTF(dirOnly);
					FileMonitor fm = null;
					if (rootDoc == null) {

						fm = new FileMonitor(this.session, doc);
					} else {
						fm = new FileMonitor(this.session, rootDoc);
					}

					File forList = new File(fname);

					logger.info("adding file there: "
							+ forList.getAbsolutePath());

					fm.addListener(fm.new TestListener(rootDoc), forList
							.getPath());

					fm.addFile(forList);

				}
				this.cdataout.writeUTF(fileOnly);
				fname = dirName;

				FileInputStream fin = new FileInputStream(fname);

				int inBytes = fin.available();

				this.cdataout.writeInt(inBytes);

				logger.info("This many bytes available!!: " + inBytes);

				byte[] buff = new byte[4096];

				int bytesread = 0;

				int bytestotal = 0;

				while (true) {

					bytesread = fin.read(buff);

					if (bytesread == -1) {
						logger.info("SENT bytetotal!!: " + bytestotal);
						break;
					}
					this.cdataout.write(buff, 0, bytesread);

					bytestotal += bytesread;

				}
				logger.info("STOPPED WRITING FILE!!!!:");
				this.extraInfo = null;

			} else {

				logger.info("that does not work!!!!");

				if (this.extraInfo != null) {

					if (this.extraInfo.equals("mirrorToServer")) {

						this.cdataout.writeInt(-300);

					} else {
						this.cdataout.writeInt(-100);
					}
				} else {

					this.cdataout.writeInt(-100);
				}
				logger.info("Sent -100 from sendidrs");

				// File f = new File(bdir);
				String basePath = f.getPath();
				int lastPos = basePath.lastIndexOf(this.fsep);
				String beginDir = basePath
						.substring(lastPos, basePath.length());
				beginDir = beginDir.replace(this.fsep, "");
				logger.info("BEGING DIR: " + beginDir);

				this.cdataout.writeUTF(this.fsep);
				this.cdataout.writeUTF(beginDir);

				logger.info("Sent fsep! : " + this.fsep);

				RecursiveList rl = new RecursiveList();
				FileMonitor monitor = null;
				if (rootDoc == null) {
					monitor = new FileMonitor(this.session, doc);
				} else {
					monitor = new FileMonitor(this.session, rootDoc);
				}

				logger.info("When sending....will add listener now! "
						+ basePath);

				monitor
						.addListener(monitor.new TestListener(rootDoc),
								basePath);

				rl.listDirContents(rootDoc, f, monitor);

				Vector allFiles = monitor.getCurrentFiles();
				Vector fileNames = monitor.getFileNames(basePath);

				logger.info("Size of files!: " + allFiles.size());

				byte[] objectBytes = objectToBytes(fileNames);

				this.cdataout.writeInt(objectBytes.length);
				this.cdataout.write(objectBytes, 0, objectBytes.length);

				Hashtable<String, String> sent = new Hashtable<String, String>();

				for (int i = 0; i < allFiles.size(); i++) {

					File oneFile = (File) allFiles.get(i);

					if (!oneFile.isDirectory()) {

						String dirPath = getDifferentialDirectory(basePath,
								oneFile.getPath(), oneFile.getName());
						String dirOnly = dirPath.substring(0, dirPath.length()
								- oneFile.getName().length());
						if (dirOnly.length() > 1) {

							if (dirOnly.endsWith(this.fsep)) {
								dirOnly = dirOnly.substring(0,
										dirOnly.length() - 1);
							}

							if (!sent.containsKey(dirOnly)) {

								sent.put(dirOnly, dirOnly);

								this.cdataout.writeInt(-150);
								this.cdataout.writeUTF(dirOnly);

							}
						}

						FileInputStream fin = new FileInputStream(oneFile);
						int inBytes = fin.available();

						this.cdataout.writeInt(-50);
						this.cdataout.writeInt(inBytes);

						byte[] inobjectBytes = new byte[inBytes];
						fin.read(inobjectBytes, 0, inBytes);
						this.cdataout.write(inobjectBytes);

					} else {

					}

				}
				this.extraInfo = null;
			}

		} catch (IOException ex) {

			logger.error(ex);

		}

		this.isReusable = true;
	}

}
