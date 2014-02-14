package ss.client.ui.root;

import java.util.Comparator;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.domainmodel.SphereStatement;

/**
 *
 */
final class RootSphereComparator implements Comparator<ManagedSphere> {
	
	public int compare(ManagedSphere o1, ManagedSphere o2) {
		SphereStatement st1 = o1.getStatement();
		SphereStatement st2 = o2.getStatement();
		if (st1.isEmailBox()){
			if (st2.isEmailBox()){
				return (st1.getDisplayName()).compareTo(st2.getDisplayName());
			} else {
				return 1;
			}
		} else if (st2.isEmailBox()){
			return -1;
		} else return (st1.getDisplayName()).compareTo(st2.getDisplayName());
	}
	
}