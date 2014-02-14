/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.protocol.actions.AssosiateFileWithClubDealsCommand;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class AssosiateFileWithClubDealsHandler extends AbstractActionHandler<AssosiateFileWithClubDealsCommand> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AssosiateFileWithClubDealsHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public AssosiateFileWithClubDealsHandler(DialogsMainPeer peer) {
		super(AssosiateFileWithClubDealsCommand.class, peer);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(AssosiateFileWithClubDealsCommand action) {
		final Hashtable<Long, Boolean> data = action.getClubDeals();
		if (data == null) {
			logger.error("ClubDeals info is null");
			return;
		}
		// TODO
		//ClubDealsServerSingleton.INSTANCE.assosiateFileWithClubDeal(action.getMessageId(), data, this.peer);
	}
}
