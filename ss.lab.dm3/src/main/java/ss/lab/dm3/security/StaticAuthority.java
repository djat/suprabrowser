package ss.lab.dm3.security;

public class StaticAuthority extends Authority {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.security.Authority#providedFor(java.lang.Object, ss.lab.dm3.security.Authentication)
	 */
	@Override
	public boolean isProvidedFor(Object object, IAuthentication authentication) {
		return authentication.hasAuthority( getName() );
	}

}
