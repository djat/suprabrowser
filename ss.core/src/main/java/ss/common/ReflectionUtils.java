/**
 * 
 */
package ss.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class ReflectionUtils {
	
	/**
	 * 
	 */
	private static final String [] JUNIT_FRAMEWORK_MARKS = 
		new String[] { 
			"junit.framework", "org.eclipse.jdt.internal.junit.runner" };

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private static <T> T create(Constructor<T> ctor, Object ... args ) {
		try {
			return ctor.newInstance(args);
		} catch (IllegalArgumentException ex) {
			throw new CannotCreateObjectException( ctor, ex);
		} catch (InstantiationException ex) {
			throw new CannotCreateObjectException( ctor, ex);
		} catch (IllegalAccessException ex) {
			throw new CannotCreateObjectException( ctor, ex);
		} catch (InvocationTargetException ex) {
			throw new CannotCreateObjectException( ctor, ex);
		}
	}
	
	public static <T> T create(Class<T> objectClass) {
		return create( objectClass, EMPTY_ARRAY );
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> objectClass, Object ... args ) {
		for( Constructor constructor : objectClass.getConstructors() ) {
			if ( constructor.getParameterTypes().length == args.length ) {
				return create( (Constructor<T>) constructor, args );
			}
		}
		throw new CannotCreateObjectException( objectClass, "Cannot find constructor for " + ListUtils.valuesToString( args ) );
	}
	
	/**
	 * 
	 */
	public static <T> T create( String className, Class<T> baseClass ) throws CannotCreateObjectException {
		try {
			final Class<T> clazz = forName( className, baseClass );
			return create( clazz );
		} catch (ClassNotFoundException ex) {
			throw new CannotCreateObjectException( "Class not found: " + className, ex ); 
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName( String className, Class<T> baseClass ) throws ClassNotFoundException {
		Class clazz = Class.forName(className);
		if ( !baseClass.isAssignableFrom( clazz ) ) {
			throw new CannotCreateObjectException( "Can't cast from " + clazz + " to " + className );
		}
		return clazz;
	}
	
	public static boolean isCalledByPackage( String packageName ) {
		StackTraceElement [] stackTrace = Thread.currentThread().getStackTrace();
		for (int n = 0; n < stackTrace.length; n++) {
			StackTraceElement element = stackTrace[n];
			if ( element.getClassName().startsWith( packageName ) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *
	 */
	public static class CannotCreateObjectException extends RuntimeException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -605991246720092047L;
		
		/**
		 * @param ctor
		 * @param ex
		 */
		public CannotCreateObjectException(Constructor objectCtor, Throwable ex) {
			super( "Cannot create " + objectCtor.getDeclaringClass() + " via " + objectCtor, ex );
		}
		
		/**
		 * @param ctor
		 * @param ex
		 */
		public CannotCreateObjectException(Class objectClass, String details ) {
			super( "Cannot create " + objectClass + ". Details " + details  );
		}
		
		/**
		 * @param message
		 * @param ex
		 */
		public CannotCreateObjectException(String message, ClassNotFoundException ex) {
			super( message, ex );
		}

		/**
		 * @param string
		 */
		public CannotCreateObjectException(String message ) {
			super( message );
		}
	}

	/**
	 * @return
	 */
	public static boolean isCalledByJUnit() {
		for( String packageName : JUNIT_FRAMEWORK_MARKS ) {
			if ( ReflectionUtils.isCalledByPackage( packageName ) ) {
				return true;
			}
		}
		return false;
	}
	
}
