package ss.lab.dm3.persist.model;

import ss.lab.dm3.persist.changeset.CrudSet;

public interface ModelChangedListener {
	
	void modelChanged( CrudSet changeSet );
	
}
