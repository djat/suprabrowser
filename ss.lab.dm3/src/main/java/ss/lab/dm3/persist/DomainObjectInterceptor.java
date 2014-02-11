package ss.lab.dm3.persist;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.proxy.HibernateProxy;

import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;
import ss.lab.dm3.persist.wrap.IWrappedDomainObject;
import ss.lab.dm3.utils.ReflectionHelper;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 
 * @author Dmitry Goncharov
 */
final class DomainObjectInterceptor implements MethodInterceptor {
	

	/**
	 * 
	 */
	private static final String GET_WRAPPED_ENTITY_CLAZZ_METHOD_NAME = "getWrappedEntityClazz";
	private static final String WRITE_REPLACE_METHOD_NAME = "writeReplace";

	protected static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(DomainObjectInterceptor.class );
	
	private static final String MARK_4DM = "4DM$$";
	
	/**
	 * 
	 */
	private static final DmNamingPolicy DM_NAMING_POLICY = new DmNamingPolicy();
	
	private static final Map<Class<?>,Class<?>> classToProxy = Collections.synchronizedMap( new HashMap<Class<?>, Class<?>>() );

	
	private final Class<? extends DomainObject> entityClazz;

	/**
	 * @param clazz
	 */
	public DomainObjectInterceptor(Class<? extends DomainObject> entityClazz ) {
		super();
		this.entityClazz = entityClazz;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[],
	 *      net.sf.cglib.proxy.MethodProxy)
	 */
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
			throws Throwable {
		// 0. If method is getWrappedEntityClazz
		final Class<?>[] parameterTypes = method.getParameterTypes();
		final String methodName = method.getName();
		if ( parameterTypes.length == 0 && 
			methodName.equals( GET_WRAPPED_ENTITY_CLAZZ_METHOD_NAME ) ) {
			return this.entityClazz;
		}
		if ( parameterTypes.length == 0 && 
			methodName.equals( WRITE_REPLACE_METHOD_NAME ) ) {
			final DomainObject domainObject = (DomainObject)obj;
			final ObjectController ctrl = (domainObject).ctrl;
			if ( ctrl.isDetached() ) {
				return domainObject;
			}
			else {
				return new SerializableDomainObjectProxy( domainObject.getQualifiedId() );			
			}
		}
		final DomainObject domainObject = (DomainObject)obj;
		final ObjectController ctrl = (domainObject).ctrl;
		// 1. If object is detached skip interception
		if ( ctrl.isDetached() ) {
			return proxy.invokeSuper(obj, args);
		}
		// 2. If method is set/get id or default object method then skip it 			
		// TODO think more about what methods should be intercepted
		if ( parameterTypes.length == 0 ) {
			if ( methodName.equals( "getId" ) ||
				 methodName.equals( "toString" ) ||	
				 methodName.equals( "hashCode" ) ) {
				return proxy.invokeSuper(obj, args);
			}
		}
		else if ( parameterTypes.length == 1 ) {
			if ( methodName.equals( "setId" ) || 
				 methodName.equals( "equals" )	) {
				return proxy.invokeSuper(obj, args);
			}
		}
		// 3. Interception case
		// 3.1. If object is proxy - load it
		if ( ctrl.isProxy() ) {
			ctrl.ensureLoaded();
		}
		// Invoke implementation
		Object ret = proxy.invokeSuper(obj, args);
		if ( ReflectionHelper.hasSetterSignature(method) &&
			 ReflectionHelper.hasSetterPrefix(method) ) {
			// So it's setter
			// 1. If it clean change state to dirty 
			if ( ctrl.isClean() ) {
				ctrl.markDirty();
			}	
			// 2. If reference property then update notify orm about update
			Property<?> property = ctrl.getMapper().getPropertyBySettedMethod(method );
			if ( property != null && property.isReference() ) {
				// NativeReferenceAccessor accessor = (NativeReferenceAccessor) property.getAccessor();
				Object value = args[ 0 ];
				Long objectId = getObjectId( value );
				final OrmManager ormManager = ctrl.getDomain().getOrmManager();
				// TODO remove this?
				if ( !ormManager.isObjectManaged(domainObject) ) {
					log.error( "Object is not managed " + domainObject );
				}
				ormManager.referenceChanged( domainObject, property.getName(), objectId );				
			}
		}
		return ret;
	}

