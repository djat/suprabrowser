package ss.lab.dm3.orm.mapper.map;

import java.lang.reflect.Method;
import java.util.Set;

import ss.lab.dm3.orm.managed.IManagedCollection;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * 
 * It see only get/set properties and get-only ManagedCollection  
 * 
 * @author Dmitry Goncharov
 */
public class PropertyFindByMethodStrategy extends PropertyFindStrategy {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.map.PropertyFindStrategy#collectDeclaredProperties(java.lang.Class, java.util.List)
	 */
	@Override
	protected void collectDeclaredProperties(Class<?> beanClazz,
			Set<String> properties) {
		for( Method method : beanClazz.getDeclaredMethods() ) {
			final String propertyNameByGetter = ReflectionHelper.getPropertyNameByGetter(method);
			if ( propertyNameByGetter != null ) {
				if ( ReflectionHelper.findSetterByGetter(method, propertyNameByGetter ) != null ||
					 ( IManagedCollection.class.isAssignableFrom( method.getReturnType() ) &&
					   ReflectionHelper.findPropertyDeclaration(beanClazz, propertyNameByGetter ) != null ) ) {
					properties.add( propertyNameByGetter );
				}
			}
		}		
	}

}
