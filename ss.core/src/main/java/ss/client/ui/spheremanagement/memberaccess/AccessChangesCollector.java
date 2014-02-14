/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.ArrayList;

import ss.client.ui.spheremanagement.ManagedSphere;

/**
 *
 */
public class AccessChangesCollector {

	final ArrayList<SphereMemberBundle> removed = new ArrayList<SphereMemberBundle>(); 
	
	final ArrayList<SphereMemberBundle> added = new ArrayList<SphereMemberBundle>(); 
	
	public void collectChanges(ManagedSphere sphere) {
		for( MemberAccess memberPresence : sphere.getMembers() ) {
			if ( memberPresence.isPresentChanged() ) {
				SphereMemberBundle bundle = new SphereMemberBundle( sphere.getStatement(), memberPresence.getMemberReference() );
				if ( memberPresence.isAccess() ) {
					this.added.add( bundle );
				}
				else {
					this.removed.add( bundle );
				}
			}
		}
		for( ManagedSphere child : sphere.getChildren() ) {
			collectChanges(child);
		}
	}
	
	public void fixChanges(ManagedSphere sphere) {
		for( MemberAccess memberPresence : sphere.getMembers() ) {
			memberPresence.fixChanges();
		}
		for( ManagedSphere child : sphere.getChildren() ) {
			fixChanges(child);
		}
	}

	/**
	 * @return the added
	 */
	public ArrayList<SphereMemberBundle> getAdded() {
		return this.added;
	}

	/**
	 * @return the removed
	 */
	public ArrayList<SphereMemberBundle> getRemoved() {
		return this.removed;
	}
	

	/**
	 * @param rootSphere
	 */
	public void rollbackChanges(ManagedSphere sphere) {
		for( MemberAccess memberPresence : sphere.getMembers() ) {
			memberPresence.resetToDefault();
		}
		for( ManagedSphere child : sphere.getChildren() ) {
			rollbackChanges(child);
		}
	}

}
