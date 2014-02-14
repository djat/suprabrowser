package ss.client.ui;

import java.io.File;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.common.XmlDocumentUtils;
import ss.util.LocationUtils;

public class ProfileManager {
	
	String fsep = System.getProperty("file.separator");
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProfileManager.class);
	
	private SupraSphereFrame sF = null;
		
	public ProfileManager(SupraSphereFrame sF) {
		this.sF = sF;
		
		
	}
	
	public void saveProfile(Hashtable session, String profileName) {
		try {
			final File last = LocationUtils.getLastLoginFile();
			final Document doc = XmlDocumentUtils.loadOrCreateEmpty(last);
			if (logger.isDebugEnabled()) {
				logger.debug("saveProfile source document: " + doc.asXML());
			}
			final String xpath = "//login_info/prev_logins/login[@real_name=\""
					+ (String) session.get("real_name") + "\"]";
			Element login = null;
			try {
				login = (Element) doc.selectObject(xpath);
			} catch (Exception e) {
			}
			String profile = null;
			boolean saveNewProfile = false;
			if (login != null) {
				profile = login.attributeValue("profile_id");
			}
			if (profile != null) {
				if (!profile.equals((String) session.get("profile_id"))) {
					saveNewProfile = true;
				}
			} else {
				saveNewProfile = true;
			}
			if (saveNewProfile) {
				Hashtable machineLogin = this.sF.client.createNewProfile(session,
						profileName);
				String machinePassphrase = (String) machineLogin
						.get("machinePassphrase");
				String profileId = (String) machineLogin.get("profileId");
				logger.info("Profileid: " + profileId);
				String moment = (String) machineLogin.get("moment");
				logger.info("NEW MACHINE PASSPHRASE: " + machinePassphrase);
				final Element elem = DocumentHelper.createElement("login");
				elem.addAttribute("moment", moment).addAttribute("username",
						(String) session.get("username")).addAttribute(
						"real_name", (String) session.get("real_name"))
						.addAttribute("passphrase", machinePassphrase)
						.addAttribute("profile_id", profileId);
				final boolean found = saveLogintoProfile(doc, profileId, elem);
				if (!found) {
					logger.info("will just plain out add it");
					doc.getRootElement().element("prev_logins").add(elem);
				} else {
					logger.info("it was found??");
				}
				XmlDocumentUtils.save(last, doc);
			}
		} catch (Exception ex) {
			logger.error( "Can't save profile", ex);			
		}		
	}

	/**
	 * @param doc
	 * @param profileId
	 * @param elem
	 * @return
	 */
	private boolean saveLogintoProfile(final Document doc, String profileId, Element elem) {
		if ( doc.getRootElement() == null || 
			 doc.getRootElement().element("prev_logins") == null ) {
			return false;
		}
		for( Object itemObj : doc.getRootElement().element("prev_logins").elements() )
		{
			final Element one = (Element) itemObj;
			String oneProfile = one.attributeValue("profile_id");
			if (oneProfile != null) {
				if (oneProfile.equals(profileId)) {
					doc.getRootElement().element("prev_logins").remove(
							one);
					doc.getRootElement().element("prev_logins").add(
							elem);
					return true;
				}
			}
		}
		return false;
	}
	
	

}
