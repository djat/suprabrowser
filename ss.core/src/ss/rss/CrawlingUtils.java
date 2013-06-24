/**
 * 
 */
package ss.rss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import ss.search.URLParser;
import ss.util.VariousUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author david
 * 
 */
public class CrawlingUtils {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CrawlingUtils.class);

	public static void main(String[] args) {

		CrawlingUtils cu = new CrawlingUtils();
		// cu.urlFileListToRDF(false);
		cu.createRSSNutchIndex();
	}

	private final String baseDir = "/home/david/nutchRSS";

	//private RSSParser rp = new RSSParser();

	public void urlFileListToRDF(boolean findTitle) {

		try {

			RDFUtils utils = new RDFUtils();

			ArrayList<String> fileContents = new ArrayList<String>();
			File f = new File(this.baseDir + "/orig_rss_feed_urls.txt");
			try {

				String line;

				BufferedReader in = new BufferedReader(new FileReader(f));

				if (!in.ready())
					throw new IOException();

				while ((line = in.readLine()) != null)
					fileContents.add(line);

				in.close();
			} catch (FileNotFoundException ex) {
				logger.error("File Not Found", ex);
			} catch (IOException ex) {
				logger.error("IO Error", ex);
			}

			for (int j = 0; j < fileContents.size(); j++) {

				String oneRSS = (String) fileContents.get(j);
				logger.info("ONE RSS: " + oneRSS);
				SyndFeed feed = RSSParser.checkForRSS(oneRSS);
				java.util.List items = RSSParser.getFeedItems(feed);

				if (items != null) {

					for (int i = 0; i < items.size(); i++) {

						SyndEntry entry = (SyndEntry) items.get(i);

						String link = entry.getLink();
						String title = null;
						if (findTitle) {
							title = RSSParser.getTitleFromURL(link);
						}
						if (title == null) {
							title = link;
						}

						utils.addLinkToTopic(link);
						utils.addExternalPage(link, title);
					}

					try {
						OutputFormat format = OutputFormat.createPrettyPrint();
						FileOutputStream xmlout = new FileOutputStream(
								new File(this.baseDir + "/items.xml"));
						XMLWriter writer = new XMLWriter(xmlout, format);
						writer.write(utils.returnCreatedDoc());
						writer.close();

					} catch (Exception ex) {
						logger.error(ex);
					}

				} else {

					logger.info("Was not a feed: " + oneRSS);
				}
			}
		} catch (IllegalArgumentException ex) {
			logger.error(ex);
		}

	}

	public void urlsToRDF(boolean findTitle) {

		try {

			RDFUtils utils = new RDFUtils();
			File outf = new File(this.baseDir + "/new_rss_feed_urls.txt");
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(outf);
			} catch (FileNotFoundException ex) {
				logger.error(ex);
			}

			// PrintStream ps =
			new PrintStream(fout);

			ArrayList<String> fileContents = new ArrayList<String>();
			File f = new File(this.baseDir + "/orig_rss_feed_urls.txt");
			try {

				String line;

				BufferedReader in = new BufferedReader(new FileReader(f));

				if (!in.ready())
					throw new IOException();

				while ((line = in.readLine()) != null)
					fileContents.add(line);

				in.close();
			} catch (FileNotFoundException ex) {
				logger.error(ex);
			} catch (IOException ex) {
				logger.error(ex);
			}

			for (int j = 0; j < fileContents.size(); j++) {

				String oneRSS = (String) fileContents.get(j);
				SyndFeed feed = RSSParser.checkForRSS(oneRSS);
				java.util.List items = RSSParser.getFeedItems(feed);

				if (items != null) {
					for (int i = 0; i < items.size(); i++) {

						SyndEntry entry = (SyndEntry) items.get(i);

						String link = entry.getLink();
						String title = null;
						if (findTitle) {
							title = RSSParser.getTitleFromURL(link);
						}
						if (title == null) {
							title = link;
						}

						utils.addLinkToTopic(link);
						utils.addExternalPage(link, title);
					}
				}

				try {
					OutputFormat format = OutputFormat.createPrettyPrint();
					FileOutputStream xmlout = new FileOutputStream(new File(
							this.baseDir + "/out.xml"));
					XMLWriter writer = new XMLWriter(xmlout, format);
					writer.write(utils.returnCreatedDoc());
					writer.close();
					fout.close();
				} catch (Exception ex) {
					logger.error(ex);
				}

			}
		} catch (IllegalArgumentException ex) {
			logger.error(ex);
		}
	}

	/**
	 * This is just temporary for future use with crawling code
	 * 
	 * 
	 * @param baseURL
	 * @return
	 */

	public Vector findAllHRefs(String baseURL) {

		File f = new File("/home/david/nutch-0.7.2/all.txt");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(f);
		} catch (FileNotFoundException ex) {
			logger.error(ex);
		}
		PrintStream ps = new PrintStream(fout);

		// Close our output stream

		Vector allLinks = new Vector();
		URL url;
		try {
			url = new URL(baseURL);

			URLConnection urlConnection = url.openConnection();

			urlConnection.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();

			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {

				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}

			String lowerCaseContent = content.toLowerCase();
			int index = 0;
			while ((index = lowerCaseContent.indexOf("<a href", index)) != -1) {

				index++;
				String remaining = content.substring(index - 1);

				StringTokenizer st = new StringTokenizer(remaining, ">");
				String strLink = st.nextToken();

				String endURL = strLink + ">";
				String lowerURL = endURL.toLowerCase();

				int href = 0;

				href = lowerURL.indexOf("href=\"");
				boolean isSingle = false;

				if (href == -1) {
					href = lowerURL.indexOf("href='");
				}
				if (href == -1) {
					href = lowerURL.indexOf("href=");

				}

				String rest = endURL.substring(href + 5);
				StringTokenizer st1 = null;

				if (!isSingle) {
					st1 = new StringTokenizer(rest, "\t\n\r\">#");
				} else {
					st1 = new StringTokenizer(rest, "\t\n\r\'>#");
				}

				String real = null;

				try {
					real = st1.nextToken();

				}

				catch (Exception ex) {
					try {
						real = real.replace("'", "");
					} catch (Exception ex1) {

					}
				}

				if (real != null) {

					if (!real.startsWith("http:")) {
						real = baseURL + "/" + real;
					}

					if (!real.startsWith(baseURL)) {
						ps.println(real);
					}
				}
			}
		} catch (MalformedURLException ex) {
			logger.error(ex);
		} catch (IOException ex) {
			logger.error(ex);
		}

		try {
			fout.close();
		} catch (IOException ex) {
			logger.error(ex);
		}
		return allLinks;

	}

	public Vector getURLSInsideFilesInDirectory() {
		Vector<String> allURLS = new Vector<String>();

		File dir = new File("/home/david/techdinner_archives");

		File[] files = dir.listFiles();

		for (int j = 0; j < files.length; j++) {

			File f = files[j];

			try {
				FileReader fileReader = new FileReader(f);
				BufferedReader bufReader = new BufferedReader(fileReader);

				do {
					String line = bufReader.readLine();
					if (line == null)
						break;

					Vector urls = URLParser.getURLSInsideString(line);
					for (int i = 0; i < urls.size(); i++) {
						String oneURL = (String) urls.get(0);

						if (!VariousUtils.vectorContains(oneURL, allURLS)) {
							allURLS.add(oneURL);
							logger.info(oneURL);
						}

					}

				} while (true);
				bufReader.close();
			} catch (FileNotFoundException ex) {
				logger.error(ex);
			} catch (IOException ex) {
				logger.error(ex);
			}
		}
		return allURLS;

	}

	@SuppressWarnings("unchecked")
	public Vector getUrlsFromRDF(String file) {

		Vector existingLinks = new Vector();

		try {
			File f = new File(this.baseDir
					+ System.getProperty("file.separator") + file);

			// String xpath = "//RDF/Topic/link";
			String xpath = "//RDF/*";
			SAXReader reader1 = new SAXReader();

			Document doc = null;

			doc = reader1.read(f);

			Vector vector = new Vector((List) doc.selectObject(xpath));
			// Element elem = (Element)doc.selectObject(xpath);

			for (int i = 0; i < vector.size(); i++) {

				Element one = (Element) vector.get(i);
				if (one.getName().equals("Topic")) {
					Vector links = new Vector(one.elements("link"));
					for (int j = 0; j < links.size(); j++) {
						Element link = (Element) links.get(j);
						existingLinks.add(link.attributeValue("resource"));
					}

				}

			}

			return existingLinks;

		} catch (Exception e) {
			return existingLinks;
		}
	}

	public void createRSSNutchIndex() {

		boolean findTitle = false;
		RDFUtils utils = new RDFUtils();

		File outf = new File(this.baseDir + "/newurls.txt");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(outf);
		} catch (FileNotFoundException ex) {
			logger.error(ex);
		}

		PrintStream ps = new PrintStream(fout);

		Vector existing = this.getUrlsFromRDF("allitems.xml");

		logger.info("Existing: " + existing.size());

		this.urlsToRDF(false); // / Creates it inside out.xml...

		SAXReader reader1 = new SAXReader();

		Document masterDoc = null;
		File f = new File(this.baseDir + "/allitems.xml");
		try {
			masterDoc = reader1.read(f);
		} catch (Exception ex) {
			logger.error(ex);
		}

		RDFUtils masterUtils = new RDFUtils(masterDoc);

		Vector newLinks = this.getUrlsFromRDF("items.xml");

		logger.info("NEW: " + newLinks.size());

		int o = 0;
		int n = 0;
		for (int i = 0; i < newLinks.size(); i++) {

			String link = (String) newLinks.get(i);
			if (!VariousUtils.vectorContains(link, existing)) {
				ps.println(link);
				String title = null;

				if (findTitle) {
					title = RSSParser.getTitleFromURL(link);
				}
				if (title == null) {
					title = link;
				}

				masterUtils.addLinkToTopic(link);
				masterUtils.addExternalPage(link, title);

				utils.addLinkToTopic(link);
				utils.addExternalPage(link, title);
				n++;
			} else {
				o++;
			}

		}
		try {
			fout.close();
		} catch (IOException ex) {
			logger.error(ex);
		}

		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream xmlout = new FileOutputStream(new File(
					this.baseDir + "/justnewurls.xml"));
			XMLWriter writer = new XMLWriter(xmlout, format);
			writer.write(utils.returnCreatedDoc());
			writer.close();

		} catch (Exception ex) {
			logger.error(ex);
		}

		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream xmlout = new FileOutputStream(new File(
					this.baseDir + "/newmasterurls.xml"));
			XMLWriter writer = new XMLWriter(xmlout, format);
			writer.write(masterUtils.returnCreatedDoc());
			writer.close();

		} catch (Exception ex) {
			logger.error(ex);
		}

		logger.info("There are: " + n + " new and " + o + " old");
	}

	public void createIndexFromMailArchives() {

		boolean findTitle = false;
		RDFUtils utils = new RDFUtils();

		Vector newLinks = this.getURLSInsideFilesInDirectory();
		for (int i = 0; i < newLinks.size(); i++) {

			String link = (String) newLinks.get(i);

			String title = null;

			if (findTitle) {
				title = RSSParser.getTitleFromURL(link);
			}
			if (title == null) {
				title = link;
			}

			if (link.lastIndexOf("lists.whirlycott.com") == -1) {
				utils.addLinkToTopic(link);
				utils.addExternalPage(link, title);
			}

		}

		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			FileOutputStream xmlout = new FileOutputStream(new File(
					"/home/david/nutch-0.7.2/techdinnerurls.xml"));
			XMLWriter writer = new XMLWriter(xmlout, format);
			writer.write(utils.returnCreatedDoc());
			writer.close();

		} catch (Exception ex) {
			logger.error(ex);
		}

	}

}
