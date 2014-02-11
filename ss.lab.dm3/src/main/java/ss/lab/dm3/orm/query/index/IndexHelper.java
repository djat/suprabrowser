package ss.lab.dm3.orm.query.index;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.Property;

public class IndexHelper {

	/**
	 * @param source
	 * @param property
	 */
	public static Index get(Iterable<? extends MappedObject> items, Property<?> property) {
		if ( items instanceof IIndexProvider ) {
			return ((IIndexProvider) items).getIndex(property);
		}
		else {
			return null;
		}
	}

}
