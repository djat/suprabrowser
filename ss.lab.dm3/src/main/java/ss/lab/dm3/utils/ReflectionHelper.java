package ss.lab.dm3.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ss.lab.dm3.orm.MappedObject;

/**
 * 
 * TODO try use CGLIB FastClass for performance (see 
 * http://jira.jboss.com/jira/browse/JBSEAM-1977
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2867
 * 
 * )
 *  
 * 
 * @author Dmitry Goncharov
 * 
 */
public class ReflectionHelper {

	/**
	 * 
	 */
	private static final String EQUALS_METHOD_NAME = "equals";

	/**
	 * 
	 */
	private static final String HASH_CODE_METHOD_NAME = "hashCode";
	
	/**
	 * 
	 */
	private static final Object TOSTRING_METHOD_NAME = "toString";

	/**
	 * 
	 */
	private static final String SET_PREFIX = "set";

	/**
	 * 
	 */
	private static final String GET_PREFIX = "get";

	/**
	 * 
	 */
	private static final String IS_PREFIX = "is";

	private static final Log log = LogFactory.getLog(ReflectionHelper.class);

	private static final String CLASS_FILE_DELIMETER = "/";
	private static final String PACKAGE_DELIMETER_REGEX = "\\.";
	private static final char PACKAGE_DELIMETER_CHAR = '.';
	private static final String CLASS_FILE_SUFIX = ".class";

	

	public static boolean isClassExists(String className) {
		String accessorFileName = CLASS_FILE_DELIMETER
				+ className.replaceAll(PACKAGE_DELIMETER_REGEX,
						CLASS_FILE_DELIMETER) + CLASS_FILE_SUFIX;
		if (log.isDebugEnabled()) {
			log.debug("Look up for " + accessorFileName);
		}
		return ReflectionHelper.class.getResourceAsStream(accessorFileName) != null;
	}

	/**
	 * @param customAccessorPackageName
	 * @param string
	 * @return
	 */
	public static String combineClassName(String... classNameParts) {
		StringBuilder sb = new StringBuilder();
		for (String classNamePart : classNameParts) {
			if (sb.length() > 0) {
				if (sb.charAt(sb.length() - 1) != PACKAGE_DELIMETER_CHAR) {
					sb.append(PACKAGE_DELIMETER_CHAR);
				}
			}
			sb.append(classNamePart);
		}
		return sb.toString();
	}

	/**
	 * @param lookupPackage
	 * @param subName
	 * @return
	 */
	public static String combineClassName(Package packaze, String subName) {
		return combineClassName(packaze.getName(), subName);
	}

	public static <T> T create(Class<T> clazz)
			throws CantCreateObjectException {
		if ( clazz == null ) {
			throw new NullPointerException(	"clazz" );
		}
		try {
			Object obj = clazz.newInstance();
			return clazz.cast(obj);
		}
		catch (InstantiationException ex) {
			throw new ReflectionException( "Can't create object by " + clazz, ex );
		}
		catch (IllegalAccessException ex) {
			throw new ReflectionException( "Can't create object by " + clazz, ex );
		}
	}

	/**
	 * @param beanClazz
	 * @param propertyName
	 * @return
	 */
	public static Field findPropertyDeclaration(Class<?> beanClazz,
			String propertyName) {
		while (beanClazz != null && beanClazz != Object.class) {
			for (Field field : beanClazz.getDeclaredFields()) {
				if (field.getName().equals(propertyName)) {
					return field;
				}
			}
			beanClazz = beanClazz.getSuperclass();
		}
		return null;
	}

	/**
	 * @param beanClazz
	 * @param propertyName
	 * @return
	 */
	public static Field getPropertyDeclaration(
			Class<? extends MappedObject> beanClazz, String propertyName) {
		Field field = findPropertyDeclaration(beanClazz, propertyName);
		if (field == null) {
			throw new CantFindPropertyWithNameException(beanClazz, propertyName);
		}
		return field;
	}

	/**
	 * @param beanClazz
	 * @param name
	 * @return
	 */
	public static Method getGetter(Class<? extends MappedObject> beanClazz,
			String propertyName) {
		Method getter = findGetter(beanClazz, propertyName);
		if (getter == null) {
			throw new CantFindPropertyWithNameException(beanClazz, propertyName);
		}
		return getter;
	}

	/**
	 * @param beanClazz
	 * @param propertyName
	 * @return
	 */
	public static Method findGetter(Class<?> beanClazz, String propertyName) {
		while (beanClazz != null && beanClazz != Object.class) {
			for (Method method : beanClazz.getDeclaredMethods()) {
				boolean isGetter = isGetter(propertyName, method);
				if ( isGetter ) {
					return method;
				}
			}
			beanClazz = beanClazz.getSuperclass();
		}
		return null;
	}

