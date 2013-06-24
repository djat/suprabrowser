/**
 * 
 */
package ss.client.ui.email;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class AttachedFileCollection implements Iterable<IAttachedFile>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7617537025320266840L;
	private final List<IAttachedFile> items = new ArrayList<IAttachedFile>();

	/**
	 * @param item
	 * @return
	 * @see java.util.List#addChildren(java.lang.AttachedFile)
	 */
	public boolean add(IAttachedFile item) {
		return this.items.add(item);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public IAttachedFile get(int index) {
		return this.items.get(index);
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<IAttachedFile> iterator() {
		return this.items.iterator();
	}

	/**
	 * @param item
	 * @return
	 * @see java.util.List#remove(java.lang.AttachedFile)
	 */
	public boolean remove(IAttachedFile item) {
		return this.items.remove(item);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int getCount() {
		return this.items.size();
	}

	/**
	 * @return
	 */
	public Object[] toObjectArray() {
		return this.items.toArray();
	}
	
	
}
