package ss.lab.dm3.security;

import java.util.concurrent.atomic.AtomicReference;

public class SecurityHelper {

	private static final AtomicReference<IAuthenticationProvider> authenticationProvider = new AtomicReference<IAuthenticationProvider>();
	
	public static boolean canCreate( Object object ) {
		final IAuthenticationProvider authenticationProvider = getAuthenticationProvider();
		final IAuthentication authentication = authenticationProvider != null ? authenticationProvider.get() : null;
		if ( authentication != null ) {
			Operation canCreateOperation = getOper();
			return canCreateOperation.isAllowed(object, authentication);
		}
		else {
			return false;
		}
	}

	/**
	 * @return
	 */
	private static Operation getOper() {
		// TODO Auto-generated method stub
		return null;
	}

	public static synchronized IAuthenticationProvider getAuthenticationProvider() {
		return authenticationProvider.get();
	}

	public static synchronized void setAuthenticationProvider(
			IAuthenticationProvider authenticationProvider) {
		SecurityHelper.authenticationProvider.set( authenticationProvider );
	}
	
	
}
