package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;

/**
 * @author Dmitry Goncharov
 */
public interface IBeanMapperProvider {

	/**
	 * @param bean
	 * @return
	 */
	BeanMapper<?> get(MappedObject bean);
	
	BeanMapper<?> get(Class<? extends MappedObject> beanClazz);
	
	BeanMapper<?> get(String qualifier);
	
	Mapper<?> get();	
}
