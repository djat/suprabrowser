package ss.client.ui.spheremanagement;

public interface IManagedSphereVisitor {

	void beginNode( ManagedSphere sphere );
	
	void endNode( ManagedSphere sphere );
	
}
