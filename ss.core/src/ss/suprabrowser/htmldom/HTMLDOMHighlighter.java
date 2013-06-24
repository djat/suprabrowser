package ss.suprabrowser.htmldom;

import org.apache.log4j.Logger;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;

import ss.global.SSLogger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import ss.suprabrowser.highlight.HighlightItems;
import ss.suprabrowser.highlight.HighlightProperties;
import ss.suprabrowser.exception.HTMLDOMHighlighterException;
import ss.suprabrowser.common.HTMLDOMHighlighterConstants;

/**
 * User: krishnm
 * Date: Jul 26, 2006
 * Time: 8:27:54 PM
 */

/**
 * Traverses through the HTML DOM and hightlights the keywords in the text nodes of the DOM.
 * The keywords are provided as HightlightItems object. See HightlightItems class for more information.
 */

public class HTMLDOMHighlighter
{
    private static HTMLDOMHighlighter highlighter = null;
    private ArrayList<nsIDOMNode> textNodeList;
    private HighlightItems highlightItems;
    private nsIDOMHTMLDocument htmlDOM;
    private nsIDOMNode headNode;
    
    private static final Logger logger = SSLogger.getLogger(HTMLDOMHighlighter.class);

    private HTMLDOMHighlighter()
    {
    }

    //Single instance
    public static HTMLDOMHighlighter getInstance()
    {
        if (null == highlighter)
            highlighter = new HTMLDOMHighlighter();
        return highlighter;
    }

    /**
     * This method is the entry point into the HTMLDOMHighlighter module which calls various helper methods to
     * traverse the HTML DOM tree and highlight the keywords.
     * @param htmlDOM
     * @param highlightItems
     * @throws HTMLDOMHighlighterException
     */
    public void processHTMLDOM(nsIDOMHTMLDocument htmlDOM, HighlightItems highlightItems, boolean matchMultipleKeys) throws HTMLDOMHighlighterException
    {
		try {
			// Check for null data
			if (null == htmlDOM || null == highlightItems) {
				throw new HTMLDOMHighlighterException(
						HTMLDOMHighlighterConstants.ERR_HTMLDOM_INPUT);
			}

			this.highlightItems = highlightItems;
			this.htmlDOM = htmlDOM;

			this.textNodeList = new ArrayList<nsIDOMNode>();

			// Get all the keyword contained nodes to hightlight
			getKeyWordContainedNodes(htmlDOM.getChildNodes());

			String keywords[] = highlightItems.getKeywords();

			if (null == keywords || 0 == keywords.length) {
				throw new HTMLDOMHighlighterException(
						HTMLDOMHighlighterConstants.ERR_KEYWORD_INPUT);
			}

			Hashtable<String, Integer> keywordCounterTable = new Hashtable<String, Integer>();
			fillCounterTable(keywordCounterTable, keywords);

			// In a given text node, hightlight all the keywords
			for (int i = 0; i < this.textNodeList.size(); i++) {
				for (int j = 0; j < keywords.length; j++) {
					boolean highlighted = false;
					int count = keywordCounterTable.get(keywords[j]);
					if(count>3) {
						continue;
					}
					String multKeys = (String) keywords[j];
					if (false) {//if (matchMultipleKeys) {
						StringTokenizer st = new StringTokenizer(multKeys, " ");
						while (st.hasMoreTokens()) {
							String keyword = st.nextToken();
							logger.info("Doing highlight for THIS keyword: "
									+ keyword);
							highlighted = highlightNode(this.textNodeList
									.get(i), keywords[j], keyword, j);
						}
					} else {
						highlighted = highlightNode(this.textNodeList.get(i),
								keywords[j], multKeys, j);
					}
					if (highlighted) {
						keywordCounterTable.put(keywords[j], count+1);
					}
				}
			}

			this.headNode = htmlDOM.getDocumentElement().getFirstChild();

			// Set JavaScript path into Head node
			// TODO:Implement this after integration with Punya
			// headNode.appendChild(getJSLibNode("http://www.macridesweb.com/oltest/overlibmws.js"));

			// Load all the highlight properties into Head node

			this.headNode.appendChild(getJSNode(highlightItems
					.getPropertyInfo()));

		} catch (HTMLDOMHighlighterException e) {
			throw e;
		} catch (Exception e) {
			throw new HTMLDOMHighlighterException(e.getMessage());
		}
	}

    /**
	 * @param keywordCounterTable
	 * @param keywords
	 */
	private void fillCounterTable(
			Hashtable<String, Integer> keywordCounterTable, String[] keywords) {
		for(String keyword : keywords) {
			keywordCounterTable.put(keyword, 0);
		}
	}

	/**
	 * Adds all the text nodes which has keywords in HTML DOM to the list.
	 * 
	 * @param dom
	 * @return nsIDOMNode ArrayList
	 */
    private ArrayList<nsIDOMNode> getKeyWordContainedNodes(nsIDOMNodeList dom)
    {
        for (int i=0; i<dom.getLength(); i++)
        {
            if (dom.item(i).getNodeType() == Node.TEXT_NODE)
            {
                if (keywordFound(dom.item(i).getNodeValue()))
                {
                    addToTextNodeList(dom.item(i));
                }
            }

            getKeyWordContainedNodes(dom.item(i).getChildNodes());
        }
        return this.textNodeList;
    }

    /**
     * Checks for the keyword in the given text node value.
     * @param textNodeValue
     * @return true = Keyword found, false = keyword not found
     */

