package ss.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XmlDocumentUtils {
	
	/**
	 * 
	 */
	private static final String EMPTY_STRING = "";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CannotParseXmlException.class);
	
	/**
	 * @author d!ma
	 *
	 */
	public static final class CannotParseXmlException extends
			IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4389876932705964884L;

		/**
		 * @param cause
		 */
		public CannotParseXmlException(Throwable cause) {
			super(cause);
		}

	}

	private static final OutputFormat PRETTY_OUTPUT_FORMAT = createPrettyOutputFormat();
	
	private static final OutputFormat COMPACT_OUTPUT_FORMAT = OutputFormat.createCompactFormat();
	
	/**
	 * Select element by xpath.
	 * 
	 * @return element that was found or null
	 */
	public static Element selectElementByXPath(Element element, String xpath) {
		if (element == null) {
			return null;
		}
		return getSelectedElement(element.selectObject(xpath));
	}

	/**
	 * @return
	 */
	private static OutputFormat createPrettyOutputFormat() {
		final OutputFormat outputFormat = new OutputFormat( "\t", true ); //  may be use OutputFormat.createPrettyPrint(); ?
		outputFormat.setIndent( true );
		outputFormat.setTrimText( true );
		outputFormat.setLineSeparator( StringUtils.getLineSeparator() );
		return outputFormat;
	}

	/**
	 * Returns element if selectedObject is element or null in all other cases.
	 */
	@SuppressWarnings("unchecked")
	private static Element getSelectedElement(Object selectedObject) {
		if ( selectedObject instanceof Element) {
			return (Element) selectedObject;
		} 
		if ( selectedObject instanceof List ) {
			List<Element> selectedElements = (List<Element>) selectedObject;
			if ( selectedElements.size() == 1 ) {
				return selectedElements.get( 0 );
			}
		}
		return null;
	}

	/**
	 * Returns attribte if selectedObject is attribute or null in all other
	 * cases.
	 */
	private static Attribute getSelectedAttibute(Object selectedObject) {
		if (selectedObject != null && selectedObject instanceof Attribute) {
			return (Attribute) selectedObject;
		} else {
			return null;
		}
	}

	/**
	 * Select element by xpath.
	 * 
	 * @return element that was found or null
	 */
	public static Element selectElementByXPath(Document document, String xpath) {
		if (document == null) {
			return null;
		}
		return getSelectedElement(document.selectObject(xpath));
	}

	/**
	 * Find child element with specified element name. If child element is not
	 * found it will be created.
	 * 
	 * @return child element that was found or created
	 */
	public static Element putElement(Element parentElement,
			String childElementName) {
		Element childElement = parentElement.element(childElementName);
		if (childElement == null) {
			childElement = parentElement.addElement(childElementName);
		}
		return childElement;
	}

	/**
	 * Find child element with specified element name and insertCopy attribute to it.
	 * If child element is not found it will be created.
	 * 
	 * @return child element that was found or created
	 */
	public static Element putElementWithAttribute(Element parentElement,
			String childElementName, String attributeName, String attributeValue) {
		Element childElement = putElement(parentElement, childElementName);
		childElement.addAttribute(attributeName, attributeValue);
		return childElement;
	}

	/**
	 * Add child element with specified name and attribute
	 * 
	 * @param parentElement
	 * @param childElementName
	 * @param attributeName
	 * @param attributeValue
	 * @return added element
	 */
	public static Element addElementWithAttribute(Element parentElement,
			String childElementName, String attributeName, String attributeValue) {
		Element childElement = parentElement.addElement(childElementName);
		childElement.addAttribute(attributeName, attributeValue);
		return childElement;
	}

	/**
	 * Find child element with given name and attribute. If element not found
	 * than creates new child element with specified name and attribute.
	 * 
	 * @return found or created element
	 */
	public static Element selectOrCreateElementWithAttribute(
			Element parentElement, String childElementName,
			String attributeName, String attributeValue) {
		Element childElement = selectElementByXPath(parentElement, String
				.format("%s[@%s = '%s']", childElementName, attributeName,
						attributeValue));
		if (childElement == null) {
			childElement = addElementWithAttribute(parentElement,
					childElementName, attributeName, attributeValue);
		}
		return childElement;
	}

	/**
	 * Returns attribute surname by xpath
	 *
	 */
	public static String selectAttibuteValueByXPath(Document document,
			String xpath) {
		Attribute attribute = selectAttibuteByXPath(document, xpath);
		return attribute != null ? attribute.getText() : null;
	}

	/**
	 * Returns attribute by xpath
	 * 
	 * @return
	 */
	public static Attribute selectAttibuteByXPath(Document document, String xpath) {
		if (document == null) {
			return null;
		}
		return getSelectedAttibute( document.selectObject(xpath) );
	}

	public static String toFormattedString( final Node node, final OutputFormat format ) {
		if ( node == null ) {
			return null;
		}
		if ( format == null ){
			throw new ArgumentNullPointerException( "format" );
		}
		final StringWriter stringWriter = new StringWriter();
		final XMLWriter xmlWriter = new XMLWriter( stringWriter, format );
		try {
			xmlWriter.write( node );
		}
		catch( IOException ex ) {
			ExceptionHandler.handleException( XmlDocumentUtils.class, ex );
		}
		return stringWriter.toString();
	}


	public static String toPrettyString(final Node node) {
		return toFormattedString( node, PRETTY_OUTPUT_FORMAT );
	}

	public static String toCompactString(final Node node) {
		return toFormattedString( node, COMPACT_OUTPUT_FORMAT );
	}
	
	public static String requireAttibuteValueByXPath(Document document,  String xpath) {
		if ( document == null ){
			throw new ArgumentNullPointerException( "document");
		}
		if ( xpath == null ) {
			throw new ArgumentNullPointerException( "xpath");
		}	
		
		Attribute attr = selectAttibuteByXPath(document, xpath);
		if ( attr == null ) {
			throw new UnexpectedRuntimeException( String.format( "attribute not found. XPath %s, Document %s", xpath, document.asXML() ) );
		}
		return attr.getText();		
	}

	/**
	 * Compare two xmls, retuns true if x xml is equal to y
	 * @param x xml document, can be null
	 * @param y xml document, can be null
	 */
	public static boolean isEqual(Node x, Node y) {
		String xStr = toPrettyString( x );
		String yStr = toPrettyString( y );
		return (xStr == yStr) ||
			( xStr != null &&
		      yStr != null &&
		      xStr.equals( yStr ) );
	}

	/**
	 * Compare two xmls, retuns true if x xml is equal to y
	 * @param x xml document, can be null
	 * @param y xml document, can be null
	 */
	public static boolean isEqual(String x, Node y) {
		Document xDoc = XmlDocumentUtils.parse(x, true );
		if ( xDoc == null && xDoc != y ) {
			return false;
		}
		return isEqual(xDoc, y);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Element> selectElementListByXPath(Node root, String xpath) {
		if ( root == null ) {
			throw new ArgumentNullPointerException("root");
		}
		if (xpath == null  ) {
			throw new ArgumentNullPointerException("xpath");
		}
		List<Node> nodes = root.selectNodes(xpath);
		List<Element> elements = new ArrayList<Element>();
		if ( nodes != null ) {
			for( Node node : nodes ) {
				if ( node instanceof Element ) {
					elements.add( (Element) node);
				}
			}	
		}
		return elements;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Attribute> selectAttributeListByXPath(Node root, String xpath) {
		if ( root == null ) {
			throw new ArgumentNullPointerException("root");
		}
		if (xpath == null  ) {
			throw new ArgumentNullPointerException("xpath");
		}
		List<Node> nodes = root.selectNodes(xpath);
		List<Attribute> attributes = new ArrayList<Attribute>();
		if ( nodes != null ) {
			for( Node node : nodes ) {
				if ( node instanceof Attribute ) {
					attributes.add( (Attribute) node);
				}
			}	
		}
		return attributes;
	}
	
	public static List<String> selectAttributeValuesListByXPath(Document document, String xpath) {
		final List<Attribute> attributes = selectAttributeListByXPath(document, xpath);
		final List<String> values = new ArrayList<String>(attributes.size());
		for( Attribute attribute : attributes ) {
			values.add( attribute.getValue() );
		}
		return values;
	}

	/**
	 * @param lastLoginFile
	 */
	public static Document load(final File file) throws DocumentException {
		final SAXReader saxReader = new SAXReader();
		return saxReader.read( file );
	}
	
	/**
	 * @param lastLoginFile
	 */
	public static Document load(final InputStream stream) throws DocumentException {
		final SAXReader saxReader = new SAXReader();
		return saxReader.read( stream );
	}
	
	public static Document loadOrCreateEmpty(final File file) throws DocumentException {
		if ( file.exists() ) {
			return load( file );
		}
		else {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				throw new DocumentException( "Cannot create file " + file.getName(), ex );
			}
			return DocumentHelper.createDocument();
		}
	}

	/**
	 * @param file
	 * @throws  
	 * @throws  
	 */
	public static void save(final File file, final Document document ) throws DocumentException  {
		try {
			if ( !file.exists() ) {
				file.createNewFile();
			}
			final FileOutputStream fout = new FileOutputStream(file);
			try {
				final XMLWriter writer = new XMLWriter(fout, PRETTY_OUTPUT_FORMAT );
				try {
					writer.write(document);
				}
				finally {
					writer.close();
				}
			}
			finally {
				fout.close();
			}
		}
		catch( IOException ex) {
			throw new DocumentException( "Cannot save document to file " + file.getName(), ex );
		}
	}

	/**
	 * 
	 */
	public static Document parse( String xmlText ) {
		return parse(xmlText, false);	
	}

	/**
	 * 
	 */
	public static Document parse( String xmlText, boolean ignoreXmlParseError ) {
		xmlText = xmlText != null ? xmlText.trim() : EMPTY_STRING;
		if ( xmlText.length() == 0 ) {
			return null;
		}
		try {
			return DocumentHelper.parseText(xmlText);
		} catch (DocumentException ex) {
			if ( ignoreXmlParseError ) {
				logger.warn(  "Cannot parse document", ex );
				return null;
			}
			else {
				throw new CannotParseXmlException( ex );
			}
			
		}		
	}

	/**
	 * @param doc
	 * @param apath
	 */
	public static Element selectFirstElementByXPath(Node node, String apath) {
		List<Element> elements = selectElementListByXPath(node, apath);
		return elements.size() > 0 ? elements.get( 0 ) : null;
	}

}
