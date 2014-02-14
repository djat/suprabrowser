/**
 * 
 */
package ss.suprabrowser;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMHTMLInputElement;
import org.mozilla.interfaces.nsIDOMHTMLOptionElement;
import org.mozilla.interfaces.nsIDOMNodeList;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.LuceneSearchDialog;
import ss.client.ui.SupraSphereFrame;
import ss.common.FileUtils;
import ss.common.LocationUtils;
import ss.common.PathUtils;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class AdvancedSearchHelper {

	@SuppressWarnings("unused")
	private final static Logger logger = SSLogger.getLogger(AdvancedSearchHelper.class); 
	
	public static String getAdvancedBlock(DialogsMainCli cli) {
		String html = "";
		html += "<hr style=\"margin:5px; width:95%\">";
		html += "<div style=\"position:relative; top:0; left:0; width:70%; margin:5px;\">";
		html += "<b>Select types for search:</b><br>";
		html += "<input type=checkbox class=type checked=true value=terse><img id=img_terse>Terse<br>";
		html += "<input type=checkbox class=type checked=true value=message><img id=img_message>Message<br>";
		html += "<input type=checkbox class=type checked=true value=file><img id=img_file>File<br>";
		html += "<input type=checkbox class=type checked=true value=bookmark><img id=img_bookmark>Bookmark<br>";
		html += "<input type=checkbox class=type checked=true value=keywords><img id=img_tag>Tag<br>";
		html += "<input type=checkbox class=type checked=true value=externalemail><img id=img_email>Email<br>";
		html += "<input type=checkbox class=type checked=true value=contact><img id=img_contact>Contact<br>";
		html += "<input type=checkbox class=type checked=true value=comment><img id=img_comment>Comment<br>";
		html += "<button style=\"margin:5px\" onclick=\"advanced_object.selectAllTypes();\" type=button>Select All</button> <button onclick=\"advanced_object.deselectAllTypes();\" style=\"margin:5px\" type=button>Deselect All</button>";
		html += "<div style=\"position:absolute; top:0; left:50%; width:50%;\">";;
		html += "<b>Result document contains:</b><br>";
		html += "<input type=radio name=contains class=radio value=1>exact phrase";
		html += "<input type=radio name=contains class=radio checked=true value=2>every word";
		html += "<input type=radio name=contains class=radio value=0>any word";
		html += "<br><br>";
		html += "<b>Fields for search:</b><br>";
		html += "<input type=checkbox class=field checked=true value=subject>subject";
		html += "<input type=checkbox class=field checked=true value=content>content";
		html += "<input type=checkbox class=field checked=true value=comment>comment";
		html += "<input type=checkbox class=field checked=true value=body>body";
		html += "<input type=checkbox class=field checked=true value=role>type";
		html += "<br><br>";
		html += "<b>Match in </b>";
		html += "<select class=select name=match_in>";
		html += "<option value=any selected=true>any</option>";
		html += "<option value=all>all</option>";
		html += "</select>";
		html += "</div>";
		html += "</div>";
		
		html += "<hr style=\"margin:5px; width:95%\">";
		html += "<div style=\"margin:5px\">";
		html += "<b>Givers for search:</b><br>";
		Vector<String> sphereList = cli.getVerifyAuth().getAvailableSpheres();
		List<String> giverList = cli.getVerifyAuth().getAvailablePrivateSpheres(); 
		for(String contactName : giverList) {
				html += "<input type=checkbox checked=true class=contact value=\""+contactName+"\">"+contactName+"<br>";
		}
		html += "<button style=\"margin:5px\" onclick=\"advanced_object.selectAllContacts()\" type=button>Select All</button> <button style=\"margin:5px\" onclick=\"advanced_object.deselectAllContacts()\" type=button>Deselect All</button>";
		html += "</div>";
		
		html += "<hr style=\"margin:5px; width:95%\">";
		html += "<div style=\"margin:5px\">";
		html += "<b>Spheres for search:</b><br>";
		for(String sphere : sphereList) {
			html += "<input type=checkbox class=sphere value=\""+sphere+"\" checked=true>"+cli.getVerifyAuth().getDisplayName(sphere)+"<br>";
		}
		html += "<button style=\"margin:5px\" onclick=\"advanced_object.selectAllSpheres()\" type=button>Select All</button> <button onclick=\"advanced_object.deselectAllSpheres()\" style=\"margin:5px\" type=button>Deselect All</button>";
		html += "</div>";
		html += "<input type=hidden id=not_default>";
		
		return html;
	}

	/**
	 * @param domHtmlDocument
	 */
	public static void addImagesToAdvancedBlock(
			final nsIDOMHTMLDocument domHtmlDocument) {
		if(domHtmlDocument==null) {
			return;
		}
		setImageToElement(domHtmlDocument, "img_terse", "Notes16.png");
		setImageToElement(domHtmlDocument, "img_bookmark", "Bookmark16.png");
		setImageToElement(domHtmlDocument, "img_tag", "Tag16.png");
		setImageToElement(domHtmlDocument, "img_file", "Files16.png");
		setImageToElement(domHtmlDocument, "img_message", "MailGatewayico16.png");
		setImageToElement(domHtmlDocument, "img_email", "invite16.png");
		setImageToElement(domHtmlDocument, "img_contact", "Contact16.png");
		setImageToElement(domHtmlDocument, "img_comment", "IM16.png");
	}
	
	private static void setImageToElement(final nsIDOMHTMLDocument domHtmlDocument, final String elementId, final String filename) {
		String pathToImage = PathUtils.combinePath( LocationUtils.getMicroblogBase(), "microblog", filename );
		pathToImage = FileUtils.toUri(pathToImage).toString();
		nsIDOMElement elem = domHtmlDocument.getElementById(elementId);
		elem.setAttribute("src", pathToImage);
	}
	
	public static void performSearch(final nsIDOMHTMLDocument document, String stringQuery) {
		if(document==null) {
			return;
		}
		if (useDefault(document)) {
			LuceneSearchDialog.performDefaultSupraSearch(stringQuery, true);
			return;
		}

		String rawQuery = extractParametersAndGetQuery(document, stringQuery);
		Query query = LuceneSearchDialog.getQuery(rawQuery);
		SupraSphereFrame.INSTANCE.client.searchSupraSphere(stringQuery, query, true, false);
	}
	

	private static boolean useDefault(final nsIDOMHTMLDocument document) {
		if(document==null) {
			return false;
		}
		return document.getElementById("not_default")==null;
	}
	
	private static String extractParametersAndGetQuery(final nsIDOMHTMLDocument document, final String stringQuery) {
		nsIDOMNodeList list = document.getElementsByTagName("input");
		List<String> spheresList = new ArrayList<String>();
		List<String> typesList = new ArrayList<String>();
		List<String> fieldsList = new ArrayList<String>();
		List<String> contactsList = new ArrayList<String>();
		String matchIn = "";
		String fieldKey = "";
		for(int i=0; i<list.getLength(); i++) {
			nsIDOMHTMLInputElement element = (nsIDOMHTMLInputElement)list.item(i).queryInterface(nsIDOMHTMLInputElement.NS_IDOMHTMLINPUTELEMENT_IID); 
			String nodeClass = element.getAttribute("class");
			boolean checked = element.getChecked();
			if(nodeClass==null) {
				continue;
			}
			if(nodeClass.equals("sphere") && checked) {
				spheresList.add(element.getAttribute("value"));
			} else if(nodeClass.equals("contact") && checked) {
				contactsList.add(element.getAttribute("value"));
			} else if(nodeClass.equals("type") && checked) {
				typesList.add(element.getAttribute("value"));
			} else if(nodeClass.equals("field") && checked) {
				fieldsList.add(element.getAttribute("value"));
			} else if(nodeClass.equals("radio") && checked) {
				fieldKey = element.getAttribute("value");
			}
		}
		list = document.getElementsByTagName("option");
		for(int i=0; i<list.getLength(); i++) {
			nsIDOMHTMLOptionElement element = (nsIDOMHTMLOptionElement)list.item(i).queryInterface(nsIDOMHTMLOptionElement.NS_IDOMHTMLOPTIONELEMENT_IID);
			if(element.getSelected()) {
				matchIn = element.getAttribute("value");
			}
		}
		return getQuery(stringQuery, typesList, fieldsList, spheresList, contactsList, matchIn, Integer.parseInt(fieldKey));
	}
	
	/**
	 * @param typesList
	 * @param fieldsList
	 * @param spheresList
	 * @param contactsList
	 * @param matchIn
	 * @param parseInt
	 * @return
	 */
	private static String getQuery(String sQuery, List<String> typesList,
			List<String> fieldsList, List<String> spheresList,
			List<String> contactsList, String matchIn, int fieldkey) {
		String constructedString = "";
		fieldsList.add("contact");
		fieldsList.add("keywords");
		for (int i = 0; i < fieldsList.size(); i++) {
			String fieldQuery = getFieldQuery(sQuery, fieldkey);
			constructedString += (matchIn.equals("all") ? (i > 3 ? " "
					: " +") : " ")
					+ fieldsList.get(i) + ":(" + fieldQuery + ")";
		}
		
		constructedString = "+(" + constructedString + ")";

		String typeQuery = getTypeQuery(typesList);
		constructedString = (typeQuery.length() > 0) ? 
				( constructedString + " +(" + typeQuery + ")")
				: (constructedString);

		String spheresQuery = getSpheresQuery(spheresList);
		constructedString = (spheresQuery.length() > 0) ? (
				constructedString + " +(" + spheresQuery + ")")
				: (constructedString);

		String giversQuery = getGiversQuery(contactsList);
		constructedString = (giversQuery.length() > 0) ? (
				constructedString + " +(" + giversQuery + ")")
				: (constructedString);

		return constructedString;
	}

	private static String getFieldQuery(final String sQuery, final int fieldkey) {
		String fieldQuery = "";
		switch (fieldkey) {
		case 1:
			fieldQuery += "\"" + sQuery + "\"";
			break;
		case 2:
			String[] words = sQuery.split(" ");
			for (int i = 0; i < words.length; i++) {
				fieldQuery += "+" + words[i]
						+ ((i == words.length - 1) ? "" : " ");
			}
			break;
		default:
			fieldQuery += sQuery;
			break;
		}
		return fieldQuery;
	}
	
	private static String getTypeQuery(final List<String> types) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("type:(");
		for(String type : types) {
			queryBuffer.append(" ||" + type);
		}
		queryBuffer.append(")");
		return queryBuffer.toString();
	}
	
	private static String getSpheresQuery(final List<String> spheres) {
		if(spheres.size()==SupraSphereFrame.INSTANCE.client.getVerifyAuth().getAvailableSpheres().size()) {
			return "";
		}
		String spheresQuery = "";
		if(spheres.size()>0) {
			spheresQuery = "sphere_id:(";
			for (String sphere : spheres) {
				spheresQuery += " ||" + sphere;
			}
			spheresQuery += ")";
		}
		return spheresQuery;
	}
	
	private static String getGiversQuery(final List<String> givers) {
		if(givers.size()==SupraSphereFrame.INSTANCE.client.getVerifyAuth().getAvailablePrivateSpheres().size()) {
			return "";
		}
		String giversQuery = "";
		if(givers.size()>0) {
			giversQuery = "giver:(";
			for (String giver : givers) {
				giversQuery += " ||" + giver;
			}
			giversQuery += ")";
		}
		return giversQuery;
	}
}