	/**
	 * @param obj
	 */
	private static Long getObjectId(Object obj) {
		if ( obj == null ) {
			return null;
		}
		else if ( obj instanceof QualifiedReference ) {
			return ((QualifiedReference<?>) obj).getTargetId();
		}
		else if ( obj instanceof DomainObject ) {
			return ((DomainObject) obj ).getId();
		}
		else if ( obj instanceof QualifiedObjectId ) {
			return ((QualifiedObjectId<?>) obj).getId();
		}
		else {
			throw new IllegalArgumentException( "Unexpected object " + obj );
		}
	}

	public static <T extends DomainObject> T newProxy(Class<T> clazz, Long id ) {
		final T obj = newWrapped(clazz);
		obj.setId(id);
		obj.ctrl.changeState(ObjectController.State.PROXY);
		return obj;
	}
	
	public static <T extends DomainObject> T newWrapped(Class<T> clazz) {
		try {
			if ( isGeneratedClass(clazz) ) {
				log.error( "Clazz already enchanced by CGLIB " + clazz );	
			}
			Class<?> proxyClazz;
			synchronized(DomainObjectInterceptor.class) {
				proxyClazz = classToProxy.get(clazz);
				if ( proxyClazz == null ) {
					 proxyClazz = createWrappedClass(clazz);
					 if ( !proxyClazz.getSimpleName().contains( MARK_4DM ) ) {
						 throw new IllegalStateException( "Invalid wrapped class " + proxyClazz );
					 }
					 classToProxy.put(clazz,proxyClazz);
				}
			}
			Enhancer.registerCallbacks( proxyClazz, new Callback[] {new DomainObjectInterceptor( clazz ) } );
			T obj = clazz.cast( ReflectionHelper.create( proxyClazz ) );
			return obj;
		}
		catch (Throwable e) {
			throw new Error("Can't create wrapper for "+ clazz, e);
		}

	}

	/**
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> boolean isGeneratedClass(Class<T> clazz) {
		return Enhancer.isEnhanced( clazz );
	}

	/**
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private static Class<?> createWrappedClass(Class<?> clazz) {		
		Enhancer e = new Enhancer();
		e.setNamingPolicy( DM_NAMING_POLICY);
		e.setSuperclass(clazz);
		e.setInterfaces( new Class<?>[] { IWrappedDomainObject.class } );
		e.setCallbackType( DomainObjectInterceptor.class );
		return e.createClass();
	}
	
	public static Class<? extends DomainObject> getClassWoProxy(DomainObject object) {
		if ( object instanceof IWrappedDomainObject ) {
			return ((IWrappedDomainObject) object).getWrappedEntityClazz();
		}
		// TODO May be remove dependency of Hibernate?
		else if ( object instanceof HibernateProxy ) {
			return HibernateUtils.getClassWoProxy(object);
		}
		else {
			return object.getClass();
		}
	}

	private static final class DmNamingPolicy implements NamingPolicy {

		public String getClassName(String prefix, String source, Object key, Predicate names) {
			 
		    StringBuffer sb = new StringBuffer();
		    sb.append( 
		              (prefix != null) ? 
		                                 ( 
		                                  prefix.startsWith("java") ? 
		                                               "$" + prefix : prefix 
		                                 )
		                                : "net.sf.cglib.empty.Object"
		             );
		    sb.append("$$");
		    sb.append(source.substring(source.lastIndexOf('.') + 1));
		    sb.append("CGLIB$$" + MARK_4DM);
		    sb.append(Integer.toHexString(key.hashCode()));
		    String base = sb.toString();
		    String attempt = base;
		    int index = 2;
		    while (names.evaluate(attempt)) {
		        attempt = base + "_" + index++;
		    }
		   
		    return attempt;
		}
	}

}

