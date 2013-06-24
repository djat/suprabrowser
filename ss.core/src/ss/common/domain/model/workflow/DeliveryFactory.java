/**
 * 
 */
package ss.common.domain.model.workflow;

import java.util.ArrayList;
import java.util.List;

import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.preferences.SphereOwnPreferences;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.workflow.ConfirmReceiptDelivery;
import ss.domainmodel.workflow.DecisiveDelivery;
import ss.domainmodel.workflow.NormalDelivery;
import ss.domainmodel.workflow.PollDelivery;

/**
 * @author roman
 *
 */
public class DeliveryFactory {

	private final List<DeliveryDescriptor> descriptors = new ArrayList<DeliveryDescriptor>(); 
	
	public final static DeliveryFactory INSTANCE = new DeliveryFactory();

	private DeliveryFactory() {
		addDescriptor( DeliveryType.NORMAL, NormalDelivery.class );
		addDescriptor( DeliveryType.CONFIRM_RECEIPT, ConfirmReceiptDelivery.class );
		addDescriptor( DeliveryType.DECISIVE, DecisiveDelivery.class );
		addDescriptor( DeliveryType.POLL, PollDelivery.class );		
	}
	
	@SuppressWarnings("unchecked")
	private void addDescriptor( final DeliveryType type, Class clazz )  {
		this.descriptors.add( new DeliveryDescriptor( type, clazz ) );
	}

	public DeliveryDescriptor getDefaultDeliveryDescriptor() {
		return this.descriptors.get( 0 );
	}
	
	/**
	 * @param deliveryType
	 * @return
	 */
	public AbstractDeliveryObject create(final DeliveryType deliveryType) {
		if (  deliveryType != null ) {
			for( DeliveryDescriptor descriptor : this.descriptors ) {
				if ( descriptor.getType().equals( deliveryType ) ) {
					return descriptor.createDelivery();
				}
			}
		}
		return getDefaultDeliveryDescriptor().createDelivery();
	}
	
	@SuppressWarnings("unchecked")
	public DeliveryType getDeliveryTypeByDeliveryClass( Class deliveryClass ) {
		if ( deliveryClass != null ) {
			for( DeliveryDescriptor descriptor : this.descriptors ) {
				if ( descriptor.getDeliveryClass().equals( deliveryClass ) ) {
					return descriptor.getType();
				}
			}
		}
		return getDefaultDeliveryDescriptor().getType();
	}
	
	@SuppressWarnings("unchecked")
	public String getDefaultDisplayNameForDeliveryClass( Class deliveryClass ) {
		if ( deliveryClass != null ) {
			for( DeliveryDescriptor descriptor : this.descriptors ) {
				if ( descriptor.getDeliveryClass().equals( deliveryClass ) ) {
					return descriptor.getDefaultDisplayName();
				}
			}
		}
		return getDefaultDeliveryDescriptor().getType().name();
	}

	/**
	 * @return
	 */
	public NormalDeliveryObject getDeliveryTypeNormal(String sphereId) {
		return SsDomain.SPHERE_HELPER.getSpherePreferencesObject(sphereId)
				.getWorkflowConfiguration().getNormalDelivery();
	}

	public AbstractDeliveryObject getCurrentDeliveryForSphere(
			String sphereId, String displayName) {
		return SsDomain.SPHERE_HELPER.getSpherePreferencesObject(sphereId)
				.getWorkflowConfiguration().getDeliveryByDisplayName(
						displayName);
	}

	/**
	 * @param currentSphere
	 * @param workflowType
	 * @return
	 */
	public AbstractDeliveryObject getDeliveryForSphereByType(final String currentSphere, final DeliveryType deliveryType) {
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferencesObject(currentSphere);
		WorkflowConfigurationObject configuration = preferences.getWorkflowConfiguration();
		AbstractDeliveryObject delivery = configuration.getDeliveryByTypeOrNormal(deliveryType);
		return delivery;
	}
	
	/**
	 * @param currentSphere
	 * @param workflowType
	 * @return
	 */
	public AbstractDeliveryObject getDeliveryForSphereByTypeForServer(final String currentSphere, final DeliveryType deliveryType) {
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferencesObject(currentSphere);
		WorkflowConfigurationObject configuration = preferences.getWorkflowConfiguration();
		AbstractDeliveryObject delivery = configuration.getDeliveryByTypeOrNormal(deliveryType);
		return delivery;
	}

	/**
	 * @param systemName
	 * @return
	 */
	public WorkflowConfigurationObject getWorkflowConfiguration(final String systemName) {
		return SsDomain.SPHERE_HELPER.getSpherePreferencesObject(systemName).getWorkflowConfiguration();
	}


	/**
	 * @param deliveryType
	 * @return
	 */
	public boolean isTextInputReply(final DeliveryType deliveryType) {
		return deliveryType != null && deliveryType.equals( DeliveryType.CONFIRM_RECEIPT );
	}
	
	@SuppressWarnings("unchecked")
	public String getDefaultDeliveryDisplayName() {
		Class clazz = getDefaultDeliveryDescriptor().getClass();
		DeliveryType defaultDeliveryType = getDeliveryTypeByDeliveryClass(clazz);
		return defaultDeliveryType.name();
	}
}
