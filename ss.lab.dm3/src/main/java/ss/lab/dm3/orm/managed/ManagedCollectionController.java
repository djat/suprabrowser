package ss.lab.dm3.orm.managed;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmException;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * @author Dmitry Goncharov
 */
public class ManagedCollectionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1261200803304915971L;

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	protected final MappedObject owner;
	
	private String mappedByName;
	
	private Class<? extends MappedObject> itemType;
	
	/**
	 * @param owner
	 */
	public ManagedCollectionController(MappedObject owner) {
		super();
		this.owner = owner;
	}

	/**
	 * @return the owner
	 */
	public MappedObject getOwner() {
		return this.owner;
	}

	public Class<? extends MappedObject> getItemType() {
		return this.itemType;
	}

	public void setItemType(Class<? extends MappedObject> itemType) {
		this.itemType = itemType;
	}

	/**
	 * @param item
	 */
	public void addByOrm(MappedObject item) {
	}

	/**
	 * @param item
	 */
	public void removeByOrm(MappedObject item) {
	}
	
	public void setUpByOrm(List<? extends MappedObject> items) {		
	}

	/**
	 * @return the mappedByName
	 */
	public final String getMappedByName() {
		return this.mappedByName;
	}

	/**
	 * @param mappedByName the mappedByName to set
	 */
	public void setMappedByName(String mappedByName) {
		this.mappedByName = mappedByName;
	}

	public final void bindItemToOwner( MappedObject item  ) {
		if ( item == null ) {
			throw new NullPointerException( "item" ); 
		}
		OrmManager orm = getOrmManager();
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Bind " + item + " to " + this.owner + " by " + getMappedByName() );
		}
		QualifiedObjectId<?> qualifiedId = orm.getQualifiedObjectId( this.owner );
		orm.changeReference( item, getMappedByName(), qualifiedId);		
	}

	/**
	 * @return
	 */
	protected OrmManager getOrmManager() {
		return OrmManagerResolveHelper.resolve( this.owner );
	}
	
	public final void unbindItemFromOwner( MappedObject item ) {
		if ( item == null ) {
			this.log.warn( "Can't unbind, Item is null for " + this ); 
		}
		else {
			OrmManager orm = getOrmManager();
			orm.changeReference( item, getMappedByName(), null );
		}
	}

	/**
	 * 
	 */
	public final void checkInitialized() {
		if ( this.mappedByName == null ) {
			throw new OrmException( "Object " + this + " is not initialized" );
		}
		if ( this.itemType == null ) {
			throw new OrmException( "Object " + this + " is not initialized" );
		}
	}

	/**
	 * 
	 */
	public void resetFetchedItems() {
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "owner", this.owner );
		tsb.append( "mappedByName", this.mappedByName );
		tsb.append( "itemType", this.itemType != null ? this.itemType.getSimpleName() : null );
		return tsb.toString();
	}
	
}