	/**
	 * @param propertyName
	 * @param method
	 * @return
	 */
	private static boolean isGetter(String propertyName, Method method) {
		if ( hasGetterSignature( method ) ) {
			String propertyNameByGetter = getPropertyNameByGetter(method);
			return propertyName.equals(propertyNameByGetter);
		}
		else {
			return false;
		}
	}
	
	public static Map<String,Method> getDeclaredGetProperties(Class<?> clazz ) {
		Map<String,Method> map = new HashMap<String,Method>();
		for( Method method : clazz.getDeclaredMethods() ) {
			if ( hasGetterSignature(method) ) {
				String propertyName = getPropertyNameByGetter(method);
				map.put(propertyName, method);
			}
		}
		return map;
	}
	
	public static boolean hasGetterSignature(Method method) {
		final Class<?> returnType = method.getReturnType();
		return returnType != null &&
			   !isStatic( method ) &&
			   !isAbstract( method ) && 
		(method.getParameterTypes() == null || method
						.getParameterTypes().length == 0);
	}
	
	public static boolean hasSetterSignature(Method method) {
		final Class<?> returnType = method.getReturnType();
		return returnType == void.class &&
			   !isStatic( method ) &&
			   !isAbstract( method ) && 
		(method.getParameterTypes() != null && 
		 method.getParameterTypes().length == 1);
	}
	
	public static boolean hasSetterPrefix(Method method) {
		return method.getName().startsWith( SET_PREFIX );
	}
	
