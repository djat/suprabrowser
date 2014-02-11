package ss.lab.dm3.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Dmirty Goncharov
 *
 * @param <T>
 */
public class ClassResolver<T> {

	private final Class<T> superClazz;
	
	private final Set<Package> lookupPackages;
	
	private final HashMap<String, Class<? extends T>> nameToClass = new HashMap<String,Class<? extends T>>();

	protected ClassResolver(Class<T> superClazz ) {
		this( superClazz, Collections.EMPTY_LIST );
	}
	
	public ClassResolver(Class<T> superClazz, Object[] lookupPackages) {
		this( superClazz, Arrays.asList( lookupPackages ) );
	}
	
	/**
	 * @param superClazz
	 * @param lookupPackages set of String of Packages
	 */
	public ClassResolver(Class<T> superClazz, Collection<?> lookupPackages) {
		super();
		this.superClazz = superClazz;
		this.lookupPackages = toPackages(lookupPackages);
	}

	/**
	 * @param packages
	 * @return
	 */
	private static Set<Package> toPackages(Collection<?> packages) {
		Set<Package> result = new HashSet<Package>();
		for( Object packazeObj : packages ) {
			if ( packazeObj instanceof String ) {
				result.add( Package.getPackage((String)packazeObj) );
			}
			else if ( packazeObj instanceof Package ) {
				result.add( (Package) packazeObj );
			}
			else {
				throw new IllegalArgumentException( "Expected String or Package, but given object is " + packazeObj );
			}
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public final Class<? extends T> resolve( String simpleName ) {
		Class<? extends T> clazz = this.nameToClass.get( simpleName );
		if ( clazz != null ) {
			return clazz;
		}
		final String className = resolveClassName(simpleName);
		if ( className == null ) {
			throw new CantResolveClassException( simpleName, this.lookupPackages );
		}
		try {
			final Class<?> rawClazz = Class.forName( className );
			if ( ! this.superClazz.isAssignableFrom( rawClazz ) ) {
				throw new CantResolveClassException( this.superClazz, rawClazz );
			}
			clazz = (Class<? extends T>) rawClazz;
			this.nameToClass.put( simpleName, clazz );
			return clazz;
		} catch (ClassNotFoundException ex) {
			throw new CantResolveClassException( className, ex );
		}
	}
	
	public String resolveClassName( String simpleName ) {
		for( Package lookupPackage : this.lookupPackages ) {
			final String objectClassName = ReflectionHelper.combineClassName( lookupPackage, simpleName );
			if ( ReflectionHelper.isClassExists(objectClassName) ) {
				return objectClassName;
			}			
		}
		return null;
	}

	/**
	 * @return
	 */
	public Class<T> getSuperClazz() {
		return this.superClazz;
	}
	
	
}
