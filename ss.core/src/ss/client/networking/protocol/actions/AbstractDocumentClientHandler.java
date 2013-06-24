package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.client.networking.DialogsMainCli;
import ss.common.protocolobjects.AbstractProtocolObject;
import ss.global.SSLogger;
import ss.util.SessionConstants;

public abstract class AbstractDocumentClientHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(AbstractDocumentClientHandler.class);

	private final DialogsMainCli cli;	

	public AbstractDocumentClientHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	/**
	 * Returns protocol name
	 */
	public abstract String getProtocol();

	/**
	 * Add default values to update and send it to server
	 * 
	 * @param update
	 *            not null hastable with values that should be send
	 */
	protected final void sendUpdate(Hashtable<String, Object> update) {
		if (update == null) {
			throw new NullPointerException("update argument is null");
		}
		putMandatoryParams(update);
		this.cli.sendFromQueue(update);
	}

	private void putMandatoryParams(Hashtable<String, Object> update) {
		update.put(SessionConstants.PROTOCOL, this.getProtocol());
		update.put(SessionConstants.SESSION, this.cli.getSession());
	}

	/**
	 * @param protocolObject
	 */
	public void sendUpdate(AbstractProtocolObject protocolObject) {
		sendUpdate( protocolObject.getValues() );	
	}

}
