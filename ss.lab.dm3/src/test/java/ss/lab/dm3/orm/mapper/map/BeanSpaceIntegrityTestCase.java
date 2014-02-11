/**
 * 
 */
package ss.lab.dm3.orm.mapper.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.NativeCollectionDescriptor;
import junit.framework.TestCase;

/**
 *
 */
public class BeanSpaceIntegrityTestCase extends TestCase {

	@SuppressWarnings("unchecked")
	public void test() {
		BeanSpaceFactory spaceFactory = new BeanSpaceFactory();
		BeanSpace beanSpace = spaceFactory.create( BaseObject.class, new Class<?> [] { 
			Bid.class, Item.class, FavoriteItem.class
		} );
		BeanMap itemMap = beanSpace.get( Item.class );
		PropertyDescriptor<?> bigsProperty = itemMap.findProperty( "bids" );
		assertNotNull( bigsProperty );
		NativeCollectionDescriptor<Collection<?>> bigsCollection = (NativeCollectionDescriptor<Collection<?>>) bigsProperty;
		assertSame( Bid.class, bigsCollection.getItemType() );
		System.out.println( bigsProperty );
		
//		Generation generation = new Generation( beanSpace  );
//		generation.run("./src/test/java/ss/lab/dm3/orm/mapper/map/dg/",
//		"ss.lab.dm3.orm.mapper.map.dg");			
	}
	
	public static class BaseObject implements MappedObject {
		
		private Long id;

		/**
		 * @return the id
		 */
		public Long getId() {
			return this.id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(Long id) {
			this.id = id;
		}
	}
	
	public static class Bid extends BaseObject {
	
		private Item item;
		
		/**
		 * @return the item
		 */
		@ManyToOne()
		public Item getItem() {
			return this.item;
		}
		
		/**
		 * @param item the item to set
		 */
		public void setItem(Item item) {
			this.item = item;
		}
				
		
	}
	
	public static class Item extends BaseObject {
	
		private Set<Bid> bids = new HashSet<Bid>();

		/**
		 * @return the bids
		 */
		@OneToMany(mappedBy="item")
		public Set<Bid> getBids() {
			return this.bids;
		}

		/**
		 * @param bids the bids to set
		 */
		public void setBids(Set<Bid> bids) {
			this.bids = bids;
		}
		
	}
	
	public static class FavoriteItem extends BaseObject {
		
		private Item item;

		/**
		 * @return the item
		 */
		public Item getItem() {
			return this.item;
		}

		/**
		 * @param item the item to set
		 */
		public void setItem(Item item) {
			this.item = item;
		}
		
		
	}
}
