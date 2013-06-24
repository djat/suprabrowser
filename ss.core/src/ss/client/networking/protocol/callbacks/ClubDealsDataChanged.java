/**
 * 
 */
package ss.client.networking.protocol.callbacks;

import ss.client.networking.DialogsMainCli;
import ss.server.networking.protocol.callbacks.ClubDealsDataChangedEvent;

/**
 * @author zobo
 *
 */
public class ClubDealsDataChanged extends AbstractCallbackHandler<ClubDealsDataChangedEvent> {

	public ClubDealsDataChanged( final DialogsMainCli client ) {
		super(ClubDealsDataChangedEvent.class, client);
	}

	@Override
	protected void handleEvent(ClubDealsDataChangedEvent event) {
		
		// TODO
		//ClubDealsSingleton.INSTANCE.dataChanged();
	}
}
