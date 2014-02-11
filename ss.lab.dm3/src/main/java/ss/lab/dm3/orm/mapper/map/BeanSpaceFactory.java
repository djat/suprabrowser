package ss.lab.dm3.orm.mapper.map;

import ss.lab.dm3.orm.MappedObject;

public class BeanSpaceFactory {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private BeanMapFactory beanMapFactory = new BeanMapFactory();

	@SuppressWarnings("unchecked")
	public synchronized BeanSpace create(
			Class<? extends MappedObject> baseBeanClazz, Class<?>[] beanClazzes) {
		BeanSpace beanSpace = new BeanSpace(baseBeanClazz);
		for (Class<?> beanClazz : beanClazzes) {
			BeanMap beanMap = this.beanMapFactory
					.create((Class<? extends MappedObject>) beanClazz);
			beanSpace.add(beanMap);
		}
		beanSpace.resolveCollectionItemTypes(); 
		beanSpace.validate();
		return beanSpace;
	}

}
