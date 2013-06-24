/**
 * 
 */
package ss.framework.domainmodel2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class UpdateData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6651847354645665253L;
	
	private final String ownerId;
	
	private final List<Record> newRecords = new ArrayList<Record>();
	
	private final List<Record> dirtyRecords = new ArrayList<Record>();
	
	private final List<Record> removedRecords = new ArrayList<Record>();
	
	/**
	 * @param ownerId
	 */
	public UpdateData(final String ownerId) {
		super();
		this.ownerId = ownerId;
	}
	
	/**
	 * @param ownerId
	 */
	public UpdateData() {
		this( "DEFAULT" );
	}
	
	/**
	 * @return the dirtyRecords
	 */
	public Iterable<Record> getDirtyRecords() {
		return this.dirtyRecords;
	}
	/**
	 * @return the newRecords
	 */
	public Iterable<Record> getNewRecords() {
		return this.newRecords;
	}
	/**
	 * @return the removedRecords
	 */
	public Iterable<Record> getRemovedRecords() {
		return this.removedRecords;
	}
	
	public void addRemoved(Record item ) {
		this.removedRecords.add( item );
	}
	
	public void addRemoved(List<Record> items ) {
		this.removedRecords.addAll( items );		
	} 
	
	public void addDirty(List<Record> items ) {
		this.dirtyRecords.addAll( items );
	}
	
	public void addDirty(Record item ) {
		this.dirtyRecords.add( item );
	}
	
	public void addNew(List<Record> items ) {
		this.newRecords.addAll( items );
	}

	public void addNew(Record item ) {
		this.newRecords.add( item );
	}

	/**
	 * @return the updateId
	 */
	public String getOwnerId() {
		return this.ownerId;
	}

	
	
}
