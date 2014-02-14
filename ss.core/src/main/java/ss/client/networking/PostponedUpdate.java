/**
 * 
 */
package ss.client.networking;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.domainmodel.Statement;
import ss.util.SessionConstants;

/**
 *
 */
public class PostponedUpdate {

	private final Hashtable map;
	
	/**
	 * @param update
	 */
	public PostponedUpdate(Hashtable update) {
		this.map = update;
	}

	/**
	 * @return
	 */
	public Hashtable getMap() {
		return this.map;
	}

	/**
	 * @return
	 */
	public Document getDocument() {
		return (Document) this.map.get(SessionConstants.DOCUMENT);
	}

	/**
	 * @return
	 */
	public String getTypeOfUpdate() {
		return (String) this.map.get(SessionConstants.IS_UPDATE);
	}

	/**
	 * @return
	 */
	public Statement getStatement() {
		final Document document = getDocument();
		return document != null ? Statement.wrap( document ) : null;
	}

}
