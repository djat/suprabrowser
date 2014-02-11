package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperFactory;
import ss.lab.dm3.persist.DomainObject;

public abstract class AbstractMapperTestCase extends AbstractOrmTestCase {

	private Mapper<DomainObject> mapper = null;
	
	private OrmManager orm = null;
	
	private final MapperFactory mapperFactory = new MapperFactory();
	
	/**
	 * @return
	 */
	protected Mapper<DomainObject> getMapper() {
		if (this.mapper == null) {
			this.mapper = createMapper();
		}
		return this.mapper;
	}
	
	public OrmManager getOrm() {
		if ( this.orm == null ) {
			this.orm = new OrmManager();
			this.orm.setBeanMapperProvider( new TestBeanMapperProvider() {
				@Override
				public BeanMapper<?> get(MappedObject bean) {
					return getMapper().get( (DomainObject) bean);
				}
			});
			this.orm.setObjectResolver( new ObjectResolver() {

				@Override
				public <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
					return AbstractMapperTestCase.this.resolve( entityClass, id );
				}
				
			});
		}
		return this.orm;
	}

	protected <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
		throw new UnsupportedOperationException();
	}
	
	private Mapper<DomainObject> createMapper() {
		return this.mapperFactory.create(DomainObject.class, getMappedClasses());
	}
	
	protected abstract Class<?>[] getMappedClasses();

	protected BeanMapper<DomainObject> getBeanMapper(Class<? extends DomainObject> beanClazz ) {
		return getMapper().get(beanClazz);
	}
	
	protected BeanMapper<DomainObject> getBeanMapper(DomainObject bean) {
		return getBeanMapper(bean.getEntityClass());
	}
	
}
