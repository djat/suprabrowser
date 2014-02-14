/**
 * 
 */
package ss.client.ui.tree;

import ss.client.ui.MessagesPane;
import ss.common.operations.AbstractOperation;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.server.networking.SC;

/**
 *
 */
public abstract class AbstractMessageTreeOperation extends AbstractOperation {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractMessageTreeOperation.class);
	
	protected final MessagesPane messagesPaneOwner;

	/**
	 * @param messagesPaneOwner
	 */
	public AbstractMessageTreeOperation(final MessagesPane messagesPaneOwner) {
		super();
		this.messagesPaneOwner = messagesPaneOwner;
	}
	
	/**
	 * @return
	 */
	protected void updateResponseIdForSphere( Statement statement ) {
		logger.debug( "updating responseId for Sphere" ); 
		if ( statement.isSphere() ) {
			SphereStatement	sphere = SphereStatement.wrap( statement.getBindedDocument() );
			SphereStatement parent = findSphereBySystemName(sphere.getSphereCoreId());
			if ( parent == null && 
			 	 isSupraSpherePane() ) { 
			 	parent = findSphereBySystemName( (String)this.messagesPaneOwner.getRawSession().get( SC.SUPRA_SPHERE ) );
			}
			if ( logger.isDebugEnabled() ) {
				logger.debug( "Parent for by " + sphere.getSphereCoreId() + " is " + parent );
			}
			if ( parent != null ) { 
				parent.bindChild( statement );
			}
		}	
	}

	/**
	 * @param systemName
	 */
	private SphereStatement findSphereBySystemName(String systemName) {
		if (systemName == null) {
			return null;
		}
		for (Statement statement : this.messagesPaneOwner.getTableStatements()) {
			if (statement.isSphere()
					&& SphereStatement.wrap(statement.getBindedDocument())
							.getSystemName().equals(systemName)) {
				return SphereStatement.wrap(statement.getBindedDocument());
			}
		}
		return null;
	}
	

	/**
	 * @return
	 */
	private boolean isSupraSpherePane() {
		return this.messagesPaneOwner.isRootView();
	}
}
