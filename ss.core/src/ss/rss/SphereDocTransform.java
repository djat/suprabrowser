/**
 * 
 */
package ss.rss;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.domainmodel.SphereMember;
import ss.domainmodel.SpherePhisicalLocationItem;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.configuration.SphereRoleObject;

/**
 * @author roman
 *
 */
public class SphereDocTransform {
	
	public static String getString(Document doc) {
		String head = "<html><head></head><body>";
		String tail = "</body></html>";
		String content = "";

		final SphereStatement sphere = SphereStatement.wrap(doc);
		final SpherePhisicalLocationItem locationItem = sphere.getPhisicalLocation();

		content += "<table style=\"font-size:12\">";
		content += "<tr><td><i>Name:</i></td> <td><b>"+sphere.getDisplayName()+"</b></td></tr>";
		content += "<tr><td><i>Type:</i></td> <td><b>"+
		( StringUtils.isNotBlank(sphere.getRole()) ? sphere.getRole() : SphereRoleObject.getDefaultName() )
		+"</b></td></tr>";
		
		/// sphere location
		content += "<tr><td><i>Zip Code:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getZipcode())+"</b></td></tr>";
		content += "<tr><td><i>Country:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getCountry())+"</b></td></tr>";
		content += "<tr><td><i>Region:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getRegion())+"</b></td></tr>";
		content += "<tr><td><i>State:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getState())+"</b></td></tr>";
		content += "<tr><td><i>City:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getCity())+"</b></td></tr>";
		content += "<tr><td><i>Street:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getStreet())+"</b></td></tr>";
		content += "<tr><td><i>Street Cont:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getStreetcont())+"</b></td></tr>";
		content += "<tr><td><i>Address:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getAddress())+"</b></td></tr>";
		content += "<tr><td><i>Telephone:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getTelephone())+"</b></td></tr>";
		content += "<tr><td><i>Fax:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getFax())+"</b></td></tr>";
		content += "<tr><td><i>Email:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getEmail())+"</b></td></tr>";
		content += "<tr><td><i>Description:</i></td> <td><b>"+StringUtils.getTrimmedString(locationItem.getDescription())+"</b></td></tr>";
		content += "</table><br><br>";

		content += "<span style=\"font-size:12\">Members:</span><ul type=\"disc\" style=\"font-size:12\" align=\"left\">";
		for(SphereMember sm : sphere.getSphereMembers()) {
			content += "<li>"+sm.getContactName();
		}
		content += "</ul><br>";
		content += "<div style=\"font-size:12\"><div>Default type: "+sphere.getDefaultType()+"</div>";
		content += "<div>Default delivery: "+sphere.getDefaultDelivery()+"</div>";
		content += "<div>Date range: "+(StringUtils.isNotBlank(sphere.getExpiration()) ? sphere.getExpiration() : "-" )+"</div></div>";

		return head+content+tail;
	}
}
