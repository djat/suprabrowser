/**
 * 
 */
package ss.server.domain.service;

import java.util.Vector;

import org.dom4j.Document;

/**
 *
 */
public interface ICreateSphere extends ISupraSphereFeature {

	public void createSphere(Vector membersDocs, String system_name,
			String display_name, Document sphereDoc, String username,
			String real_name, String sphereId, String sphereURL, String prefEmailAlias);
}
