/**
 * 
 */
package ss.client.event.tagging;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class KeywordsLoader {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeywordsLoader.class);
	
	private final MessagesPane mp;
	
	private final Hashtable<String, Document> tags;
	
	public KeywordsLoader( final MessagesPane mp ){
		this.mp = mp;
		this.tags = new Hashtable<String, Document>();
	}
	
	public Document getTagDocument( final Document doc, final Element elem, final String otherSphere ){
		final String id = elem.attributeValue("unique_id");
		if ( id == null ) {
			logger.error("id is null");
			return null;
		}
		Document tag = this.tags.get( id );
		if (tag == null) {
			tag = loadNewTagDocument(doc, elem, otherSphere);
			if (tag != null) {
				this.tags.put(id, tag);
			} else {
				logger.error("Tag cannot be obtained with unique_id: " + id + ", null");
			}
		}
		return tag;
	}
	
	private Document loadNewTagDocument( final Document doc, final Element elem, final String otherSphere ){
		final Hashtable sendSession = (Hashtable) this.mp
			.getRawSession().clone();
		final String subject = elem.attributeValue("value");
		final String sphereId;
		if (!this.mp.isSupraQuery(doc)) {
			if (logger.isDebugEnabled()) {
				logger.debug("trying to get new generic: " + subject);
			}
			if (elem.attributeValue("current_location") != null) {
				sphereId = elem
						.attributeValue("current_location");
			} else {
				sphereId = otherSphere;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("sphereId: " + sphereId);
			}
			sendSession.put("sphere_id", sphereId);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("trying to get existing generic: " + subject);
			}
			sphereId = (String) sendSession
					.get("sphere_id");
			String parentSphere = this.mp.getLastSelectedDoc()
					.getRootElement().element(
							"current_sphere")
					.attributeValue("value");
			if (logger.isDebugEnabled()) {
				logger.debug("parent sphere..." + parentSphere);
			}
			sendSession.put("sphere_id", parentSphere);
		}

		Document tag = this.mp.client.getExistingQuery(
				sendSession, subject, sphereId); 
		return tag;
	}
}
