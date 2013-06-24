package ss.server.debug.ssrepair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ss.domainmodel.MemberRelation;
import ss.domainmodel.PrivateSphereReference;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;

public class PersonalVisibilityRepairer extends AbstractRepairer {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PersonalVisibilityRepairer.class);

	private List<PrivateSphereReference> missedSpheres;

	/**
	 * @param context
	 */
	public PersonalVisibilityRepairer(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.debug.ssrepair.AbstractRepairer#prepareToRepair()
	 */
	@Override
	protected void prepareToRepair() {
		this.missedSpheres = new ArrayList<PrivateSphereReference>();
		final Set<MemberRelation> keyToMissedSphere = new HashSet<MemberRelation>();	
		for (SupraSphereMember member : getSupraSphere().getSupraMembers()) {
			final Set<String> contactNames = new TreeSet<String>();
			for (SphereItem sphere : member.getSpheres()) {
				if (sphere.isEnabled()
						&& sphere.getSphereType() == SphereItem.SphereType.GROUP) {
					contactNames.addAll(getSupraSphere().getMembersContantNamesFor(sphere.getSystemName()));
				}
			}
			final Set<String> missedContactNames = new TreeSet<String>(
					contactNames);
			for (SphereItem sphere : member.getSpheres()) {
				missedContactNames.remove(sphere.getDisplayName());
			}
			if (missedContactNames.size() > 0) {
				for (String missedContactName : missedContactNames) {
					MemberRelation missedSphereKey = new MemberRelation(
							member.getContactName(), missedContactName);
					if (!keyToMissedSphere.contains(missedSphereKey)) {
						final String sphereId = String.valueOf(this.context
								.getXmlDb().getNextTableId());
						if (getSupraSphere().getMembersForSphere(sphereId)
								.size() > 0) {
							final String message = "Sphere id " + sphereId
									+ " in use already.";
							this.context.addError(message);
						}
						PrivateSphereReference personalSphere = new PrivateSphereReference(
								sphereId, missedSphereKey, true );
						keyToMissedSphere.add(personalSphere.getForwardRelation());
						keyToMissedSphere.add(personalSphere.getBackwarkRelation());
						this.missedSpheres.add(personalSphere);
					}
				}
			}
		}

		if ( this.missedSpheres.size() > 0 ) {
			for (PrivateSphereReference missedSphere : this.missedSpheres) {
				this.context.addMessage("Missed personal sphere", missedSphere
						.toString());
			}			
		}
		else {
			this.context.addMessage( "Personal sphere visibility is OK" );
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.debug.ssrepair.AbstractRepairer#performRepair()
	 */
	@Override
	protected void performRepair() {
		SupraSphereStatement supraSphere = getSupraSphere();
		for (PrivateSphereReference sphere : this.missedSpheres) {
			sphere.createSphere(supraSphere);
		}
		this.context
				.changeSupraSphere("After missed p2p creation", supraSphere);
	}	

}
