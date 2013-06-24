/**
 * 
 */
package ss.framework.networking2.properties;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ProtocolPropertiesBuilder {

	private final String displayName;

	private final List<ProtocolProperty> additionalProperties = new ArrayList<ProtocolProperty>();
	
	public ProtocolPropertiesBuilder( String displayName ) {
		this.displayName = displayName;		
	}
	
	public void add( ProtocolProperty property ) {
		this.additionalProperties.add( property );
	}
	
	public ProtocolProperties getResult() {
		ProtocolProperties result = new ProtocolProperties( this.displayName );
		for (ProtocolProperty additionalProperty : this.additionalProperties) {
			result.addProperty( additionalProperty );
		}
		return result;
	}
}
 