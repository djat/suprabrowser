/**
 * Jul 5, 2006 : 6:00:16 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.sphereopen.SphereCreationContext;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;

/**
 * @author dankosedin
 */
public class ReceiveResultsFromXMLSearchHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(ReceiveResultsFromXMLSearchHandler.class);
	
	private final DialogsMainCli clent;

	public ReceiveResultsFromXMLSearchHandler(DialogsMainCli cli) {
		this.clent = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleReceiveResultsFromXMLSearch(final Hashtable update) {
		if (logger.isDebugEnabled()){
			logger.debug(update);
		}
		SphereOpenManager.INSTANCE.recieve(new SphereCreationContext(update, this.clent));
	}
	
	public String getProtocol() {
		return SSProtocolConstants.RECEIVE_RESULTS_FROM_XMLSEARCH;
	}

	public void handle(Hashtable update) {
		handleReceiveResultsFromXMLSearch(update);
	}

}