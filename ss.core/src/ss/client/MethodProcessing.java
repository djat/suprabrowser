/*
 * Created on Jun 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

// This is an overflow of method processing in different classes
package ss.client;

import java.io.File;
import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;
import org.dom4j.Document;

import ss.client.networking.ByteRouterClient;
import ss.client.networking.SupraClient;
import ss.client.ui.SupraSphereFrame;
import ss.common.FileMonitor;
import ss.common.OsUtils;
import ss.common.build.AntBuilder;

public class MethodProcessing {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MethodProcessing.class);

	static String fsep = System.getProperty("file.separator");

	static String bdir = System.getProperty("user.dir");	

	public static void startBuild(final Hashtable session, final Document doc,
			final SupraSphereFrame sF, final String cliSerServant,
			final String restart) {

		Thread t = new Thread() {
			public void run() {
 
				AntBuilder build = new AntBuilder();
				build.setSrcDir(doc.getRootElement().element("subject")
						.attributeValue("value"));
				int port = new Integer((String) session.get("port")).intValue();
				port = port + 1;
				build.setPort(new Integer(port).toString());
				if (restart.equals("true")) {
					build.setSF(sF);
					build.startBuild(cliSerServant, true, true);

				} else {
					build.setSF(sF);
					build.startBuild(cliSerServant, true, false);
				}

			}

		};
		t.start();
	}

	@SuppressWarnings("unchecked")
	public static void initByteRouter(Hashtable session, Document doc,
			Document rootDoc, SupraSphereFrame sF, String extraInfo) {

		Hashtable bootStrapInfo = new Hashtable();
		if (extraInfo != null) {
			logger.info("putting bootstrapinfo extrainfo: " + extraInfo);
			bootStrapInfo.put("extraInfo", extraInfo);
		}
		Hashtable brcSession = sF.getRegisteredSession((String) session
				.get("supra_sphere"), "ByteRouterClient");
		ByteRouterClient br = null;
		if (brcSession != null) {
			br = sF.getActiveByteRouters().getLatestByteRouter(doc);// getActiveByteRouter((String)brcSession.get("session"));
		}

		if (extraInfo == null) {
			extraInfo = "none";
		}
		if (!extraInfo.equals("none")) {

			if (extraInfo.equals("mirrorToServer")) {

				if (br == null) {

					final Hashtable sendSession = (Hashtable) session.clone();

					bootStrapInfo.put("doc", doc);
					bootStrapInfo.put("rootDoc", rootDoc);
					bootStrapInfo.put("senderOrReceiver", "sender");
					sendSession.put("bootStrapInfo", bootStrapInfo);

					SupraClient sClient = new SupraClient((String) sendSession
							.get("address"), (String) sendSession.get("port"));

					sClient.setSupraSphereFrame(sF);
					sendSession.put("passphrase", sF.getTempPasswords()
							.getTempPW(((String) session.get("supra_sphere"))));
					sClient.startZeroKnowledgeAuth(sendSession,
							"StartByteRouter");

				} else {

					logger
							.info("see if you can use the existing byte router...will be hard...may have to change it");

					if (br.isReusable()) {

						br.setReusable(false);
						br.setExtraInfo("mirrorToServer");

						bootStrapInfo.put("doc", doc);
						bootStrapInfo.put("rootDoc", rootDoc);
						bootStrapInfo.put("senderOrReceiver", "sender");
						session.put("bootStrapInfo", bootStrapInfo);
						br.writeInitBytesToEndpoint();
						br.writeBytesToEndpoint(doc, rootDoc);

					} else {

						final Hashtable sendSession = (Hashtable) session
								.clone();

						bootStrapInfo.put("doc", doc);
						bootStrapInfo.put("rootDoc", rootDoc);
						bootStrapInfo.put("senderOrReceiver", "sender");
						sendSession.put("bootStrapInfo", bootStrapInfo);

						SupraClient sClient = new SupraClient(
								(String) sendSession.get("address"),
								(String) sendSession.get("port"));

						sClient.setSupraSphereFrame(sF);
						sendSession
								.put("passphrase", sF.getTempPasswords()
										.getTempPW(
												((String) session
														.get("supra_sphere"))));
						sClient.startZeroKnowledgeAuth(sendSession,
								"StartByteRouter");

					}

				}

			}

		} else {
			if (br == null) {

				logger.info("byte router was null");

				sF.client.sendByteRouterInit(session, doc);

				final Hashtable sendSession = (Hashtable) session.clone();

				bootStrapInfo.put("doc", doc);
				bootStrapInfo.put("senderOrReceiver", "receiver");
				sendSession.put("bootStrapInfo", bootStrapInfo);

				SupraClient sClient = new SupraClient((String) sendSession
						.get("address"), (String) sendSession.get("port"));

				sClient.setSupraSphereFrame(sF);
				sendSession.put("passphrase", sF.getTempPasswords().getTempPW(
						((String) session.get("supra_sphere"))));
				sClient.startZeroKnowledgeAuth(sendSession, "StartByteRouter");

			} else {

				logger
						.info("see if you can use the existing byte router...will be hard...may have to change it");

				if (br.isReusable()) {

					br.setReusable(false);

					bootStrapInfo.put("doc", doc);
					bootStrapInfo.put("senderOrReceiver", "receiver");
					session.put("bootStrapInfo", bootStrapInfo);
					sF.client.sendByteRouterInit(session, doc);

				} else {

					sF.client.sendByteRouterInit(session, doc);
					final Hashtable sendSession = (Hashtable) session.clone();

					bootStrapInfo.put("doc", doc);
					bootStrapInfo.put("senderOrReceiver", "receiver");
					sendSession.put("bootStrapInfo", bootStrapInfo);

					SupraClient sClient = new SupraClient((String) sendSession
							.get("address"), (String) sendSession.get("port"));

					sClient.setSupraSphereFrame(sF);
					sendSession.put("passphrase", sF.getTempPasswords()
							.getTempPW(((String) session.get("supra_sphere"))));
					sClient.startZeroKnowledgeAuth(sendSession,
							"StartByteRouter");

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void initBRForTransferOnly(Hashtable session,
			Document rootDoc, String fullFileName, String preDir,
			String fileOnly, SupraSphereFrame sF, String extraInfo) {

		logger.info("initbr for transfer only: " + extraInfo + " : "
				+ fullFileName + " : " + preDir + " : " + fileOnly);

		Hashtable bootStrapInfo = new Hashtable();

		if (extraInfo != null) {
			logger.info("putting bootstrapinfo extrainfo: " + extraInfo);
			bootStrapInfo.put("extraInfo", extraInfo);
		}

		logger.info("putting bootstrapinfo extrainfo: " + extraInfo);
		bootStrapInfo.put("extraInfo", extraInfo);
		bootStrapInfo.put("preDir", preDir);
		bootStrapInfo.put("fileOnly", fileOnly);
		bootStrapInfo.put("fullFileName", fullFileName);

		Hashtable brcSession = sF.getRegisteredSession((String) session
				.get("supra_sphere"), "ByteRouterClient");
		ByteRouterClient br = null;
		if (brcSession != null) {
			br = sF.getActiveByteRouters().getLatestByteRouter(rootDoc);// getActiveByteRouter((String)brcSession.get("session"));
		}

		if (br == null) {

			logger.info("byte router was null");

			sF.client.sendByteRouterInit(session, rootDoc);

			final Hashtable sendSession = (Hashtable) session.clone();

			bootStrapInfo.put("doc", rootDoc);
			bootStrapInfo.put("senderOrReceiver", "receiver");
			sendSession.put("bootStrapInfo", bootStrapInfo);

			SupraClient sClient = new SupraClient((String) sendSession
					.get("address"), (String) sendSession.get("port"));

			sClient.setSupraSphereFrame(sF);
			sendSession.put("passphrase", sF.getTempPasswords().getTempPW(
					((String) session.get("supra_sphere"))));
			sClient.startZeroKnowledgeAuth(sendSession, "StartByteRouter");

		} else {

			logger
					.info("see if you can use the existing byte router...will be hard...may have to change it");

			if (br.isReusable()) {

				br.setExtraInfo("transferOnly");
				br.setReusable(false);

				bootStrapInfo.put("doc", rootDoc);
				bootStrapInfo.put("senderOrReceiver", "sender");

				session.put("bootStrapInfo", bootStrapInfo);

				// sF.client.sendByteRouterInit(session,rootDoc);

				// br.writeInitBytesToEndpoint();
				br.writeInitBytesToEndpoint();

				br.writeFileChangedBytesToEndpoint(rootDoc, fullFileName,
						preDir, fileOnly);

			} else {

				sF.client.sendByteRouterInit(session, rootDoc);
				final Hashtable sendSession = (Hashtable) session.clone();
				bootStrapInfo.put("doc", rootDoc);
				bootStrapInfo.put("senderOrReceiver", "receiver");
				sendSession.put("bootStrapInfo", bootStrapInfo);

				SupraClient sClient = new SupraClient((String) sendSession
						.get("address"), (String) sendSession.get("port"));

				sClient.setSupraSphereFrame(sF);
				sendSession.put("passphrase", sF.getTempPasswords().getTempPW(
						((String) session.get("supra_sphere"))));
				sClient.startZeroKnowledgeAuth(sendSession, "StartByteRouter");

			}
		}

	}

	public static void doExec(Hashtable session, Document doc,
			SupraSphereFrame sF, final String filename) {

		boolean music = false;

		if (filename.toLowerCase().endsWith("mp3")
				|| filename.toLowerCase().endsWith("wav")
				|| filename.toLowerCase().endsWith("ogg")) {
			music = true;
		}
		String sname = "file:///" + bdir + fsep + "Assets" + fsep + "File"
				+ fsep + filename;

		Perl5Util perl = new Perl5Util();

		final String newname = perl.substitute("s/\\s/%20/g", sname);

		if (music) {

			logger.info("it was music");

			Thread t = new Thread() {
				public void run() {

					// String sname = "file:///" + bdir + fsep + "Assets" + fsep
					// + "File" + fsep + filename;

					// /if (os.startsWith("Win")) {
					// String os = System.getProperty("os.name");
					// String execName = "";
					/*
					 * if (os.startsWith("Win")) {
					 * 
					 * execName = ("cmd /c \"start " + newname + "\"");
					 * 
					 * if (execName!=null) { try { Process p =
					 * Runtime.getRuntime().exec(execName); if (p == null) {
					 * //System.out.println("what the fuck"); } } catch
					 * (IOException ioe) { } } } else {
					 * 
					 */
					// logger.info("Starting here");
					// String newFilename = bdir + fsep +filename;
//					String newFilename = bdir + fsep + "Assets" + fsep + "File"
//							+ fsep + filename;

					// String[] args = {filename};

					

				}

			};
			t.start();

		} else {

			
			String execName = newname;

			FileMonitor fm = new FileMonitor(session, doc);

			String nameForListener = bdir + fsep + "Assets" + fsep + "File"
					+ fsep + filename;
			File f = new File(nameForListener);

			logger.info("adding file there: " + f.getAbsolutePath());

			fm.addListener(fm.new TestListener(doc), f.getPath());

			fm.addFile(f);

			OsUtils.execCommand(execName);
		}

	}

}
