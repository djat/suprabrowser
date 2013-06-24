/**
 * 
 */
package ss.client.networking.protocol.actions;

import org.dom4j.Document;

import ss.domainmodel.SphereStatement;

/**
 * @author zobo
 *
 */
public class UpdateSphereDefinitionAction extends AbstractAction {

	private static final long serialVersionUID = -6670611132557328030L;

	private static final String SPHERE_DEFINITION = "SphereDefinition";

	public void setDefinition( final SphereStatement sphere ){
		putArg(SPHERE_DEFINITION, sphere.getBindedDocument());
	}

	public SphereStatement getDefinition(){
		final Document doc = getDocumentArg(SPHERE_DEFINITION);
		return (doc != null) ? SphereStatement.wrap(doc) : null;
	}
}
