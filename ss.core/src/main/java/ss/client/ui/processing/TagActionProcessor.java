/**
 * 
 */
package ss.client.ui.processing;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class TagActionProcessor {

	@SuppressWarnings("unused")
	private static Logger logger = SSLogger.getLogger(TagActionProcessor.class);

	private Document parentDoc = null;

	private final DialogsMainCli client;

	private Hashtable rawSession;

	private final String sphereId;

	public TagActionProcessor(final DialogsMainCli client, String sphereId, Document parentDoc) {
		this.client = client;
		this.rawSession = (Hashtable) this.client.getSession().clone();
		this.rawSession.put(SessionConstants.SPHERE_ID2, sphereId);
		this.sphereId = sphereId;
		this.parentDoc = parentDoc;
	}
	
	public void doTagAction( final String keywordText ) {	
		this.client.doTagAction(this.parentDoc, this.sphereId, keywordText);
	}
}