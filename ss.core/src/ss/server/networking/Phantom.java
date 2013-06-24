/**
 * 
 */
package ss.server.networking;

import org.dom4j.Document;

import ss.common.SphereDefinitionCreator;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class Phantom {

	/**
	 * Singleton instance
	 */
	public final static Phantom INSTANCE = new Phantom();

	private SphereDefinitionCreator definitionCreator = new SphereDefinitionCreator();
	
	//private TempIdGenerator generator = new TempIdGenerator();
	  
	private Phantom() {
	}
		
	public SphereStatement createSphere() {
		final String systemName = "1000000"; // String.valueOf( this.generator.nextId() );
		return createSphere( systemName, systemName);
	}

	/**
	 * @param title
	 * @param systemName
	 * @return
	 */
	private SphereStatement createSphere(String title, final String systemName) {
		Document sphereDocument = this.definitionCreator.createDefinition( title, systemName );
		SphereStatement sphere = SphereStatement.wrap( sphereDocument );
		sphere.setDisplayName( title );
		sphere.setSubject( title );
		sphere.setOriginalId( systemName );
		sphere.setMessageId( systemName );
		sphere.setConfirmed( true );
		sphere.setGiver( "" );
		return sphere;
	}
}
