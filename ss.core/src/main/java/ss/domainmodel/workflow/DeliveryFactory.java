/**
 * 
 */
package ss.domainmodel.workflow;

import java.util.ArrayList;
import java.util.List;

import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;

/**
 *
 */
public class DeliveryFactory {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeliveryFactory.class);
		
	private static final String CONFIRM_RECEIPT_TYPE = "confirm_receipt";
	private static final String DECISIVE_TYPE = "decisive";
	private static final String NORMAL_TYPE = "normal";
	private static final String POLL_TYPE = "poll";

	private final List<DeliveryDescriptor> descriptors = new ArrayList<DeliveryDescriptor>(); 
	
	public final static DeliveryFactory INSTANCE = new DeliveryFactory();

	private DeliveryFactory() {
		addDescriptor( NORMAL_TYPE, NormalDelivery.class );
		addDescriptor( CONFIRM_RECEIPT_TYPE, ConfirmReceiptDelivery.class );
		addDescriptor( DECISIVE_TYPE, DecisiveDelivery.class );
		addDescriptor( POLL_TYPE, PollDelivery.class );		
	}
	
	@SuppressWarnings("unchecked")
	private void addDescriptor( String type, Class clazz )  {
		this.descriptors.add( new DeliveryDescriptor( type, clazz ) );
	}

	public DeliveryDescriptor getDefaultDeliveryDescriptor() {
		return this.descriptors.get( 0 );
	}
	
	/**
	 * @param deliveryType
	 * @return
	 */
	public AbstractDelivery create(String deliveryType) {
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
	public String getDeliveryTypeByDeliveryClass( Class deliveryClass ) {
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
		return getDefaultDeliveryDescriptor().getType();
	}

	/**
	 * @return
	 */
	public AbstractDelivery getDeliveryTypeNormal(String sphereId) {
		return SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId)
				.getWorkflowConfiguration().getNormalDelivery();
	}

	public AbstractDelivery getCurrentDeliveryForSphere(
			String sphereId, String displayName) {
		return SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId)
				.getWorkflowConfiguration().getDeliveryByDisplayName(
						displayName);
	}

	/**
	 * @param currentSphere
	 * @param workflowType
	 * @return
	 */
	public AbstractDelivery getDeliveryForSphereByType(String currentSphere, String deliveryType) {
		if ( logger.isDebugEnabled() ) {
			logger.debug("id : "+currentSphere+"    delivery : "+deliveryType);
		}
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferences(currentSphere);
		WorkflowConfiguration configuration = preferences.getWorkflowConfiguration();
		if ( logger.isDebugEnabled() ) {
			logger.debug(preferences);
		}
		AbstractDelivery delivery = configuration.getDeliveryByTypeOrNormal(deliveryType);
		return delivery;
	}
	
	/**
	 * @param currentSphere
	 * @param workflowType
	 * @return
	 */
	public AbstractDelivery getDeliveryForSphereByTypeForServer(String currentSphere, String deliveryType) {
		if ( logger.isDebugEnabled() ) {
			logger.debug("id : "+currentSphere+"    delivery : "+deliveryType);
		}
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferences(currentSphere);
		WorkflowConfiguration configuration = preferences.getWorkflowConfiguration();
		if ( logger.isDebugEnabled() ) {
			logger.debug(preferences);
		}
		AbstractDelivery delivery = configuration.getDeliveryByTypeOrNormal(deliveryType);
		return delivery;
	}

	/**
	 * @param systemName
	 * @return
	 */
	public WorkflowConfiguration getWorkflowConfiguration(String systemName) {
		return SsDomain.SPHERE_HELPER.getSpherePreferences(systemName).getWorkflowConfiguration();
	}


	/**
	 * @param deliveryType
	 * @return
	 */
	public boolean isTextInputReply(String deliveryType) {
		return deliveryType != null && deliveryType.equals( CONFIRM_RECEIPT_TYPE );
	}
	
	@SuppressWarnings("unchecked")
	public String getDefaultDeliveryDisplayName() {
		Class clazz = getDefaultDeliveryDescriptor().getClass();
		String defaultDeliveryName = getDeliveryTypeByDeliveryClass(clazz);
		return defaultDeliveryName;
	}

}
