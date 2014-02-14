package ss.common.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.search.SphereIndex;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.DnldURL;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
class DCI1 {

	String text = null;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DocumentConvertAndIndex.class);

	static Vector processQueue = new Vector();

	static Hashtable ids = new Hashtable();

	static String bdir = System.getProperty("user.dir");

	static String fsep = System.getProperty("file.separator");

	static Hashtable objects = new Hashtable();

	Hashtable session = null;

	public DCI1() {

	}

	public void setSession(Hashtable session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	public synchronized void addId(String filename, String messageId) {
		synchronized (ids) {

			ids.put(filename, messageId);

		}

	}

	@SuppressWarnings("unchecked")
	public synchronized void addIdToObjects(String filename, Hashtable one) {
		synchronized (objects) {

			objects.put(filename, one);

		}

	}

	public synchronized String getId(String filename) {
		synchronized (ids) {
			String id = (String) ids.get(filename);
			ids.remove(filename);
			return id;

		}

	}

	public synchronized Hashtable getAndRemoveIdFromObjects(String filename) {
		synchronized (objects) {
			Hashtable object = (Hashtable) objects.get(filename);
			objects.remove(filename);
			return object;
		}

	}

	boolean isThreadRunning = false;

	static void process(ConvertingElement element) {
		
	}
	
//	private static void processConvert(ConvertingElement element){
//		String filename = element.getName();
//		String lowerCase = filename.toLowerCase();
//		String threadId = element.getThreadId();
//		String messageId = element.getMessageId();
//		Hashtable session = element.getSession();
//		XMLDB xmldb = new XMLDB(session);
//		
//		String text;
//		
//		if (lowerCase.endsWith("htm") || lowerCase.endsWith("html")) {
//
//			DnldURL dl = new DnldURL();
//
//			dl.copyFilenameToIndexLocation(session, threadId ,
//					messageId , filename);
//
//			dl.indexOnly(messageId, threadId, xmldb);
//
//		} else if (lowerCase.endsWith("pdf")) {
//
//			text = addToLucene(filename, messageId, threadId, xmldb);
//
//		} else if (lowerCase.endsWith("doc")
//				|| lowerCase.endsWith("ppt")
//				|| lowerCase.endsWith("xls")
//				|| lowerCase.endsWith("rtf")
//				|| lowerCase.endsWith("sxc")
//				|| lowerCase.endsWith("odp")
//				|| lowerCase.endsWith("odt")) {
//
//			try {
//				DocumentConvert dc = new DocumentConvert();
//				dc.init(filename);
//				dc.convert();
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//
//				addToLucene(filename, messageId, threadId, xmldb);
//
//			}
//
//			text = addToLucene(filename, messageId, threadId, xmldb);
//		}
//	}

	@SuppressWarnings("unchecked")
	public void process(final String forQueueFilename, final String messageId,
			final String threadId, final XMLDB xmldb) {

		processQueue.addElement(forQueueFilename);

		addId(forQueueFilename, messageId);

		if (this.isThreadRunning == true) {

			return;
		}

		this.isThreadRunning = true;

		Thread t = new Thread() {
			public void run() {

				while (processQueue.size() > 0) {

					String filename = (String) processQueue.elementAt(0);

					String lowerCase = filename.toLowerCase();

					if (lowerCase.endsWith("htm") || lowerCase.endsWith("html")) {

						DnldURL.INSTANCE.copyFilenameToIndexLocation(DCI1.this.session, threadId,
								messageId, forQueueFilename);

						DnldURL.INSTANCE.indexOnly(messageId, threadId, xmldb);

					} else if (lowerCase.endsWith("pdf")) {

						// PDFTextExtractor pdf = new PDFTextExtractor();
						// String text = pdf.extract(filename);

						// logger.info("TEXT: "+text);

						DCI1.this.text = addToLucene(filename, messageId, threadId, xmldb);

					} else if (lowerCase.endsWith("doc")
							|| lowerCase.endsWith("ppt")
							|| lowerCase.endsWith("xls")
							|| lowerCase.endsWith("rtf")
							|| lowerCase.endsWith("sxc")
							|| lowerCase.endsWith("odp")
							|| lowerCase.endsWith("odt")) {

						try {
							DocumentConvert dc = new DocumentConvert();
							dc.init(filename);
							dc.convert();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);

							addToLucene(filename, messageId, threadId, xmldb);

						}

						DCI1.this.text = addToLucene(filename, messageId, threadId, xmldb);

						// logger.info("Done with first!: "+filename);

						// PDFTextExtractor pdf = new PDFTextExtractor();
						// String text = pdf.extract(filename+".pdf");

						// logger.info("TEXT: "+text);

					}
					processQueue.removeElementAt(0);
				}
				DCI1.this.isThreadRunning = false;

			}
		};
		t.start();

	}

	@SuppressWarnings("unchecked")
	public void processCommitAndDeliver(Hashtable session, String sphereId,
			String forQueueFilename, String messageId, String threadId,
			org.dom4j.Document fileDoc, XMLDB commit,
			Iterable<DialogsMainPeer> handlers, String contact) {

		processQueue.addElement(forQueueFilename);
		logger.warn("Added to processQueue: " + forQueueFilename);
		Hashtable object = new Hashtable();
		object.put("messageId", messageId);
		object.put("sphereId", sphereId);
		object.put("session", session);
		object.put("threadId", threadId);
		object.put("fileDoc", fileDoc);
		object.put("handlers", handlers);
		object.put("contact", contact);
		object.put("commit", commit);

		addId(forQueueFilename, messageId);
		addIdToObjects(forQueueFilename, object);

		if (this.isThreadRunning == true) {

			return;

		}

		this.isThreadRunning = true;

		Thread t = new Thread() {
			public void run() {

				while (processQueue.size() > 0) {

					String filename = (String) processQueue.elementAt(0);
					Hashtable object = getAndRemoveIdFromObjects(filename);
					if (object == null) {

						logger.warn("Object is null...");
					}
					String messageId = (String) object.get("messageId");
					String threadId = (String) object.get("threadId");
					Hashtable session = (Hashtable) object.get("session");
					org.dom4j.Document fileDoc = (org.dom4j.Document) object
							.get("fileDoc");
					XMLDB commit = (XMLDB) object.get("commit");
					Vector<DialogsMainPeer> handlers = (Vector<DialogsMainPeer>) object
							.get("handlers");
					String sphereId = (String) object.get("sphereId");
					String contact = (String) object.get("contact");

					String lowerCase = filename.toLowerCase();

					if (lowerCase.endsWith("pdf")) {

						// PDFTextExtractor pdf = new PDFTextExtractor();
						// String text = pdf.extract(filename);

						// logger.info("TEXT: "+text);
						DCI1.this.text = addToLucene(filename, messageId, threadId,
								commit);
						if (DCI1.this.text != null) {
							fileDoc.getRootElement().element("body").setText(
									DCI1.this.text);

						}

					} else if (lowerCase.endsWith("doc")
							|| lowerCase.endsWith("ppt")
							|| lowerCase.endsWith("xls")
							|| lowerCase.endsWith("rtf")
							|| lowerCase.endsWith("sxc")
							|| lowerCase.endsWith("odp")
							|| lowerCase.endsWith("odt")) {
						logger.info("do the convert!");

						try {
							DocumentConvert dc = new DocumentConvert();
							dc.init(filename);
							dc.convert();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);

							DCI1.this.text = addToLucene(filename, messageId, threadId,
									commit);
							if (DCI1.this.text != null) {
								fileDoc.getRootElement().element("body")
										.setText(DCI1.this.text);

							}
						}

						DCI1.this.text = addToLucene(filename, messageId, threadId,
								commit);
						if (DCI1.this.text != null) {
							fileDoc.getRootElement().element("body").setText(
									DCI1.this.text);

						}

						// logger.info("Done with first!: "+filename);

						// PDFTextExtractor pdf = new PDFTextExtractor();
						// String text = pdf.extract(filename+".pdf");

						// logger.info("TEXT: "+text);

					}
					processQueue.removeElementAt(0);

					logger.info("Will commit right here");

					deliverFile(session, sphereId, handlers, contact, fileDoc);

					logger.info("file delivered");
					commit.insertDoc(fileDoc, sphereId);
					logger.info("This file committed....text too? "
							+ fileDoc.asXML());
				}

				DCI1.this.isThreadRunning = false;
			}
		};
		t.start();

	}

	public void deliverFile(Hashtable session, String sphereId,
			Vector<DialogsMainPeer> handlers, String contact,
			org.dom4j.Document file) {
		

		for (DialogsMainPeer handler : DmpFilter.filter(handlers,sphereId) ) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.UPDATE);
			dmpResponse.setDocumentValue(SC.DOCUMENT, file);
			dmpResponse.setStringValue(SC.DELIVERY_TYPE, "normal");
			dmpResponse.setStringValue(SC.SPHERE, sphereId);
			handler.sendFromQueue(dmpResponse);
		}
	}

	private static String addToLucene(String filename, String messageId,
			String threadId, XMLDB xmldb) {

		deleteFromLucene(messageId, threadId);
		String text = null;

		try {
			String indexFilename = null;
			if (!filename.toLowerCase().endsWith(".pdf")) {
				indexFilename = filename + ".pdf";
			} else {

				indexFilename = filename;
			}
			try {
				PDFTextExtractor pdf = new PDFTextExtractor();
				text = pdf.extract(indexFilename);
				storeText(text, indexFilename);
				indexText(messageId, indexFilename, xmldb);
			} catch (Exception e) {

			}

			Document luceneDocument = org.apache.pdfbox.lucene.LucenePDFDocument.getDocument(new File(
					indexFilename));
			// logger.info("Ok...save text
			// too..."+luceneDocument.getField("contents").toString());

			// text = luceneDocument.getField("contents").toString();

			Analyzer analyzer = new StandardAnalyzer();

			luceneDocument.add(new Field("id", messageId,
					Field.Store.YES, Field.Index.UN_TOKENIZED));

			File dir = new File(bdir + fsep + "urls" + fsep + threadId);

			try {
				dir.mkdir();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

			}

			File indexFile = new File(bdir + fsep + "urls" + fsep + threadId
					+ fsep + "index");
			logger.info("Try to index file!");
			try {
				throw new Exception();
			} catch (Exception e) {
				logger.info("", e);
			}
			IndexWriter writer = null;
			try {
				writer = new IndexWriter(indexFile, analyzer, false);
			} catch (IOException e) {

				try {
					writer = new IndexWriter(indexFile, analyzer, true);
				} catch (IOException e1) {

				}

			}

			if (writer != null) {

				writer.addDocument(luceneDocument);

				writer.optimize();
				writer.close();
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return text;

	}

	/**
	 * @param messageId
	 * @param xmldb
	 * @param indexFilename
	 */
	private static void indexText(String messageId, String documentFileName,
			XMLDB xmldb) {
		File documentFile = new File(documentFileName + ".txt");
		SphereIndex.addToSphereIndex(messageId, documentFile, xmldb);

	}

	/**
	 * @param text
	 * @param indexFilename
	 */
	private static void storeText(String text, String indexFilename) {
		File file = new File(indexFilename + ".txt");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(text);
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			logger.error("", ex);
		}

	}

	private static void deleteFromLucene(String messageId, String threadId) {

		try {
			Term term = new Term("id", messageId);

			File indexFile = new File(bdir + fsep + "urls" + fsep + threadId
					+ fsep + "index");

			if (indexFile.exists()) {
				logger.warn("AT LEAST THE FILE EXISTS");
			}

			IndexReader reader = IndexReader.open(indexFile);
			reader.deleteDocuments(term);
			reader.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
}