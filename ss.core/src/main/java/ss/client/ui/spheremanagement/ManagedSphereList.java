/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class ManagedSphereList implements Iterable<ManagedSphere>{

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ManagedSphereList.class);
	
	private final ManagedSphere sphereOwner; 
	
	private SortedSet<ManagedSphere> items = new TreeSet<ManagedSphere>();

	/**
	 * @param sphereOwner
	 */
	public ManagedSphereList(final ManagedSphere sphereOwner) {
		super();
		this.sphereOwner = sphereOwner;
	}

	/**
	 * 
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return
	 */
	public List<ManagedSphere> duplicate() {
		return new ArrayList<ManagedSphere>( this.items );
	}
	
	/**
	 * @param item
	 */
	public void remove(ManagedSphere item) {
		if ( item.getParent() != this.sphereOwner ) {
			logger.error( "Invalid item parent " + item );
		}
		this.items.remove(item);
		item.setParent( null );
	}
	

	/**
	 * @param item
	 */
	public void add(ManagedSphere item) {
		item.remove();
		item.setParent( this.sphereOwner );
		this.items.add(item);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return this.items.toArray();
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	public Iterator<ManagedSphere> iterator() {
		return this.items.iterator();
	}
	
	public void sort(Comparator<ManagedSphere> comp){
		SortedSet<ManagedSphere> oldItems = this.items;
		this.items = new TreeSet<ManagedSphere>(comp);
		this.items.addAll(oldItems);
	}
	
	public int size() {
		return this.items.size();
	}
}
