package ss.lab.dm3.orm.mapper.map;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Inheritance;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptorFactory;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * @author Dmitry Goncharov
 */
public class BeanMapFactory {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private volatile long counter = 0;
	
	private PropertyFindStrategy propertyFindStrategy = new PropertyFindByMethodStrategy();
	
	private NamingStrategy namingStrategy = new NamingStrategy();
		
	public synchronized BeanMap create( Class<? extends MappedObject> beanClazz ) {
		String name = this.namingStrategy.getEntityName( beanClazz );
		List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<PropertyDescriptor<?>>(); 
		for( String propertyName : this.propertyFindStrategy.find(beanClazz) ) {
			// TODG update find logic
			if ( ReflectionHelper.findPropertyDeclaration(beanClazz,
					propertyName) != null ) { 
				propertyDescriptors.add( PropertyDescriptorFactory.INSTANCE.create(beanClazz, propertyName) );
			}
		}
		final Inheritance inheritance = beanClazz.getAnnotation( Inheritance.class );
		return new BeanMap( ++this.counter, name, beanClazz, inheritance != null, propertyDescriptors.toArray( new PropertyDescriptor<?>[]{} ) ); 
	}
	
}
