package ss.lab.dm3.security;

public abstract class Operation {

	private final String name = null;
	
	private final AuthorityProvider authorityProvider = null;
	
	/**
	 * @param allowedAccount
	 * @param authentication
	 */
	public boolean isAllowed(Object object,
			IAuthentication authentication) {
		if ( object == null ) {
			return false;
		}
		OperationAuthorityList authorities = getAuthorities( object );
		for( Authority authority : authorities ) {
			if ( authority.isProvidedFor( object, authentication ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param object
	 */
	protected OperationAuthorityList getAuthorities(Object object) {
		String name = object.getClass().getSimpleName();
		return this.authorityProvider.get( name, this.name	);
	}
	
}
