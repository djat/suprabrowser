/**
 * 
 */
package ss.server.networking.util;

import org.dom4j.Document;

import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.ConfirmReceiptDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class WorkflowConfigurationSetup {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(WorkflowConfigurationSetup.class);

	public static void setupWorkflowConfigurationForSphere(SphereStatement sphere) {
		if (logger.isDebugEnabled()){
			logger.debug("setupWorkflowConfigurationForSphere for sphere: " + sphere.getDisplayName() + " started");
		}
		SphereOwnPreferences preferences = new SphereOwnPreferences();
		
		WorkflowConfiguration configuration = preferences.getWorkflowConfiguration();
		
		String defaultDeliveryType = sphere.getDefaultDelivery();
		String defaultDisplayName = DeliveryFactory.INSTANCE.getDefaultDeliveryDisplayName();
		if(defaultDeliveryType.equals("confirm_receipt")) {
			defaultDisplayName = DeliveryFactory.INSTANCE.getDefaultDisplayNameForDeliveryClass(ConfirmReceiptDelivery.class);
		}
		
		if (logger.isDebugEnabled()){
			logger.debug("Default delivery for sphere is " + defaultDisplayName);
		}
		configuration.setDefaultDelivery(defaultDisplayName);
		
		String defaultType = VariousUtils.firstLetterToUpperCase(sphere.getDefaultType());
		if (logger.isDebugEnabled()){
			logger.debug("Default type for sphere is " + defaultType);
		}
		configuration.setDefaultType(defaultType);
		
		String dateRange = sphere.getExpiration();
		if (logger.isDebugEnabled()){
			logger.debug("dateRange type for sphere is " + dateRange);
		}
		configuration.setExpirationDate(dateRange);
		
		configuration.setUpSphereMembers(sphere);
		
		SsDomain.SPHERE_HELPER.setSpherePreferences(sphere.getSystemName(), preferences);
		logger.info("WorkflowConfiguration for sphere " + sphere.getDisplayName() + " created");
	}

	/**
	 * @param doc
	 */
	public static void setupWorkflowConfigurationForSphere(Document sphereDoc) {
		SphereStatement st = SphereStatement.wrap(sphereDoc);
		if (st.isSphere()){
			setupWorkflowConfigurationForSphere(st);
		} else {
			logger.error("Not sphere document " + sphereDoc.asXML());
		}
	}
	
}
