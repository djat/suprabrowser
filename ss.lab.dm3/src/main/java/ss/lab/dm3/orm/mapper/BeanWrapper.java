package ss.lab.dm3.orm.mapper;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.utils.CantCreateObjectException;

/**
 * 
 * @author Dmitry Goncharov 
 */
public class BeanWrapper<T extends MappedObject> {

	/**
	 * Class<? extends T> is less convenient
	 * @param objectClazz
	 * @return
	 */
	 @SuppressWarnings("unchecked")
	public Class<? extends T> getBeanClass(T objectClazz) {
		 return (Class<? extends T>) objectClazz.getClass();
	 }

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.IBeanFactory#newInstance()
	 */
	public T newInstance( Class<? extends T> beanClazz) {
		try {
			final T newInstance = beanClazz.newInstance();
			return newInstance;
		} catch (InstantiationException ex) {
			throw new CantCreateObjectException(beanClazz, ex);
		} catch (IllegalAccessException ex) {
			throw new CantCreateObjectException(beanClazz, ex);
		}
	}	 
	 
}
