package ss.lab.dm3.orm;

import java.util.Date;

import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityBuilder;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.persist.DomainObject;

/**
 * 
 * @author Dmitry Goncharov
 */
public class PerformanceRun extends AbstractMapperTestCase {

	public static void main(String[] args) {
		new PerformanceRun().test();		
	}

	public void test() {
		final TestItem parent = new TestItem();
		parent.setId( 2L );
	
		final OrmManager ormManager = new OrmManager();
		ormManager.setObjectResolver( new ObjectResolver() {
			@Override
			public boolean isObjectManagedByOrm(MappedObject object) {
				return false;
			}

			@Override
			public <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
				return entityClass.cast(parent);
			}			
			
		});
		ormManager.setBeanMapperProvider( new TestBeanMapperProvider() {
			@Override
			public BeanMapper<?> get(MappedObject bean) {
				return get(bean);
			}			
		});
		OrmManagerResolveHelper.beginInterceptionForCurrentThread( ormManager );
		try {
			EntityBuilder eb = new EntityBuilder( getBeanMapper( TestItem.class ).getMap() );
			eb.setValue( "id", 1L );
			//eb.setValue( "title", "test" );
			
			
			eb.setValue( "parent", parent.getQualifiedId() );
			Entity entity = eb.create();
			TestItem item = new TestItem();
			BeanMapper<DomainObject> beanMapper = getBeanMapper( TestItem.class );
			final Date start = new Date();
			System.out.println( start );		
			for( int n = 0; n < 5000 * 1000; ++ n ) {
				beanMapper.toObject( item, entity );
			}
			Date end = new Date();
			System.out.println( end );
			System.out.println( end.getTime() - start.getTime()  + " ms" );
		}
		finally {
			OrmManagerResolveHelper.endInterceptionForCurrentThread();
		}
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.AbstractMapperTestCase#getMappedClasses()
	 */
	@Override
	protected Class<?>[] getMappedClasses() {
		return new Class<?>[] { TestItem.class };
	}
	
	public static class TestItem extends DomainObject {
		
		private String title;
		
		private TestItem parent;

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public TestItem getParent() {
			return this.parent;
		}

		public void setParent(TestItem parent) {
			this.parent = parent;
		}
		
		
		
	}
}