	/**
	 * @param method
	 * @return
	 */
	private static boolean isAbstract(Method method) {
		return (method.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
	}

	/**
	 * @param method
	 * @return
	 */
	private static boolean isStatic(Method method) {
		return (method.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}

	public static String getPropertyNameByGetter( Method method ) {
		if ( hasGetterSignature(method) ) {
			String name = method.getName();
			for( int n = 0; n < name.length(); ++ n ) {
				final char ch = name.charAt( n );
				if ( Character.isUpperCase( ch ) ) {
					return Character.toLowerCase(ch) + name.substring( n + 1 ); 
				}
			}
		}
		return null; 
	}
	
	/**
	 * @param propertyName
	 * @param returnType
	 */
	public static String getGetterName(String propertyName, Class<?> returnType) {
		String firstCapitalPropertyName = Character.toUpperCase(propertyName
				.charAt(0))
				+ propertyName.substring(1);
		if (returnType == boolean.class) {
			return IS_PREFIX + firstCapitalPropertyName;
		} else {
			return GET_PREFIX + firstCapitalPropertyName;
		}
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private static String getSetterMethodName(String propertyName) {
		String firstCapitalPropertyName = Character.toUpperCase(propertyName
				.charAt(0))
				+ propertyName.substring(1);
		return SET_PREFIX + firstCapitalPropertyName;
	}

	/**
	 * @param beanClazz
	 * @param name
	 * @return
	 */
	public static Method getSetter(Class<?> beanClazz, String propertyName) {
		Method getter = findSetter(beanClazz, propertyName);
		if (getter == null) {
			throw new CantFindPropertyWithNameException(beanClazz, propertyName);
		}
		return getter;
	}

	/**
	 * @param beanClazz
	 * @param propertyName
	 * @return
	 */
	private static Method findSetter(Class<?> beanClazz, String propertyName) {
		Method getter = findGetter(beanClazz, propertyName);
		if (getter != null) {
			Method method = findSetterByGetter(getter, propertyName);
			if ( method == null ) {
				log.warn("Can't find setter for setter. Getter is " + getter );
			}
			return method;
		} else {
			return null;
		}
	}

	/**
	 * @param propertyName
	 * @param getter
	 * @return
	 */
	public static Method findSetterByGetter(Method getter, String propertyName) {
		try {
			return getter.getDeclaringClass().getMethod(
					getSetterMethodName(propertyName),
					new Class<?>[] { getter.getReturnType() });
		} catch (SecurityException ex) {
			return null;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}
	
	@Deprecated
	public <T> T createNew(Class<T> clazz) {
		return create(clazz);
	}
	
	public static Object invoke(Object obj, Method method, Object ... args ) {
		if ( obj == null ) {
			throw new NullPointerException(	"obj" );
		}
		if ( method == null ) {
			throw new NullPointerException(	"method" );
		}
		try {
			return method.invoke( obj, args );
		}
		catch (IllegalArgumentException ex) {
			throw new ReflectionException( "Can't invoke " + method + " on " + obj + " with " + args, ex );
		}
		catch (IllegalAccessException ex) {
			throw new ReflectionException( "Can't invoke " + method + " on " + obj + " with " + args, ex );
		}
		catch (InvocationTargetException ex) {
			final Throwable cause = ex.getCause();
			if ( cause == null ) {
				throw new ReflectionException( "Can't invoke " + method + " on " + obj + " with " + args, ex );
			}
			else {
				throw new ReflectionException( "Can't invoke " + method + " on " + obj + " with " + args, cause );
			}
		}
	}

	/**
	 * @param objClass
	 * @param methodName
	 */
	public static Method findDeclaredMethodByName(Class<?> objClass, String methodName) {
		for( Method method : objClass.getDeclaredMethods() ) {
			if ( method.getName().equals(methodName) ) {
				return method;
			}			
		}
		Class<?> superClazz = objClass.getSuperclass();
		if ( superClazz != null && 
			 superClazz != Object.class ) {
			Method method = findDeclaredMethodByName(superClazz, methodName);
			if ( method != null ) {
				return method;
			}
		}
		for( Class<?> interfaceClazz : objClass.getInterfaces() ) {
			Method method = findDeclaredMethodByName(interfaceClazz, methodName);
			if ( method != null ) {
				return method;
			}
		}
		return null;		
	}

	/**
	 * @param method
	 * @return
	 */
	public static boolean isHashCodeMethod(Method method) {
		return method.getName().equals(HASH_CODE_METHOD_NAME)
				&& method.getParameterTypes().length == 0;
	}

	/**
	 * @param method
	 * @return
	 */
	public static boolean isEqualsMethod(Method method) {
		return method.getName().equals(EQUALS_METHOD_NAME)
				&& method.getParameterTypes().length == 1;
	}
	
	public static boolean isToStringMethod(Method method) {
		return method.getName().equals(TOSTRING_METHOD_NAME)
		&& method.getParameterTypes().length == 0;
	}

	/**
	 * @param clazz
	 * @param cfg
	 */
	public static <T> T create(Class<T> clazz, Object ... args) {
		Constructor<?> bestMatch = null; 
		for( Constructor<?> constructor : clazz.getConstructors() ) {
			if ( isSuitable( constructor.getParameterTypes(), args ) ) {
				if ( bestMatch != null ) {
					throw new IllegalArgumentException( "Ambigous arguments " + args + " for " + clazz + ". Found 2 valid constructors " + bestMatch + ", " + constructor );
				}
				bestMatch = constructor;
			}
		}
		if ( bestMatch == null ) {
			throw new IllegalArgumentException( "Can't find suitable constructor for " + clazz + ", args " + args ); 
		}
		try {
			return clazz.cast( bestMatch.newInstance( args ) );
		} catch (IllegalArgumentException ex) {
			throw new CantCreateObjectException( clazz, ex );
		} catch (InstantiationException ex) {
			throw new CantCreateObjectException( clazz, ex );
		} catch (IllegalAccessException ex) {
			throw new CantCreateObjectException( clazz, ex );
		} catch (InvocationTargetException ex) {
			throw new CantCreateObjectException( clazz, ex );
		}
	}

	/**
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	private static boolean isSuitable(Class<?>[] parameterTypes, Object[] args) {
		if ( parameterTypes.length != args.length ) {
			return false;
		}
		for (int n = 0; n < args.length; n++) {
			final Object arg = args[ n ];
			if ( arg == null ) {
				continue;
			}
			Class<?> parameterType = parameterTypes[n];
			if ( !parameterType.isAssignableFrom( arg.getClass() ) ) {
				return false;
			}
		}
		return true;
	}
	
	
	public static Object getPropertyValue(final Object obj, final String propertyName) {
		try {
			String propertyGetterMethodName = ReflectionHelper.getGetterName(propertyName, Object.class);
			Method method = obj.getClass().getMethod(propertyGetterMethodName);
			return method.invoke(obj);
		} catch (SecurityException e) {
			new ReflectionException( "Can't get value of property : " + propertyName , e );
		} catch (IllegalArgumentException e) {
			new ReflectionException( "Can't get value of property : " + propertyName , e );
		} catch (IllegalAccessException e) {
			new ReflectionException( "Can't get value of property : " + propertyName , e );
		} catch (NoSuchMethodException e) {
			new ReflectionException( "Can't get value of property : " + propertyName , e );
		} catch (InvocationTargetException e) {
			new ReflectionException( "Can't get value of property : " + propertyName , e );
		}
		return null;
	}
	
}
