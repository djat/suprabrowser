package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.orm.objects.Bid;
import ss.lab.dm3.orm.objects.Item;
import ss.lab.dm3.orm.objects.TypeEnum;

public class PropertyAccessorTestCase extends AbstractOrmTestCase {

	public void testGetters() {
		
		Bid bid = new Bid();
		Item item = new Item();
		item.setId( 1L );
		item.setHeight(20);
		item.setItemId(new Long(16));
		item.setVisible(new Boolean(true));
		assertEquals( 1L, getValue( item, "id" ) );
		assertEquals( 20, getValue( item, "height" ) );
		assertEquals( 16L, getValue( item, "itemId" ) );
		assertEquals( true, getValue( item, "visible" ) );
		
		
		bid.setItem( item  );
		bid.setSize(5);
		bid.setEnabled(true);
		bid.setBidId(20L);
		bid.setState("normal");
		bid.setType(TypeEnum.DOUBLE);
		assertEquals( item, getValue( bid, "item" ) );  
		assertEquals( 5, getValue( bid, "size") );
		assertEquals( true, getValue( bid, "enabled") );
		assertEquals( new Long(20), getValue( bid, "bidId") );
		assertEquals( "normal", getValue( bid, "state") );
		assertEquals( TypeEnum.DOUBLE, getValue( bid, "type") );
	}
	
	public void testSetters() {
		Bid bid = new Bid();
		Item item = new Item();
		
		setValue(item, "itemId", 2L);
		setValue(item, "visible", false);
		setValue(item, "height", 17);
		setValue(item, "id", 8L);
		assertEquals( new Long(2), item.getItemId() );
		assertEquals(new Boolean(false), item.getVisible());
		assertEquals(new Integer(17), item.getHeight());
		assertEquals(new Long(8), item.getId());
		
		setValue(bid, "size", 21);
		setValue(bid, "enabled", false);
		setValue(bid, "bidId", 99L);
		setValue(bid, "state", "normal");
		setValue(bid, "type", TypeEnum.TRIPLE);
		setValue(bid, "id", 3L);
		assertEquals(99, bid.getBidId());
		assertEquals(new Long(3), bid.getId());
		assertEquals(21, bid.getSize());
		assertEquals("normal", bid.getState());
		assertEquals(TypeEnum.TRIPLE, bid.getType());
	}
	
	protected IAccessor createAccessor( Class<? extends DomainObject> beanClazz, String propertyName ) {
		return createPropertyDescriptor(beanClazz, propertyName).createPropertyAccessor();
	}
	
	protected Object getValue(DomainObject beanObject, String propertyName) {
		IAccessor accessor = createAccessor( beanObject.getEntityClass(), propertyName );
		return accessor.getValue( beanObject );
	}
	
	protected void setValue(DomainObject beanObject, String propertyName, Object value) {
		IAccessor accessor = createAccessor( beanObject.getEntityClass(), propertyName );
		accessor.setValue(beanObject,value);
	}
}
