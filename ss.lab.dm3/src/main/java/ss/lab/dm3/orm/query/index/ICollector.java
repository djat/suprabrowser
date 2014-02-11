package ss.lab.dm3.orm.query.index;

import ss.lab.dm3.orm.MappedObject;

public interface ICollector<T extends MappedObject> {

	void add(T object);
	
}
