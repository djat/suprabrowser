/**
 * 
 */
package ss.server.debug.ssrepair;

import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;

import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
/**
 *
 */
public class InvalidSphereDefinitionRepairer extends AbstractRepairer {
	
	private Set<String> allGroupsSpheresIds;
	
	private Set<String> unboundedSpheresIds;

	private Set<String> invalidDisplayNamesSpheresIds;

	/**
	 * @param context
	 */
	public InvalidSphereDefinitionRepairer(final Context context) {
		super( context );
	}
	
	/* (non-Javadoc)
	 * @see ss.server.debug.ssrepair.AbstractRepairer#prepareToRepair()
	 */
	@Override
	protected void prepareToRepair() {
		this.allGroupsSpheresIds = new TreeSet<String>();
		this.unboundedSpheresIds = new TreeSet<String>();
		this.invalidDisplayNamesSpheresIds = new TreeSet<String>();
		for( SupraSphereMember member : getSupraSphere().getSupraMembers() ) {
			for( SphereItem sphereItem : member.getSpheres() ) {
				if ( sphereItem.getSphereType() == SphereItem.SphereType.GROUP ) {
					this.allGroupsSpheresIds.add( sphereItem.getSystemName() );
				}
			}			
		}
		for( Document sphereDocument : this.context.getXmlDb().getAllSpheres() ) {
			final SphereStatement sphere = SphereStatement.wrap(sphereDocument);
			if ( this.allGroupsSpheresIds.contains( sphere.getSystemName() ) ) {
				if ( !sphere.hasValidDisplayName() ) {
					this.invalidDisplayNamesSpheresIds.add( sphere.getSystemName() );
				}
			}
			else {
				this.unboundedSpheresIds.add( sphere.getSystemName() );
			}
		}
	}

	/* (non-Javadoc)
	 * @see ss.server.debug.ssrepair.AbstractRepairer#performRepair()
	 */
	@Override
	protected void performRepair() {
		this.context.addMessage( formatSelectSphereDefinitionQuery( "GROUP spheres definitions that unknown for surpasphere", this.unboundedSpheresIds) );
		this.context.addMessage( formatSelectSphereDefinitionQuery( "Sphere definitions that with invalid display names", this.invalidDisplayNamesSpheresIds ) );
	}

}
