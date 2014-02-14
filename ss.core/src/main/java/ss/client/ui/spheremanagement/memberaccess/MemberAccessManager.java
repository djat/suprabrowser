/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.Hashtable;
import java.util.List;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;
import ss.client.ui.spheremanagement.SphereManager;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SupraSphereMember;

/**
 * 
 */
public class MemberAccessManager extends SphereManager {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MemberAccessManager.class);
	
	private IMemberDefinitionProvider memberDefinitionProvider;
	
	private ChangedLoginSphereObject loginSphereObject = null;

	/**
	 * @param sphereDefinitionProvider
	 */
	public MemberAccessManager(ISphereDefinitionProvider sphereDefinitionProvider, IMemberDefinitionProvider memberDefinitionProvider) {
		super( sphereDefinitionProvider );
		this.memberDefinitionProvider = memberDefinitionProvider;
	}
	
	public SupraSphereMember getMemberByContactName( String contactName ) {
		if ( contactName == null ) {
			return null;
		}
		for( SupraSphereMember member : this.memberDefinitionProvider.getMembers() ) {
			if ( contactName.equals( member.getContactName() ) ) {
				return member;
			}
		}
		return null;
	}
	
	
	@Override
	protected void outOfDateing() {
		super.outOfDateing();
		this.memberDefinitionProvider.outOfDate();
	}

	@Override
	protected SphereHierarchyBuilder createSphereHierarchyBuilder() {
		return new SphereHierarchyBuilderWithMembers( this.sphereDefinitionProvider, this.memberDefinitionProvider );
	}
	
	public Hashtable<MemberReference, Boolean> getMembersOnlineState() {
		return this.memberDefinitionProvider.getMembersOnlineState();
	}

	/**
	 * 
	 */
	public void collectChangesAndUpdate() {
		AccessChangesCollector collector = new AccessChangesCollector();
		collector.collectChanges(getRootSphere());		
		List<SphereMemberBundle> added = collector.getAdded();
		List<SphereMemberBundle> removed = collector.getRemoved();
		collector.fixChanges(getRootSphere());
		
		this.memberDefinitionProvider.update(added,removed);
		
		saveLoginSphereChanges();
	}
	
	public void rollbackChanges() {
		AccessChangesCollector collector = new AccessChangesCollector();
		collector.rollbackChanges(getRootSphere());
		setNullChangedLoginSphere();
	}

	
	public void setChangedLoginSphereObject(MemberReference member, String sphereId) {
		this.loginSphereObject = new ChangedLoginSphereObject(member, sphereId);
	}
	
	public void setNullChangedLoginSphere() {
		this.loginSphereObject = null;
	}
	
	@SuppressWarnings("unchecked")
	public void saveLoginSphereChanges() {
		if (this.loginSphereObject == null ) {
			return;
		}
		DialogsMainCli clientProtocol = SupraSphereFrame.INSTANCE.client;
		Hashtable session = (Hashtable) clientProtocol.getSession().clone();
		session.put("sphere_id", this.loginSphereObject.getSphereId());
		clientProtocol.makeCurrentSphereCore(session, this.loginSphereObject.getMember().getLoginName());
		
		setNullChangedLoginSphere();
	}
	
	@Override
	public void clearListenersList() {
		super.clearListenersList();
	}
}
