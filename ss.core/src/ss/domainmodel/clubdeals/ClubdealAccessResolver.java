package ss.domainmodel.clubdeals;

import ss.client.networking.DialogsMainCli;
import ss.util.SessionConstants;

public class ClubdealAccessResolver {

	private boolean isAdmin; 
	
	private final String currentRealName; 
	
	/**
	 * 
	 */
	public ClubdealAccessResolver(DialogsMainCli client) {
		super();
		this.isAdmin = client.getVerifyAuth().isAdmin();
		this.currentRealName = (String)client.session.get(SessionConstants.REAL_NAME);
	}

	/**
	 * @param cd
	 * @return
	 */
	public boolean hasAccess(ClubdealWithContactsObject cd) {
		return this.isAdmin || ( cd != null && cd.hasContact( this.currentRealName ) );
	}

}
