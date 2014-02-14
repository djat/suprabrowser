/**
 * 
 */
package ss.client.networking.protocol.actions;

import org.dom4j.Document;

import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class TagActionCommand extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TagActionCommand(final Document doc, final String sphereId, final String keywordText) {
		super();
		setDocument(doc);
		setSphereId(sphereId);
		setKeywordText(keywordText);
	}
	
	public void setSphereId(final String sphereId) {
		putArg(SessionConstants.SPHERE_ID2, sphereId);
	}
	
	public void setDocument(final Document doc) {
		putArg(SessionConstants.DOCUMENT, doc);
	}
	
	public void setKeywordText(final String keywordText) {
		putArg(SessionConstants.KEYWORD_ELEMENT, keywordText);
	}
	
	public String getSphereId() {
		return getStringArg(SessionConstants.SPHERE_ID2);
	}
	
	public String getKeywordText() {
		return getStringArg(SessionConstants.KEYWORD_ELEMENT);
	}
	
	public Document getDocument() {
		return getDocumentArg(SessionConstants.DOCUMENT);
	}
}
