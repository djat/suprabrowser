/**
 * 
 */
package ss.server.networking.protocol.actions;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.MoveSphereCommand;
import ss.common.StringUtils;
import ss.domainmodel.SphereStatement;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class MoveSphereHandler extends AbstractActionHandler<MoveSphereCommand> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MoveSphereHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public MoveSphereHandler(DialogsMainPeer peer) {
		super(MoveSphereCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute( final MoveSphereCommand action ) {
		final String sourceId = action.getSourceSphereId();
		final String targetId = action.getTargetSphereId();
		checkValue( sourceId, "sourceId is null" );
		checkValue( targetId, "targetId is null" );
		move( sourceId, targetId );
	}
	
	private void move( final String sourceId, final String targetId ) {
		if (logger.isDebugEnabled()) {
			logger.debug("Moving sourceId: " + sourceId + " to targetId: " + targetId );
		}
		
		final Document doc1 = this.peer.getXmldb().getSphereDefinition(sourceId, sourceId);
		checkDoc( doc1, "Own sphere definition for sourceId : " + sourceId + " can not be found" );
		final SphereStatement ownSourceDefinition = SphereStatement.wrap( doc1 );

		final String coreId = ownSourceDefinition.getSphereCoreId();
		checkValue( coreId, "coreId is null for source definition" );
		
		final Document doc2 = this.peer.getXmldb().getSphereDefinition(coreId, sourceId);
		checkDoc( doc2, "Core sphere definition for sourceId : " + sourceId + " in coreId: " + coreId + " can not be found" );
		final SphereStatement coreSourceDefinition = SphereStatement.wrap( doc2 );
		
		ownSourceDefinition.setSphereCoreId( targetId );
		coreSourceDefinition.setSphereCoreId( targetId );
		
		this.peer.getXmldb().replaceDoc(ownSourceDefinition.getBindedDocument(), sourceId);
		this.peer.getXmldb().moveDoc(coreSourceDefinition.getBindedDocument(), 
				coreId, targetId, true);
	}

	private void checkValue( final String value, final String message ){
		if ( StringUtils.isBlank( value) ) {
			throw new NullPointerException( message );
		}
	}
	
	private void checkDoc( final Document doc, final String message ){
		if ( doc == null ) {
			throw new NullPointerException( message );
		}
	}
}
