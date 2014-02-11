/**
 * 
 */
package ss.lab.dm3.orm.managed;

import java.util.HashMap;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author Dmitry Goncharov
 */
public class CollectionAccessorList {

	private final HashMap<Key, ICollectionAccessor> mappedByToCollectionAccessor = new HashMap<Key, ICollectionAccessor>();

	/**
	 * @param mappedByName
	 * @return
	 */
	public ICollectionAccessor get(String mappedByName,Class<? extends MappedObject> itemType ) {
		return this.mappedByToCollectionAccessor.get( new Key( mappedByName, itemType ) );
	}

	/**
	 * @param collectionAccessor
	 */
	public void add(ICollectionAccessor collectionAccessor) {
		final Key key = new Key( collectionAccessor.getMappedByName(), collectionAccessor.getItemType() );
		if ( this.mappedByToCollectionAccessor.containsKey(key) ) {
			throw new IllegalStateException( "Collection accessor with same key already exists" );
		}
		this.mappedByToCollectionAccessor.put( key,
			collectionAccessor);
	}

	
	private static class Key {
		
		private final String mappedBy;
		
		private Class<? extends MappedObject> itemType;

		/**
		 * @param mappedBy
		 * @param itemType
		 */
		public Key(String mappedBy, Class<? extends MappedObject> itemType) {
			super();
			this.mappedBy = mappedBy;
			this.itemType = itemType;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.itemType == null) ? 0 : this.itemType.hashCode());
			result = prime * result + ((this.mappedBy == null) ? 0 : this.mappedBy.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Key other = (Key) obj;
			if (this.itemType == null) {
				if (other.itemType != null)
					return false;
			}
			else if (!this.itemType.equals(other.itemType))
				return false;
			if (this.mappedBy == null) {
				if (other.mappedBy != null)
					return false;
			}
			else if (!this.mappedBy.equals(other.mappedBy))
				return false;
			return true;
		}
		
		
		
	}
}
