/**
 * 
 */
package ss.server.networking.protocol;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.common.domainmodel2.SsDomain;
import ss.common.protocolobjects.MemberVisibilityProtocolObject;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 * 
 */
@Refactoring(classify=SupraSphereRefactor.class, message="Move implementation of it to the ss.refactor.ssdoc")
public class UpdateMemberVisibilityServerHandler implements ProtocolHandler {

	private static final Object REMOVE_MEMBER_FROM_GROUP_SPHERE_MUTEX = new Object();
	
	private DialogsMainPeer peer;
	

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateMemberVisibilityServerHandler.class);
	
	public UpdateMemberVisibilityServerHandler(DialogsMainPeer peer) {
		this.peer = peer;
	} 

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_MEMBER_VISIBILITY;
	}

	public void handle(Hashtable update) {
		logger.debug( "Handling member visibility update" );
		MemberVisibilityProtocolObject protocolObject = new MemberVisibilityProtocolObject( update );
		synchronized( REMOVE_MEMBER_FROM_GROUP_SPHERE_MUTEX ) {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "pass mutex" );
			}
			addMembersToSpheres(protocolObject.getAdded());
			removeMembersFromSpheres(protocolObject.getRemoved());
			notifyClientsAboutChanges( protocolObject );
		}
	}

	/**
	 * @param removed
	 */
	private void removeMembersFromSpheres(List<SphereMember> sphereMembers) {
		for( SphereMember sphereMember : sphereMembers ) {
			try {
				this.peer.getXmldb().getEditableSupraSphere().removeMemberForGroupSphereLight( sphereMember.getParentSphereSystemName(),
															sphereMember.getSphereSystemName(),
															sphereMember.getSphereDisplayName(),
															sphereMember.getMemberLogin(),
															sphereMember.getMemberContactName() );
				removeMemberFromRolesModel(sphereMember);
			} catch (Exception ex) {
				logger.error( "Cannot add contact to the sphere " + sphereMember, ex );
			}
		}
	}

	/**
	 * @param sphereMember
	 */
	private void removeMemberFromRolesModel(SphereMember sphereMember) {
		final String sphereId = sphereMember.getSphereSystemName();
		SphereOwnPreferences spherePreferences = SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId);
		WorkflowConfiguration workflowConfiguration = spherePreferences.getWorkflowConfiguration();
		workflowConfiguration.sphereMemberRemoved( sphereMember.getMemberLogin() );
		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId, spherePreferences );
	}

	/**
	 * @param sphereMembers
	 */
	private void addMembersToSpheres(List<SphereMember> sphereMembers) {
		for( SphereMember sphereMember : sphereMembers ) {
			try {
				final ISupraSphereEditFacade editableSupraSphere = this.peer.getXmldb().getEditableSupraSphere();
				editableSupraSphere.addMemberToGroupSphereLight(sphereMember.getParentSphereSystemName(),
															sphereMember.getSphereSystemName(),
															sphereMember.getSphereDisplayName(),
															sphereMember.getMemberLogin(),
															sphereMember.getMemberContactName() );
				if (logger.isDebugEnabled()) {
					logger.debug("add to sphere "+sphereMember.getSphereDisplayName()+" member : "+sphereMember.getMemberLogin());
				}
				addMemberToRolesModel(sphereMember);				
			} catch (Exception ex) {
				logger.error( "Cannot add contact to the sphere " + sphereMember, ex );
			}
		}
	}

	
	/**
	 * @param sphereMember
	 */
	private void addMemberToRolesModel(SphereMember sphereMember) {
		final String sphereId = sphereMember.getSphereSystemName();
		SphereOwnPreferences spherePreferences = SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId);
		WorkflowConfiguration workflowConfiguration = spherePreferences.getWorkflowConfiguration();
		workflowConfiguration.sphereMemberAdded( sphereMember );
		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId, spherePreferences );
	}

	/**
	 * @param protocolObject 
	 * 
	 */
	private void notifyClientsAboutChanges(MemberVisibilityProtocolObject protocolObject) {
		try {
			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers() ) {
				// logger.info("SENDING AUTH");
				final VerifyAuth verifyAuth = handler.getVerifyAuth();
				SupraSphereProvider.INSTANCE.configureVerifyAuth(verifyAuth);
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue("protocol", "updateVerify");
				dmpResponse.setVerifyAuthValue("verifyAuth", verifyAuth);
				handler.sendFromQueue(dmpResponse);
				refreshPresence(handler, protocolObject);
				final List<SphereStatement> addedSpheres = getSpheres( handler, protocolObject.getAdded() );
				final List<SphereStatement> removedSpheres = getSpheres( handler, protocolObject.getRemoved() );
				if ( logger.isDebugEnabled() ) {
					logger.debug( "For " + handler.getName() + " have "  + addedSpheres.size() + " added, and " 
							+ removedSpheres.size() + " removed." );
				}
				sendSpheresForMember(handler, addedSpheres );
				recallSpheresFromMember(handler, removedSpheres );
			}
		} catch (NullPointerException ex) {
			logger.error( "Cannot notify about changes in verify auth ", ex );
		}
	}

	/**
	 * @param protocolObject
	 * @param handler
	 */
	private void refreshPresence(DialogsMainPeer handler,MemberVisibilityProtocolObject protocolObject) {
		for( SphereMember sphereMember : protocolObject.getAdded() ) {
			refreshPresence( handler, sphereMember );
		}
		for( SphereMember sphereMember : protocolObject.getRemoved() ) {
			refreshPresence( handler, sphereMember );
		}
	}

	/**
	 * @param handler
	 * @param removed
	 */
	private void recallSpheresFromMember(DialogsMainPeer handler, List<SphereStatement> spheres) {
		for (SphereStatement sphere : spheres) {
			logger.info("Notify about removed sphere for " + handler.getUserLogin() + " sphere "
					+ sphere.getDisplayName());
			final String targetSphere = getTargetSphereId(sphere);
			sphere.setCurrentSphere(targetSphere);
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.RECALL_MESSAGE);
			dmpResponse.setDocumentValue(SC.DOCUMENT, sphere.getBindedDocument() );
			dmpResponse.setStringValue(SC.SPHERE, targetSphere );			
			handler.sendFromQueue(dmpResponse);

		}
	}
	

	/**
	 * 
	 * @param handler
	 * @param shpereMembers
	 */
	private void sendSpheresForMember(DialogsMainPeer handler, List<SphereStatement> spheres ) {
		for( SphereStatement sphere : spheres ) {
			logger.info( "Notify about added sphere  " + handler.getUserLogin() +  " sphere " + sphere.getDisplayName() );
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
			dmpResponse.setDocumentValue(SC.DOCUMENT, sphere.getBindedDocument() );
			String coreSphere = getTargetSphereId(sphere);
			dmpResponse.setStringValue(SC.SPHERE, coreSphere );
			//dmpResponse.setStringValue(SessionConstants.IS_UPDATE, SSProtocolConstants.FORCE_ONLY_IF_EXISTS );
			handler.sendFromQueue( dmpResponse );
		}
	}

	/**
	 * @param sphere
	 * @return
	 */
	private String getTargetSphereId(SphereStatement sphere) {
		String coreSphere = sphere.getSphereCoreId();
		if ( coreSphere == null ) {
			coreSphere = "";
		}
		return coreSphere;
	}
	

	/**
	 * @param handler
	 * @param removed
	 */
	private List<SphereStatement> getSpheres( DialogsMainPeer handler, List<SphereMember> shpereMembers) {
		List<SphereStatement> spheres = new ArrayList<SphereStatement>();
		if(handler==null) {
			return spheres;
		}
		for( SphereMember sphereMember : shpereMembers ) {
			final String login = handler.getUserLogin();
			if ( sphereMember.getMemberLogin().equals(login ) ) {
				Document definition = this.peer.getXmldb().getSphereDefinition( sphereMember.getSphereSystemName(), sphereMember.getSphereSystemName() );
				if ( definition != null ) {
					spheres.add( SphereStatement.wrap(definition) );					
				}
			}
		}
		return spheres;
	}
	
	private void refreshPresence( DialogsMainPeer handler, SphereMember sphereMember ) {
		final String sphereId = sphereMember.getSphereSystemName();
		final String contactName = sphereMember.getMemberContactName();
		DialogsMainPeer.sendForAllRefreshPresence(contactName, sphereId);		
	}
	
}