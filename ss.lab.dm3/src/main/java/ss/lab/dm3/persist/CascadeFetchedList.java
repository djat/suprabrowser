package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * @author dmitry
 * 
 */
public class CascadeFetchedList {

	private final String propertyName;

	private final List<QualifiedObjectId<DomainObject>> objectsIds = new ArrayList<QualifiedObjectId<DomainObject>>();

	private final TypedQuery<?> fetchQuery;

	public CascadeFetchedList(String propertyName, TypedQuery<?> fetchQuery) {
		super();
		this.propertyName = propertyName;
		this.fetchQuery = fetchQuery;
	}

	/**
	 * @return
	 */
	public TypedQuery<?> getFetchQuery() {
		return this.fetchQuery;
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}

	public List<QualifiedObjectId<DomainObject>> getObjectsIds() {
		return this.objectsIds;
	}

	public List<DomainObject> restoreObjects(DomainObject domainObject) {
		List<DomainObject> restored = new ArrayList<DomainObject>();
		Domain domain = DomainResolverHelper.getCurrentDomain();
		for (QualifiedObjectId<DomainObject> objectId : this.objectsIds) {
			final DomainObject obj = domain.resolve(objectId);
			if (obj != null) {
				restored.add(obj);
			}
		}
		return restored;
	}

	/**
	 * @param qualifiedId
	 */
	public void add(QualifiedObjectId<DomainObject> qualifiedId) {
		this.objectsIds.add(qualifiedId);
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "propertyName", this.propertyName );
		return tsb.toString();
	}

}
