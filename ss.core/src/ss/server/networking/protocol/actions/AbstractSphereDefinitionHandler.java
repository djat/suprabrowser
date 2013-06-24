package ss.server.networking.protocol.actions;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.AbstractAction;
import ss.domainmodel.SphereStatement;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;

public abstract class AbstractSphereDefinitionHandler<A extends AbstractAction> extends AbstractActionHandler<A>{

	@SuppressWarnings("unused")
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public AbstractSphereDefinitionHandler(Class<A> acceptableCommandClass, DialogsMainPeer peer) {
		super(acceptableCommandClass, peer);
	}

	protected final SphereStatement getSphere( String locationSphereId, String sphereId ) {
		final XMLDB xmldb = this.peer.getXmldb();
		final Document sphereDoc = xmldb.getSphereDefinition( locationSphereId, sphereId);
		if ( sphereDoc != null ) {
			return SphereStatement.wrap( sphereDoc );
		}
		else {
			return null;
		}
	}
	
	protected final void updateSphereDefinition(String sphereId, ISphereDefinitionHandler handler ) {
		final SphereStatement sphere = getSphere(sphereId, sphereId);
		if ( sphere == null ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Can't find sphere defintion in " + sphereId + " sphereId " + sphereId);
			}
			return;
		}
		if (!handler.update( sphere )) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("No update performed for current sphere: " + sphere.getDisplayName());
			}
			return;
		}
		final XMLDB xmldb = this.peer.getXmldb();
		xmldb.replaceDoc( sphere.getBindedDocument(), sphereId );
		// Update definition in the parent
		final String sphereCoreId = sphere.getSphereCoreId();
		if ( sphereCoreId != null ) {
			final SphereStatement sphereInCore = getSphere( sphereCoreId, sphereId);
			if ( sphereInCore != null ) {
				handler.update( sphereInCore );
				xmldb.replaceDoc( sphereInCore.getBindedDocument(), sphereCoreId );
			}
			else {
				this.log.warn( "Can't find sphere defintion in " + sphereCoreId + " sphereId " + sphereId );
			}
		}
	}

	public static interface ISphereDefinitionHandler {
		boolean update(SphereStatement sphere);		
	}

}
