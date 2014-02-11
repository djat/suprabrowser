package ss.lab.dm3.persist;

public interface IChangeSetHandler {

	void addClean(DomainObject externalObject);

	<T extends DomainObject> T resolveOrNull(Class<T> objectClazz, Long id);
	
	DomainObject addCleanOrUpdate(DomainObject externalObject);

	void removeAndDetach(DomainObject object);

}
