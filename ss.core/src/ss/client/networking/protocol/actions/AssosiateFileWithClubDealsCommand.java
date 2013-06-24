/**
 * 
 */
package ss.client.networking.protocol.actions;

import java.util.Hashtable;

/**
 * @author zobo
 *
 */
public class AssosiateFileWithClubDealsCommand extends AbstractAction {

	private static final String CLUB_DEAL_ID = "club_deal_id";

	private static final String MESSAGE_ID = "message_id";

	private static final long serialVersionUID = 6493204962830291526L;

	public void setMessageId( final String messageId){
		putArg(MESSAGE_ID, messageId);
	}

	public String getMessageId() {
		return getStringArg(MESSAGE_ID);
	}

	public Hashtable<Long, Boolean> getClubDeals() {
		return (Hashtable<Long, Boolean>) getObjectArg(CLUB_DEAL_ID);
	}
	
	public void setClubDeals( final Hashtable<Long, Boolean> data) {
		putArg(CLUB_DEAL_ID, data);
	}	
}
