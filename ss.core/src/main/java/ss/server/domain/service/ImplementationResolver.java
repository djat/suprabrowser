/**
 * 
 */
package ss.server.domain.service;

import java.util.HashMap;
import ss.common.ReflectionUtils;

/**
 *
 */
public class ImplementationResolver {

	private final HashMap<Class<?>,Class<?>> interfaceToImpl = new HashMap<Class<?>, Class<?>>();
	
	public <T> void put( Class<T> interfaceClazz, Class<? extends T> implementationClazz ) {
		if ( !interfaceClazz.isInterface() ) {
			throw new IllegalArgumentException( "interfaceClazz should be interface" );
		}
		if ( !interfaceClazz.isAssignableFrom( implementationClazz ) ) {
			throw new IllegalArgumentException( "Implementation class " + implementationClazz + " does not implement interface " + interfaceClazz );
		}
		this.interfaceToImpl.put( interfaceClazz, implementationClazz );
	}
	
	public <T> Class<T> get( Class<T> interfaceClazz ) {
		final Class<T> implClazz = (Class<T>) this.interfaceToImpl.get( interfaceClazz );
		if ( implClazz == null ) {
			throw new IllegalArgumentException( "Can't find implementation for " + interfaceClazz );
		}
		return implClazz;
	}
	
	public <T> T create(Class<T> interfaceClazz ) {
		Class<T> implClazz = get( interfaceClazz );
		return ReflectionUtils.create(implClazz);
	}
}
