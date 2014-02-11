package ss.lab.dm3.orm.query.index;

import ss.lab.dm3.orm.mapper.property.Property;

public interface IIndexProvider {

	/**
	 * @param property
	 * @return
	 */
	Index getIndex(Property<?> property);
	
}
