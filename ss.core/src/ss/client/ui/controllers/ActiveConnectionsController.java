/**
 * Jul 3, 2006 : 7:46:56 PM
 */
package ss.client.ui.controllers;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.client.networking.DialogsMainCli;
import ss.global.SSLogger;

/**
 * @author dankosedin
 * 
 */
public class ActiveConnectionsController {
	private Hashtable<String, DialogsMainCli> activeConnections = new Hashtable<String, DialogsMainCli>();

	private Logger logger = SSLogger.getLogger(this.getClass());

	public void putActiveConnection(String uniqueId, DialogsMainCli client) {
		this.logger.info("New DialogsMainCli registered. uniqueId="+uniqueId);
		synchronized (this.activeConnections) {
			this.activeConnections.put(uniqueId, client);
		}
	}

	public DialogsMainCli getActiveConnection(String uniqueId) {
		synchronized (this.activeConnections) {

			this.logger.info("trying unique: " + uniqueId);
			DialogsMainCli dialogsMainCli = this.activeConnections
					.get(uniqueId);

			if (dialogsMainCli == null) {

				// return startConnection(uniqueId);
				return null;

			} else {
				return dialogsMainCli;
			}

		}

	}

}
