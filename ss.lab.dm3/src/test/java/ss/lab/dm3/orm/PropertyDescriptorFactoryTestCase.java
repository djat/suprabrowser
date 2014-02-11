package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.NativeReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.PlainDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.TransientDescriptor;
import ss.lab.dm3.orm.objects.Bid;
import ss.lab.dm3.orm.objects.Item;
import ss.lab.dm3.orm.objects.TypeEnum;

public class PropertyDescriptorFactoryTestCase extends AbstractOrmTestCase {

	public void test() {
		PropertyDescriptor<?> itemPropertyDescriptor = createPropertyDescriptor( Bid.class, "item" );
		PropertyDescriptor<?> statePropertyDescriptor = createPropertyDescriptor( Bid.class, "state" );
		PropertyDescriptor<?> sizePropertyDescriptor = createPropertyDescriptor( Bid.class, "size" );
		PropertyDescriptor<?> enabledPropertyDescriptor = createPropertyDescriptor( Bid.class, "enabled" );
		PropertyDescriptor<?> bidIdPropertyDescriptor = createPropertyDescriptor( Bid.class, "bidId" );
		PropertyDescriptor<?> typePropertyDescriptor = createPropertyDescriptor( Bid.class, "type" );
		
		checkPropertyDescriptor( itemPropertyDescriptor, "item", Item.class, NativeReferenceDescriptor.class );
		checkPropertyDescriptor( statePropertyDescriptor, "state", String.class, TransientDescriptor.class );
		checkPropertyDescriptor( sizePropertyDescriptor, "size", int.class, PlainDescriptor.class );
		checkPropertyDescriptor( enabledPropertyDescriptor, "enabled", boolean.class, PlainDescriptor.class );
		checkPropertyDescriptor( bidIdPropertyDescriptor, "bidId", long.class, PlainDescriptor.class );
		checkPropertyDescriptor( typePropertyDescriptor, "type", TypeEnum.class, PlainDescriptor.class );
		
		//PropertyDescriptor<?> bidsListPropertyDescriptor = createPropertyDescriptor( Item.class, "bidsList" );
		PropertyDescriptor<?> visiblePropertyDescriptor = createPropertyDescriptor( Item.class, "visible" );
		PropertyDescriptor<?> heightPropertyDescriptor = createPropertyDescriptor( Item.class, "height" );
		PropertyDescriptor<?> itemIdPropertyDescriptor = createPropertyDescriptor( Item.class, "itemId" );
		
		//checkPropertyDescriptor( bidsListPropertyDescriptor, "bidsList", List.class, NativeReferenceDescriptor.class );
		checkPropertyDescriptor( visiblePropertyDescriptor, "visible", Boolean.class, PlainDescriptor.class );
		checkPropertyDescriptor( heightPropertyDescriptor, "height", Integer.class, PlainDescriptor.class );
		checkPropertyDescriptor( itemIdPropertyDescriptor, "itemId", Long.class, PlainDescriptor.class );
	}

	@SuppressWarnings("unchecked")
	private void checkPropertyDescriptor(
			PropertyDescriptor<?> propertyDescriptor, String name,
			Class<?> propertyValueClazz, Class<? extends PropertyDescriptor> descriptorClazz ) {
		assertEquals("Check property name", name, propertyDescriptor.getName());
		assertEquals("Check property value class", propertyValueClazz, propertyDescriptor.getValueClazz());
		assertEquals("Check descriptor class", descriptorClazz, propertyDescriptor.getClass());
	}
}
