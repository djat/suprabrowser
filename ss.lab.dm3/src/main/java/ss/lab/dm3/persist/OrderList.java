package ss.lab.dm3.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author dmitry
 *
 */
public class OrderList implements Iterable<Order>, Serializable {

	private static final long serialVersionUID = 714262998674837045L;
	
	List<Order> orders = new ArrayList<Order>();
	
	public Iterator<Order> iterator() {
		return this.orders.iterator();
	}
	
	public void add( Order order ) {
		this.orders.add( order );
	}

	public void addAll(Collection<Order> list) {
		this.orders.addAll( list );
	}
	
	public void remove( Order order ) {
		if ( order == null ) {
			return;
		}
		this.orders.remove( order );
	}
	
	public void clear() {
		this.orders.clear();
	}
	
	public int size() {
		return this.orders.size();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.orders.isEmpty();
	}

}
