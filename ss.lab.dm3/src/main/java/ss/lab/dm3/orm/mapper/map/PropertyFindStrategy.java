package ss.lab.dm3.orm.mapper.map;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitry Goncharov
 */
public abstract class PropertyFindStrategy {

	public Set<String> find( Class<?> beanClazz ) {
		Set<String> properties = new HashSet<String>();
		while( beanClazz != null && beanClazz != Object.class ) {
			collectDeclaredProperties(beanClazz, properties);
			beanClazz = beanClazz.getSuperclass();
		}
		return properties;
	}
	
	protected abstract void collectDeclaredProperties( Class<?> beanClazz, Set<String> properties );
	
}
