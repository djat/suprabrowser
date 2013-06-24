/*
 * Created on Mar 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import ss.common.DmpFilter;
import ss.common.GenericXMLDocument;
import ss.common.TimeLogWriter;
import ss.common.VerifyAuth;
import ss.common.build.AntBuilder;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.refactor.supraspheredoc.old.EntitleContactForSphere;
import ss.rss.RSSParser;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.util.Expression;
import ss.server.networking.util.Filter;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.util.CheckFileExtension;
import ss.util.DnldURL;
import ss.util.XMLSchemaTransform;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * 
 */
public class MethodProcessing {

	static String fsep = System.getProperty("file.separator");

	static String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MethodProcessing.class);

	public MethodProcessing() {
	}

	public static void main(String[] args) {
		logger.info("Startgin: \n" + MethodProcessing.getInviteTextFromFile());
	}

	public static String getInviteTextFromFile() {

		File f = new File(bdir + fsep + "invite.txt");
		String text = null;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(f));

			String line = null;

			text = "";
			int i = 0;
			while (true) {
				try {
					line = buff.readLine();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				if (i == 0 && line != null) {
					text = line;

				}

				else if (line != null) {
					text = text + line + "\n";

				} else {
					break;
				}
				i++;
			}
		} catch (FileNotFoundException ex) {
			logger.error(ex);
		}

		return text;

	}

	public static String getInviteTextFromXMLFile() {

		String body = null;
		File file = new File(System.getProperty("user.dir") + fsep
				+ "invite.xml");

		SAXReader reader = new SAXReader();

		try {

			Document doc = reader.read(file);

			String downloadURL = doc.getRootElement().element("downloadSSURL")
					.attributeValue("value");
			body = doc.getRootElement().element("body").getText();

			body = body.replace("$downloadSSURL", downloadURL);

		} catch (Exception ex) {

		}

		return body;

	}

	public static void startBuild(final Hashtable session, final Document doc,
			final String cliSerServant, final boolean exit) {

		Thread t = new Thread() {
			public void run() {

				AntBuilder build = new AntBuilder();

				String backwards = doc.getRootElement().element(
						"file_separator").attributeValue("value");
				String fullPath = doc.getRootElement().element("subject")
						.attributeValue("value");
				String remoteFsep = null;

				remoteFsep = "/";

				int pos = fullPath.lastIndexOf(remoteFsep);

				logger.info("backwards: " + backwards);
				logger.info("rse: " + remoteFsep);
				logger.info("POS: " + pos);
				logger.info("fullpath : " + fullPath);

				String remain = fullPath.substring(pos, fullPath.length());
				remain = remain.replace(remoteFsep, "");

				logger.info("remain in antbuild in server: " + remain);

				File f = new File(bdir + fsep + "roots" + fsep
						+ (String) session.get("supra_sphere") + fsep
						+ "Assets" + fsep + "Filesystem" + fsep + remain);

				build.setSrcDir(f.getAbsolutePath());
				int port = new Integer((String) session.get("port")).intValue();
				port = port + 1;
				build.setPort(new Integer(port).toString());

				if (exit == true) {
					logger.info("start build with exit true");
					build.startBuild(cliSerServant, true, true);
				} else {
					logger.info("start build with exit false");
					build.startBuild(cliSerServant, true, false);
				}

			}

		};
		t.start();
	}


	public Document convertBookmarkRSS(Document doc) {
		boolean isRss = false;

		final String url = doc.getRootElement().element("address")
				.attributeValue("value");

		SyndFeed feed = RSSParser.checkForRSS(url);

		if (feed != null) {
			logger.info("its definitely a feed!!");
			isRss = true;
		}
		if (isRss == true) {

			doc.getRootElement().element("type").addAttribute("value", "rss");
			doc.getRootElement().element("thread_type").addAttribute("value",
					"rss");

		}
		return doc;

	}

	public void createDocAndSend(Hashtable session,
			XMLDB xmldb, final Iterable<DialogsMainPeer> handlers, String oneUrl) {

		// if (i == 0) {

		logger.warn("Checking one: " + oneUrl);

		// SyndEntry entry =
		// (SyndEntry)items.get(i);

		// logger.info("Entry
		// Title:
		// "+entry.getTitle());
		String title = RSSParser.getTitleFromURL(oneUrl);

		String subjectText = title;
		String bodyText = null;

		bodyText = "No body available for this item";

		GenericXMLDocument genericDoc = new GenericXMLDocument(session);

		Document createDoc = genericDoc.XMLDoc(subjectText, bodyText);

		createDoc.getRootElement().element("type").addAttribute("value", "rss");

		createDoc.getRootElement().addElement("address").addAttribute("value",
				oneUrl);
		createDoc.getRootElement().addElement("current_sphere").addAttribute(
				"value", (String) session.get("sphere_id"));

		Document exists = xmldb.checkIfSeenRSS((String) session
				.get("sphere_id"), subjectText);

		if (exists == null) {

			xmldb.insertDoc(createDoc, (String) session.get("sphere_id"));
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue("protocol", "update");
			dmpResponse.setDocumentValue("document", createDoc);
			dmpResponse.setStringValue("sphere", (String) session
					.get("sphere_id"));

			String sphereId = (String) session.get("sphere_id");
			for (DialogsMainPeer handler : DmpFilter.filter(handlers,sphereId)) {
				handler.sendFromQueue(dmpResponse);
			}

		} else {

			logger.info("Already exists! : " + oneUrl);

			xmldb.useDoc(exists, (String) session.get("sphere_id"),
					(String) session.get("real_name"), "100");
			createDoc = exists;

		}
	}

	public Document processPublishedBookmark(final Hashtable session,
			final Document doc, final Iterable<DialogsMainPeer> handlers,
			final XMLDB xmldb) {

		final String url = doc.getRootElement().element("address")
				.attributeValue("value");

		if (!CheckFileExtension.isKnownFile(url)) {

			logger.info("its not known as a file type");

			DnldURL.INSTANCE.download(url, doc, false, xmldb);

			try {

				if (RSSParser.checkForRSS(url) != null) {
					logger.info("its definitely a feed!!");

				} else {

					Vector items = RSSParser.findRSSURL(url);

					logger.info("its not a feed....checkf or rss urls: "
							+ items.size());

					for (int i = 0; i < items.size(); i++) {

						String one = (String) items.get(i);

						logger.info("ONE rss: " + one);
						createDocAndSend(session, xmldb, handlers, one);

					}

					Vector items2 = RSSParser.findAllRSSLinks(url);

					logger.info("its not a feed....checkf or rss urls: "
							+ items2.size());
					for (int i = 0; i < items2.size(); i++) {

						String one = (String) items2.get(i);

						createDocAndSend(session, xmldb, handlers, one);

					}

				}

			} catch (Exception ex) {
			}

		} else {
			// Do nothing for now, was loading of files to server.
//			Thread t = new Thread() {
//				public void run() {
//
//					DnldURL dl = new DnldURL();
//
//					Hashtable result = dl.getAsFile(session, url);
//
//					GenericXMLDocument genericDoc = new GenericXMLDocument(
//							session);
//
//					Document createDoc = genericDoc.XMLDoc((String) result
//							.get("subject"), "", true);
//
//					createDoc.getRootElement().addElement("response_id")
//							.addAttribute(
//									"value",
//									doc.getRootElement().element("message_id")
//											.attributeValue("value"));
//
//					createDoc.getRootElement().addElement("type").addAttribute(
//							"value", "file");
//					createDoc.getRootElement().addElement("thread_type")
//							.addAttribute("value", "bookmark");
//
//					createDoc.getRootElement().addElement("current_sphere")
//							.addAttribute("value",
//									(String) session.get("sphere_id"));
//					String fname = (String) result.get("name");
//
//					createDoc
//							.getRootElement()
//							.addElement("bytes")
//							.addAttribute("value", (String) result.get("bytes"));
//					createDoc.getRootElement().addElement("original_data_id")
//							.addAttribute("value", fname);
//					createDoc.getRootElement().addElement("data_id")
//							.addAttribute("value", fname);
//
//					xmldb.insertDoc(createDoc, (String) session
//							.get("sphere_id"));
//					final DmpResponse dmpResponse = new DmpResponse();
//					dmpResponse.setStringValue("protocol", "update");
//					dmpResponse.setDocumentValue("document", createDoc);
//					dmpResponse.setStringValue("sphere", (String) session
//							.get("sphere_id"));
//
//					for (DialogsMainPeer handler : handlers) {
//
//						String contact = handler.get(HandlerKey.USERNAME);
//
//						String sphere_id = (String) session.get("sphere_id");
//						String apath = DialogsMainPeer.createApath(contact,
//								sphere_id);
//
//						if (handler.getVerifyAuth().check(apath)) {
//							handler.sendFromQueue(dmpResponse);
//						}
//					}
//
//				}
//			};
//			t.start();

		}

		return doc;

	}

}
