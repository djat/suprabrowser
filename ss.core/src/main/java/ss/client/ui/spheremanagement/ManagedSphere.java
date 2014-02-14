/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.spheremanagement.memberaccess.MemberAccess;
import ss.client.ui.spheremanagement.memberaccess.MemberVisibilityList;
import ss.domainmodel.SphereStatement;
/**
 *
 */
public class ManagedSphere implements Comparable<Object> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ManagedSphere.class);
	
	private final IManagedSphereOwner managedSphereOwner;
	
	private final SphereStatement sphere;
	
	private final ManagedSphereList children = new ManagedSphereList( this ); 
	
	private final MemberVisibilityList members = new MemberVisibilityList();   
	
	private ManagedSphere parent = null;
	
	/**
	 * @param displayName
	 */
	public ManagedSphere(IManagedSphereOwner managedSphereOwner, SphereStatement sphere ) {
		super();
		this.managedSphereOwner = managedSphereOwner;
		this.sphere = sphere;
	}


	/**
	 * @return
	 */
	public ManagedSphere getParent() {
		return this.parent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(!getStatement().isDeleted()) {
			return this.sphere.getDisplayName();
		} else {
			return this.sphere.getDisplayName()+" (deleted)";
		}
	}


	/**
	 * 
	 */
	public void remove() {
		if ( this.parent != null ) {
			this.parent.getChildren().remove( this );			
		}
	}

	/**
	 * @param object
	 */
	void setParent(ManagedSphere parent) {
		this.parent = parent;
	}

	/**
	 * @return
	 */
	public String getId() {
		return this.sphere.getSystemName();
	}

	/**
	 * @return
	 */
	public String getDesiredParentId() {
		return this.sphere.getSphereCoreId();
	}

	

	/**
	 * @return
	 */
	public SphereStatement getStatement() {
		return this.sphere;
	}

	/**
	 * @return the children
	 */
	public ManagedSphereList getChildren() {
		return this.children;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if ( o instanceof ManagedSphere ) {
			return this.managedSphereOwner.compare( this, ((ManagedSphere) o) );
		}
		else {
			return -1;
		}		
	}

	/**
	 * @return the members
	 */
	public MemberVisibilityList getMembers() {
		return this.members;
	}
	
	public void setMemberEnabled( String displayName, boolean enabled ) {
		final MemberAccess member = getMembers().require( displayName );
		member.setAccess(enabled);		
	}
	
	public boolean isMemberEnabled( String displayName ) {
		final MemberAccess member = getMembers().require( displayName );
		return member.isAccess();
	}


	/**
	 * @return
	 */
	public boolean isEditable() {
		return true;
	}


	/**
	 * @return
	 */
	public boolean isRoot() {
		if (getParent() == null)
			return true;
		return false;
	}


	/**
	 * @return
	 */
	public List<ManagedSphere> listDescendants() {
		List<ManagedSphere> fullList = new ArrayList<ManagedSphere>();
		collectSphere(this, fullList);
		return fullList;
	}


	private void collectSphere(ManagedSphere parentSphere, List<ManagedSphere> fullList) {
		fullList.add(parentSphere);
		for(ManagedSphere child : getChildren()) {
			collectSphere(child, fullList);
		}
	}


	/**
	 * @param contactName
	 */
	public List<ManagedSphere> listEnabledSpheres(String contactName) {
		List<ManagedSphere> enabledSpheres = new ArrayList<ManagedSphere>();
		collectEnabledSpheres( enabledSpheres, contactName );		
		return enabledSpheres;		
	}

	public ManagedSphere findSphere( String sphereId ) {
		if ( sphereId == null ) {
			return null;
		}
		if ( this.getId().equals( sphereId ) ) {
			return this;
		}
		for( ManagedSphere sphere : getChildren() ) {
			final ManagedSphere result = sphere.findSphere(sphereId);
			if ( result != null ) {
				return result;
			}
		}
		return null;
	}

	/**
	 * @param contactName
	 * @param enabledSpheres
	 */
	private void addSelfIfEnabled(String contactName, List<ManagedSphere> enabledSpheres) {
		final MemberAccess member = this.getMembers().find(contactName);
		if ( member != null ) {
			if ( member.isAccess() ) {
				enabledSpheres.add( this );
			}
		}
		else {
			logger.warn( "Can't find member access in " + this + " for " + contactName );
		}
	}


	/**
	 * @param enabledSpheres
	 * @param contactName
	 */
	private void collectEnabledSpheres(List<ManagedSphere> enabledSpheres, String contactName) {
		this.addSelfIfEnabled(contactName, enabledSpheres);
		for( ManagedSphere child : this.children ) {
			child.collectEnabledSpheres(enabledSpheres, contactName);
		}
		
	}
	
	public void traverse( IManagedSphereVisitor visitor ) {
		visitor.beginNode( this );
		for( ManagedSphere sphere : getChildren() ) { 
			sphere.traverse(visitor);
		}
		visitor.endNode( this );
	}
	
	public boolean isLeaf() {
		return this.children.size() == 0;
	}

	public ManagedSphere find( String sphereId ) {
		if ( sphereId == null ) {
			return null;
		}
		if ( this.getId().equals( sphereId ) ) {
			return this;
		}
		for( ManagedSphere sphere : this.children ) {
			ManagedSphere result = sphere.find(sphereId);
			if ( result != null ) {
				return result;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return this.sphere.getDisplayName();
	}


	/**
	 * @return
	 */
	public Object[] getPathAsArray() {
		return getPathAsList().toArray();
	}
	
	/**
	 * 
	 */
	public List<ManagedSphere> getPathAsList() {
		List<ManagedSphere> path = new ArrayList<ManagedSphere>(); 
		fillPath( path );
		return path;
	}
	
	/**
	 * @return
	 */
	public void fillPath( List<ManagedSphere> path ) {
		ManagedSphere parent = getParent();
		if ( parent != null ) {
			parent.fillPath(path);
		}
		path.add( this );
	}


	
	
}
