/**
 * 
 */
package ss.client.ui.clubdealmanagement.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zobo
 *
 */
class UserAdminList {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UserAdminList.class);
	
	private final List<UserAdmin> users = new ArrayList<UserAdmin>();

	public UserAdminList(){
		
	}
	
	public void add( UserAdmin user ){
		if ( user != null ) {
			this.users.add(user);
		} else {
			logger.error("user is null");
		}
	}
	
	public UserAdmin[] getAsArray(){
		final UserAdmin[] a = new UserAdmin[1];
		return this.users.toArray( a );
	}
	
	public boolean isEmpty(){
		return this.users.isEmpty();
	}
}
