package ss.graph;

import ss.common.CompareUtils;

public class ItemIdentity implements IItemIdentity {

	/**
	 * 
	 */
	private static final int NULL_ID = 0;
	
	
	private final String id;
	
	/**
	 * @param id
	 */
	public ItemIdentity(String id) {
		super();		
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj == this ) { 
			return true;
		}
		if ( obj instanceof ItemIdentity ) {
			ItemIdentity other = (ItemIdentity) obj;
			return CompareUtils.equals( this.id, other.id );			
		}
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id != null ? this.id.hashCode() : NULL_ID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ItemId:" + this.id;
	}


	
}
