package ss.client.networking.protocol.actions;

import ss.domainmodel.ObjectRelationCollection;
import ss.framework.arbitrary.change.ArbitraryChangeSet;

public class SaveSphereRelationsCommand extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6572638971657307152L;

	private final String sphereId;
	
	private final String relationsXml;
	
	private final ArbitraryChangeSet<String> changeSet;
	
	/**
	 * @param sphereId
	 * @param relations
	 */
	public SaveSphereRelationsCommand(String sphereId, ObjectRelationCollection relations, ArbitraryChangeSet<String> changeSet) {
		super();
		this.sphereId = sphereId;
		this.relationsXml = relations.toXml();
		this.changeSet = changeSet;
	}

	public String getSphereId() {
		return this.sphereId;
	}

	public ObjectRelationCollection getRelations() {
		ObjectRelationCollection relations = new ObjectRelationCollection();
		relations.fromXml( this.relationsXml );
		return relations;
	}

	public ArbitraryChangeSet<String> getChangeSet() {
		return this.changeSet;
	}
	
}
