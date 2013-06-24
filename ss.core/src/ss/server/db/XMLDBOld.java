package ss.server.db;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.refactor.supraspheredoc.old.Utils;
import ss.server.domain.service.SupraSphereProvider;


public class XMLDBOld extends XMLDB {

	private final XMLDB xmldb;
	
	/**
	 * @param xmldb
	 */
	private XMLDBOld(XMLDB xmldb) {
		this.xmldb = xmldb;
	}

	/**
	 * @param xmldb
	 * @return
	 */
	public static XMLDBOld get(XMLDB xmldb) {
		return new XMLDBOld(xmldb);
	}

	/**
	 * @return
	 */
	public Document getSupraSphereDocument() throws DocumentException {
		return Utils.getUtils(this.xmldb).getSupraSphereDocument();
	}

}
