/**
 * 
 */
package ss.obsolete;

import java.awt.Color;

import ss.suprabrowser.exception.HTMLDOMHighlighterException;
import ss.suprabrowser.highlight.HighlightItems;
import ss.suprabrowser.highlight.HighlightProperties;
import ss.suprabrowser.htmldom.AlternativeHighlighter;
import ss.suprabrowser.htmldom.HTMLDOMHighlighter;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMXPathEvaluator;
import org.mozilla.interfaces.nsIDOMXPathNSResolver;
import org.mozilla.interfaces.nsIDOMXPathResult;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

import ss.client.ui.browser.SupraBrowser;
import ss.global.SSLogger;


/**
 * @author zobo
 * 
 */
public class MozillaButtonsListener implements Listener{
    static int count = 0;

    static String xPath = "";

    private SupraBrowser mb;
    
    private static final Logger logger = SSLogger.getLogger(MozillaButtonsListener.class);

    /**
     * 
     */
    public MozillaButtonsListener(SupraBrowser mb) {
        super();
        this.mb = mb;
    }

    @SuppressWarnings("static-access")
    public void handleEvent(Event event) {
        ToolItem item = (ToolItem) event.widget;
        String string = item.getText();

        logger.info("Text: " + string);
        final nsIDOMHTMLDocument domHtmlDocument = this.mb.getDomHtmlDocument();
		if (string.equals("Re-Search")) {
            String mouseOverInfo =

            "'<b><a href=\"http://www.suprasphere.com\">Microsoft Buys Google</a></b><br><br> It appears that Microsoft will be buying Google in the near future, reports AP Press.'";

            /*
             * 
             * "'Following are the example:" + "<br><ul>" + "<li><b><a
             * href=\"http://google.com\">Google</a></b> : Google is moving
             * into the enterprise space. They will likely add a suite of
             * web-based applications to their appliance offering.</li>" + "<li><b><a
             * href=\"http://www.suprasphere.com\">SupraSphere</a></b> : we
             * are super!</li>" + "</ul></br>'";
             * 
             */
            HighlightProperties p1 = new HighlightProperties();
            p1.setColor(Color.BLACK);
            p1.setBg_color(Color.red);
            /*
             * p1.setOnmouseover("return overlib( eclipse,
             * TEXTPADDING,6,CAPTION,'Sample title', " +
             * "STICKY,WIDTH,200,BASE,2,REF,'sample1',REFC,'UR',REFP,'LL',REFX,10,REFY,50," +
             * "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');");
             */
            p1
                    .setOnmouseover("return overlib( eclipse, TEXTPADDING,6,CAPTION,'Sample title', "
                            + "STICKY,"
                            + "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');");
            // p1.setOnmouseover("return overlib( eclipse, STICKY,
            // MOUSEOFF);");
            // p1.setOnmouseover("return overlib( eclipse, STICKY,
            // CLOSECOLOR,'#ffccff');");

            // p1.setOnmouseover("return overlib( eclipse);");
            p1.setOnmouseout("nd();");
            p1.setOnclick("nd();");

            p1.setInfo("eclipse", mouseOverInfo);

            HighlightItems highlightItems = new HighlightItems();
            highlightItems.add("nasD", p1);
            highlightItems.add("collaboration", p1);

            HTMLDOMHighlighter htmlDOMHighlighter = ss.suprabrowser.htmldom.HTMLDOMHighlighter
                    .getInstance();
            AlternativeHighlighter highlighter = new AlternativeHighlighter(this.mb, highlightItems, true);
    		highlighter.highlightItems();
//            try {
//                htmlDOMHighlighter.processHTMLDOM(domHtmlDocument,
//                        highlightItems,true);
//            } catch (HTMLDOMHighlighterException e) {
//                logger.error(e.getMessage(), e);
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
        } else if (string.equals("Text")) {
            count = 0;
            xPath = "";
            // TODO:Overlib
            /*
             * String mouseOverInfoJS = "return overlib( eclipse"+count+",
             * TEXTPADDING,6,CAPTION,'Sample title', " + "STICKY," +
             * "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');";
             */
            /*
             * String mouseOverInfoJS = "return coolTip(
             * eclipse"+count+",CAPTION,'Sample title', " + "STICKY," +
             * "BGCOLOR,'#333399',CLOSECOLOR,'#ffccff');";
             */
            // String mouseOverInfoJS = "return coolTip('This is the phrase in
            // the coolTip');";
            String mouseOverInfoJS = "javascript:checkout();";

            String mouseOverInfo = "";
            if (count == 0)

                mouseOverInfo =

                "'<b><a href=\"http://www.suprasphere.com\">Microsoft Buys Google</a></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<B onmouseover=\"javascript:checkout()\">+</B></a><br><br> It appears that Microsoft will be buying Google in the near future, reports AP Press.'";

            else

                mouseOverInfo =

                "'<b><a href=\"http://www.suprasphere.com\">Suprasphere Buys Google</a></b><br><br> It appears that Microsoft will be buying Google in the near future, reports AP Press.'";

            HighlightProperties p = new HighlightProperties();
            p.setInfo("eclipse" + count, mouseOverInfo);

            nsIDOMNode headNode = domHtmlDocument.getDocumentElement()
                    .getFirstChild();
            nsIDOMNode bodyNode = headNode.getNextSibling();

            nsIDOMElement div = domHtmlDocument.createElement("div");
            div.setAttribute("id", "ctDiv");
            div.setAttribute("style",
                    "position: absolute; visibility: hidden; z-index: 1000;");

            bodyNode.insertBefore(div, bodyNode.getFirstChild());

            if (count == 0) {
                // Set JavaScript path into Head node
                // TODO:overlib
                // headNode.appendChild(getJSLibNode("http://www.macridesweb.com/oltest/overlibmws.js",
                // mb.getDocument()));

                headNode.appendChild(getJSLibNode(
                        "http://www.acooltip.com/srcCode/cCore.js", domHtmlDocument));
                // headNode.appendChild(getJSLibNode("http://www.acooltip.com/srcCode/cExclusive.js",
                // mb.getDocument()));
            }

            // Load all the highlight properties into Head node
            // TODO:overlib
            // headNode.appendChild(getJSNode(p.getInfo(), mb.getDocument()));
            // headNode.appendChild(getJSNode("function checkout(){return
            // overlib( eclipse0, TEXTPADDING,6,CAPTION,'Sample
            // title',STICKY,FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');}",
            // mb.getDocument()));
            headNode
                    .appendChild(getJSNode(
                            "function checkout(){ alert('Murali'); return coolTip('This is the phrase in the coolTip'); alert('Murali1');}",
                            domHtmlDocument));

            // System.out.println("Debug");
            try {

                logger.info("Start selection range: "
                        + this.mb.getWebBrowser().getContentDOMWindow()
                                .getSelection().getRangeAt(0).getStartOffset());

                logger.info("End selection range: "
                        + this.mb.getWebBrowser().getContentDOMWindow()
                                .getSelection().getRangeAt(0).getEndOffset());

                // System.out.println(" ID:
                // "+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).NS_IDOMRANGE_IID);

                // The offsets above are relative to the anchor node. TODO: Find
                // some way to access the same position in the DOM again, either
                // via xpath or some other mechanism. DJ@

                nsIDOMHTMLDocument document = domHtmlDocument;

                Mozilla moz = this.mb.getMozBrowser();

                nsIComponentManager componentManager = moz
                        .getComponentManager();
                String NS_IDOMXPATHEVALUATOR_CONTACTID = "@mozilla.org/dom/xpath-evaluator;1"; //$NON-NLS-1$
                nsIDOMXPathEvaluator xpathEval = (nsIDOMXPathEvaluator) componentManager
                        .createInstanceByContractID(
                                NS_IDOMXPATHEVALUATOR_CONTACTID, null,
                                nsIDOMXPathEvaluator.NS_IDOMXPATHEVALUATOR_IID);
                nsIDOMXPathNSResolver res = xpathEval
                        .createNSResolver(document);
                // String xpath =
                // "//HTML/BODY/TABLE/TBODY/TR/TD/TABLE/TBODY/TR/TD/mur/P[4]";
                // String xpath =
                // "/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/mur/p[0]";
                // String xpath =
                // "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/P[1]";
                String xpath = getXPath(this.mb.getWebBrowser()
                        .getContentDOMWindow().getSelection().getRangeAt(0)
                        .getStartContainer());

                nsIDOMDocumentFragment docFrag = this.mb.getWebBrowser()
                        .getContentDOMWindow().getSelection().getRangeAt(0)
                        .cloneContents();

                String selectedText = getText(docFrag);

                logger.info("SelectedText[" + selectedText + "]");

                int startOffSet = this.mb.getWebBrowser().getContentDOMWindow()
                        .getSelection().getRangeAt(0).getStartOffset();
                int endOffSet = this.mb.getWebBrowser().getContentDOMWindow()
                        .getSelection().getRangeAt(0).getEndOffset();

                if (selectedText.lastIndexOf(" ") != -1)
                    endOffSet = endOffSet - 1;
                logger.info("End selection range: " + endOffSet);

                xpath = xpath.substring(0, xpath.length() - 1);
                logger.info("XPath[" + xpath + "]");

                xpath = xpath.toLowerCase();
                // String xpath = "html";

                nsIDOMNode context = domHtmlDocument;
                nsISupports obj = xpathEval.evaluate(xpath, context, res,
                        nsIDOMXPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

                nsIDOMXPathResult result = (nsIDOMXPathResult) obj
                        .queryInterface(nsIDOMXPathResult.NS_IDOMXPATHRESULT_IID);

                // System.out.println("Result
                // Size["+result.getSnapshotLength()+"]");
                // System.out.println("String
                // Value["+result.getStringValue()+"]");
                // System.out.println("Single Node
                // Value["+result.getSingleNodeValue().getNodeName()+"]");
                // System.out.println("StartOffSet["+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartOffset()+"]");
                // System.out.println("EndOffSet["+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getEndOffset()+"]");

                try {
                    nsIDOMNode node = null;
                    String origText = "";
                    for (int i = 0; i < result.getSnapshotLength(); i++) {

                        for (int j = 0; j < result.snapshotItem(i)
                                .getChildNodes().getLength(); j++) {
                            origText = "";

                            // System.out.println("asdf:
                            // "+result.snapshotItem(0));
                            if (nsIDOMNode.TEXT_NODE == result.snapshotItem(i)
                                    .getChildNodes().item(j).getNodeType()
                                    && !("").equals(result.snapshotItem(i)
                                            .getChildNodes().item(j)
                                            .getNodeValue().trim())) {
                                origText = result.snapshotItem(i)
                                        .getChildNodes().item(j).getNodeValue();
                                logger.info("Original Text["
                                        + origText.trim() + "]");
                                node = result.snapshotItem(i).getChildNodes()
                                        .item(j);
                                if (origText.trim()
                                        .indexOf(selectedText.trim()) != -1)
                                    break;
                            }
                        }
                    }
                    // nsIDOMNode node = result.snapshotItem(i).getFirstChild();
                    nsIDOMNode parent = node.getParentNode();
                    nsIDOMElement elem = this.mb.getWebBrowser()
                            .getContentDOMWindow().getDocument().createElement(
                                    "B");

                    // elem.setAttribute("style", "color: rgb(0,0,0);
                    // background-color: rgb(250,0,0); ");
                    elem.setAttribute("style", p.assembleStyle());
                    elem.setAttribute("onmouseover", mouseOverInfoJS);
                    elem.setAttribute("onmouseout", "nd()");

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

                    parent.removeChild(node);

                    // }

                } catch (NullPointerException npe) {

                }

                // rNodes.add(result.snapshotItem(i));

                /*
                 * nsIDOMElement elem = mb.getWebBrowser()
                 * .getContentDOMWindow().getDocument() .createElement("B");
                 * 
                 * 
                 * String mouseOverInfoJS = "return overlib( eclipse"+count+",
                 * TEXTPADDING,6,CAPTION,'Sample title', " + "STICKY," +
                 * "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');";
                 * 
                 * //elem.setAttribute("style", "color: rgb(0,0,0);
                 * background-color: rgb(250,0,0); ");
                 * elem.setAttribute("style", p.assembleStyle());
                 * elem.setAttribute("onmouseover", mouseOverInfoJS);
                 * 
                 * System.out.println("Start Off
                 * Set["+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartOffset()+"]");
                 * System.out.println("End Off
                 * Set["+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getEndOffset()+"]");
                 * 
                 * mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).queryInterface(mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).NS_IDOMRANGE_IID);
                 * 
                 * System.out.println(getXPath(mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartContainer()));
                 * System.out.println("Index["+getXPathIndex(mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartContainer())+"]");
                 * 
                 * 
                 * 
                 * nsIDOMNode frag =
                 * mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).extractContents();
                 * nsIDOMNode newFrag = frag.cloneNode(true);
                 */

                /*
                 * nsIDOMNode parentFrag = frag.getParentNode();
                 * parentFrag.insertBefore(elem, frag);
                 * //parentFrag.removeChild(frag); elem.appendChild(newFrag);
                 */

                /*
                 * System.out.println("Murali["+frag.getChildNodes().item(0).getNodeValue()+"]");
                 * System.out.println("Murali["+frag.getChildNodes().item(0).getNodeType()+"]");
                 * 
                 * 
                 * 
                 * elem.appendChild(newFrag);
                 * 
                 * count = count + 1;
                 */

                /*
                 * nsIDOMText text = mb.getWebBrowser()
                 * .getContentDOMWindow().getDocument()
                 * .createTextNode(frag.getChildNodes().item(0).getNodeValue());
                 */

                // nsIDOMNode testNode =
                // mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartContainer().cloneNode(true);
                // System.out.println("Murali["+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getStartContainer().getNodeValue()+"]");
                // mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).deleteContents();
                // elem.appendChild(text);
                // mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).insertNode(elem);
                // mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).selectNodeContents(mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getCommonAncestorContainer());

                // nsIDOMDocumentFragment fragment =
                // mb.getWebBrowser().getContentDOMWindow().getSelectiion().getRangeAt(0).cloneContents();
                // System.out.println("Name: "+fragment.getNodeName());
                // System.out.println("ACTUA:
                // "+fragment.getFirstChild().getNodeValue()); // It
                // needs to iterate through all the nodes as a node list
                // and then process
                // System.out.println("ACTUA:
                // "+fragment.getLastChild().getNodeValue());
                // System.out.println("HI this is text selected:
                // "+mb.getWebBrowser().getContentDOMWindow().getSelection().getRangeAt(0).getEndOffset());
                // mb.setData(doc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);

            }

        } else if (string.equals("XPath")) {
            // updateDOM("//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
            // 395, 401, "search ", mb);
            // updateDOM("//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
            // 391, -1, "web ", mb);
            // updateDOM("//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
            // 1, 8, "through ", mb);
            updateDOM(
                    "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/P[1]",
                    0,
                    100,
                    "Gartner estimates that IM will replace e-mail as the primary corporate communications tool by 2006,",
                    this.mb);
            updateDOM(
                    "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/P[5]",
                    103, 145, "corporate setting to improve productivity.", this.mb);

            updateDOM(
                    "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
                    0,
                    145,
                    "SupraSphere is a light-weight extremely secure collaboration product. It integrates the best functionality of numerous commonly used applications",
                    this.mb);
            updateDOM(
                    "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
                    131,
                    232,
                    "The unique features include: group instant messaging with multi-site message delivery for compliance, ",
                    this.mb);
            updateDOM(
                    "//HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[3]/TD[3]/TABLE[1]/TBODY[1]/TR[1]/TD[1]",
                    152,
                    243,
                    "much you needed until you had it, and one that your competitors are honestly glad you don't.",
                    this.mb);
        }
    }

    private static nsIDOMElement getJSNode(String script,
            nsIDOMHTMLDocument htmlDOM) {
        nsIDOMElement jsElement = htmlDOM.createElement("script");
        jsElement.setAttribute("type", "text/javascript");
        nsIDOMNode js = htmlDOM.createTextNode(script);
        jsElement.appendChild(js);

        return jsElement;
    }

    private static nsIDOMElement getJSLibNode(String libPath,
            nsIDOMHTMLDocument htmlDOM) {
        nsIDOMElement jsElement = htmlDOM.createElement("script");
        jsElement.setAttribute("type", "text/javascript");
        jsElement.setAttribute("src", libPath);

        return jsElement;
    }

    private static String getXPath(nsIDOMNode node) {
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

    /*
     * private static int getXPathIndex(nsIDOMNode node, String nodeName) { if
     * (null == node.getPreviousSibling()) { return index; }
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
     * 
     * return index; }
     */
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

    private static String getText(nsIDOMDocumentFragment docFrag) {
        StringBuffer text = new StringBuffer("");

        for (int i = 0; i < docFrag.getChildNodes().getLength(); i++) {
            if (nsIDOMNode.TEXT_NODE == docFrag.getChildNodes().item(i)
                    .getNodeType()) {
                text.append(docFrag.getChildNodes().item(i).getNodeValue());
            }
        }

        return text.toString();
    }

    private static void updateDOM(String xPath, int startOffSet, int endOffSet,
            String selectedText, SupraBrowser mb) {
        String mouseOverInfoJS = "return overlib( eclipse"
                + count
                + ", TEXTPADDING,6,CAPTION,'Sample title', "
                + "STICKY,"
                + "FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');";

        String mouseOverInfo = "";

        if (count == 0)

            mouseOverInfo = "'<b><a href=\"http://www.suprasphere.com\">Microsoft Buys Google</a></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<B onmouseover=\"javascript:checkout()\">+</B></a><br><br> It appears that Microsoft will be buying Google in the near future, reports AP Press.'";

        else

            mouseOverInfo = "'<b><a href=\"http://www.suprasphere.com\">Suprasphere Buys Google</a></b><br><br> It appears that Microsoft will be buying Google in the near future, reports AP Press.'";

        HighlightProperties p = new HighlightProperties();
        p.setInfo("eclipse" + count, mouseOverInfo);

        final nsIDOMHTMLDocument domHtmlDocument = mb.getDomHtmlDocument();
		nsIDOMNode headNode = domHtmlDocument.getDocumentElement()
                .getFirstChild();

        if (count == 0)
            // Set JavaScript path into Head node
            headNode.appendChild(getJSLibNode(
                    "http://www.macridesweb.com/oltest/overlibmws.js", domHtmlDocument));

        // Load all the highlight properties into Head node
        headNode.appendChild(getJSNode(p.getInfo(), domHtmlDocument));
        headNode
                .appendChild(getJSNode(
                        "function checkout(){return overlib( eclipse0, TEXTPADDING,6,CAPTION,'Sample title',STICKY,FGCOLOR,'#ffffcc',BGCOLOR,'#333399',CGCOLOR,'#336699',CLOSECOLOR,'#ffccff');}",
                        domHtmlDocument));

        nsIDOMHTMLDocument document = domHtmlDocument;

        Mozilla moz = mb.getMozBrowser();
        nsIComponentManager componentManager = moz.getComponentManager();
        String NS_IDOMXPATHEVALUATOR_CONTACTID = "@mozilla.org/dom/xpath-evaluator;1"; //$NON-NLS-1$
        nsIDOMXPathEvaluator xpathEval = (nsIDOMXPathEvaluator) componentManager
                .createInstanceByContractID(NS_IDOMXPATHEVALUATOR_CONTACTID,
                        null, nsIDOMXPathEvaluator.NS_IDOMXPATHEVALUATOR_IID);
        nsIDOMXPathNSResolver res = xpathEval.createNSResolver(document);

        nsIDOMNode context = domHtmlDocument;
        nsISupports obj = xpathEval.evaluate(xPath.toLowerCase(), context, res,
                nsIDOMXPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        nsIDOMXPathResult result = (nsIDOMXPathResult) obj
                .queryInterface(nsIDOMXPathResult.NS_IDOMXPATHRESULT_IID);

        selectedText = selectedText.trim();

        try {
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
                        logger.info("Original Text[" + origText.trim()
                                + "]");
                        node = result.snapshotItem(i).getChildNodes().item(j);
                        if (origText.trim().indexOf(selectedText.trim()) != -1)
                            break;
                    }
                }
            }

            nsIDOMNode parent = node.getParentNode();
            nsIDOMElement elem = mb.getWebBrowser().getContentDOMWindow()
                    .getDocument().createElement("B");

            elem.setAttribute("style", p.assembleStyle());
            elem.setAttribute("onmouseover", mouseOverInfoJS);

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

            parent.removeChild(node);
        } catch (NullPointerException npe) {
        }
    }
};
