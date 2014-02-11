package ss.lab.dm3.persist;

import java.util.List;

import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * @author dmitry
 * 
 */
public class FetchedDomainObjectLists {

	private QualifiedObjectId<DomainObject> ownerId;

	private List<CascadeFetchedList> cascadeFetchedLists;

	public FetchedDomainObjectLists(QualifiedObjectId<DomainObject> ownerId,
			List<CascadeFetchedList> cascadeFetchedLists) {
		super();
		this.ownerId = ownerId;
		this.cascadeFetchedLists = cascadeFetchedLists;
	}

	public QualifiedObjectId<DomainObject> getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(QualifiedObjectId<DomainObject> ownerId) {
		this.ownerId = ownerId;
	}

	public List<CascadeFetchedList> getCascadeFetchedLists() {
		return this.cascadeFetchedLists;
	}

	public void setCascadeFetchedLists(
			List<CascadeFetchedList> cascadeFetchedLists) {
		this.cascadeFetchedLists = cascadeFetchedLists;
	}

	public DomainObject resolveDomainObjectById() {
		Domain domain = DomainResolverHelper.getCurrentDomain();
		final DomainObject resolve = domain.resolve(QueryHelper.eq(this.ownerId
				.getObjectClazz(), this.ownerId.getId()));
		return resolve;
	}
}
