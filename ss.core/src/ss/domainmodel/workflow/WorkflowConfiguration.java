/**
 * 
 */
package ss.domainmodel.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import ss.client.ui.ControlPanel;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereStatement;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class WorkflowConfiguration extends XmlEntityObject {
	
	private static final Logger logger = SSLogger.getLogger(WorkflowConfiguration.class);
	
	private final IComplexEntityProperty<NormalDelivery> normalDelivery = super
	.createComplexProperty( "normal", NormalDelivery.class );
	
	private final IComplexEntityProperty<ConfirmReceiptDelivery> confirmDelivery = super
	.createComplexProperty( "confirm", ConfirmReceiptDelivery.class );
	
	private final IComplexEntityProperty<PollDelivery> pollDelivery = super
	.createComplexProperty( "poll", PollDelivery.class );
	
	private final IComplexEntityProperty<DecisiveDelivery> decisiveDelivery = super
	.createComplexProperty( "decisive", DecisiveDelivery.class );
	
	private final ISimpleEntityProperty defaultDelivery = super
	.createAttributeProperty( "default_delivery/@value" );
	
	private final ISimpleEntityProperty defaultType = super
	.createAttributeProperty( "default_type/@value" );
	
	private final ISimpleEntityProperty expiration = super
	.createAttributeProperty( "expiration/@value" );

	private final List<AbstractDelivery> deliveries;
	
	public WorkflowConfiguration() {
		super( "workflow");
		List<AbstractDelivery> collectedDeliveries = new ArrayList<AbstractDelivery>();
		collectedDeliveries.add(this.normalDelivery.getValue());
		collectedDeliveries.add(this.confirmDelivery.getValue());
		collectedDeliveries.add(this.decisiveDelivery.getValue());
		collectedDeliveries.add(this.pollDelivery.getValue());
		this.deliveries = Collections.unmodifiableList(collectedDeliveries);
	}
	
	/**
	 * @return
	 */
	public Iterable<String> getDeliveryDisplayNames() {
		List<String> displayNames = new ArrayList<String>();
		for(AbstractDelivery delivery : getDeliveries()) {
			displayNames.add(delivery.getDisplayName());
		}
		return displayNames;
	}

	/**
	 * @return
	 */
	public AbstractDelivery getDefaultDelivery() {
		try {
			AbstractDelivery delivery = getDeliveryByDisplayName(this.defaultDelivery.getValue());
			if(delivery != null) {
				return delivery;
			}
		} catch (NullPointerException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return getDeliveryByTypeOrNormal(this.defaultDelivery.getValue());
	}
	
	/**
	 * @return
	 */
	public void setDefaultDelivery(AbstractDelivery delivery) {
		setDefaultDelivery(delivery.getDisplayName());
	}
	
	/**
	 * @return
	 */
	public void setDefaultDelivery(String delivery) {
		this.defaultDelivery.setValue(delivery);
	}
	
	/**
	 * @return
	 */
	public String getDefaultType() {
		return this.defaultType.getValueOrDefault(ControlPanel.getTypeTerse());
	}
	
	/**
	 * @return
	 */
	public void setDefaultType(String type) {
		this.defaultType.setValue(type);
	}
	
	/**
	 * @return
	 */
	public String getExpirationDate() {
		return this.expiration.getValueOrDefault("1 week");
	}
	
	/**
	 * @return
	 */
	public void setExpirationDate(String value) {
		this.expiration.setValue(value);
	}

	/**
	 * @return
	 */
	public List<AbstractDelivery> getDeliveries() {
		return this.deliveries;
	}
	
	/**
	 * @return
	 */
	public List<AbstractDelivery> getEnabledDeliveries() {
		List<AbstractDelivery> deliveries = new ArrayList<AbstractDelivery>();
		for(AbstractDelivery delivery : getDeliveries()) {
			if(delivery.isEnabled()) {
				deliveries.add(delivery);
			}
		}
		return deliveries;
	}
	
	public List<String> getEnabledDisplayNames() {
		List<String> names = new ArrayList<String>();
		for(AbstractDelivery delivery : getEnabledDeliveries()) {
			names.add(delivery.getDisplayName());
		}
		return names;
	}

	/**
	 * @param memberLogin
	 */
	public void sphereMemberRemoved(String memberLogin) {
		for(AbstractDelivery delivery : getDeliveries()) {
			ModelMemberCollection collection = delivery.getMemberCollection();
			ModelMemberEntityObject m = collection.getByLogin(memberLogin);
			collection.remove(m);
		}
	}

	/**
	 * @param memberLogin
	 */
	public void sphereMemberAdded(SphereMember sphereMember) {
		for(AbstractDelivery delivery : getDeliveries()) {
			delivery.addNewMember(sphereMember);
		}
	}
	
	/**
	 * @param memberLogin
	 */
	public void sphereMemberAdded(MemberReference sphereMember) {
		for(AbstractDelivery delivery : getDeliveries()) {
			delivery.addNewMember(sphereMember);
		}
	}

	/**
	 * @param displayName
	 * @return
	 */
	public AbstractDelivery getDeliveryByDisplayName(String displayName) {
		for(AbstractDelivery delivery : getDeliveries()) {
			if(delivery.getDisplayName().equals(displayName)) {
				return delivery;
			}
		}
		return getNormalDelivery();
	}

	/**
	 * @return
	 */
	public NormalDelivery getNormalDelivery() {
		return this.normalDelivery.getValue();
	}

	/**
	 * @param deliveryType
	 * @return
	 */
	public AbstractDelivery getDeliveryByTypeOrNormal(String deliveryType) {
		if(deliveryType !=null) {
			for( AbstractDelivery delivery : getDeliveries() ) {
				if ( delivery.getType().equals( deliveryType ) ) {
					return delivery;
				}
			}
		}
		return getNormalDelivery();
	}

	/**
	 * @param contact
	 */
	public void sphereMemberAdded(ContactStatement contact) {
		for(AbstractDelivery delivery : getDeliveries()) {
			delivery.addNewMember(contact);
		}
	}
	
	public void setUpSphereMembers(SphereStatement sphere) {
		for(AbstractDelivery delivery : getDeliveries()) {
			for(ss.domainmodel.SphereMember sphereMember : sphere.getSphereMembers()) {
				delivery.addNewMember(sphereMember);
			}
		}
	}

	/**
	 * @param contact
	 * @return
	 */
	public boolean containsMember(MemberReference contact) {
		for(ModelMemberEntityObject member : getDefaultDelivery().getMemberCollection()) {
			if(member.getUserName().equals(contact.getLoginName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param contacts
	 */
	public void synchronizeContactsForSphere(List<MemberReference> contacts) {
		addFailing(contacts);
		
		findAndRemoveSuperfluous(contacts);
	}

	private void addFailing(List<MemberReference> contacts) {
		for(MemberReference contact : contacts) {
			if(!containsMember(contact)) {
				logger.info("add "+contact.getContactName());
				sphereMemberAdded(contact);
			}
		}
	}

	private void findAndRemoveSuperfluous(List<MemberReference> contacts) {
		List<ModelMemberEntityObject> toRemove = new ArrayList<ModelMemberEntityObject>();
		for(ModelMemberEntityObject member :getDefaultDelivery().getMemberCollection()) {
			if(!containsMember(member, contacts)) {
				toRemove.add(member);
			}
		}
		
		for(ModelMemberEntityObject member : toRemove) {
			logger.info("remove "+member.getContactName());
			sphereMemberRemoved(member);
		}
	}

	/**
	 * @param member
	 * @param contacts
	 * @return
	 */
	private boolean containsMember(ModelMemberEntityObject member, List<MemberReference> contacts) {
		for(MemberReference contact : contacts) {
			if(contact.getLoginName().equals(member.getUserName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param member
	 */
	private void sphereMemberRemoved(ModelMemberEntityObject member) {
		sphereMemberRemoved(member.getUserName());
	}

}
