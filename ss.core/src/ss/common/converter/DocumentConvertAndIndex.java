/*
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.common.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.pdfbox.searchengine.lucene.LucenePDFDocument;

import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.ThreadUtils;
import ss.search.SphereIndex;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.CheckFileExtension;
import ss.util.DnldURL;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
class DocumentConvertAndIndex {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DocumentConvertAndIndex.class);

	static String bdir = System.getProperty("user.dir");

	static String fsep = System.getProperty("file.separator");

	public DocumentConvertAndIndex() {

	}

	boolean isThreadRunning = false;

	static void process(ConvertingElement element) {
		XMLDB xmldb = new XMLDB(element.getSession());
		String text = processConvert(element, xmldb);
		if (element.isPublish()){
			processPublish(element, text, xmldb);
		}
	}
	
	private static String processConvert(ConvertingElement element, XMLDB xmldb){
		String filename = element.getName();
		String threadId = element.getThreadId();
		String messageId = element.getMessageId();
		Hashtable session = element.getSession();
		
		String text = null;
		
		if (CheckFileExtension.isHTMLDocument( filename )) {

			DnldURL.INSTANCE.copyFilenameToIndexLocation(session, threadId ,
					messageId , filename);

			DnldURL.INSTANCE.indexOnly(messageId, threadId, xmldb);

		} else if (CheckFileExtension.isPDF( filename )) {

			text = addToLucene(filename, messageId, threadId, xmldb);

		} else if (CheckFileExtension.isKnownConvertableDocument( filename )) {

			try {
				DocumentConvert dc = new DocumentConvert();
				dc.init(filename);
				dc.convert();
			} catch (Exception e) {
				logger.error("Error converting file " + filename, e);
			}

			text = addToLucene(filename, messageId, threadId, xmldb);
		}
		return text;
	}
	
	private static void processPublish(final ConvertingElement element, final String text, final XMLDB xmldb){
		
		final Hashtable session = element.getSession();
		final String sphereId = element.getSphereId();
		final org.dom4j.Document fileDoc = element.getDoc();
		final String contact = element.getContact();
		
		if ( StringUtils.isNotBlank(text) ) {
			fileDoc.getRootElement().element("body").setText(text);
			if (logger.isDebugEnabled()) {
				logger.debug("Will commit right file here");
			}
			xmldb.replaceDoc(fileDoc, sphereId);
		}
		//xmldb.insertDoc(fileDoc, sphereId, element.isIndexing());
		if (logger.isDebugEnabled()) {
			logger.debug("This file committed....text too? "
					+ fileDoc.asXML());
		}
		
		ThreadUtils.start(new Thread(){

			@Override
			public void run() {
				deliverFile(session, sphereId, DialogsMainPeerManager.INSTANCE.getHandlers(), contact, fileDoc);
				logger.info("file delivered");
			}
			
		}, DocumentConvertAndIndex.class);
	}

	private static void deliverFile(Hashtable session, String sphereId,
			Iterable<DialogsMainPeer> handlers, String contact,
			org.dom4j.Document file) {

		for (DialogsMainPeer handler : DmpFilter.filter(handlers,sphereId)) {
			try {
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL,
						SSProtocolConstants.UPDATE);
				dmpResponse.setDocumentValue(SC.DOCUMENT, file);
				dmpResponse.setStringValue(SC.DELIVERY_TYPE, "normal");
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				DialogsMainPeer.sendFromQueue(dmpResponse, handler.getName());

			} catch (Exception ex) {
				logger.error("Exception delivering file", ex);
			}
		}
	}

	private static String addToLucene(String filename, String messageId,
			String threadId, XMLDB xmldb) {

		deleteFromLucene(messageId, threadId);
		String text = null;

		try {
			String indexFilename = null;
			if (!CheckFileExtension.isPDF( filename )) {
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
				logger.error("PDF extraction failed", e);
			}

			Document luceneDocument = LucenePDFDocument.getDocument(new File(
					indexFilename));

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