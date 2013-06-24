/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author roman
 * 
 */
public class RemoveSphereFromFavouritesHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RemoveSphereFromFavouritesHandler.class);

	private final DialogsMainCli cli;
	
	public RemoveSphereFromFavouritesHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REMOVE_FROM_FAVORITES;
	}

	@SuppressWarnings("unchecked")
	public void removeSphereFromFavourites(Hashtable session, String id) {
		logger.info("remove sphere from favourites");
		Hashtable toSend = (Hashtable) session.clone();

		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.REMOVE_FROM_FAVORITES);
		update.put(SessionConstants.SESSION, toSend);
		update.put("toRemove", id);
		this.cli.sendFromQueue(update);
	}

}
