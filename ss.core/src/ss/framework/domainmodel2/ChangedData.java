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
public final class ChangedData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -580051059575026347L;

	private final String ownerId;
	
	private final List<QualifiedObjectId> removed = new ArrayList<QualifiedObjectId>();
	
	private final List<Record> modified = new ArrayList<Record>();
	
	private final List<Class> created = new ArrayList<Class>();
	
	
	/**
	 * @param updateId
	 */
	public ChangedData(final String updateId) {
		super();
		this.ownerId = updateId;
	}

	/**
	 * @return the created
	 */
	public Iterable<Class> getCreated() {
		return this.created;
	}

	/**
	 * @return the modified
	 */
	public Iterable<Record> getModified() {
		return this.modified;
	}

	/**
	 * @return the removed
	 */
	public Iterable<QualifiedObjectId> getRemoved() {
		return this.removed;
	}

	/**
	 * @param newRecords
	 */
	public void addCreated(Iterable<Record> records ) {
		for( Record record : records ) {
			if ( !this.created.contains( record.getDomainObjectClass() ) ) {
				this.created.add(record.getDomainObjectClass() );
			}
		}
	}

	/**
	 * @param dirtyRecords
	 */
	public void addModified(Iterable<Record> records ) {
		for( Record record : records ) {
			this.modified.add( record );
		}
	}

	/**
	 * @param removedRecords
	 */
	public void addRemoved(Iterable<Record> records) {
		for( Record record : records ) {
			this.removed.add( new QualifiedObjectId( record.getDomainObjectClass(), record.getId() ) );
		}
	}

	/**
	 * @return the updateId
	 */
	public String getOwnerId() {
		return this.ownerId;
	}
	
	
}
