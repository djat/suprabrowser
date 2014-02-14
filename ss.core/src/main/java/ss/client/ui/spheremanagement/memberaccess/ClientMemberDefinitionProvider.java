/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.Hashtable;
import java.util.List;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.spheremanagement.AbstractOutOfDateable;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SupraSphereMember;

/**
 *
 */
public class ClientMemberDefinitionProvider extends AbstractOutOfDateable implements IMemberDefinitionProvider {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientMemberDefinitionProvider.class);
	
	private final DialogsMainCli clientProtocol;
	
	private Hashtable<MemberReference,Boolean> membersOnlineState = null;
	
	/**
	 * @param clientProtocol
	 */
	public ClientMemberDefinitionProvider(final DialogsMainCli clientProtocol) {
		super();
		this.clientProtocol = clientProtocol;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#getMembers()
	 */
	public List<SupraSphereMember> getMembers() {
		return this.clientProtocol.getVerifyAuth().getAllMembers();
	}

	public Hashtable<MemberReference,Boolean> getMembersOnlineState() {
		return this.membersOnlineState;
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#isMemberPresent(java.lang.String, java.lang.String)
	 */
	public boolean isMemberPresent(String sphereId, String memberLogin) {
		final SupraSphereMember member = this.clientProtocol.getVerifyAuth().getSupraSphere().findMemberByLogin(memberLogin);
		boolean ret = member.getSpheres().isEnabled(sphereId);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Sphere "+ sphereId + " -> Member " + memberLogin + " enabled: " +  ret );
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.ISphereDefinitionaProvider#update(java.util.List, java.util.List)
	 */
	public void update(List<SphereMemberBundle> added, List<SphereMemberBundle> removed) {
		this.clientProtocol.updateMemberVisibility(added, removed);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.AbstractOutOfDateable#reload()
	 */
	@Override
	protected void reload() {
		this.membersOnlineState = this.clientProtocol.getAllMembersStates();
	}
	
	



}
