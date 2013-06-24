/**
 * 
 */
package ss.client.ui.messagedeliver;

import org.dom4j.Document;


/**
 * @author zobo
 *
 */
public class ReplaceDeliveringElement extends AbstractDeliveringElement {

	private final Document newDoc;
	
	private final Document oldDoc;
	
	private final boolean onlyIfExists;
	
	protected ReplaceDeliveringElement(final Document newDoc, final Document oldDoc, final String sphereId, final boolean onlyIfExists) {
		super(DeliveringElementType.REPLACE, sphereId);
		this.newDoc = newDoc;
		this.oldDoc = oldDoc;
		this.onlyIfExists = onlyIfExists;
	}

	Document getNewDoc() {
		return this.newDoc;
	}

	Document getOldDoc() {
		return this.oldDoc;
	}

	public boolean isOnlyIfExists() {
		return this.onlyIfExists;
	}

}
