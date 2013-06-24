/**
 * Jul 3, 2006 : 1:53:17 PM
 */
package ss.client.ui.controllers;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.ByteRouterClient;
import ss.global.SSLogger;

/**
 * @author dankosedin
 *
 */
public class ActiveByteRoutersController {
	
	private Hashtable<String, ByteRouterClient> activeByteRouters = new Hashtable<String, ByteRouterClient>();
	
	private Vector<String> activeBRSessions = new Vector<String>();
	
	private Logger logger = SSLogger.getLogger(this.getClass());
	
	public ByteRouterClient getActiveByteRouter(String uniqueId) {
		this.logger.info("trying getactivebyte for this unique: " + uniqueId);
		synchronized (this.activeByteRouters) {

			ByteRouterClient byteRouterClient = this.activeByteRouters.get(uniqueId);

			return byteRouterClient;
		}

	}
	
	public ByteRouterClient getLatestByteRouter(final Document doc) {
		synchronized (this.activeByteRouters) {
			String testLoc = doc.getRootElement().element(
			"physical_location").attributeValue("value");
			for (int i = this.activeBRSessions.size() - 1; i >= 0; i--) {
				// String latest =
				// (String)activeBRSessions.elementAt(activeBRSessions.size()-1);
				String latest = this.activeBRSessions.elementAt(i);
				this.logger.info("LATEST SESSION ID ADDED TO ACTIVE BR SESSIONS: "
						+ latest);
				ByteRouterClient byteRouterClient = this.activeByteRouters.get(latest);				
				if (testLoc.equals(byteRouterClient.getLocation())) {
					return byteRouterClient;
				}

			}
		}

		return null;
	}
	
	public void putActiveByteRouter(String uniqueId, ByteRouterClient client) {
		this.logger.info("Putting active byte: " + uniqueId);
		synchronized (this.activeByteRouters) {
			this.activeByteRouters.put(uniqueId, client);
			this.activeBRSessions.add(uniqueId);
		}
	}		

}
