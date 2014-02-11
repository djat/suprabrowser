package ss.lab.dm3.persist.backend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.persist.FetchedDomainObjectLists;
import ss.lab.dm3.persist.Query;

/**
 * @author dmitry
 *
 */
public class EntitiesSelectResult {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Query query; 
	
	private final EntityList selected;

	private final EntityList cascaded;
	
	private final List<FetchedDomainObjectLists> fetchedCollections;
	
	private Object generic = null;
	
	private int selectedTotalCount = -1;

	/**
	 * @param query
	 */
	public EntitiesSelectResult(Query query) {
		super();
		this.query = query;
		this.selected = new EntityList();
		this.cascaded = new EntityList();
		this.fetchedCollections = new ArrayList<FetchedDomainObjectLists>();
	}
	
	public Object getGeneric() {
		return this.generic;
	}

	public void setGeneric(Object generic) {
		this.generic = generic;
	}

	public EntityList getSelected() {
		return this.selected;
	}

	public EntityList getCascaded() {
		return this.cascaded;
	}

	public List<FetchedDomainObjectLists> getFetchedCollections() {
		return this.fetchedCollections;
	}
	
	public Query getQuery() {
		return this.query;
	}

	/**
	 * @param string
	 */
	public void debugDump(String message) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( message + " " + this );
		}
	}

	public EntityList getAllEntities() {
		EntityList entityList = new EntityList();
		for (Entity entity : this.selected) {
			if ( !entityList.contains( entity ) ) {
				entityList.add( entity );
			}
		}
		for (Entity entity : this.cascaded) {
			if ( !entityList.contains( entity ) ) {
				entityList.add( entity );
			}
		}
		return entityList;
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "query", this.query );
		tsb.append( "selected", this.selected );
		tsb.append( "cascaded", this.cascaded );
		return tsb.toString();
	}

	public int getSelectedTotalCount() {
		return selectedTotalCount;
	}

	public void setSelectedTotalCount(int selectedTotalCount) {
		this.selectedTotalCount = selectedTotalCount;
	}

	
}
