package ss.server.networking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import org.dom4j.Document;

public class ByteRouter extends Thread {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ByteRouter.class);

	private static Hashtable byteRouters = new Hashtable();

	private Hashtable session = null;

	private DataInputStream cdatain = null;

	private DataOutputStream cdataout = null;

	private String routerName = null;

	private String clientSession = null;

	private String bdir = System.getProperty("user.dir");

	private String fsep = System.getProperty("file.separator");

	private String extraInfo;

	@SuppressWarnings("unchecked")
	public ByteRouter(Hashtable session, String clientSession,
			String routerName, DataInputStream cdatain,
			DataOutputStream cdataout, String senderOrReceiver, Document doc,
			String extraInfo) {

		this.cdatain = cdatain;
		this.cdataout = cdataout;
		this.routerName = routerName;
		this.clientSession = clientSession;
		this.session = session;
		this.extraInfo = extraInfo;

		logger.info("ROUTER NAME: " + routerName + "sendor receiver : "
				+ senderOrReceiver);

		start();

		String unique = getUniqueId();
		byteRouters.put(routerName + "," + clientSession + "," + unique, this);

		routerOrder.add(unique);
	}

	private static Vector routerOrder = new Vector();

	private static Random tableIdGenerator = new Random();

	public synchronized String getUniqueId() {
		return new Long(Math.abs(tableIdGenerator.nextLong())).toString();
	}

	public void run() {

		String sendingClientSession = null;

		while (true) {
			ByteRouter brRecipient = null;

			try {

				String getStart = this.cdatain.readUTF();

				if (!getStart.equals("keepAlive")) {

					logger
							.info("start sent...should indicate if sender or receiver...next get "
									+ getStart);

					String getByteRouterName = this.cdatain.readUTF();
					sendingClientSession = this.cdatain.readUTF();

					logger.info("byteroutersize...: " + byteRouters.size());

					logger.info("extraINFO in byte router..." + this.extraInfo);

					boolean checkForByteRouter = true;
					if (this.extraInfo != null) {
						if (this.extraInfo.equals("mirrorToServer")
								|| this.extraInfo.equals("transferOnly")) {

							checkForByteRouter = false;
						}

					}
					if (checkForByteRouter) {

						while (brRecipient == null) {

							brRecipient = getLatestByteRouter(
									getByteRouterName, sendingClientSession);// getByteRouterFor(getByteRouterName,sendingClientSession);

							try {
								logger.info("it was null..."
										+ byteRouters.size()
										+ getByteRouterName + " : "
										+ this.clientSession + " : "
										+ this.routerName);

								sleep(500);

							} catch (InterruptedException ex) {
								logger.error(ex);
							}

						}
					}

					int protocol = 0;
					try {

						protocol = this.cdatain.readInt();
					} catch (Exception ex) {
						logger.error(ex);
					}
					logger.info("in byte router...protocol!: " + protocol);

					// Match this with client
					if (protocol == -200) {
						logger.info("starting 200");

						String fileName = this.cdatain.readUTF();
						logger.info("got filename: " + fileName);

						int objectSize = this.cdatain.readInt();
						logger.info("got objectsize: " + objectSize);

						while (brRecipient == null) {

							brRecipient = getLatestByteRouter(
									getByteRouterName, sendingClientSession);// getByteRouterFor(getByteRouterName,sendingClientSession);

							try {

								sleep(500);
								logger.info("still null");

							} catch (InterruptedException ex) {
								logger.error(ex);
							}

						}
						brRecipient.cdataout.writeUTF("start");
						brRecipient.cdataout.writeInt(protocol);

						brRecipient.cdataout.writeUTF(fileName);

						brRecipient.cdataout.writeInt(objectSize);

						int bytesread = 0;
						int bytestotal = 0;

						while (true) {

							byte[] buff = new byte[4096];
							bytesread = this.cdatain.read(buff);
							bytestotal += bytesread;
							if (bytestotal == objectSize) {

								logger.info("BREAKING IN BYTE ROUTER ITSELF");
								brRecipient.cdataout.write(buff, 0, bytesread);
								break;
							}

							brRecipient.cdataout.write(buff, 0, bytesread);

						}

						logger.info("ending 200");

					} else if (protocol == -300) {

						logger.info("got -300 ");
						String remoteFsep = null;
						String remoteBeginDir = null;

						remoteFsep = this.cdatain.readUTF();
						remoteBeginDir = this.cdatain.readUTF();

						File dir = new File(this.bdir + this.fsep + "roots"
								+ this.fsep
								+ (String) this.session.get("supra_sphere")
								+ this.fsep + "Assets" + this.fsep
								+ "Filesystem" + this.fsep + remoteBeginDir);

						if (!dir.exists()) {
							dir.mkdir();
						}

						int objectSize = this.cdatain.readInt();

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
									File f = null;

									logger.info("FILE STARTING NOW: " + file);
									f = new File(this.bdir
											+ this.fsep
											+ "roots"
											+ this.fsep
											+ (String) this.session
													.get("supra_sphere")
											+ this.fsep + "Assets" + this.fsep
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

								} catch (FileNotFoundException ex) {
									logger.error(ex);

								}

							} else {

								String dirName = this.cdatain.readUTF();

								logger.info("DIR NAME GOT : " + dirName);
								dirName = convertFseps(remoteFsep, this.fsep,
										dirName);

								File f = new File(this.bdir
										+ "roots"
										+ this.fsep
										+ (String) this.session
												.get("supra_sphere")
										+ this.fsep + "Assets" + this.fsep
										+ "Filesystem" + this.fsep
										+ remoteBeginDir + dirName);
								if (!f.exists()) {

									makeSubDirs(remoteBeginDir, dirName);
								}
								i--;
								logger.info("must create this directory..");

							}

						}

						logger.info("ending 300");
					} else if (protocol == -350) {
						logger.info("starting 350");

						String preDir = this.cdatain.readUTF();
						String dirOnly = this.cdatain.readUTF();
						String fileOnly = this.cdatain.readUTF();

						dirOnly = this.convertFseps("/", this.fsep, dirOnly);

						this.makeSubDirs(preDir, dirOnly);

						int objectSize = this.cdatain.readInt();
						File dir = new File(this.bdir + this.fsep + "roots"
								+ this.fsep
								+ (String) this.session.get("supra_sphere")
								+ this.fsep + "Assets" + this.fsep
								+ "Filesystem" + this.fsep + preDir + dirOnly
								+ fileOnly);

						FileOutputStream fout = new FileOutputStream(dir);

						int bytesread = 0;
						int bytestotal = 0;

						while (true) {

							byte[] buff = new byte[4096];

							bytesread = this.cdatain.read(buff);

							fout.write(buff, 0, bytesread);

							bytestotal += bytesread;

							if (bytestotal == objectSize) {

								logger
										.info("breaking in byte router SERVER...must have gotten total bytes");

								break;
							}

						}
						fout.close();
						logger.info("ending the 350");

					} else if (protocol == -400) {

						logger.info("okay, now start whatever for -400");

						String dirName = this.cdatain.readUTF();
						String file = this.cdatain.readUTF();
						logger.info("now just get the file..." + dirName
								+ " : " + file);

						int objectSize = this.cdatain.readInt();
						File dir = new File(this.bdir + this.fsep + "roots"
								+ this.fsep
								+ (String) this.session.get("supra_sphere")
								+ this.fsep + "Assets" + this.fsep
								+ "Filesystem" + dirName + this.fsep + file);
						logger.info("dir to save as: " + dir.getAbsolutePath());

						FileOutputStream fout = new FileOutputStream(dir);

						int bytesread = 0;
						int bytestotal = 0;

						while (true) {

							byte[] buff = new byte[4096];

							bytesread = this.cdatain.read(buff);

							fout.write(buff, 0, bytesread);

							bytestotal += bytesread;

							if (bytestotal == objectSize) {

								logger
										.info("breaking in byte router SERVER...must have gotten total bytes");

								break;
							}

						}

						fout.close();
						logger.info("ending 400");
					}

					else {

						logger.info("starting final else");
						byte[] inobjectBytes = null;

						brRecipient.cdataout.writeUTF("start");
						brRecipient.cdataout.writeInt(-100);

						String fsep = this.cdatain.readUTF();
						String beginDir = this.cdatain.readUTF();

						int filesObjectSize = this.cdatain.readInt();
						logger.info("got file object size; " + filesObjectSize);

						inobjectBytes = new byte[filesObjectSize];
						brRecipient.cdataout.writeInt(filesObjectSize);
						brRecipient.cdataout.writeUTF(fsep);
						brRecipient.cdataout.writeUTF(beginDir);

						this.cdatain.readFully(inobjectBytes);
						brRecipient.cdataout.write(inobjectBytes);

						while (true) {

							logger
									.info("iterating once on file send part of byte router");

							try {

								int type = this.cdatain.readInt();

								logger.info("Got type..." + type);

								if (type == -50) {
									brRecipient.cdataout.writeInt(type);
									int size = this.cdatain.readInt();

									logger.info("size here then" + size);
									brRecipient.cdataout.writeInt(size);

									inobjectBytes = new byte[size];
									this.cdatain.readFully(inobjectBytes);
									brRecipient.cdataout.write(inobjectBytes);

								} else if (type == -500) {

									logger
											.info("GOT END OF GET ENTIRE FILESYSTEM");

									break;

								} else {

									logger
											.info("do something else....write UTF PERHAPS!!: "
													+ type);

									String name = this.cdatain.readUTF();

									brRecipient.cdataout.writeInt(-150);
									brRecipient.cdataout.writeUTF(name);
									logger
											.info("name!!!!! should be directory...figure out relativism: "
													+ name);

								}

							} catch (EOFException e) {

								logger.error(e.getMessage(), e);
								break;
							}

						}
						logger.info("ending final else");

					}

				} else {

					if (brRecipient != null) {

						brRecipient.cdataout.writeUTF("keepAlive");
					} else {
						if (this.cdataout != null) {
							this.cdataout.writeUTF("keepAlive");
						}
					}

				}
			} catch (EOFException ex) {
				logger.error(ex);
				removeByteRouter(this.routerName, this.clientSession);
				removeByteRouter(this.routerName, sendingClientSession);

				break;

			} catch (IOException ex) {
				removeByteRouter(this.routerName, this.clientSession);
				removeByteRouter(this.routerName, sendingClientSession);

				break;

			}

		}

	}

	private void removeByteRouter(String getByteRouterName, String clientSession) {

		logger.info("Calling remove byte router");

		for (Enumeration enumer = byteRouters.keys(); enumer.hasMoreElements();) {

			String name = (String) enumer.nextElement();
			if (name.lastIndexOf(getByteRouterName) != -1) {
				byteRouters.remove(name);

			}

		}
	}

	private ByteRouter getLatestByteRouter(String getByteRouterName,
			String clientSession) {
		ByteRouter toReturn = null;
		synchronized (routerOrder) {
			for (int i = routerOrder.size() - 1; i >= 0; i--) {
				String order = (String) routerOrder.get(i);

				for (Enumeration enumer = byteRouters.keys(); enumer
						.hasMoreElements();) {

					String name = (String) enumer.nextElement();
					if (name.lastIndexOf(getByteRouterName) != -1
							&& name.lastIndexOf(order) != -1) {
						if (name.lastIndexOf(clientSession) == -1) {
							return (ByteRouter) byteRouters.get(name);
						}
					}
				}

			}
		}
		return toReturn;

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

	public String convertFseps(String remoteFsep, String localFsep, String name) {
		String result = null;

		if (name.lastIndexOf(remoteFsep) == -1) {
			return name;
		} else {

			result = name.replace(remoteFsep, localFsep);
			return result;

		}

	}

	public void makeSubDirs(String remoteBeginDir, String dir) {

		logger.info("in make this dir");
		StringTokenizer st = new StringTokenizer(dir, this.fsep);
		String last = "";

		File dirFile = new File(this.bdir + this.fsep + "roots" + this.fsep
				+ (String) this.session.get("supra_sphere") + this.fsep
				+ "Assets" + this.fsep + "Filesystem" + this.fsep
				+ remoteBeginDir);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		while (st.hasMoreTokens()) {

			String begin = st.nextToken();
			last = last + this.fsep + begin;
			logger.info("last:: " + last);

			File f = new File(this.bdir + this.fsep + "roots" + this.fsep
					+ (String) this.session.get("supra_sphere") + this.fsep
					+ "Assets" + this.fsep + "Filesystem" + this.fsep
					+ remoteBeginDir + last);

			if (!f.exists()) {
				f.mkdir();
			}

		}

	}

}
