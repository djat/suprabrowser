/**
 * 
 */
package ss.server.networking.protocol.actions;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.networking.protocol.actions.UpdateSphereDefinitionAction;
import ss.common.SphereReferenceList;
import ss.common.StringUtils;
import ss.domainmodel.ObjectRelation;
import ss.domainmodel.ObjectRelationCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereMemberCollection;
import ss.domainmodel.SupraSphereStatement;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class UpdateSphereDefinitionHandler extends AbstractSphereDefinitionHandler<UpdateSphereDefinitionAction> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateSphereDefinitionHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public UpdateSphereDefinitionHandler( DialogsMainPeer peer ) {
		super(UpdateSphereDefinitionAction.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(UpdateSphereDefinitionAction action) {
		final SphereStatement st = action.getDefinition();
		if ( st == null ) {
			logger.error("SphereDefinition is null");
			return;
		}
		updateSphereDefinition( st.getSystemName(), new ISphereDefinitionHandler() {
			public boolean update(SphereStatement sphere) {
				copy( st, sphere );
				return true;
			}
		} );	
		if ( this.updateDisplayName ) {
			processRelations( st );
			AddChangesToSupraSphereDoc( st );
			updateVerifyAuth();
		}
	}

	/**
	 * @param st
	 */
	private void processRelations( final SphereStatement st ) {
		SphereReferenceList spheres = this.peer.getVerifyAuth().getSupraSphere().getAllSpheres();
		if ( spheres == null ) {
			logger.error("spheres list is null");
			return;
		}
		final String sphereSourceId = st.getSystemName();
		final String newDisplayName = st.getDisplayName();
		for ( SphereReference sp : spheres ) {
			updateSphereDefinition( sp.getSystemName(), new ISphereDefinitionHandler() {
				public boolean update(SphereStatement sphere) {
					final ObjectRelationCollection relations = sphere.getRelations();
					if ( relations == null ) {
						if (logger.isDebugEnabled()) {
							logger.debug("No relations for sphere: " + sphere.getDisplayName());
						}
						return false;
					}
					boolean isUpdated = false;
					for ( ObjectRelation relation : relations ) {
						if ( sphereSourceId.equals( relation.getSphereId() ) ) {
							if (logger.isDebugEnabled()) {
								logger.debug("changed name in relations for sphere " + sphere.getDisplayName());
							}
							relation.setDisplayName(newDisplayName);
							isUpdated = true;
						}
					}
					return isUpdated;
				}
			} );
		}
	}

	private boolean updateDisplayName = false;
	
	private void copy( final SphereStatement source, final SphereStatement target ){
		target.setRole( StringUtils.getNotNullString(source.getRole()) );
		target.getPhisicalLocation().copyAll(source.getPhisicalLocation());
		if (displayNameUpdate(source, target)) {
			this.updateDisplayName = true;
		}
	}
	
	private boolean displayNameUpdate( final SphereStatement source, final SphereStatement target ){
		final String sourceDisplayName = source.getDisplayName();
		final String targetDisplayName = target.getDisplayName();
		if ( StringUtils.isNotBlank(sourceDisplayName) && StringUtils.isNotBlank(targetDisplayName)
				&& !targetDisplayName.equals(sourceDisplayName) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Sphere renamed: " + targetDisplayName + " to: " + sourceDisplayName);
			}
			target.setDisplayName( sourceDisplayName );
			target.setSubject( sourceDisplayName );
			return true;
		} else {
			return false;
		}
	}
	
	private void AddChangesToSupraSphereDoc( final SphereStatement source ){
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
		} catch(DocumentException ex) {
			logger.error("Can not get suprasphere document", ex);
			return;
		}
		if ( supraDoc == null ) {
			logger.error("SupraDoc is null");
			return;
		}
		final SupraSphereStatement supraSt = SupraSphereStatement.wrap(supraDoc);
		
		final SupraSphereMemberCollection supraMembers = supraSt.getSupraMembers();
		
		if ( supraMembers == null ) {
			logger.error("supraMembers is null");
			return;
		}
		final String sphereId = source.getSystemName();
		final String newDisplayName = source.getDisplayName();
		for ( SupraSphereMember member : supraMembers ) {
			SphereItem sphere = member.getSphereBySystemName(sphereId);
			if ( sphere != null ) {
				sphere.setDisplayName(newDisplayName);
			}
			if (sphereId.equals(member.getSphereCoreSystemName())) {
				member.setSphereCoreDisplayName(newDisplayName);
			}
			if (sphereId.equals(member.getLoginSphereSystemName())) {
				member.setLoginDisplayName(newDisplayName);
			}
		}
		
		this.peer.getXmldb().updateSupraSphereDoc(supraSt.getBindedDocument());
	}
	
	private void updateVerifyAuth() {
		Document supraDoc = null;
		try {
			supraDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			
		} catch(DocumentException ex) {
			logger.error("can't update verify:(", ex);
		}		
		
		if(supraDoc==null) {
			logger.warn("supra doc from database is null");
			return;
		}
		
		this.peer.getVerifyAuth().setSphereDocument(supraDoc);
		DialogsMainPeer.updateVerifyAuthForAll(supraDoc);
	}
}
