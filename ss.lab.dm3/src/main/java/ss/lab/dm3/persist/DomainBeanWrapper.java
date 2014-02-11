package ss.lab.dm3.persist;

import ss.lab.dm3.orm.mapper.BeanWrapper;

/**
 * 
 * @author Dmitry Goncharov
 */
public final class DomainBeanWrapper extends BeanWrapper<DomainObject> {

	@Override
	public Class<? extends DomainObject> getBeanClass(DomainObject objectClazz) {
		return (Class<? extends DomainObject>) DomainObjectInterceptor.getClassWoProxy(objectClazz);
	}

	@Override
	public DomainObject newInstance(Class<? extends DomainObject> beanClazz) {
		return DomainObjectInterceptor.newWrapped(beanClazz);
	}

}
