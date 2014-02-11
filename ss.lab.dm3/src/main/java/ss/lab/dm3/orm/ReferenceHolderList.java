/**
 * 
 */
package ss.lab.dm3.orm;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Dmitry Goncharov
 */
public class ReferenceHolderList implements Iterable<ReferenceHolder> {

	private HashMap<String, ReferenceHolder> nameToHolder = new HashMap<String, ReferenceHolder>();
	/**
	 * @param name
	 * @return
	 */
	public ReferenceHolder get(String name) {
		return this.nameToHolder.get(name);
	}

	/**
	 * @param holder
	 */
	public void add(ReferenceHolder holder) {
		this.nameToHolder.put(holder.getPropertyName(), holder);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ReferenceHolder> iterator() {
		return this.nameToHolder.values().iterator();
	}

}
