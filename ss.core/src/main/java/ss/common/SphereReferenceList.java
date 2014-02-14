/**
 * 
 */
package ss.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ss.domainmodel.SphereReference;

/**
 * 
 */
public class SphereReferenceList implements Iterable<SphereReference>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6285979658309139092L;

	/**
	 * 
	 */
	

	private final List<SphereReference> items;

	private final Hashtable<String, SphereReference> systemNameToItem = new Hashtable<String, SphereReference>();

	public SphereReferenceList() {
		this(new ArrayList<SphereReference>());
	}

	/**
	 * @param items
	 */
	public SphereReferenceList(final List<SphereReference> items) {
		super();
		this.items = items;
		for (SphereReference item : this.items) {
			this.systemNameToItem.put(item.getSystemName(), item);
		}
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<SphereReference> iterator() {
		return this.items.iterator();
	}

	/**
	 * @return
	 */
	public SphereReference getFirst() {
		return this.items.get(0);
	}

	/**
	 * @param systemName
	 * @return
	 */
	public boolean containsSystemName(String systemName) {
		return this.systemNameToItem.containsKey(systemName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ListUtils.valuesToString(this.items);
	}

	/**
	 * @return
	 */
	public SphereReferenceList getWorkflowableSpheres() {
		SphereReferenceList result = new SphereReferenceList();
		for (SphereReference sphereReference : this) {
			if (!sphereReference.isMember() && !sphereReference.isEmailBox()) {
				result.add(sphereReference);
			}
		}
		return result;
	}

	/**
	 * @param sphereReference
	 */
	private void add(SphereReference sphereReference) {
		if (!containsSystemName( sphereReference.getSystemName() )) {
			this.items.add(sphereReference);
			this.systemNameToItem.put(sphereReference.getSystemName(),
				sphereReference);
		}
	}

	public void addAll( final SphereReferenceList other ) {
		for ( SphereReference item : other ) {
			add( item );
		}
	}
	
	/**
	 * @param displayName
	 * @return
	 */
	public SphereReference getSphereByDisplayName(String displayName) {
		return getSphereByDisplayName(displayName, null);
	}

	/**
	 * @param displayName
	 * @return
	 */
	public SphereReference getSphereByDisplayName(String displayName,
			SphereReference defaultValue) {
		if (displayName == null) {
			return defaultValue;
		}
		for (SphereReference sphereRef : this) {
			if (sphereRef.getDisplayName() != null
					&& sphereRef.getDisplayName().equals(displayName)) {
				return sphereRef;
			}
		}
		return defaultValue;
	}

	/**
	 * @return
	 */
	public List<String> toSpheresIds() {
		List<String> result = new ArrayList<String>();
		for (SphereReference reference : this) {
			result.add(reference.getSystemName());
		}
		return result;
	}

	/**
	 * @return
	 */
	public Vector<String> toStringVector() {
		Vector<String> spheres = new Vector<String>();
		for (SphereReference sphere : this) {
			spheres.add(sphere.getSystemName());
		}
		return spheres;
	}

	/**
	 * @return
	 */
	public int getSize() {
		return this.systemNameToItem.keySet().size();
	}

}
