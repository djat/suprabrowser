/**
 * 
 */
package ss.client.networking.protocol.getters;

import ss.client.ui.clubdealmanagement.admin.UserAdmin;

/**
 * @author zobo
 *
 */
public class UpdateAdminsCommand extends AbstractGetterCommand {

	private static final String USER_ADMIN = "UserAdmin";
	
	private static final long serialVersionUID = -8894022891550045724L;

	public void setUserAdmin( final UserAdmin ua ){
		putArg(USER_ADMIN, ua);
	}
	
	public UserAdmin getUserAdmin(){
		return (UserAdmin)getObjectArg( USER_ADMIN );
	}
}
