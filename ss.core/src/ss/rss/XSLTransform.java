package ss.rss;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import ss.common.LocationUtils;

import com.sun.syndication.feed.synd.SyndFeed;

public class XSLTransform {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XSLTransform.class);

	public static void main(String[] args) {
		try {
			logger.info("result: "
					+ XSLTransform
							.transformRSS("http://radar.oreilly.com/feed"));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static InputStream getTransformForFeed(String feedType) {
		String relativePath =getTransformForFeedPath( feedType );
		return XSLTransform.class.getResourceAsStream( "/" + relativePath );
	}
	
	public static String getTransformForFeedPath(String feedType) {
		if ((feedType.toLowerCase().lastIndexOf("atom") != -1)
				&& (feedType.toLowerCase().lastIndexOf("1.0") != -1)) {
			logger.info("It's a 1.0 file...");
			return "microblog/atom10.xsl";
		} else if (feedType.toLowerCase().lastIndexOf("atom") != -1) {
			return "microblog/atom03.xsl";
		} else if (feedType.toLowerCase().lastIndexOf("rss") != -1) {
			if (feedType.lastIndexOf("2") != -1) {

				return "microblog/rss2.xsl";
			} else if (feedType.lastIndexOf("1") != -1) {

				return "microblog/rss1.xsl";

			} else if (feedType.lastIndexOf("09") != -1) {

				return "microblog/rss09.xsl";
			}
		} else if (feedType.toLowerCase().equals("contact")) {
			return "microblog/contact.xsl";
		}
		return  "microblog/rss2.xsl";
	}

	public static String transformRSS(String url) {

		StringWriter writer = new StringWriter();

		SyndFeed feed = RSSParser.checkForRSS(url);

		String type = feed.getFeedType();

		logger.info("Type: " + type);

		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();

			Transformer transformer = tFactory
					.newTransformer(new javax.xml.transform.stream.StreamSource(
							getTransformForFeed(type)));

			transformer.transform(new javax.xml.transform.stream.StreamSource(
					url), new javax.xml.transform.stream.StreamResult(writer));

		} catch (TransformerConfigurationException ex) {
			logger.error(ex);
		} catch (TransformerFactoryConfigurationError ex) {
			logger.error(ex);
		} catch (TransformerException ex) {
			logger.error(ex);
		}

		return writer.toString();

	}

	public static String transformContact(Document inputDoc) {

		try {

			final String type = "contact";

			final TransformerFactory tFactory = TransformerFactory.newInstance();

			final Transformer transformer = tFactory
					.newTransformer(new javax.xml.transform.stream.StreamSource(
							getTransformForFeed(type)));

			final DocumentSource source = new DocumentSource(inputDoc);
			final DocumentResult result = new DocumentResult();
			transformer.transform(source, result);

			return result.getDocument().asXML();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	public static String transformSphere(Document inputDoc) {

		try {

			String type = "sphere";

			TransformerFactory tFactory = TransformerFactory.newInstance();

			Transformer transformer = tFactory
					.newTransformer(new javax.xml.transform.stream.StreamSource(
							getTransformForFeed(type)));

			// Templates stylesheet = tFactory.newTemplates(new
			// javax.xml.transform.stream.StreamSource(new
			// File("microblog/contact.xsl")));

			// now lets style the given document
			DocumentSource source = new DocumentSource(inputDoc);
			DocumentResult result = new DocumentResult();
			transformer.transform(source, result);

			// return the transformed document
			Document transformedDoc = result.getDocument();

			return transformedDoc.asXML();

			// return ((Document)resultDoc).asXML();

			// transformer.transform(source, new
			// javax.xml.transform.stream.StreamResult(writer));

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return null;

	}

}
