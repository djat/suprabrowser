/**
 * 
 */
package ss.suprabrowser.htmldom;

import org.apache.log4j.Logger;
import org.mozilla.interfaces.nsIDOMDocumentRange;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMHTMLElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsIFind;

import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;
import ss.global.SSLogger;
import ss.suprabrowser.highlight.HighlightItems;
import ss.suprabrowser.highlight.HighlightProperties;

/**
 * @author roman
 *
 */
public class AlternativeHighlighter {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(AlternativeHighlighter.class);
	
	private final HighlightItems items;
	private nsIFind finder;
	private final nsIDOMHTMLDocument domDoc;
	private final ResearchComponentDataContainer dataContainer;
	
	public AlternativeHighlighter(final SupraBrowser browser, HighlightItems highlightItems, boolean matchMultipleKeys, 
			final ResearchComponentDataContainer dataContainer) {
		this.items = highlightItems;
		this.finder = browser.getFinder();
		this.domDoc = browser.getDomHtmlDocument();
		this.dataContainer = dataContainer;
	}
	
	public AlternativeHighlighter(final SupraBrowser browser, HighlightItems highlightItems, boolean matchMultipleKeys) {
		this(browser, highlightItems, matchMultipleKeys, null);
	}
	
	public void highlightItems() {
		nsIDOMHTMLElement body = this.domDoc.getBody();
		int bodyLength = (int)body.getChildNodes().getLength();
		
		int j=-1;
		for(String str : this.items.getOrder() ) {
			int i=0;
			nsIDOMRange startRange = getRangeByParameters(body, 0, 0);
			nsIDOMRange endRange = getRangeByParameters(body, bodyLength, bodyLength);
			nsIDOMRange result = null;
			result = this.finder.find(str, getSearchRange(), startRange, endRange);
			if ( result != null ) {
				j++;
			}
			while(result!=null && i< getSameKeywordsCount() ) {
				nsIDOMNode node = processResultNotNull(str, result, j);
				startRange = getRangeByParameters(node, 1, 1);
				result = this.finder.find(str, getSearchRange(), startRange, endRange);
				i++;
			}
			if (checkStop( str, j )) {
				break;
			}
		}
	}
	
	/**
	 * @param j
	 * @return
	 */
	private boolean checkStop( final String tagName, final int j ) {
		if ( this.dataContainer == null ) {
			return false;
		}
		if ( this.dataContainer.getNumberRecentTags() <= 0 ) {
			return false;
		}
		if ( this.dataContainer.getContacts() != null ) {
			if (this.dataContainer.getContacts().contains( tagName )){
				return false;
			}
		}
		if ( this.dataContainer.getNumberRecentTags() > (j + 1) ) {
			return false;
		}
		return true;
	}

	/**
	 * @param result
	 */
	private nsIDOMNode processResultNotNull(String keyword, nsIDOMRange result, int index) {
		HighlightProperties property = this.items.get(keyword);
		nsIDOMElement inserting = this.domDoc.createElement("span");
		inserting.setAttribute("onmouseover", property.getOnmouseover());
		inserting.setAttribute("onclick", property.getOnclick());
		inserting.setAttribute("class", "highlight"+(index%10));
		inserting.appendChild(result.cloneContents());
		result.deleteContents();
		result.insertNode(inserting);
		return inserting;
	}

	private nsIDOMRange getSearchRange() {
		nsIDOMHTMLElement body = this.domDoc.getBody();
		int bodyLength = (int)body.getChildNodes().getLength();
	
		return getRangeByParameters(body, 0, bodyLength);
	}
	
	private nsIDOMRange getRangeByParameters(nsIDOMNode container, int start, int end) {
		nsIDOMDocumentRange domRange = getDomDocumentRange();
		nsIDOMRange range = domRange.createRange();
		range.setStart(container, start);
		range.setEnd(container, end);
		
		return range;
	}
	
	private nsIDOMDocumentRange getDomDocumentRange() {
		return (nsIDOMDocumentRange)this.domDoc.queryInterface(nsIDOMDocumentRange.NS_IDOMDOCUMENTRANGE_IID);
	}
	
	private int getSameKeywordsCount(){
		if (this.dataContainer == null) {
			return ResearchComponentDataContainer.DEAFULT_SAME_KEYWORDS_COUNT;
		} else {
			return (this.dataContainer.getSameKeywordsMaxCount() <= 0) ? Integer.MAX_VALUE : this.dataContainer.getSameKeywordsMaxCount();
		}
	}
}
