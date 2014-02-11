package ss.lab.dm3.persist;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;

public interface IDomainObject extends MappedObject {

	QualifiedObjectId<? extends IDomainObject> getQualifiedId();
	
}
