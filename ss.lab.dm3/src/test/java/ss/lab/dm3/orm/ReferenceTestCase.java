package ss.lab.dm3.orm;

import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.simpleobjects.Bid;
import ss.lab.dm3.orm.simpleobjects.Item;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.DomainObject;

public class ReferenceTestCase extends AbstractMapperTestCase {

	

	public void testBidToItems() {
		final MockObjectResolver mockObjectResolver = new MockObjectResolver();
		intializeOrm(mockObjectResolver);
		final Mapper<DomainObject> mapper = getMapper();
		// Create items
		final Bid bid = new Bid();
		// Set up bid managed features
		mapper.setUpManagedFeatures(bid);
		bid.setId(1L);
		final Item item1 = new Item();
		// Set up mock item bids 
		setUpMockBidList(item1);
		//mapper.setUpManagedFeatures(item1);
		item1.setId(1L);
		final Item item2 = new Item();
		//mapper.setUpManagedFeatures(item2);
		item2.setId(2L);
		// Add it to resolver
		mockObjectResolver.add(bid);
		mockObjectResolver.add(item1);
		mockObjectResolver.add(item2);
		
		BeanMapper<DomainObject> bidMapper = mapper.get(Bid.class);
		assertEquals(2, bidMapper.getProperties().size());
		bidMapper.getProperty("item").setValue(bid, item1);
		System.out
				.println("Item bids is " + item1.getBids().toArray()[0].equals(bid));

	}


	/**
	 * @param item1
	 * @return
	 */
	private void setUpMockBidList(final Item item) {
		item.setBids( new ChildrenDomainObjectList<Bid>() {;
			private static final long serialVersionUID = 1L;
			{
				this.controller = new DomainCollectionController( item );
				this.controller.setItemType( Bid.class );
				this.controller.setMappedByName( "item" );
			}
		} );
	}

	private void setUpMockChildrenList(final Item item) {
		item.setChildren( new ChildrenDomainObjectList<Item>() {
			private static final long serialVersionUID = 1L;
			{
				this.controller = new DomainCollectionController( item );
				this.controller.setItemType( Item.class );
				this.controller.setMappedByName( "parent" );
			}
		});
	}

	public void testItems() {
	final MockObjectResolver mockObjectResolver = new MockObjectResolver();
		intializeOrm(mockObjectResolver);
		final Mapper<DomainObject> mapper = getMapper();
		// Create items
		final Bid bid = new Bid();
		mapper.setUpManagedFeatures( bid );
		
		bid.setId(1L);
		final Item item1 = new Item();
		item1.setId(1L);
		setUpMockChildrenList(item1);
		final Item item2 = new Item();
		item2.setId(2L);
		final Item item3 = new Item();
		item3.setId(3L);
		// Set up mock item bids 
		setUpMockBidList(item3);	
		setUpMockChildrenList(item3);
		final Item item4 = new Item();
		// Set up mock item bids 
		setUpMockBidList(item4);
		item4.setId(4L);
		// Add it to resolver
		mockObjectResolver.add(bid);
		mockObjectResolver.add(item1);
		mockObjectResolver.add(item2);
		mockObjectResolver.add(item3);
		mockObjectResolver.add(item4);
		// Try orm
		// First set bid to item4
		BeanMapper<DomainObject> bidMapper = mapper.get(Bid.class);
		bidMapper.getProperty("item").setValue(bid, item4);
		assertEquals( 1, item4.getBids().size() );
		assertEquals( bid, item4.getBids().toArray()[ 0 ] );
		assertEquals( bid.getItem(), item4 );
		// Then set it to item3
		bidMapper.getProperty("item").setValue(bid, item3);
		assertEquals( 0, item4.getBids().size() );
		assertEquals( 1, item3.getBids().size() );
		assertEquals( bid, item3.getBids().toArray()[ 0 ] );
		assertEquals( bid.getItem(), item3 );
		// 
		BeanMapper<DomainObject> itemMapper = mapper.get(Item.class);
		itemMapper.getProperty( "parent" ).setValue( item2, item1 );
		itemMapper.getProperty( "parent" ).setValue( item3, item1 );
		itemMapper.getProperty( "parent" ).setValue( item4, item3 );		
		System.out.println("Item1 children " + item1.getChildren().size() );
		System.out.println("Item2 children " + item2.getChildren().size() );
		System.out.println("Item3 children " + item3.getChildren().size() );
		System.out.println("Item4 children " + item4.getChildren().size() );	
	}

	/**
	 * @param mockObjectResolver
	 */
	private void intializeOrm(final MockObjectResolver mockObjectResolver) {
		final Mapper<DomainObject> mapper = getMapper();
		OrmManager ormManager = new OrmManager();
		ormManager.setObjectResolver(mockObjectResolver);
		ormManager.setBeanMapperProvider(new TestBeanMapperProvider() {
			@Override
			public BeanMapper<?> get(MappedObject bean) {
				return mapper.get((DomainObject) bean);
			}
		});
		OrmManagerResolveHelper.setOrmManagerProvider(new SimpleOrmManagerProvider(
				ormManager));
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.AbstractMapperTestCase#getMappedClasses()
	 */
	@Override
	protected Class<?>[] getMappedClasses() {
		return new Class<?>[] {
				Bid.class, Item.class };
	}



}
