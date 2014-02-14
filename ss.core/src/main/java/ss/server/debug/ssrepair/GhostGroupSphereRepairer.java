/**
 * 
 */
package ss.server.debug.ssrepair;

import java.util.Set;
import java.util.TreeSet;

import ss.common.ListUtils;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
/**
 *
 */
public class GhostGroupSphereRepairer extends AbstractRepairer {

	private Set<String> goodGroupSpheresIds;
	private Set<String> unreadableInvisibleSpheresIds;
	private Set<String> unreadableSpheresIds;

	/**
	 * @param context
	 */
	public GhostGroupSphereRepairer(final Context context) {
		super( context );
	}
	
	/* (non-Javadoc)
	 * @see ss.server.debug.ssrepair.AbstractRepairer#prepareToRepair()
	 */
	@Override
	protected void prepareToRepair() {
		this.goodGroupSpheresIds = new TreeSet<String>();
		this.unreadableInvisibleSpheresIds = new TreeSet<String>();
		this.unreadableSpheresIds = new TreeSet<String>();
		for( SupraSphereMember member : getSupraSphere().getSupraMembers() ) {
			for( SphereItem sphereItem : member.getSpheres() ) {
				if ( hasInvalidDisplayName(sphereItem) ) {
					this.unreadableSpheresIds.add( sphereItem.getSystemName() );
					if ( sphereItem.isEnabled() ) {
						this.unreadableInvisibleSpheresIds.remove( sphereItem.getSystemName() );
					}
					else {
						this.unreadableInvisibleSpheresIds.add( sphereItem.getSystemName() );
					}
				}
				else if ( sphereItem.getSphereType() == SphereItem.SphereType.GROUP ) {
					this.goodGroupSpheresIds.add( sphereItem.getSystemName() );
				}
			}			
		}
	}
	
	/* (non-Javadoc)
	 * @see ss.server.debug.ssrepair.AbstractRepairer#performRepair()
	 */
	@Override
	protected void performRepair() {
		removeUnreadableVisibleSpheres();
		this.context.addMessage( formatSelectForGhostSpheresDefinitions() );
		this.context.addMessage( "Unreadable id's", ListUtils.allValuesToString( this.unreadableSpheresIds ) );
		final SupraSphereStatement supraSphere = getSupraSphere();
		this.context.changeSupraSphere( "Result of ghost removing: ", supraSphere );		
	}

	/**
	 * @param ghostSphereIds
	 */
	private String formatSelectForGhostSpheresDefinitions() {
		final Set<String> ghostSphereIds = new TreeSet<String>( this.unreadableInvisibleSpheresIds );
		ghostSphereIds.removeAll( this.goodGroupSpheresIds );
		return formatSelectSphereDefinitionQuery( "Select for ghost spheres", ghostSphereIds);
	}

	/**
	 * @param ghostSphereIds
	 */
	private void removeUnreadableVisibleSpheres() {
		for( SupraSphereMember member : getSupraSphere().getSupraMembers() ) {
			for( SphereItem sphereItem : member.getSpheres() ) {
				if ( hasInvalidDisplayName(sphereItem) &&
					 this.unreadableInvisibleSpheresIds.contains( sphereItem.getSystemName() ) ) {
					member.removeItem( sphereItem );
				}
			}		
		}
	}

	/**
	 * @param sphereItem
	 * @return
	 */
	private boolean hasInvalidDisplayName(SphereItem sphereItem) {
		return !sphereItem.hasValidDisplayName() && sphereItem.getDisplayName().equals( sphereItem.getSystemName() );
	}
	
}
