package ss.lab.dm3.orm.query.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.Property;

/**
 * @author dmitry
 *
 */
public class IndexProvider implements Iterable<MappedObject>, IIndexProvider {

	private Set<MappedObject> items = new HashSet<MappedObject>();
	
	private Map<Property<?>,Index> propertyNameToIndex = new HashMap<Property<?>, Index>();
	
	public void add(MappedObject obj ) {
		this.items.add(obj);
	}
	
	public void addAll(Iterable<? extends MappedObject> items ) {
		for( MappedObject item : items ) {
			add(item);
		}
	}
	
	public Index getIndex(Property<?> property) {
		Index index = this.propertyNameToIndex.get( property );
		if ( index == null ) {
			index = new Index(property);
			for( MappedObject item : this.items ) {
				index.add(item);
			}
			this.propertyNameToIndex.put( property, index );
		}
		return index;
	}


	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<MappedObject> iterator() {
		return this.items.iterator();
	}

	
}
