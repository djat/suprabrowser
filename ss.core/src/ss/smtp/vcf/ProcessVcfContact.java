/*
 * Created on Sep 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp.vcf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.w3c.dom.*;
import net.sf.vcard4j.parser.*;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.*;

import org.apache.xml.serialize.*;

import ss.global.SSLogger;

import net.sf.vcard4j.parser.DomParser;

public class ProcessVcfContact {

	static String bdir = System.getProperty("user.dir");

	static String fsep = System.getProperty("file.separator");
	
	private static final Logger logger = SSLogger.getLogger(ProcessVcfContact.class);

	public static void main(String args[]) {

		DomParser parser = new DomParser();
		Document document = new DocumentImpl();

		try {
			parser.parse(new FileInputStream(bdir + fsep
					+ "WillmanOtterfeister.vcf"), document);

			OutputFormat format = new OutputFormat(document);
			format.setOmitDocumentType(true);
			format.setOmitXMLDeclaration(true);
			format.setMethod("xml");
			format.setIndenting(true);
			format.setPreserveSpace(true);
			format.setLineWidth(0);
			XMLSerializer serializer = new XMLSerializer(System.out, format);
			serializer.serialize(document);

		} catch (VCardParseException e) {
			logger.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}
