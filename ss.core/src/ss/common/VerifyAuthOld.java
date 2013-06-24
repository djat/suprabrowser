package ss.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.domainmodel.SphereReference;
import ss.global.SSLogger;
import ss.refactor.Refactoring;

@ss.refactor.Refactoring(classify=ss.refactor.supraspheredoc.SupraSphereRefactor.class)
public class VerifyAuthOld extends VerifyAuth {
	
	private static final Logger logger = SSLogger.getLogger(VerifyAuthOld.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3907698361794483386L;



	/**
	 * @param supraSphereDocument
	 */
	public VerifyAuthOld(Document supraSphereDocument) {
		super(supraSphereDocument);
		// TODO Auto-generated constructor stub
	}

	private Document persona_document = null;
	
	

	/**
	 * Execute xpath to sphereDocument
	 * 
	 * @param xpath
	 *            xpath
	 * @return selected element or null if element is not found
	 */
	public Element selectSuprasphereElement(String xpath) {
		return XmlDocumentUtils
				.selectElementByXPath(this.getSupraSphereDocument(), xpath);
	}
	
	/**
	 * @return
	 */
	public Document getSupraSphereDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public Vector getCreateAssets() {

		Vector result = new Vector();
		try {

			String apath = "//asset_types";

			Element elem = (Element) this.persona_document.selectObject(apath);

			List real = elem.elements();

			for (int i = 0; i < real.size(); i++) {

				Element one = (Element) real.get(i);

				Element create = one.element("create");

				if (create != null) {

					if (create.nodeCount() > 0) {

						result.add(one.getName());

					}

				}

			}

		} catch (ClassCastException cce) {

			// logger.debug("must not be an element");

		}

		return result;

	}

	

	@SuppressWarnings("unchecked")
	public Vector getGenerateAssets() {

		Vector result = new Vector();
		try {

			String apath = "//asset_types";

			Element elem = (Element) this.persona_document.selectObject(apath);

			List real = elem.elements();

			for (int i = 0; i < real.size(); i++) {

				Element one = (Element) real.get(i);

				Element create = one.element("generate");

				if (create != null) {

					if (create.nodeCount() > 0) {

						result.add(one.getName());

					}

				}

			}

		} catch (ClassCastException cce) {

			// logger.debug("must not be an element");

		}

		return result;

	}

	

	@SuppressWarnings("unchecked")
	public Vector getViewAssets() {

		Vector result = new Vector();
		try {

			String apath = "//asset_types";

			Element elem = (Element) this.persona_document.selectObject(apath);

			List real = elem.elements();

			for (int i = 0; i < real.size(); i++) {

				Element one = (Element) real.get(i);

				Element create = one.element("view");

				if (create != null) {

					Element as = create.element("absolute");
					if (as != null) {

						result.add(one.getName());

					}

				}

			}

		} catch (ClassCastException cce) {

			// logger.debug("must not be an element");

		}

		return result;

	}

	

	@SuppressWarnings("unchecked")
	public Hashtable listModel(String apath) {

		Hashtable model_options = new Hashtable();

		try {

			Element elem = (Element) this.persona_document.selectObject(apath);

			List real = elem.elements();

			for (int i = 0; i < real.size(); i++) {

				Element priv = (Element) real.get(i);

				model_options.put(priv.getName(), priv.attributeValue("desc"));

			}

		} catch (ClassCastException cce) {

			// logger.debug("must not be an element");

		}

		return model_options;

	}

	@SuppressWarnings("unchecked")
	public Vector listThreshold(String apath) {

		Vector model_threshold = new Vector();

		try {

			Element elem = (Element) this.persona_document.selectObject(apath);

			List real = elem.elements();

			for (int i = 0; i < real.size(); i++) {

				Element thresh = (Element) real.get(i);

				model_threshold.add(thresh);

			}

		} catch (ClassCastException cce) {

			// logger.debug("must not be an element");

		}

		return model_threshold;

	}

	/**
	 * @param verifyAuth
	 * @return
	 */
	@ss.refactor.Refactoring(classify=ss.refactor.supraspheredoc.SupraSphereRefactor.class, level=Refactoring.Level.POTENTIAL_BUG)
	public static VerifyAuthOld requiredOldVerifyAuth(VerifyAuth verifyAuth) {
		throw new UnsupportedOperationException();
	}

	
	@SuppressWarnings("unchecked")
	public Vector getMembersEnabled(String apath, Document sphereDoc) {
		Vector enabledMembers = new Vector();
		if (sphereDoc != null) {
			try {

				Element elem = (Element) this.getSupraSphereDocument1()
						.selectObject(apath);
				Element member = elem.getParent();
				enabledMembers.add(member.attributeValue("contact_name"));

			} catch (ClassCastException npe) {
				logger.error(npe.getMessage(), npe);
				Vector results = new Vector((List) this.getSupraSphereDocument1()
						.selectObject(apath));

				for (int i = 0; i < results.size(); i++) {

					Element elem = (Element) results.get(i);
					Element member = elem.getParent();
					enabledMembers.add(member.attributeValue("contact_name"));
				}
			}
		}
		logger.debug("Enabled size returning: " + enabledMembers.size());
		return enabledMembers;
	}

	/**
	 * @return
	 */
	private Document getSupraSphereDocument1() {
		// TODO Auto-generated method stub
		return null;

	}
	

	@SuppressWarnings("unchecked")
	public Vector getMembersEnabled(String apath) {
		Vector enabledMembers = new Vector();
		try {

			Element elem = (Element) this.getSupraSphereDocument1().selectObject(apath);
			Element member = elem.getParent();
			enabledMembers.add(member.attributeValue("contact_name"));

		} catch (ClassCastException npe) {
			logger.error(npe.getMessage(), npe);
			Vector results = new Vector((List) this.getSupraSphereDocument1()
					.selectObject(apath));

			for (int i = 0; i < results.size(); i++) {

				Element elem = (Element) results.get(i);
				Element member = elem.getParent();
				enabledMembers.add(member.attributeValue("contact_name"));
			}
		}
		logger.info("Enabled size returning: " + enabledMembers.size());
		return enabledMembers;
	}

	/**
	 * @param xpath
	 * @return
	 */
	protected SphereReferenceList getAllSpheresByXPath(final String xpath) {
		if (logger.isDebugEnabled() ) {
			logger.debug( "getAllSpheresByXPath by " + xpath + " in " + XmlDocumentUtils.toPrettyString( getSupraSphereDocument1() ) );			
		}
		List<SphereReference> references = new ArrayList<SphereReference>();
		for (Element elem : XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument1(), xpath)) {
			references.add(SphereReference.wrap(elem));
		}
		return new SphereReferenceList( references );
	}
	

