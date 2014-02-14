package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.SaveSphereRelationsCommand;
import ss.domainmodel.ObjectRelationCollection;
import ss.domainmodel.SphereStatement;
import ss.framework.arbitrary.change.IObjectHandler;
import ss.server.networking.DialogsMainPeer;

public class SaveSphereRelationsHandler extends AbstractSphereDefinitionHandler<SaveSphereRelationsCommand> {

	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SaveSphereRelationsHandler(DialogsMainPeer peer) {
		super(SaveSphereRelationsCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(final SaveSphereRelationsCommand action) {
		final SphereStatement editedSphere = getSphere( action.getSphereId(), action.getSphereId() );
		final ObjectRelationCollection relations = action.getRelations();
		updateSphereDefinition( action.getSphereId(), new ISphereDefinitionHandler() {
			public boolean update(SphereStatement sphere) {
				sphere.getRelations().from( relations );
				return true;
			}
		} );		
		action.getChangeSet().getCreated().foreach( new IObjectHandler<String>() {
			public void handle(String item) {
				updateSphereDefinition( item, new ISphereDefinitionHandler() {
					public boolean update(SphereStatement sphere) {
						sphere.getRelations().add( editedSphere );
						return true;
					}
				});
			}
		}); 
		action.getChangeSet().getDeleted().foreach( new IObjectHandler<String>() {
			public void handle(String item) {
				updateSphereDefinition( item, new ISphereDefinitionHandler() {
					public boolean update(SphereStatement sphere) {
						sphere.getRelations().removeBySphereId( editedSphere.getSystemName() );
						return true;
					}
				});
			}
		});
	}
	
}

