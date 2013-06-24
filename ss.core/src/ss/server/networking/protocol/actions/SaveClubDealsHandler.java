/**
 * 
 */
package ss.server.networking.protocol.actions;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.SaveClubDealsCommand;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SaveClubDealsHandler extends AbstractActionHandler<SaveClubDealsCommand> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveClubDealsHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SaveClubDealsHandler(DialogsMainPeer peer) {
		super(SaveClubDealsCommand.class, peer);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(SaveClubDealsCommand action) {
		final Document data = action.getData();
		if (data == null) {
			logger.error("BundleData is null");
		}
		// TODO
		//ClubDealsServerSingleton.INSTANCE.save( data );
	}
}
