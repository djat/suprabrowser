/**
 * 
 */
package ss.client.event.executors;

import java.util.concurrent.atomic.AtomicReference;

import ss.client.ui.MessagesPane;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class ContactExecutor extends StatementExecutor {

	/**
	 * @param mp
	 * @param statement
	 */
	public ContactExecutor(MessagesPane mp, Statement statement) {
		super(mp, statement);
	}

	/* (non-Javadoc)
	 * @see ss.client.event.executors.StatementExecutor#browserExecute()
	 */
	@Override
	protected void browserExecute() {
		final AtomicReference<Statement> item = new AtomicReference<Statement>();
		item.set(this.statement);
		Thread t = new Thread() {
			@Override
			public void run() {

//				String content = XSLTransform
//				.transformContact(ContactExecutor.this.statement
//				.getBindedDocument());

//				getMP().showSmallBrowser(getSession(), true, "contact",
//				content, item.get(), null);

				String content = transfromContactToHtml(ContactStatement.wrap(ContactExecutor.this.statement.getBindedDocument()));

				getMP().showSmallBrowser(getSession(), true, "contact",
						content, item.get(), null);

			}
		};
		t.start();
		
	}
	
	public static final String transfromContactToHtml(final ContactStatement contact) {
		String html = "";
		html += "<html>";
		html += "<head>";
		html += "<style>";
		html += ".fieldname {font-style:italic; font-size:12; font-family:verdana;}";
		html += ".fieldtext {font-weight:bold; font-size:12; font-family:verdana;}";
		html += "</style>";
		html += "</head>";
		
		html += "<body>";
		html += "<div style=\"position:absolute; left:10px; top:10px; width:48%;\">";
		html += "<table>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">First Name: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getFirstName())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Name Prefix: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getNamePrefix())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Name Suffix: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getNameSuffix())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Title: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getMessageTitle())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Street: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getStreet())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Street 2: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getStreetCont())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">City: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getCity())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">State: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getState())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Zip Code: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getZipCode())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Country: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getCountry())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Email: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getEmailAddress())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Second Email: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getSecondEmailAddress())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">URL: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getURL())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Home Sphere: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getHomeSphere())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Contact Type: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getRole())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Original Note: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getBody())+"</td>";
		html += "</tr>";
		
		html += "</table>";
		html += "</div>";
		
		
		
		html += "<div style=\"position:absolute; left:50%; top:10px;\">";
		html += "<table>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Last Name: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getLastName())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Middle Name: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getMiddleName())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Organization: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getOrganization())+"</td>";
		html += "</tr>";
		

		html += "<tr>";
		html += "<td class=\"fieldname\">Department: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getDepartment())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Account: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getAccount())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Contact Owner: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getOwnerContact())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Mobile: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getMobile())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">WORK Tel: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getWorkTelephone())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">HOME Tel: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getHomeTelephone())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Fax: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getFax())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Second Fax: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getFaxSecond())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Time Zone: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getTimeZone())+"</td>";
		html += "</tr>";
		
		html += "<tr>";
		html += "<td class=\"fieldname\">Location: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getLocation())+"</td>";
		html += "</tr>";

		html += "<tr>";
		html += "<td class=\"fieldname\">Login: </td>";
		html += "<td class=\"fieldtext\">"+StringUtils.getTrimmedString(contact.getLogin())+"</td>";
		html += "</tr>";
		
		html += "</table>";
		html += "</div>";
		html += "</body>";
		html += "</html>";
		return html;
	}
	
	

}
