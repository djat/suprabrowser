package ss.lab.dm3.orm.mapper.map;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author Dmitry Goncharov
 */
public class NamingStrategy {

	private static final String DATA_SUFFIX = "Data";
	
	/**
	 * @param beanClazz
	 */
	public String getEntityName(Class<?> beanClazz) {
		String simpleName = beanClazz.getSimpleName();
		if ( MappedObject.class.isAssignableFrom(beanClazz) ) {
			if (simpleName.endsWith(DATA_SUFFIX)) {
				return simpleName.substring(0, simpleName.length()
						- DATA_SUFFIX.length());
			}
			return simpleName;
		}
		return simpleName;
	}
	
	
}
