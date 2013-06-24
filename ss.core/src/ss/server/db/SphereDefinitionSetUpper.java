/**
 * 
 */
package ss.server.db;

import org.dom4j.Document;

import ss.common.SphereDefinitionCreator;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class SphereDefinitionSetUpper {
	
	public static SphereDefinitionSetUpper INSTANCE = new SphereDefinitionSetUpper();
	
	public static Document setUpSphereDefinition(DialogsMainPeer peer, Document sphereDefinition,
			String sphere_id, String supraSphere, String contactName) {
		if (sphereDefinition == null) {
			sphereDefinition = peer.getXmldb().getSphereDefinition(
					sphere_id, sphere_id);

			if (sphereDefinition == null) {
				sphereDefinition = peer.getXmldb().getSphereDefinition(
						supraSphere, sphere_id);
			}

		} 

		if (sphereDefinition == null) {

			SphereDefinitionCreator sdc = new SphereDefinitionCreator();
			sphereDefinition = sdc.createDefinition(contactName, sphere_id);
		}
		return sphereDefinition;
	}

}
