package ss.lab.dm3.persist.backend.hibernate;

import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.persist.DomainObject;

public class QuerySelectResult implements Iterable<DomainObject> {

	private final List<DomainObject> items;

	private int itemsTotalCount = -1;
	
	public QuerySelectResult(List<DomainObject> items) {
		super();
		this.items = items;
	}

	public List<DomainObject> getItems() {
		return items;
	}

	public int getItemsTotalCount() {
		return Math.max( this.items.size(), itemsTotalCount );
	}

	public void setItemsTotalCount(int itemsTotalCount) {
		this.itemsTotalCount = itemsTotalCount;
	}

	public Iterator<DomainObject> iterator() {
		return items.iterator();
	}
	
}
