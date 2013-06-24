package ss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import ss.search.SphereIndex;
import ss.server.db.XMLDB;

public class DnldURL {

	private static final String INDEX = "index";

	private static final String URLS = "urls";

	private static String BDIR = System.getProperty("user.dir");

	private static String FSEP = System.getProperty("file.separator");

	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DnldURL.class);
	
	public static final DnldURL INSTANCE = new DnldURL();

	private DnldURL(){
		
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static void main(String[] args) {

		getURLAsString("http://www.suprasphere.com");

	}

	@SuppressWarnings("unchecked")
	public Hashtable getAsFile(Hashtable session, String url) {
		Hashtable result = new Hashtable();

		try {

			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();

			con
					.setRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible;MSIE 5.5; Windows NT 5.0; H010818)");
			HttpURLConnection.setFollowRedirects(true);
			InputStream is = con.getInputStream();

			int available = is.available();

			result.put("bytes", new Integer(available).toString());

			// is.read(buff);

			int end = url.lastIndexOf('/');
			String endFile = url.substring(end + 1, url.length());
			result.put("subject", endFile);
			String name = createMessageId() + "_____" + endFile;
			result.put("name", name);

			File f = new File(BDIR + FSEP + "roots" + FSEP
					+ (String) session.get("supra_sphere") + FSEP + "File"
					+ FSEP + name);

			FileOutputStream fout = new FileOutputStream(f);
			// fout.write(buff, 0, available);

			byte[] buff = new byte[768];

			int bytesread = 0;

			int bytestotal = 0;

			while (true) {

				bytesread = is.read(buff);
				bytestotal += bytesread;

				if (bytesread == -1) {
					break;
				}
				fout.write(buff, 0, bytesread);
			}

		} catch (MalformedURLException mue) {

			logger.error("Ouch - a MalformedURLException happened: "+mue.getMessage(), mue);

		} catch (IOException ioe) {

			logger.error("Oops- an IOException happened: "+ioe.getMessage(), ioe);

		} finally {
		}
		return result;

	}

	public static String getURLAsString(String url) {

		try {
			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();

			con
					.setRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible;MSIE 5.5; Windows NT 5.0; H010818)");
			HttpURLConnection.setFollowRedirects(true);
			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int bytesread = 0;

			int bytestotal = 0;
			char[] cbuf = new char[768];
			StringBuffer result = new StringBuffer();

			while (true) {

				bytesread = isr.read(cbuf);

				if (bytesread == -1) {
					break;
				} else {

					for (int i = 0; i < cbuf.length; i++) {
						result.append(cbuf[i]);
					}

				}
				bytestotal += bytesread;

				if (bytestotal >= 768) {
					cbuf = new char[768];
				}

			}

		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	public void copyFile(File in, File out) throws Exception {
		logger.warn("Starting copy file");
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}

	public void copyFilenameToIndexLocation(Hashtable session, String threadId,
			String messageId, String filename) {

		File in = new File(filename);

		File threadDir = new File(BDIR + FSEP + "urls" + FSEP + threadId);
		if (!threadDir.exists()) {
			threadDir.mkdir();
		}

		File out = new File(BDIR + FSEP + "urls" + FSEP + threadId + FSEP
				+ messageId);

		try {
			copyFile(in, out);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public String createMessageId() {

		Date current = new Date();

		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		long longnum = System.currentTimeMillis();

		String messageId = (Long.toString(longnum));

		return messageId;
	}

	public boolean download(String url, org.dom4j.Document doc, boolean isFile,
			XMLDB xmldb) {

		// -----------------------------------------------------//
		// Step 1: Start creating a few objects we'll need.
		// -----------------------------------------------------//

		URL u;
		InputStream is = null;
		File f = null;
		boolean isRss = false;
		try {

			// ------------------------------------------------------------//
			// Step 2: Create the URL. //
			// ------------------------------------------------------------//
			// Note: Put your real URL here, or better yet, read it as a //
			// command-line arg, or read it from a file. //
			// ------------------------------------------------------------//

			u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con
					.setRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible;MSIE 5.5; Windows NT 5.0; H010818)");
			HttpURLConnection.setFollowRedirects(true);

			is = con.getInputStream();
			// ----------------------------------------------//
			// Step 3: Open an input stream from the url. //
			// ----------------------------------------------//

			// StringBuffer st = new StringBuffer((String)con.getContent());
			// // throws an IOException

			// -------------------------------------------------------------//
			// Step 4: //
			// -------------------------------------------------------------//
			// Convert the InputStream to a buffered DataInputStream. //
			// Buffering the stream makes the reading faster; the //
			// readLine() method of the DataInputStream makes the reading //
			// easier. //
			// -------------------------------------------------------------//

			// dis = new DataInputStream(new BufferedInputStream(is));

			// ------------------------------------------------------------//
			// Step 5: //
			// ------------------------------------------------------------//
			// Now just read each record of the input stream, and print //
			// it out. Note that it's assumed that this problem is run //
			// from a command-line, not from an application or applet. //
			// ------------------------------------------------------------//

			String bdir = System.getProperty("user.dir");
			String fsep = System.getProperty("file.separator");

			String threadId = doc.getRootElement().element("thread_id")
					.attributeValue("value");

			File dir = new File(bdir + fsep + "urls" + fsep + threadId);
			logger.info("TRY TO MAKE THIS DIRECTORY : "
					+ dir.getAbsolutePath());
			try {
				dir.mkdir();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			f = new File(bdir
					+ fsep
					+ "urls"
					+ fsep
					+ threadId
					+ fsep
					+ doc.getRootElement().element("message_id")
							.attributeValue("value"));

			logger.info("file: " + f.getAbsolutePath());

			
			int available = is.available();

			FileOutputStream fout = new FileOutputStream(f);

			byte[] buff = new byte[768];

			int bytesread = 0;

			int bytestotal = 0;

			while (true) {

				bytesread = is.read(buff);
				bytestotal += bytesread;

				if (bytesread == -1) {
					break;
				}
				fout.write(buff, 0, bytesread);

			}

		} catch (MalformedURLException mue) {

			logger.error("Ouch - a MalformedURLException happened: "+mue.getMessage(), mue);

		} catch (IOException ioe) {

			logger.error("Oops- an IOException happened: "+ioe.getMessage(), ioe);

		} finally {

			// ---------------------------------//
			// Step 6: Close the InputStream //
			// ---------------------------------//

			try {
				is.close();
			} catch (IOException ioe) {
				// just going to ignore this one
			} catch (NullPointerException npe) {
				// In case it's not online
			}

		} // end of 'finally' clause

		
		Analyzer analyzer = new StandardAnalyzer();
		try {
			indexOnly(doc.getRootElement().element("message_id")
					.attributeValue("value"), doc.getRootElement().element(
					"thread_id").attributeValue("value"), xmldb);

			// Searcher searcher = new IndexSearcher("index");

			/*
			 * String line = "suse linux";
			 * 
			 * Query query = QueryParser.parse(line, "contents", analyzer);
			 * System.out.println("Searching for: " +
			 * query.toString("contents"));
			 * 
			 * Hits hits = searcher.search(query);
			 * System.out.println(hits.length() + " total matching documents");
			 * 
			 * for (int i = 0; i < hits.length(); i++) { Document document =
			 * hits.doc(i); //String path = doc.get("path"); String id =
			 * document.get("id");
			 * 
			 * System.out.println("Result: "+id); }
			 */

		} catch (Exception ioe) {
			/*
			 * 
			 * SSLogger.getLogger().info("IOException in indexwriter");
			 * 
			 * Analyzer analyzer2 = new StandardAnalyzer(); try { IndexWriter
			 * writer = new IndexWriter("index", analyzer, true);
			 * 
			 * Document addDoc = FileDocument.Document(f);
			 * 
			 * 
			 * addDoc.add(Field.Keyword("id",
			 * doc.getRootElement().element("message_id").attributeValue("value")));
			 * 
			 * writer.addDocument(addDoc);
			 * 
			 * writer.optimize(); writer.close();
			 */

		}

		return isRss;

	}

	public boolean downloadOnly(String url, org.dom4j.Document doc,
			String threadId) {

		// -----------------------------------------------------//
		// Step 1: Start creating a few objects we'll need.
		// -----------------------------------------------------//

		URL u;
		InputStream is = null;
		File f = null;
		try {

			// ------------------------------------------------------------//
			// Step 2: Create the URL. //
			// ------------------------------------------------------------//
			// Note: Put your real URL here, or better yet, read it as a //
			// command-line arg, or read it from a file. //
			// ------------------------------------------------------------//

			u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con
					.setRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible;MSIE 5.5; Windows NT 5.0; H010818)");
			HttpURLConnection.setFollowRedirects(true);

			is = con.getInputStream();
			// ----------------------------------------------//
			// Step 3: Open an input stream from the url. //
			// ----------------------------------------------//

			// StringBuffer st = new StringBuffer((String)con.getContent());
			// // throws an IOException

			// -------------------------------------------------------------//
			// Step 4: //
			// -------------------------------------------------------------//
			// Convert the InputStream to a buffered DataInputStream. //
			// Buffering the stream makes the reading faster; the //
			// readLine() method of the DataInputStream makes the reading //
			// easier. //
			// -------------------------------------------------------------//

			// dis = new DataInputStream(new BufferedInputStream(is));

			// ------------------------------------------------------------//
			// Step 5: //
			// ------------------------------------------------------------//
			// Now just read each record of the input stream, and print //
			// it out. Note that it's assumed that this problem is run //
			// from a command-line, not from an application or applet. //
			// ------------------------------------------------------------//

			String bdir = System.getProperty("user.dir");
			String fsep = System.getProperty("file.separator");
			File dir = new File(bdir + fsep + "urls" + fsep + threadId);
			logger.info("TRY TO MAKE THIS DIRECTORY : "
					+ dir.getAbsolutePath());
			try {
				dir.mkdir();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

			}

			f = new File(bdir
					+ fsep
					+ "urls"
					+ fsep
					+ threadId
					+ fsep
					+ doc.getRootElement().element("message_id")
							.attributeValue("value"));

			
			int available = is.available();

			// is.read(buff);

			FileOutputStream fout = new FileOutputStream(f);
			// fout.write(buff, 0, available);

			byte[] buff = new byte[768];

			int bytesread = 0;

			int bytestotal = 0;

			while (true) {

				bytesread = is.read(buff);
				bytestotal += bytesread;
				// jpb.setValue(bytestotal);

				if (bytesread == -1) {
					break;
				}
				fout.write(buff, 0, bytesread);

			}

		} catch (MalformedURLException mue) {

			logger.error("Ouch - a MalformedURLException happened: "+mue.getMessage(), mue);
			// System.exit(1);

		} catch (IOException ioe) {

			logger.error("Oops- an IOException happened: "+ioe.getMessage(), ioe);
			// System.exit(1);

		} finally {

			// ---------------------------------//
			// Step 6: Close the InputStream //
			// ---------------------------------//

			try {
				is.close();
			} catch (IOException ioe) {
				// just going to ignore this one
			}

		} // end of 'finally' clause

		return true;

	}

	public void indexOnly(String messageId, String threadId, XMLDB xmldb) {
		logger.warn("Trying index only");
		IndexWriter writer = null;
		try {
			File indexDir = new File(BDIR + FSEP + URLS + FSEP + threadId
					+ FSEP + INDEX);
			File documentFile = new File(BDIR + FSEP + URLS + FSEP + threadId
					+ FSEP + messageId);

			logger.debug("Index Dir=" + indexDir.getAbsolutePath());
			logger.debug("Document File=" + documentFile.getAbsolutePath());

			boolean needCreateIndex = false;
			if (!indexDir.exists()) {
				indexDir.mkdirs();
				needCreateIndex = true;
			}

			Document addDoc = FileDocument.Document(documentFile);
			addDoc.add(new Field("id", messageId, Field.Store.YES,
					Field.Index.UN_TOKENIZED));

			writer = new IndexWriter(indexDir, new StandardAnalyzer(),
					needCreateIndex);
			writer.addDocument(addDoc);
			writer.optimize();
			SphereIndex.addToSphereIndex(messageId, documentFile, xmldb);

		} catch (IOException ioe) {
			logger.error("IOException. threadId=" + threadId + " messageId="
					+ messageId, ioe);
		} catch (Exception exeption) {
			logger.error("Some kind of exception trying to add", exeption);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("IOExeption while trying to close index.", e);
				}
			}
		}
	}

}
