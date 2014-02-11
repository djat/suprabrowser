package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;

public abstract class TestBeanMapperProvider implements IBeanMapperProvider {

	public abstract BeanMapper<?> get(MappedObject bean);	

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.IBeanMapperProvider#get(java.lang.String)
	 */
	public BeanMapper<?> get(String qualifier) {
		throw new UnsupportedOperationException();
	}

	public BeanMapper<?> get(Class<? extends MappedObject> beanClazz) {
		throw new UnsupportedOperationException();
	}
	
	public Mapper<?> get() {
		throw new UnsupportedOperationException();
	}
	
}
