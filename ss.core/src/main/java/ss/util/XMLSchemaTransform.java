/*
 * Created on Sep 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLSchemaTransform {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XMLSchemaTransform.class);

	private static Random tableIdGenerator = new Random();

	public static Document removeLocations(Document doc) {
		try {

			doc.getRootElement().element("locations").detach();
			doc.getRootElement().addElement("locations");

		} catch (Exception e) {
			return doc;

		}
		return doc;

	}

	@SuppressWarnings("unchecked")
	public static Document removeMachineVerifiers(Document doc) {

		String apath = "//membership/machine_verifier";

		try {

			Element one = (Element) doc.selectObject(apath);

			one.detach();

		} catch (ClassCastException cce) {

			Vector vec = new Vector((List) doc.selectObject(apath));

			for (int i = 0; i < vec.size(); i++) {
				Element one = (Element) vec.get(i);

				one.detach();

			}

		}

		apath = "//membership/machine_pass";

		try {

			Element one = (Element) doc.selectObject(apath);

			one.detach();

		} catch (ClassCastException cce) {

			Vector vec = new Vector((List) doc.selectObject(apath));

			for (int i = 0; i < vec.size(); i++) {
				Element one = (Element) vec.get(i);

				one.detach();

			}

		}

		return doc;

	}

	public static Document setNewMoment(Document doc) {

		if (doc.getRootElement().element("moment") == null) {

			doc.getRootElement().addElement("moment").addAttribute("value",
					getCurrentMoment());

		} else {
			doc.getRootElement().element("moment").addAttribute("value",
					getCurrentMoment());
		}
		if (doc.getRootElement().element("last_updated") == null) {
			doc.getRootElement().addElement("last_updated").addAttribute(
					"value", getCurrentMoment());

		} else {
			doc.getRootElement().element("last_updated").addAttribute("value",
					getCurrentMoment());

		}

		return doc;

	}

	// This will take the new document and add the message id of the original
	// document to it, so that if it gets updated in its location, it will be
	// linked to the
	// update at the original location
	public static Document addLocationToDoc(Document origDoc,
			Document document, String cliURL, String currSphereId,
			String currSphereName, String newSphereId, String newSphereName) {

		if (document.getRootElement().element("locations") == null) {

			document.getRootElement().addElement("locations").addElement(
					"sphere").addAttribute("URL", cliURL).addAttribute(
					"ex_system", currSphereId).addAttribute("ex_display",
					currSphereName).addAttribute(
					"ex_message",
					origDoc.getRootElement().element("message_id")
							.attributeValue("value"));
			document.getRootElement().element("locations").addElement("sphere")
					.addAttribute("URL", cliURL).addAttribute("ex_system",
							newSphereId).addAttribute("ex_display",
							newSphereName).addAttribute(
							"ex_message",
							document.getRootElement().element("message_id")
									.attributeValue("value"));

		} else {

			document.getRootElement().element("locations").addElement("sphere")
					.addAttribute("URL", cliURL).addAttribute("ex_system",
							newSphereId).addAttribute("ex_display",
							newSphereName).addAttribute(
							"ex_message",
							document.getRootElement().element("message_id")
									.attributeValue("value"));

		}

		return document;

	}

	public static void addOneLocationToDoc(final Document document,
			final String cliURL, final String newSphereId,
			final String newSphereName) {
		final Document origDoc = document;
		logger.info("origDoc.as" + document.asXML() + cliURL + newSphereId
				+ newSphereName);
		document.getRootElement().addElement("locations").addElement("sphere")
				.addAttribute("URL", cliURL).addAttribute("ex_system",
						newSphereId).addAttribute("ex_display", newSphereName)
				.addAttribute(
						"ex_message",
						origDoc.getRootElement().element("message_id")
								.attributeValue("value"));
	}

	public static void setThreadAndOriginalAsMessage(final Document doc) {
		String messageId = null;
		if (doc.getRootElement().element("message_id") == null) {
			doc.getRootElement().addElement("message_id").addAttribute("value",
					getNextMessageId());
			messageId = doc.getRootElement().element("message_id")
					.attributeValue("value");
		} else {
			messageId = doc.getRootElement().element("message_id")
					.attributeValue("value");
		}

		if (doc.getRootElement().element("thread_id") == null) {
			doc.getRootElement().addElement("thread_id").addAttribute("value",
					messageId);

		} else {
			doc.getRootElement().element("thread_id").addAttribute("value",
					messageId);
		}
		if (doc.getRootElement().element("original_id") == null) {
			doc.getRootElement().addElement("original_id").addAttribute(
					"value", messageId);
		} else {
			doc.getRootElement().element("original_id").addAttribute("value",
					messageId);
		}
	}

	public static String getCurrentMoment() {
		Date current = new Date();
		return DateFormat.getTimeInstance(DateFormat.LONG).format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
	}

	public static String getTimeStamp() {
		long longnum = System.currentTimeMillis();
		final String messageId = (Long.toString(longnum));
		return messageId;
	}

	public static synchronized String getNextMessageId() {
		return new Long(Math.abs(tableIdGenerator.nextLong())).toString();
	}

	public static Document addOtherLocationInfo(Document doc, String sphereURL) {
		return doc;
	}

	public static Document consolidateCommentsAndRoot(Document parentDoc,
			Document childDoc) {

		Document threadDoc = DocumentHelper.createDocument();
		Element root = threadDoc.addElement("thread");

		String responseId = parentDoc.getRootElement().element("message_id")
				.attributeValue("value");

		if (childDoc.getRootElement().element("response_id") == null) {

			childDoc.getRootElement().addElement("response_id").addAttribute(
					"value", responseId);
		} else {

			childDoc.getRootElement().element("response_id").addAttribute(
					"value", responseId);

		}

		root.add((Element) childDoc.getRootElement().clone());
		return threadDoc;

	}

	public static Document consolidateMultipleToOneDocument(
			Vector highlightKeywords, Hashtable documents) {

		Document threadDoc = DocumentHelper.createDocument();
		Element root = threadDoc.addElement("thread");

		for (int j = 0; j < highlightKeywords.size(); j++) {
			Document keywordDoc = (Document) highlightKeywords.get(j);

			String key = keywordDoc.getRootElement().element("subject")
					.attributeValue("value");

			Vector docs = (Vector) documents.get(key);

			if (docs == null) {
				continue;
			}
			for (int i = 0; i < docs.size(); i++) {

				Document doc = (Document) docs.get(i);
				String responseId = keywordDoc.getRootElement().element(
						"message_id").attributeValue("value");
				if (doc.getRootElement().element("response_id") == null) {

					doc.getRootElement().addElement("response_id")
							.addAttribute("value", responseId);
				} else {

					doc.getRootElement().element("response_id").addAttribute(
							"value", responseId);

				}

				root.add((Element) doc.getRootElement().clone());

			}

		}

		return threadDoc;

	}

	public static Document prepareSingleDocumentForHighlightXML(Document doc) {

		Document newDoc = (Document) doc.clone();
		doc = newDoc;

		String responseId = null;

		Document threadDoc = DocumentHelper.createDocument();
		Element root = threadDoc.addElement("thread");

		if (doc.getRootElement().element("response_id") == null) {

			responseId = VariousUtils.getNextRandomLong();

			doc.getRootElement().addElement("response_id").addAttribute(
					"value", responseId);
		} else {

			responseId = doc.getRootElement().element("response_id")
					.attributeValue("value");

		}
		root.add(doc.getRootElement().detach());

		return threadDoc;

	}

	public static Document replaceCurrentSphere(final Document doc,
			String sphere) {

		if (doc.getRootElement().element("current_sphere") != null) {

			doc.getRootElement().element("current_sphere").addAttribute(
					"value", sphere);
		} else {
			doc.getRootElement().addElement("current_sphere").addAttribute(
					"value", sphere);
		}
		return doc;

	}

	@SuppressWarnings("unchecked")
	public static Document removeAllElementsWithName(final Document doc,
			String elementName) {

		String apath = "//" + elementName;
		try {

			Element elem = (Element) doc.selectObject(apath);
			elem.detach();

		} catch (ClassCastException cce) {
			Vector elems = new Vector((List) doc.selectObject(apath));
			for (int i = 0; i < elems.size(); i++) {
				Element one = (Element) elems.get(i);
				one.detach();

			}

		}

		return doc;

	}

}
