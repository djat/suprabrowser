package ss.server.debug.commands;

import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereItemCollection;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.server.debug.ssrepair.AbstractRepairer;
import ss.server.debug.ssrepair.Context;

public class RemoveDisabledSpheres extends AbstractRepairerCommand {

	/**
	 * @param repairerClass
	 */
	public RemoveDisabledSpheres() {
		super(Repairer.class);
		// TODO Auto-generated constructor stub
	}

	public static class Repairer extends AbstractRepairer {

		private SupraSphereStatement supraSphere; 
		/**
		 * @param context
		 */
		public Repairer(Context context) {
			super(context);
		}

		/* (non-Javadoc)
		 * @see ss.server.debug.ssrepair.AbstractRepairer#performRepair()
		 */
		@Override
		protected void performRepair() {
			this.context.changeSupraSphere("Remove disabled spheres references", this.supraSphere);			
		}

		/* (non-Javadoc)
		 * @see ss.server.debug.ssrepair.AbstractRepairer#prepareToRepair()
		 */
		@Override
		protected void prepareToRepair() {
			this.supraSphere = this.context.getSupraSphere();
			for( SupraSphereMember member : this.supraSphere.getSupraMembers() ) {
				final SphereItemCollection memberSpheres = member.getSpheres();
				for( SphereItem sphereItem : memberSpheres ) {
					if ( !sphereItem.isEnabled() ) {
						memberSpheres.remove(sphereItem);
					}
				}
			}
		}
		
	}
}


