/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.domainmodel.MemberReference;

/**
 *
 */
public class MemberAccess implements Comparable {
		
	private final MemberReference memberStatement;
	
	private boolean initialized = false;
	
	private boolean initialAccess;
	
	private boolean access;

	private final String contactName;
	
	private final IMemberDefinitionProvider memberDefinitionProvider;
	
	private final ManagedSphere sphere; 

	/**
	 * @param memberStatement
	 * @param access
	 */
	public MemberAccess(MemberReference memberStatement, IMemberDefinitionProvider memberDefinitionProvider, ManagedSphere sphere) {
		this.memberStatement = memberStatement;
		this.contactName = this.memberStatement.getContactName();
		this.memberDefinitionProvider = memberDefinitionProvider;
		this.sphere = sphere;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.contactName;
	}

	/**
	 * @return the access
	 */
	public boolean isAccess() {
		ensureAccessInitialized();
		return this.access;
	}

	/**
	 * 
	 */
	private void ensureAccessInitialized() {
		if ( !isInitialized() ) {
			this.initialized = true;
			boolean evaluatedAccess = this.memberDefinitionProvider.isMemberPresent(this.sphere.getId(),this.memberStatement.getLoginName());
			this.initialAccess = evaluatedAccess;
			this.access = evaluatedAccess;
		}				
	}


	/**
	 * @param access the access to set
	 */
	public void setAccess(boolean access) {
		ensureAccessInitialized();
		this.access = access;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if ( o == null ) {
			return -1;
		}
		return toString().compareTo( o.toString() );
	}
	
	public boolean isPresentChanged() {
		return isInitialized() && this.access != this.initialAccess;
	}

	/**
	 * @return
	 */
	private boolean isInitialized() {
		return this.initialized;
	}


	/**
	 * @return the memberStatement
	 */
	public MemberReference getMemberReference() {
		return this.memberStatement;
	}

	public String getContactName() {
		return this.contactName;
	}

	/**
	 * 
	 */
	public void resetToDefault() {
		if ( isInitialized() ) {
			this.access = this.initialAccess;
		}
	}

	/**
	 * 
	 */
	public void fixChanges() {
		if ( isInitialized() ) {
			this.initialAccess = this.access;
		}
	}
	
	
}
