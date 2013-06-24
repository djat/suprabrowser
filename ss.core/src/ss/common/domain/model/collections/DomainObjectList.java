/**
 * 
 */
package ss.common.domain.model.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ss.common.domain.model.DomainObject;

/**
 * @author roman
 *
 */
public abstract class DomainObjectList<E extends DomainObject> implements Iterable<E> {

	private final List<E> objects = new ArrayList<E>();
	
	public final Iterator<E> iterator() {
		return this.objects.iterator();
	}
	
	public final void add(final E item) {
		this.objects.add(item);
	}
	
	public final void add(final int index, final E item) {
		this.objects.add(index,item);
	}
	
	public final void remove(final E item) {
		this.objects.remove(item);
	}
	
	public final void remove(final int index) {
		this.objects.remove(index);
	}
	
	public final E get(final int index) {
		return this.get(index);
	}
	
	public final int getCount() {
		return this.objects.size();
	}
	
	public boolean contains(E item) {
		return this.objects.contains(item);
	}
}
