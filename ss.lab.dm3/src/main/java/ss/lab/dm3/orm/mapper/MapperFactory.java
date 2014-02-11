package ss.lab.dm3.orm.mapper;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.map.BeanSpace;
import ss.lab.dm3.orm.mapper.map.BeanSpaceFactory;

/**
 * @author Dmitry Goncharov
 */
public class MapperFactory {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
		.getLog(getClass());	
		
	private BeanSpaceFactory beanSpaceFactory = new BeanSpaceFactory();
	
	/**
	 * @param <T>
	 * @param baseObjectClass
	 * @return
	 */
	public <T extends MappedObject> Mapper<T> create(Class<T> baseObjectClazz, Class<?>[] objectsClasses ) {
		final BeanWrapper<T> beanWrapper = new BeanWrapper<T>();
		return create(baseObjectClazz, objectsClasses, beanWrapper);
	}

	/**
	 * @param <T>
	 * @param baseObjectClazz
	 * @param objectsClasses
	 * @param beanWrapper
	 * @return
	 */
	public <T extends MappedObject>  Mapper<T> create(Class<T> baseObjectClazz, Class<?>[] objectsClasses,
			final BeanWrapper<T> beanWrapper) {
		BeanSpace beanSpace = this.beanSpaceFactory.create(baseObjectClazz, objectsClasses);
		return new Mapper<T>( beanSpace, beanWrapper );
	}

	public <T extends MappedObject> Mapper<T> create(MapperParamerts mapperParams, BeanWrapper<T> beanWrapper ) {
		return new Mapper<T>( mapperParams.getBeanSpace(), beanWrapper );
	}	
	
}
