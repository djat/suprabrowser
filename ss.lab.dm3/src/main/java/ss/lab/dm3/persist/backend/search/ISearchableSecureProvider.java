package ss.lab.dm3.persist.backend.search;

public interface ISearchableSecureProvider {

	void collectSecureLocks( SecureLockCollector collector );
	
	boolean isPublicForSearch();
	
}