    private boolean keywordFound(String textNodeValue)
    {
        if (null == textNodeValue || ("").equals(textNodeValue))
        {
            return false;
        }

        textNodeValue = textNodeValue.toLowerCase();

        String keywords[] = this.highlightItems.getKeywords();

        if (null == keywords || 0 == keywords.length)
        {
            return false;
        }

        for (int i=0; i<keywords.length; i++)
        {
        	StringTokenizer st = new StringTokenizer(keywords[i].trim()," ");
        	while (st.hasMoreTokens()) {
        		String keyword = st.nextToken();
            if ((!keyword.equals("")) && (textNodeValue.indexOf(keyword.toLowerCase()) >-1))
            {
                return true;
            }
        	}
        }

        return false;
    }

    /**
     *  Adds a give node to the arraylist.
     * @param node
     */
    private void addToTextNodeList(nsIDOMNode node)
    {
    	this.textNodeList.add(node);
    }

    /**
     *  Adds html nodes around the keywords if found in the text node. Adding html nodes around the keywords
     * makes the keyword hightlight and if cursor hovers over the hightlighted keyword, javascript window
     * pops up.
     * @param node
     * @param keyword
     * @throws HTMLDOMHighlighterException
     */
    
    private boolean highlightNode(nsIDOMNode node, String keywords, String oneKeyword, int order) throws HTMLDOMHighlighterException
    {
    	
    	nsIDOMNode parent = node.getParentNode();

        if (parent == null) {
        		return false;
        }
        if (Node.ELEMENT_NODE != parent.getNodeType()) {
        	return false;
        }

        HighlightProperties property = this.highlightItems.get(keywords);

        if (null == property)
        {
            throw new HTMLDOMHighlighterException(HTMLDOMHighlighterConstants.ERR_HIGHLIGHTPROP_INPUT+"["+keywords+"]");
        }

        nsIDOMElement newNode = this.htmlDOM.createElement("span");
        
        newNode.setAttribute("onmouseover", property.getOnmouseover());
        newNode.setAttribute("onclick", property.getOnclick());
        newNode.setAttribute("class", "highlight"+(order%10));
        
        logger.info("SETTING ID HERE: "+property.getId());
        logger.info("property for keyword: "+keywords);
        newNode.setAttribute("id", property.getId());

        nsIDOMNode keywordNode = node.cloneNode(true);

        // original text in this node
        String origText = node.getNodeValue();

        
        if(null == origText || origText.trim() == "") {
            return false;
        
        }
        
        String lowerCaseorigText = origText.toLowerCase();
        

        logger.info("CHECKING THIS ONE KEYWORD: "+oneKeyword+ " : part of : "+keywords);
        String lowerCaseKeyword = oneKeyword.toLowerCase();

        if(lowerCaseorigText.indexOf(lowerCaseKeyword) < 0 ) {
        	logger.info("RETURN HERE? "+lowerCaseorigText);
        	return false;
        }

        int klen = oneKeyword.length();

        while(lowerCaseorigText.indexOf(lowerCaseKeyword) > -1)
        {
            int index = lowerCaseorigText.indexOf(lowerCaseKeyword);
            String s = origText.substring(0, index);

            if(s.length() != 0)
            {
                nsIDOMNode n = node.cloneNode(true);
                n.setNodeValue(s);
                parent.insertBefore(n, node);
            }

            // you cannot append the same node to the same parent.
            keywordNode.setNodeValue(origText.substring(index, index + klen));
            newNode.appendChild(keywordNode);

            parent.insertBefore(newNode.cloneNode(true), node);
            origText = origText.substring(index + klen);
            lowerCaseorigText = lowerCaseorigText.substring(index + klen);
        }
        

        if (origText.length() > 0)
        {
            nsIDOMNode n = node.cloneNode(true);
            n.setNodeValue(origText);
            parent.insertBefore(n, node);
        }

        // remove the original text at last.
        parent.removeChild(node);
        return true;
    }

    /**
	 * 
	 */
	@SuppressWarnings("unused")
	private void logScript(nsIDOMHTMLDocument doc) {
		nsIDOMNodeList list = doc.getElementsByTagName("script");
		logNodeScript(list);
	}
	
	private void logNodeScript(nsIDOMNodeList nodes) {
		for(int i = 0; i<nodes.getLength(); i++) {
			nsIDOMNode node = nodes.item(i);
			if(node.getNodeType()==nsIDOMNode.TEXT_NODE) {
				logger.info(node.getNodeValue()+"\n\n");
			} else {
				logNodeScript(node.getChildNodes());
			}
		}
	}

	/**
     * Creates the Javascript DOM element which can be injected into HTML DOM.
     * @param libPath
     * @return nsIDOMElement
     */
    @SuppressWarnings("unused")
	private nsIDOMElement getJSLibNode(String libPath)
    {
        nsIDOMElement jsElement = this.htmlDOM.createElement("script");
        jsElement.setAttribute("type", "text/javascript");
        jsElement.setAttribute("src", libPath);

        return jsElement;
    }

    /**
     * Creates the Javascript DOM element which can be injected into HTML DOM.
     * @param script
     * @return nsIDOMElement
     */
    private nsIDOMElement getJSNode(String script)
    {
        nsIDOMElement jsElement = this.htmlDOM.createElement("script");
        jsElement.setAttribute("type", "text/javascript");
        nsIDOMNode js = this.htmlDOM.createTextNode(script);
        jsElement.appendChild(js);

        return jsElement;
    }
}
