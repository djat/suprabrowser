/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author roman
 * 
 */
public class AddSphereToFavouritesHandler extends AbstractOldActionBuilder {

	private static final Logger logger = SSLogger
			.getLogger(AddSphereToFavouritesHandler.class);
	
	private final DialogsMainCli cli;

	public AddSphereToFavouritesHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_SPHERE_TO_FAVOURITES;
	}

	@SuppressWarnings("unchecked")
	public void addSphereToFavourites(final Hashtable session, Document buildDoc) {
		logger.info("client add sphere");
		Hashtable toSend = (Hashtable) session.clone();

		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_SPHERE_TO_FAVOURITES);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, buildDoc);
		this.cli.sendFromQueue(update);
	}

}