	/**
	 * Returns true if it goes from top/down, false if it goes bottom up
	 */
	public boolean getTreeOrder() {
		boolean isTopDown = false;
		String value = null;
		final String apath = "//suprasphere/ui/tree_order";
		try {
			Element elem = (Element) this.getSupraSphereDocument1().selectObject(apath);

			if (elem == null) {
				logger.debug("null element in getsystemname");
			}
			value = elem.attributeValue("value");
			if (value.equals("top_down")) {
				isTopDown = true;
			}
		} catch (ClassCastException npe) {
			logger.info("class cast exception in getspheretype");
		}
		return isTopDown;
	}
	
	/**
	 * Returns true if the middle chat is there, false otherwise
	 */
	public boolean getMiddleChat() {
		final String apath = "//suprasphere/ui/middle_chat";
		try {
			Element elem = (Element) this.getSupraSphereDocument1().selectObject(apath);
			if (elem == null) {
				logger.info("null element in getsystemname");
			}
			String value = elem.attributeValue("value");
			if (value.equals("true")) {
				return true;
			}
		} catch (ClassCastException npe) {
			logger.info("class cast exception in getspheretype");
		}
		return false;
	}

	public boolean getConfirmReceipt(String sphere_id) {

		boolean result = false;

		// logger.debug("getconfid: "+sphere_id);

		String apath = "//suprasphere/member[@contact_name=\""
				+ getContactName()
				+ "\"]/sphere[@system_name=\"" + sphere_id + "\"]";

		// logger.debug("APATH: "+apath);
		try {
			Element elem = (Element) getSupraSphereDocument1().selectObject(apath);

			if (elem == null) {
				logger.debug("null element in getCONFIRM RECIETIPsphere");
			}

			// logger.debug("NAME:
			// "+elem.attributeValue("default_delivery"));
			String type = elem.attributeValue("default_delivery");

			if (type.equals("confirm_receipt")) {

				result = true;
			}

		} catch (ClassCastException npe) {

			// authentic_pers = new
			// Vector(((ArrayList)doc.selectObject(appath)))

		}

		return result;

	}
	
	@SuppressWarnings("unchecked")
	public Hashtable getBuildOrder(String contact_name) {

		Hashtable result = new Hashtable();

		String apath = "//suprasphere/member[@contact_name=\"" + contact_name
				+ "\"]/sphere[@sphere_type=\"member\"]";

		try {
			Element elem = (Element) getSupraSphereDocument1().selectObject(apath);

			if (elem == null) {
				logger.debug("null element in getavailsphere");
			}

			// logger.debug("NAME: "+elem.attributeValue("name"));
			String order = elem.attributeValue("build_order");

			if (order != null) {
				result.put(elem.attributeValue("display_name"), order);

			}

		} catch (ClassCastException npe) {

			// authentic_pers = new
			// Vector(((ArrayList)doc.selectObject(appath)))

			List real = (ArrayList) getSupraSphereDocument1().selectObject(apath);

			for (int i = 0; i < real.size(); i++) {

				Element one = (Element) real.get(i);

				String order = one.attributeValue("build_order");

				if (order != null) {
					result.put(one.attributeValue("display_name"), order);

				}

			}

		}

		logger.debug("returning result of size: " + result.size());

		return result;

	}
	

	/**
	 * Returns system name by xpath. If no system name was found returns null or
	 * empty string.
	 * 
	 * @param xpath
	 * @return
	 */
	private String selectSystemName(String xpath) {
		Element elem = XmlDocumentUtils.selectElementByXPath(
				this.getSupraSphereDocument1(), xpath);
		return elem != null ? elem.attributeValue("system_name") : null;
	}
}

