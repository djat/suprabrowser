package ss.lab.dm3.persist;

import ss.lab.dm3.persist.changeset.CrudSet;

public interface DomainChangeListener {
	
	void domainChanged( CrudSet changeSet );
	
}
