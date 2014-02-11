package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptorFactory;
import ss.lab.dm3.persist.DomainObject;
import junit.framework.TestCase;

public abstract class AbstractOrmTestCase extends TestCase {

	protected PropertyDescriptor<?> createPropertyDescriptor(Class<? extends DomainObject> beanClazz,
			String propertyName ) {
		return PropertyDescriptorFactory.INSTANCE.create(beanClazz, propertyName);
	}
	
}
