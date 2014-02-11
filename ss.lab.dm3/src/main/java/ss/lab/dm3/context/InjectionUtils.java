package ss.lab.dm3.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.FieldAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.MethodAccessor;
import ss.lab.dm3.utils.ReflectionHelper;

public class InjectionUtils {

	private static ExecutionContext EXECUTION_CONTEXT = new ExecutionContext();
	
	public static ValueRestorer inject( Object bean, String propertyName, Object propertyValue ) {
		IAccessor accessor = createAccessor(bean.getClass(), propertyName);
		Object originalValue = accessor.getValue(bean);
		accessor.setValue(bean, propertyValue );
		return new ValueRestorer ( bean,  accessor, originalValue ); 
	}

	/**
	 * @param bean
	 * @param propertyName
	 * @return 
	 */
	private static IAccessor createAccessor(Class<?> beanClazz, String propertyName) {
		Method getter = ReflectionHelper.findGetter( beanClazz, propertyName);
		Method setter = null;
		if ( getter != null ) {
			setter = ReflectionHelper.findSetterByGetter(getter, propertyName);
		}
		if ( getter != null && setter != null ) {
			return new MethodAccessor( getter, setter );
		}
		else {
			Field field = ReflectionHelper.findPropertyDeclaration( beanClazz, propertyName);
			if ( field == null ) {
				throw new IllegalArgumentException( "Can't find getter/setter or field for property '" + propertyName + "' in " + beanClazz );
			}
			return new FieldAccessor( field );
		}
	}
	
	public static RestorePoint createRestorePoint() {
		return new RestorePoint();
	}
	
	public static void push( Object value, Object ... keys ) {
		for( Object key : keys) {
			EXECUTION_CONTEXT.push(value, key);
		}
	}
	
	public static void pop( Object ... keys ) {
		for( Object key : keys) {
			EXECUTION_CONTEXT.pop( key );
		}
	}
	
	public static Object find( Object key ) {
		return EXECUTION_CONTEXT.find( key );
	}
	
	public static <T> T find( Class<T> clazz, Object key ) {
		return clazz.cast( EXECUTION_CONTEXT.find( key ) );
	}
	
	public static Object find( Object key, Object defaultValue ) {
		return EXECUTION_CONTEXT.find( key, defaultValue );
	}
	
	public static <T> T find( Class<T> clazz, Object key, T defaultValue  ) {
		return clazz.cast( EXECUTION_CONTEXT.find( key, defaultValue ) );
	}
	
}
