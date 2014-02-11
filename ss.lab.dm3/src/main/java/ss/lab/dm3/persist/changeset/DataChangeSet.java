/**
 * 
 */
package ss.lab.dm3.persist.changeset;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.entity.EntityList;

/**
 * @author Dmitry Goncharov
 */
public class DataChangeSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 959317865523121207L;

	private final ChangeSetId id; 
	
	private final EntityList created;
	
	private final EntityList updated;

	private final EntityList deleted;

	
	/**
	 * 
	 */
	public DataChangeSet() {
		this( null, new EntityList(), new EntityList(), new EntityList() );
	}

	/**
	 * @param created
	 * @param updated
	 * @param deleted
	 */
	public DataChangeSet(ChangeSetId id, EntityList created,
			EntityList updated, EntityList deleted) {
		super();
		this.id = id;
		this.created = created;
		this.updated = updated;
		this.deleted = deleted;
	}

	/**
	 * @return the created
	 */
	public EntityList getCreated() {
		return this.created;
	}

	/**
	 * @return the updated
	 */
	public EntityList getUpdated() {
		return this.updated;
	}

	/**
	 * @return
	 */
	public EntityList getDeleted() {
		return this.deleted;
	}

	/**
	 * @return the id
	 */
	public ChangeSetId getId() {
		return this.id;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "id", this.id );
		tsb.append( "created", this.created.size() );
		tsb.append( "updated", this.updated.size() );
		tsb.append( "deleted", this.deleted.size() );
		return tsb.toString();
	}
	
	
	
}
