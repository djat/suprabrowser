package ss.domainmodel.spherehierarchy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ss.domainmodel.SphereStatement;

public class SphereCollection implements Iterable<SphereStatement> {

	private final Map<String, SphereStatement> sphereNameToSphere = new Hashtable<String, SphereStatement>();
	
	private final List<SphereStatement> spheres = new ArrayList<SphereStatement>();
	
	/**
	 * @param spheres
	 */
	public void addAll(Iterable<SphereStatement> spheres) {
		for( SphereStatement sphere : spheres ) {
			add( sphere );
		}
	}
	
	/**
	 * @param spheres
	 */
	public boolean add( SphereStatement sphere) {
		final String sphereSystemName = sphere.getSystemName();
		if ( sphereSystemName != null
				&& !this.sphereNameToSphere.containsKey(sphereSystemName)) {
			this.sphereNameToSphere.put(sphereSystemName, sphere);
			this.spheres.add(sphere);
			return true;
		}		
		return false;
	}
	/**
	 * @return
	 */
	public List<SphereStatement> asList() {
		return this.spheres;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<SphereStatement> iterator() {
		return this.spheres.iterator();
	}
	/**
	 * @return
	 */
	public int getCount() {
		return this.spheres.size();
	}
	/**
	 * @param itemSphereCoreId
	 * @return
	 */
	public SphereStatement get(String systemName) {
		return this.sphereNameToSphere.get( systemName );
	}
	
	
}
