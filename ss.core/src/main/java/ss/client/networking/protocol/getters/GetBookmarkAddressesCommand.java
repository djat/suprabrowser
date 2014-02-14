/**
 * 
 */
package ss.client.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zobo
 *
 */
public class GetBookmarkAddressesCommand extends AbstractGetterCommand {

	/**
	 * 
	 */
	private static final String LOOKUP_SPHERE = "LOOKUP_SPHERE";
	
	/**
	 * 
	 */
	private static final String FILTER = "FILTER";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4381472132328247984L;

	public void addLookupSphere( String sphereId ) {
		getLookupSphere().add(sphereId);
	}
	
	public void addLookupSpheres( Collection<String> sphereIds ) {
		getLookupSphere().addAll(sphereIds);
	}

	/**
	 * 
	 */
	public List<String> getLookupSphere() {
		ArrayList<String> lookupSphere = (ArrayList<String>) getObjectArg( LOOKUP_SPHERE );
		if ( lookupSphere == null ) {
			lookupSphere = new ArrayList<String>();
			putArg( LOOKUP_SPHERE, lookupSphere );
		}
		return lookupSphere;		
	}

	/**
	 * @param filter
	 */
	public void setFilter(String filter) {
		super.putArg( FILTER, filter);		
	}
	
	public String getFilter() {
		return super.getStringArg( FILTER );
	}
}
