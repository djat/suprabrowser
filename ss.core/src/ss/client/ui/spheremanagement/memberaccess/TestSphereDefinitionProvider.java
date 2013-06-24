/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.client.ui.spheremanagement.AbstractOutOfDateable;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;

/**
 *
 */
public class TestSphereDefinitionProvider extends AbstractOutOfDateable implements ISphereDefinitionProvider, IMemberDefinitionProvider{

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TestSphereDefinitionProvider.class);
	
	List<SupraSphereMember> members = new ArrayList<SupraSphereMember>();
	
	List<SphereStatement> spheres = new ArrayList<SphereStatement>();
	
	Hashtable<SphereMemberKey, Boolean> memberPresence = new Hashtable<SphereMemberKey, Boolean>();  
	
	/**
	 * @param spheres
	 */
	public TestSphereDefinitionProvider() {
		super();
		//First should be intialized members
		addMember( "ivan", "Ivan Ivanov" );
		addMember( "petr", "Petr Vasilievych" );
		addMember( "john", "John Doe" );
		//
		addSphere( "DS", "DS", null, true, true, true );
		addSphere( "1", "1. First", "DS", true, false, true );
		addSphere( "2", "2. Second", "DS" );
		addSphere( "3", "Visible Under (1.1.)", "1" );
		addSphere( "4", "Invisible under (1.2.)", "1" );
		addSphere( "5", "Visible Under (1.3.)", "1" );
		addSphere( "6", "Visible again (1.2.1)", "4" );
		addSphere( "7", "Visible again (1.2.2)", "4" );
	}

	/**
	 * @param fullName 
	 * @param login 
	 * 
	 */
	private void addMember(String login, String fullName) {
		SupraSphereMember member = new SupraSphereMember();
		member.setLoginName(login);
		member.setContactName(fullName);
		//String [] nameParts = fullName.split(" ");
		//contact.setFirstName( nameParts[ 0 ] );
		//contact.setLastName( nameParts[ 1 ] );
		this.members.add(member);
	}

	/**
	 * @param string
	 * @param string2
	 * @param object
	 */
	private void addSphere(String id, String displayName, String parentId, boolean ... memberPresences ) {
		SphereStatement sphere = new SphereStatement();
		sphere.setSystemName(id);
		sphere.setDisplayName( displayName );
		sphere.setSphereCoreId(parentId);
		for (int n = 0; n < memberPresences.length; n++) {
			boolean memberPresence = memberPresences[ n ];
			this.memberPresence.put( new SphereMemberKey( sphere.getSystemName(), this.members.get(n).getLoginName() ), memberPresence);
		}
		this.spheres.add( sphere );
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#getRootId()
	 */
	public String getRootId() {
		return "DS";
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#isSphereVisible(ss.domainmodel.SphereStatement)
	 */
	public boolean isSphereVisible(SphereStatement sphere) {
		return !sphere.getDisplayName().contains( "Invisible" );
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#getMembers()
	 */
	public List<SupraSphereMember> getMembers() {
		return this.members;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#isMemberPresent(java.lang.String, java.lang.String)
	 */
	public boolean isMemberPresent(String sphereId, String userLogin) {
		Boolean presence = this.memberPresence.get( new SphereMemberKey( sphereId, userLogin ) );
		return presence != null ? presence.booleanValue() : false;
	}

	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#update(java.util.List, java.util.List)
	 */
	public void update(List<SphereMemberBundle> added, List<SphereMemberBundle> removed) {
		for( SphereMemberBundle budle :	added ) {
			logger.debug( "Added " + budle );
		}
		for( SphereMemberBundle budle :	removed ) {
			logger.debug( "Removed " + budle );
		}		

	}


	private static class  SphereMemberKey {
		
		private final String sphereSystemName;
		
		private final String userLogin;

		/**
		 * @param sphereSystemName
		 * @param userLogin
		 */
		public SphereMemberKey(String sphereSystemName, String userLogin) {
			super();
			this.sphereSystemName = sphereSystemName;
			this.userLogin = userLogin;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((this.sphereSystemName == null) ? 0 : this.sphereSystemName.hashCode());
			result = PRIME * result + ((this.userLogin == null) ? 0 : this.userLogin.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SphereMemberKey other = (SphereMemberKey) obj;
			if (this.sphereSystemName == null) {
				if (other.sphereSystemName != null)
					return false;
			} else if (!this.sphereSystemName.equals(other.sphereSystemName))
				return false;
			if (this.userLogin == null) {
				if (other.userLogin != null)
					return false;
			} else if (!this.userLogin.equals(other.userLogin))
				return false;
			return true;
		}		
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionProvider#getAllSpheres()
	 */
	public List<SphereStatement> getAllSpheres() {
		return this.spheres;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.AbstractOutOfDateable#reload()
	 */
	@Override
	protected void reload() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.memberaccess.IMemberDefinitionProvider#getMembersOnlineState()
	 */
	public Hashtable<MemberReference, Boolean> getMembersOnlineState() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
