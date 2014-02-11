package ss.lab.dm3.orm.entity;

import java.util.ArrayList;

import ss.lab.dm3.context.InjectionUtils;
import ss.lab.dm3.context.ValueRestorer;
import ss.lab.dm3.orm.AbstractMapperTestCase;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ObjectResolver;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.objects.Bid;
import ss.lab.dm3.orm.objects.Item;
import ss.lab.dm3.orm.objects.TypeEnum;
import ss.lab.dm3.persist.DomainObject;

public class EntityTestCase extends AbstractMapperTestCase {

	public void testSave() {
		Bid bid = new Bid();
		bid.setSize( 1 );
		bid.setBidId(20L);
		bid.setEnabled(false);
		bid.setId(11L);
		bid.setState("state");
		bid.setType(TypeEnum.DOUBLE);
		
		java.util.List<Bid> list = new ArrayList<Bid>();
		list.add(bid);
		
		Item item = new Item();
		item.setId(18L);
		item.setHeight(120);
		item.setItemId(10L);
		item.setVisible(true);
		item.setBidsList(list);
		
		bid.setItem(item);
		
		OrmManagerResolveHelper.beginInterceptionForCurrentThread( getOrm() );
		try {
			Entity bidEntity = getMapper().toEntity( bid );
			EntityParser entityParser = new EntityParser( getMapper(), bidEntity );
			assertEquals( 6, bidEntity.getValues().length );
			assertEquals( 1, entityParser.getValue( "size" ) );
			assertEquals( QualifiedObjectId.create( Item.class, 18L ), entityParser.getValue( "item" ) );
			assertEquals( TypeEnum.DOUBLE, entityParser.getValue( "type" ) );
			assertEquals( 20L, entityParser.getValue( "bidId" ) );
			assertEquals( false, entityParser.getValue( "enabled" ) );
			assertEquals( 11L, entityParser.getValue( "id" ) );
			
			Entity itemEntity = getMapper().toEntity( item );
			EntityParser itemEntityParser = new EntityParser( getMapper(), itemEntity );
			assertEquals( 4, itemEntity.getValues().length );
			assertEquals(18L, itemEntityParser.getValue("id"));
			assertEquals(120, itemEntityParser.getValue("height"));
			assertEquals(true, itemEntityParser.getValue("visible"));
			assertEquals(10L, itemEntityParser.getValue("itemId"));
		}
		finally {
			OrmManagerResolveHelper.endInterceptionForCurrentThread();
		}
	}

	
	public void testLoad() {
		final Item bidItem = new Item();
		bidItem.setId( 10L );
		ObjectResolver objectResolver = new ObjectResolver() {
			@Override
			public <T extends MappedObject> T resolve(Class<T> entityClass,
					Long id) {
				if ( entityClass.equals( bidItem.getEntityClass() ) && id == 10 ) {
					return entityClass.cast(bidItem);
				}
				return null;
			}
		};
		final OrmManager orm = getOrm();
		final ValueRestorer inject = InjectionUtils.inject( orm, "objectResolver", objectResolver);
		OrmManagerResolveHelper.beginInterceptionForCurrentThread(orm);
		try {
			EntityBuilder entityBuilder = new EntityBuilder( getBeanMapper( Bid.class).getMap() );
			entityBuilder.setValue( "size", 1 );
			entityBuilder.setValue( "item", QualifiedObjectId.create( Item.class, 10L) );
			entityBuilder.setValue( "enabled", true );
			entityBuilder.setValue( "bidId", 3 );
			entityBuilder.setValue( "id", 7 );
			entityBuilder.setValue( "type", TypeEnum.TRIPLE );
			DomainObject rawObject = getMapper().toObject( entityBuilder.create(), false );
			assertEquals( Bid.class, rawObject.getEntityClass() );
			Bid bid = (Bid) rawObject ;
			assertEquals( 1, bid.getSize() );
			assertEquals( true, bid.isEnabled());
			assertEquals( (Object)7L, bid.getId());
			assertEquals( 3, bid.getBidId());
			assertEquals(TypeEnum.TRIPLE, bid.getType());
			assertEquals( bidItem, bid.getItem() );
			
			EntityBuilder itemBuilder = new EntityBuilder( getBeanMapper( Item.class).getMap() );
			itemBuilder.setValue("id", 5);
			itemBuilder.setValue("itemId", 7);
			itemBuilder.setValue("visible", true);
			itemBuilder.setValue("height", 45);
			DomainObject itemObject = getMapper().toObject( itemBuilder.create(), false );
			assertEquals(Item.class, itemObject.getEntityClass());
			Item item = (Item) itemObject;
			assertEquals(true, (boolean)item.getVisible());
			assertEquals(5L, (Object)item.getId());
			assertEquals(45, (Object)item.getHeight());
			assertEquals(7L, (Object)item.getItemId());	
		}
		finally {
			inject.restore();
			OrmManagerResolveHelper.endInterceptionForCurrentThread();
		}		
	}
	

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.AbstractMapperTestCase#getMappedClasses()
	 */
	@Override
	protected Class<?>[] getMappedClasses() {
		return new Class<?>[] { Bid.class, Item.class };
	}
}

