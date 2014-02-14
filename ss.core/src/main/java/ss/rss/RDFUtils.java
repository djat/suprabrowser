package ss.rss;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class RDFUtils {

	private Document baseDoc = null;

	private Element root = null;

	public RDFUtils() {

		this.baseDoc = DocumentHelper.createDocument();
		this.root = this.baseDoc.addElement("RDF").addNamespace("d",
				"http://purl.org/dc/elements/1.0/").addNamespace("r",
				"http://www.w3.org/TR/RDF/").addAttribute("xmlns",
				"http://dmoz.org/rdf");

		createTopic();
	}

	public RDFUtils(Document existingDoc) {
		this.baseDoc = existingDoc;
		this.root = existingDoc.getRootElement();

	}

	public void createTopic() {

		this.root.addElement("Topic").addAttribute("r:id", "Top");
		this.root.element("Topic").addElement("catid").setText("1000");

	}

	public void addLinkToTopic(String link) {
		this.root.element("Topic").addElement("link").addAttribute(
				"r:resource", link);

	}

	public void addExternalPage(String link, String title) {

		Element ep = this.root.addElement("ExternalPage").addAttribute("about",
				link);
		ep.addElement("d:Title").setText(title);
		ep.addElement("d:Description");
		ep.addElement("topic").setText("Top");

	}

	public Document returnCreatedDoc() {

		return this.baseDoc;

	}

}
