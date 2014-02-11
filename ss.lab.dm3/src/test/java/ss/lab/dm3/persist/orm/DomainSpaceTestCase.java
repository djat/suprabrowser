/**
 * 
 */
package ss.lab.dm3.persist.orm;


import junit.framework.TestCase;
import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperFactory;
import ss.lab.dm3.orm.mapper.map.BeanMap;
import ss.lab.dm3.orm.mapper.map.BeanSpace;
import ss.lab.dm3.orm.mapper.property.converter.MappedObjectTypeConverter;
import ss.lab.dm3.orm.mapper.property.descriptor.IPropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.ManagedCollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.NativeReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.PlainDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.QualifiedReferenceDescriptor;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.testsupport.TestConfigurationProvider;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;

/**
 *
 */
public class DomainSpaceTestCase extends TestCase {

	public void test() {
		Configuration configuration = TestConfigurationProvider.INSTANCE.get();
		final Class<?>[] dataClasses = configuration.getDomainDataClasses().toArray();
		transformAndCheck(dataClasses);
	}

	/**
	 * @param dataClasses
	 * @param domainPackages
	 */
	private void transformAndCheck(final Class<?>[] dataClasses) {
		final MapperFactory mapperFactory = new MapperFactory();
		Mapper<DomainObject> dataMapper = mapperFactory.create(
				DomainObject.class, dataClasses );
		BeanSpace domainSpace = dataMapper.getBeanSpace();	
		assertEquals( 8, domainSpace.size() );
		BeanMap supraSphereMap = domainSpace.get( "SupraSphere" );
		assertEquals( SupraSphere.class, supraSphereMap.getEntityClazz() );
		PropertyDescriptor<?>[] supraSphereProperties = supraSphereMap.getPropertyDescriptors();
		assertEquals( 3, supraSphereProperties.length );
		checkProperty( supraSphereMap.findProperty( "id" ), PlainDescriptor.class, Long.class, "id" );
		final PropertyDescriptor<?> sphereProperty = supraSphereMap.findProperty( "sphere" );
		checkProperty( sphereProperty, NativeReferenceDescriptor.class, Sphere.class, "sphere" );
		assertSame( Sphere.class, sphereProperty.getValueClazz() );
		assertSame( MappedObjectTypeConverter.class, sphereProperty.createTypeConverter().getClass() );
		
		BeanMap sphereMap = domainSpace.get( "Sphere" );
		
		assertEquals( Sphere.class, sphereMap.getEntityClazz() );
		PropertyDescriptor<?>[] sphereProperties = sphereMap.getPropertyDescriptors();
		assertEquals( 11, sphereProperties.length );
		checkProperty( sphereMap.findProperty( "id" ), PlainDescriptor.class, Long.class, "id" );
		checkProperty( sphereMap.findProperty( "parentSphere" ), NativeReferenceDescriptor.class, Sphere.class, "parentSphere" );
		checkProperty( sphereMap.findProperty( "extension" ), QualifiedReferenceDescriptor.class, QualifiedReference.class, "extension" );
		final PropertyDescriptor<?> childrenSpheres = sphereMap.findProperty( "childrenSpheres" );
		checkProperty( childrenSpheres, ManagedCollectionDescriptor.class, ChildrenDomainObjectList.class, "childrenSpheres" );
		ManagedCollectionDescriptor<?> managedChildrenSpheres = (ManagedCollectionDescriptor<?>) childrenSpheres;
		assertEquals( "parentSphere", managedChildrenSpheres.getMappedByName() );
	}

	/**
	 * @param propertyDescriptor
	 * @param valueClass
	 * @param string
	 */
	private void checkProperty(PropertyDescriptor<?> propertyDescriptor,
			Class<? extends IPropertyDescriptor> descriptorClazz, 
			Class<?> valueClass, String name) {
		assertNotNull( propertyDescriptor );
		assertEquals( descriptorClazz, propertyDescriptor.getClass() );
		assertEquals( name, propertyDescriptor.getName() );
		assertEquals( valueClass, propertyDescriptor.getValueClazz() );
	}
}
