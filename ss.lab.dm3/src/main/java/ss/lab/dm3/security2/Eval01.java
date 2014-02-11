package ss.lab.dm3.security2;

import ss.lab.dm3.connection.Connection;

/**
 * @author Dmitry Goncharov
 */
public class Eval01 {

	public static void main(String[] args) {
		
		
		
	}
	
	public void listOfAuthorities() {
		Connection connection = getConnection();
		SecurityManager manager = connection.getSecurityManager();
		Authentication authentication = manager.getAuthentication();
		AuthorityList authorities = authentication.getAuthorities();
		for( Authority authority : authorities ) {
			System.out.println(authority );
		}
	}
		
	/**
	 * @return
	 */
	private Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void testIsAdmin() {
		Connection connection = getConnection();
		SecurityManager manager = connection.getSecurityManager();
		System.out.println( "Check is admin " + manager.hasAuthority( Authority.ADMINISTRATOR ) );
	}
	
	
}
