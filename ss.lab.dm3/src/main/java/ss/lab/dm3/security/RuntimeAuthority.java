package ss.lab.dm3.security;

public abstract class RuntimeAuthority<T> extends Authority {
	
	private final Class<T> evaluableObjectClazz;

	/**
	 * @param objectClass
	 */
	public RuntimeAuthority(Class<T> evaluableObjectClazz) {
		super();
		this.evaluableObjectClazz = evaluableObjectClazz;
	}

	@Override
	public boolean isProvidedFor(Object object, IAuthentication authentication) {
		if ( this.evaluableObjectClazz.isInstance( object ) ) {
			return typeSafeIsProvidedFor( this.evaluableObjectClazz.cast(object), authentication );
		}
		else {
			return false;
		}
	}
	
	protected abstract boolean typeSafeIsProvidedFor( T object, IAuthentication authentication);
	
	
}
