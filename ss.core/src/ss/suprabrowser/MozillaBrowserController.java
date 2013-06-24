package ss.suprabrowser;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMXPathEvaluator;
import org.mozilla.interfaces.nsIDOMXPathNSResolver;
import org.mozilla.interfaces.nsIDOMXPathResult;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SimpleBrowserDataSource;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;
import ss.common.LocationUtils;
import ss.common.UiUtils;
import ss.suprabrowser.common.JSHelper;
import ss.suprabrowser.highlight.HighlightItems;
import ss.suprabrowser.highlight.HighlightProperties;
import ss.suprabrowser.htmldom.AlternativeHighlighter;
import ss.suprabrowser.htmldom.HTMLDOMHighlighter;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

public class MozillaBrowserController {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MozillaBrowserController.class);

	Hashtable session = null;

	private final SupraBrowser mb;

	private final JSDOMEventMonitor jsDEM;

	public MozillaBrowserController(SupraBrowser mb) {
		this.mb = mb;
		this.jsDEM = new JSDOMEventMonitor(mb);

	}
	
	public void closeJSEventMonitor() {
		this.jsDEM.stopMonitoring();

	}

	public void setCurrentSession(Hashtable session) {
		this.session = session;
	}

	public Hashtable getCurrentSession() {
		return this.session;
	}
	
	public SupraBrowser getBrowser() {
		return this.mb;
	}

	public void callJS() {
		MozillaBrowserController.this.mb.execute("page_setup_object.changeInput();");
		// mb.setUrl("javascript:blog_object.highlight();");
	}

	public String getHighlightStartOffset() {
		try {
			String startOffset = new Integer(MozillaBrowserController.this.mb.getBrowserSelection().getRangeAt(0).getStartOffset()).toString();

			return startOffset;
		} catch (Exception e) {
			logger.warn( "Can't get startoffset", e );
			return null;
		}
	}

	public String getHiglightEndOffset() {
		try {
			String endOffset = new Integer(MozillaBrowserController.this.mb.getBrowserSelection().getRangeAt(0).getEndOffset()).toString();
			return endOffset;
		} catch (Exception e) {
			logger.warn( "Can't get endoffset", e );
			return null;
		}
	}

	public Element checkHighlightRegionAndConstructElement(MessagesPane mp) {

		SupraBrowser mozilla = this.mb;

		Element element = DocumentHelper.createElement("web_highlight");

		String startOffset = mozilla.getMozillaBrowserController()
				.getHighlightStartOffset();

		String endOffset = mozilla.getMozillaBrowserController()
				.getHiglightEndOffset();

		String xpath = mozilla.getMozillaBrowserController()
				.getXPathForSelection();

		String selectedText = mozilla.getMozillaBrowserController()
				.getSelectedTextFromDocFrag();

		// For testing
		// sash4.getCurrentBrowser().getMozillaBrowserController().highlightTextArea();

		if (startOffset != null) {
			element.addAttribute("start_offset", startOffset);
			element.addAttribute("end_offset", endOffset);

			element.addAttribute("xpath", xpath);
			element.addAttribute("selected_text", selectedText);
			return element;
		} else {
			return null;
		}

	}

	public String getXPathForSelection() {

		try {
			String xpath = getXPath(this.mb.getBrowserSelection().getRangeAt(0)
					.getStartContainer());

			nsIDOMDocumentFragment docFrag = this.mb.getBrowserSelection().getRangeAt(0)
					.cloneContents();

			String selectedText = getText(docFrag);

			logger.info("SelectedText[" + selectedText + "]");

			/*int startOffSet = */this.mb.getBrowserSelection().getRangeAt(0).getStartOffset();
			int endOffSet = this.mb.getBrowserSelection().getRangeAt(0).getEndOffset();

			if (selectedText.lastIndexOf(" ") != -1)
				endOffSet = endOffSet - 1;
			logger.info("End selection range: " + endOffSet);

			xpath = xpath.substring(0, xpath.length() - 1);
			logger.info("XPath[" + xpath + "]");

			xpath = xpath.toLowerCase();

			return xpath;

		} catch (Exception e) {
			logger.warn( "getXPathForSelection failed", e );
			return null;
		}
	}

	public void highlightTextAreaFromElement(Element element,
			Document parentDoc, Document doc) {

		String startOffset = element.attributeValue("start_offset");

		String endOffset = element.attributeValue("end_offset");

		String xpath = element.attributeValue("xpath");

		String selectedText = element.attributeValue("selected_text");

		logger.warn("calling highlight: " + startOffset + " : " + endOffset
				+ " : " + xpath + " : " + selectedText);

		highlightRegion(xpath, startOffset, endOffset, selectedText, parentDoc,
				doc);

	}

	private static nsIDOMElement getCSSNode(String script,
			nsIDOMHTMLDocument htmlDOM) {

		nsIDOMElement cssElement = htmlDOM.createElement("style");

		nsIDOMNode style = htmlDOM.createTextNode(script);
		cssElement.appendChild(style);

		return cssElement;
	}

	@SuppressWarnings("unused")
	private static nsIDOMElement getCSSNodeAsLink(String href,
			nsIDOMHTMLDocument htmlDOM) {

		nsIDOMElement cssElement = htmlDOM.createElement("link");
		cssElement.setAttribute("rel", "stylesheet");
		cssElement.setAttribute("type", "text/css");
		cssElement.setAttribute("href", href);

		// nsIDOMNode style = htmlDOM.createTextNode(script);
		// cssElement.appendChild(style);

		return cssElement;
	}

	@SuppressWarnings("unused")
	private static nsIDOMElement getJSLibNode(String libPath,
			nsIDOMHTMLDocument htmlDOM) {
		nsIDOMElement jsElement = htmlDOM.createElement("script");
		jsElement.setAttribute("type", "text/javascript");
		jsElement.setAttribute("src", libPath);

		return jsElement;
	}

	public String stripNewlines(String inStr) {

		StringBuffer outBuf;

		outBuf = new StringBuffer(inStr.length());
		for (int i = 0; i < inStr.length(); ++i)
			switch (inStr.charAt(i)) {
			case '\n':

				// case '\t':
				// case ' ' :

				// do nothing
				break;
			case '\r':
				break;
			default:
				outBuf.append(inStr.charAt(i));

			}
		return outBuf.toString();

	}

	public void injectJS(Document doc) {

		// Set JavaScript path into Head node
		this.injectJS();
		// this.injectXMLDocumentIntoDom(doc.getRootElement().element("subject").attributeValue("value"),doc);

	}

	public String addSpaces(String text) {
		String newText;

		newText = text.replaceAll("><", "> <");

		return newText;
	}

	public String injectXMLDocumentIntoDom(Document doc) {

		final nsIDOMHTMLDocument domHtmlDocument = this.mb.getDomHtmlDocument();
		if ( domHtmlDocument == null ) {
			logger.warn( "Can't inject XML document into Doc, because target html document is null" );
			return null;
		}
		nsIDOMNode headNode = domHtmlDocument.getDocumentElement()
				.getFirstChild();

		String justDocText = doc.asXML();
		String stripped = this.stripNewlines(justDocText);

		stripped = this.addSpaces(stripped);
		stripped = VariousUtils.escapeSingleQuotes(stripped);

		String docAsTextPlusJS = "var xml_comment_unique='" + stripped + "';";

		logger.info("BEFORE INJECTING: " + docAsTextPlusJS);
		headNode
				.appendChild(getJSNode(docAsTextPlusJS, domHtmlDocument));
		return stripped;

	}

	public String injectXMLDocumentIntoDom(String subject, String responseId,
			Document doc) {
		logger.info("Subject[" + subject + "]");

		final nsIDOMHTMLDocument domHtmlDocument = this.mb.getDomHtmlDocument();
		if ( domHtmlDocument == null ) {
			logger.warn( "Can't inject XML document into Doc, because target html document is null" );
			return null;
		}
		nsIDOMNode headNode = domHtmlDocument.getDocumentElement()
				.getFirstChild();

		String justDocText = doc.asXML();
		logger.info("Docment at least: " + justDocText);
		String stripped = this.stripNewlines(justDocText);

		stripped = this.addSpaces(stripped);
		stripped = VariousUtils.escapeSingleQuotes(stripped);

		String docAsTextPlusJS = "var xml_comment_unique='" + stripped + "';";

		logger.info("BEFORE INJECTING: " + docAsTextPlusJS);
		headNode
				.appendChild(getJSNode(docAsTextPlusJS, domHtmlDocument));

		String responseJS = "var responseId='" + responseId + "';";

		String subjectJS = "var actSubject='" + subject + "';";
		headNode
				.appendChild(getJSNode(subjectJS, domHtmlDocument));
		headNode
				.appendChild(getJSNode(responseJS, domHtmlDocument));

		return stripped;

	}

	public void injectJS() {

		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				SupraBrowser sb  = MozillaBrowserController.this.mb;

				if (logger.isDebugEnabled()) {
					logger.debug("start injecting");
				}
					nsIDOMHTMLDocument htmlDoc = sb.getDomHtmlDocument();
					if ( htmlDoc == null ) {
						logger.error( "Can't insert JS. Document is null" );
						return;
					}
					nsIDOMNode headNode = htmlDoc.getDocumentElement().getFirstChild();
					
					// Set JavaScript path into Head node
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/scroller_object.js"), htmlDoc));
					
					//getCSSNode(script, htmlDOM)
					
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/research_object.js"), htmlDoc));
					
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/richtext_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/advanced_object.js"), htmlDoc));

					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/search_object.js"), htmlDoc));

					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/comment_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/insert_message_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/blog_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/debug_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/drag_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/shade_object.js"), htmlDoc));
					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/unshade_object.js"), htmlDoc));

					headNode.appendChild(getJSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/observe.js"), htmlDoc));
					headNode.appendChild(getCSSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/comments.css"), htmlDoc));
					headNode.appendChild(getCSSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/microblog.css"), htmlDoc));
					headNode.appendChild(getCSSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/research.css"), htmlDoc));
					headNode.appendChild(getCSSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/search.css"), htmlDoc));
					headNode.appendChild(getCSSNode(JSHelper
							.readJSFromFile(LocationUtils.getMicroblogBase()+"microblog/keyword_highlight.css"), htmlDoc));

					if (logger.isDebugEnabled()) {
						logger.debug("finish injecting");
					}
				}
		});
	}

	public void openNewTabAction(final String newTabURL, final boolean selectBrowser) {
		Thread t = new Thread() {
			public void run() {
				SupraSphereFrame.INSTANCE
						.addMozillaTab(getCurrentSession(), null,
								new SimpleBrowserDataSource(newTabURL), selectBrowser, null);
			}
		};
		t.start();
	}

	public void setTitleToNewURL(String newTabURL) {
		final SupraCTabItem selectedSupraItem = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedSupraItem();
		if (selectedSupraItem  != null ) {
			selectedSupraItem.setTitleDelayed(
				null, new SimpleBrowserDataSource(newTabURL),
				SupraSphereFrame.INSTANCE, MozillaBrowserController.this.mb);
		}
	}

	public void addBrowserLocationListener() {
		final SupraCTabItem selectedSupraItem = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedSupraItem();
		if ( selectedSupraItem != null && !selectedSupraItem
				.isBrowserLocationListening()) {
			selectedSupraItem.addBrowserLocationListener(SupraSphereFrame.INSTANCE, null);
		}
	}

	public void highlightRegion(String xpath, String startOffset,
			String endOffset, String selectedText, Document parentDoc,
			Document xmlDoc) {
		logger.info("Xpath in highlight region: " + xpath);
		// Document consolidatedThreadDoc =
		// XMLSchemaTransform.consolidateCommentsAndRoot(parentDoc,xmlDoc);

		// System.out.println("CON: "+consolidatedThreadDoc.asXML());
		// this.injectXMLDocumentIntoDom(consolidatedThreadDoc);
		Document doc = (Document) xmlDoc.clone(); // Prevent it from getting
		// manipulated

		HighlightProperties p = new HighlightProperties();

		try {
			String actualComment = doc.getRootElement().element("body")
					.elementText("orig_body");
			String subjectComment = doc.getRootElement().element("subject")
					.attributeValue("value");
			logger.info("&&&&&&&&&&&&&&&&&Original Value["
					+ actualComment + "]");
			logger.info("&&&&&&&&&&&&&&&&&Subject Value["
					+ subjectComment + "]");	

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

			// root.element("email").addElement("response_id").addAttribute("value",
			// responseId);
			final nsIDOMHTMLDocument document = this.mb.getDomHtmlDocument();
			if ( document == null ) {
				logger.warn( "Can't hightlight region, because html document is null" );
				return;
			}
			injectJS();

			this.injectXMLDocumentIntoDom(subjectComment, responseId,
							threadDoc);

			logger.warn(" Total script elements: "
					+ document.getElementsByTagName(
							"script").getLength());

			String text = getDocumentAsText(document);

			logger.warn("FUll text; " + text);
			// mb.getDocument().
			Mozilla moz = MozillaBrowserController.this.mb.getMozBrowser();
			if ( moz == null ) {
				logger.warn( "Can't hightlight region, because Mozilla Browser is null" );
				return;
			}

			nsIComponentManager componentManager = moz.getComponentManager();
			String NS_IDOMXPATHEVALUATOR_CONTACTID = "@mozilla.org/dom/xpath-evaluator;1"; //$NON-NLS-1$
			nsIDOMXPathEvaluator xpathEval = (nsIDOMXPathEvaluator) componentManager
					.createInstanceByContractID(
							NS_IDOMXPATHEVALUATOR_CONTACTID, null,
							nsIDOMXPathEvaluator.NS_IDOMXPATHEVALUATOR_IID);
			nsIDOMXPathNSResolver res = xpathEval.createNSResolver(document);
			// String xpath =
			// getXPath(mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartContainer());
			// nsIDOMDocumentFragment docFrag = mb.getWebBrowser()
			// .getContentDOMWindow().getSelection().getRangeAt(0)
			// .cloneContents();

			// String selectedText = getText(docFrag);

			int startOffSet = new Integer(startOffset).intValue();
			int endOffSet = new Integer(endOffset).intValue();

			// if (selectedText.lastIndexOf(" ") != -1)
			// endOffSet = endOffSet - 1;

			// xpath = xpath.substring(0, xpath.length()-1);

			xpath = xpath.toLowerCase();
			// String xpath = "html";

			logger.warn("XPATH: " + xpath);
			final nsIDOMNode context = document;
			nsISupports obj = xpathEval.evaluate(xpath, context, res,
					nsIDOMXPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

			nsIDOMXPathResult result = (nsIDOMXPathResult) obj
					.queryInterface(nsIDOMXPathResult.NS_IDOMXPATHRESULT_IID);

			nsIDOMNode node = null;
			String origText = "";
			for (int i = 0; i < result.getSnapshotLength(); i++) {

				for (int j = 0; j < result.snapshotItem(i).getChildNodes()
						.getLength(); j++) {
					origText = "";

					if (nsIDOMNode.TEXT_NODE == result.snapshotItem(i)
							.getChildNodes().item(j).getNodeType()
							&& !("").equals(result.snapshotItem(i)
									.getChildNodes().item(j).getNodeValue()
									.trim())) {
						origText = result.snapshotItem(i).getChildNodes().item(
								j).getNodeValue();

						node = result.snapshotItem(i).getChildNodes().item(j);
						if (origText.trim().indexOf(selectedText.trim()) != -1)
							break;
					}
				}
			}
			// nsIDOMNode node = result.snapshotItem(i).getFirstChild();
			nsIDOMNode parent = node.getParentNode();
			nsIDOMElement elem = MozillaBrowserController.this.mb.getWebBrowser().getContentDOMWindow()
					.getDocument().createElement("div");

			// elem.setAttribute("style", "color: rgb(0,0,0); background-color:
			// rgb(250,0,0); ");
			elem.setAttribute("style", p.assembleStyle());
			// elem.setAttribute("onmouseover", "alert('"+actualComment+"');");

			elem.setAttribute("class", "highlight");
			elem.setAttribute("id", responseId);
			// elem.setAttribute("onclick", "alert('"+threadDoc.asXML()+"');");
			logger.warn("Current threadDoc: " + threadDoc.asXML());

			elem.setAttribute("onclick", "javascript:executePopup('"
					+ responseId + "');");

			// elem.setAttribute("onclick", set_xml())
			// elem.setAttribute("onclick", "scroller_object.echo();");
			// elem.setAttribute ("onclick", "test.init ();");

			// if (0 == i)
			// {
			String s = origText.substring(0, startOffSet);
			if (s.length() != 0) {
				nsIDOMNode n = node.cloneNode(true);
				n.setNodeValue(s);
				parent.insertBefore(n, node);
			}

			if (endOffSet > 0)
				s = origText.substring(startOffSet, endOffSet);
			else
				s = origText.substring(startOffSet);

			if (s.length() != 0) {
				nsIDOMNode n = node.cloneNode(true);
				n.setNodeValue(s);
				elem.appendChild(n);
				parent.insertBefore(elem, node);
			}
			if (endOffSet > 0) {
				nsIDOMNode n = node.cloneNode(true);
				n.setNodeValue(origText.substring(endOffSet));
				parent.insertBefore(n, node);
			}
			// parent.

			nsIDOMNamedNodeMap nodeMap = parent.getAttributes();

			for (int i = 0; i < nodeMap.getLength(); i++) {

				nsIDOMNode singleNode = nodeMap.item(i);
				logger.info("Type: " + singleNode.getNodeType());
				logger.info("Value: " + singleNode.getNodeValue());
				logger.info("Name: " + singleNode.getNodeName());
			}

			logger.info("Calling execute popup right now");
			MozillaBrowserController.this.mb.execute("javascript:executePopup('" + responseId + "');");
			// mb.execute("javascript:testJS();");

			// }

			parent.removeChild(node);
		} catch (Exception ex) {
			logger.error("Can't highlight region", ex);
		}

	}

	public String getSelectedTextFromDocFrag() {
		final nsIWebBrowser webBrowser = MozillaBrowserController.this.mb
				.getWebBrowser();
		if (webBrowser != null) {
			nsIDOMDocumentFragment docFrag = webBrowser.getContentDOMWindow()
					.getSelection().getRangeAt(0).cloneContents();
			String selectedText = getText(docFrag);
			return selectedText;
		} else {
			logger.warn("WebBrowser is null");
			return null;
		}
	}

	public void highlightKeywords(Vector highlightKeywords,
			Hashtable assetsWithKeywordTag, boolean matchMultipleKeys, ResearchComponentDataContainer container) {

		this.injectJS();

		// this.
		// this.injectJSWithMultipleDocs(, taggedDocuments)
		// String mouseOverInfo =

		// "'<b><a href=\"http://www.suprasphere.com\">Microsoft Buys
		// Google</a></b><br><br> It appears that Microsoft will be buying
		// Google in the near future, reports AP Press.'";

		/*
		 * 
		 * "'Following are the example:" + "<br><ul>" + "<li><b><a
		 * href=\"http://google.com\">Google</a></b> : Google is moving into
		 * the enterprise space. They will likely add a suite of web-based
		 * applications to their appliance offering.</li>" + "<li><b><a
		 * href=\"http://www.suprasphere.com\">SupraSphere</a></b> : we are
		 * super!</li>" + "</ul></br>'";
		 * 
		 */

		/*
		 * p1.setOnmouseover("return overlib( eclipse,
		 * TEXTPADDING,6,CAPTION,'Sample title', " +
		 * "STICKY,WIDTH,200,BASE,2,REF,'sample1',REFC,'UR',REFP,'LL',REFX,10,REFY,50," +
		 * "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');");
		 */
		// p1
		// .setOnmouseover("return overlib( eclipse,
		// TEXTPADDING,6,CAPTION,'Sample title', "
		// + "STICKY,"
		// +
		// "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');");
		// p1.setOnmouseover("return overlib( eclipse, STICKY,
		// MOUSEOFF);");
		// p1.setOnmouseover("return overlib( eclipse, STICKY,
		// CLOSECOLOR,'#ffccff');");
		// p1.setOnmouseover("return overlib( eclipse);");
		// p1.setOnmouseout("nd();");
		// p1.setOnclick("nd();");
		// p1.setInfo("eclipse", mouseOverInfo);
		Document consolidatedThreadDoc = XMLSchemaTransform
				.consolidateMultipleToOneDocument(highlightKeywords,
						assetsWithKeywordTag);
		logger.info("Consolidated doc; " + consolidatedThreadDoc.asXML());

		String stripped = injectXMLDocumentIntoDom(consolidatedThreadDoc);

		VariousUtils.addXMLToFile("xml.js", stripped);
		HighlightItems highlightItems = new HighlightItems();

		for (int i = 0; i < highlightKeywords.size(); i++) {
			HighlightProperties p1 = new HighlightProperties();
			Document doc = (Document) highlightKeywords.get(i);
			p1.setId(doc.getRootElement().element("message_id").attributeValue(
					"value"));
			String keyword = doc.getRootElement().element("subject")
					.attributeValue("value");
			p1.setOnmouseover("research_object.addMark(event, '"+keyword+"');");
			p1.setOnclick("research_object.addClickMark('"+keyword+"');");

			highlightItems.add(keyword, p1);
		}

		HTMLDOMHighlighter htmlDOMHighlighter = HTMLDOMHighlighter
				.getInstance();

//		try {
//
//			htmlDOMHighlighter.processHTMLDOM(MozillaBrowserController.this.mb.getDomHtmlDocument(),
//					highlightItems,matchMultipleKeys);
//		} catch (Exception e) {
//			logger.error("Can't higlight", e);
//		}
		AlternativeHighlighter highlighter = new AlternativeHighlighter(this.mb, highlightItems, matchMultipleKeys, container);
		highlighter.highlightItems();
	}

	public void highlightTextArea() {

		logger.warn("Calling highlightTextArea()");

		/*int startOffSet = */MozillaBrowserController.this.mb.getWebBrowser().getContentDOMWindow()
				.getSelection().getRangeAt(0).getStartOffset();
		int endOffSet = MozillaBrowserController.this.mb.getWebBrowser().getContentDOMWindow()
				.getSelection().getRangeAt(0).getEndOffset();

		String xpath = getXPath(MozillaBrowserController.this.mb.getWebBrowser().getContentDOMWindow()
				.getSelection().getRangeAt(0).getStartContainer());

		String selectedText = this.getSelectedTextFromDocFrag();

		if (selectedText.lastIndexOf(" ") != -1)
			endOffSet = endOffSet - 1;
		logger.info("End selection range: " + endOffSet);

		xpath = xpath.substring(0, xpath.length() - 1);
		logger.info("XPath[" + xpath + "]");

		xpath = xpath.toLowerCase();
		// This needs xml passed through
		// this.highlightRegion(xpath, new Integer(startOffSet).toString(), new
		// Integer(endOffSet).toString(), selectedText,null);

	}

	private static nsIDOMElement getJSNode(String script,

	nsIDOMHTMLDocument htmlDOM) {
		nsIDOMElement jsElement = htmlDOM.createElement("script");
		jsElement.setAttribute("type", "text/javascript");
		nsIDOMNode js = htmlDOM.createTextNode(script);
		jsElement.appendChild(js);
		return jsElement;

	}

	private String getDocumentAsText(nsIDOMDocument docFrag) {
		StringBuffer text = new StringBuffer("");

		logger.warn("Length :" + docFrag.getChildNodes().getLength());
		for (int i = 0; i < docFrag.getElementsByTagName("script").getLength(); i++) {
			// if (nsIDOMNode.TEXT_NODE == docFrag.getChildNodes().item(i)
			// .getNodeType()) {
			try {
				logger.warn("One script: "
						+ docFrag.getElementsByTagName("script").item(i)
								.getFirstChild().getNodeValue());
			} catch (NullPointerException npe) {

			}
			// }
		}

		return text.toString();
	}

	private static String getText(nsIDOMDocumentFragment docFrag) {
		StringBuffer text = new StringBuffer("");

//		for (int i = 0; i < docFrag.getChildNodes().getLength(); i++) {
//			if (nsIDOMNode.TEXT_NODE == docFrag.getChildNodes().item(i)
//					.getNodeType()) {
//				text.append(docFrag.getChildNodes().item(i).getNodeValue());
//			}
//		}
		
		for(int i = 0; i < docFrag.getChildNodes().getLength(); i++) {
			nsIDOMNode node = docFrag.getChildNodes().item(i);
			if(node.getNodeValue()!=null) {
				text.append(node.getNodeValue());
			}
		}

		return text.toString();
	}

	private static String getXPath(nsIDOMNode node) {

		String xPath = "";
		String xPathTemp = "";
		int xPathIndex = 0;
		if (null == node)
			return xPathTemp;

		if (nsIDOMNode.ELEMENT_NODE == node.getNodeType()) {
			xPathTemp = node.getNodeName();

			xPathIndex = getXPathIndex(node);
			/*
			 * if (xPathIndex > 0) xPathIndex = xPathIndex - 1;
			 */
			// System.out.println("Tag["+xPathTemp+"]["+xPathIndex+"]");
		}

		getXPath(node.getParentNode());

		xPath = xPath + "/" + xPathTemp + ""
				+ (xPathIndex != 0 ? "[" + xPathIndex + "]" : "");

		return xPath;
	}

	private static int getXPathIndex(nsIDOMNode node) {
		int index = 1;
		nsIDOMNode preNode = node;

		while (null != preNode) {
			preNode = preNode.getPreviousSibling();
			if (null == preNode) {
				break;
			}

			if ((nsIDOMNode.ELEMENT_NODE == preNode.getNodeType())
					&& (preNode.getNodeName().equalsIgnoreCase(node
							.getNodeName()))) {
				index = index + 1;
			}
		}
		/*
		 * if (null == node.getPreviousSibling()) { return index; }
		 * 
		 * System.out.println("Pre["+node.getPreviousSibling().getNodeName()+"]");
		 * 
		 * 
		 * 
		 * getXPathIndex(node.getPreviousSibling(), null);
		 * 
		 * System.out.println("Nodename...............["+nodeName+"]");
		 * 
		 * index = index + 1;
		 */

		return index;
	}
}
