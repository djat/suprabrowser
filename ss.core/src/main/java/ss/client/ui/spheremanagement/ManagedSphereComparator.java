package ss.client.ui.spheremanagement;

import java.util.Comparator;


/**
 * Compares ManagedSphere by displayName
 * 
 */
public class ManagedSphereComparator implements Comparator<ManagedSphere> {
	public int compare(ManagedSphere x, ManagedSphere y) {
		if ( x == y ) {
			return 0;
		}
		String xDisplayName = x != null ? x.getDisplayName() : "";
		if ( xDisplayName == null ) {
			xDisplayName = ""; 
		}
		return xDisplayName.compareTo( y != null ? y.getDisplayName() : "" );
	}
}