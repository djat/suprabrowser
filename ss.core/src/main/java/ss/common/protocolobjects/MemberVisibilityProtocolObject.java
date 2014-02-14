/**
 * 
 */
package ss.common.protocolobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 */
public class MemberVisibilityProtocolObject extends AbstractProtocolObject {

	private final static String ADDED = "added";
	
	private final static String REMOVED = "removed";
	
	/**
	 * 
	 */
	public MemberVisibilityProtocolObject() {
		super();
	}

	/**
	 * @param valuesMap
	 */
	public MemberVisibilityProtocolObject(Hashtable valuesMap) {
		super(valuesMap);
	}

	/**
	 * @return the added
	 */
	public List<SphereMember> getAdded() {
		return lazyListGet( ADDED );
	}

	/**
	 * @return the removed
	 */
	public List<SphereMember> getRemoved() {
		return lazyListGet( REMOVED );
	}
	
	/**
	 * @return
	 */
	private List<SphereMember> lazyListGet( String key ) {
		ArrayList<SphereMember> items = (ArrayList<SphereMember>) super.getValue(key );
		if ( items == null ) {
			items = new ArrayList<SphereMember>();
			this.putNotNullValue(key, items );
		}
		return items;
	}

	public static class SphereMember implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6727588499269967299L;

		private final String parentSphereSystemName;
		
		private final String sphereSystemName;
		
		private final String sphereDisplayName;
		
		private final String memberLogin;
		
		private final String memberContactName;

		
		/**
		 * @param sphereSystemName
		 * @param sphereDisplayName
		 * @param memberLogin
		 * @param memberContactName
		 */
		public SphereMember(String parentSphereSystemName, String sphereSystemName, String sphereDisplayName, String memberLogin, String memberContactName) {
			super();
			this.parentSphereSystemName = parentSphereSystemName;
			this.sphereSystemName = sphereSystemName;
			this.sphereDisplayName = sphereDisplayName;
			this.memberLogin = memberLogin;
			this.memberContactName = memberContactName;
		}

		/**
		 * @return
		 */
		public String getParentSphereSystemName() {
			return this.parentSphereSystemName;
		}
		
		/**
		 * @return the memberLogin
		 */
		public String getMemberLogin() {
			return this.memberLogin;
		}

		/**
		 * @return the sphereSystemName
		 */
		public String getSphereSystemName() {
			return this.sphereSystemName;
		}

		
		/**
		 * @return the memberContactName
		 */
		public String getMemberContactName() {
			return this.memberContactName;
		}

		/**
		 * @return the sphereDisplayName
		 */
		public String getSphereDisplayName() {
			return this.sphereDisplayName;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.sphereSystemName + " -> " + this.memberLogin;
		}

		
		
		
	}
}
