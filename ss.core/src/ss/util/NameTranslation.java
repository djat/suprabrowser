/*
 * Created on Sep 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.domainmodel.ContactStatement;

/**
 * @author david
 * 
 */
public class NameTranslation {

	@SuppressWarnings("unused")
	private static Logger logger = ss.global.SSLogger
			.getLogger(NameTranslation.class);

	public static String returnQueryId( final Document sphereDocument ) {
		String queryId = null;
		if (sphereDocument != null) {
			Element query = sphereDocument.getRootElement().element("query");
			if (query != null) {
				queryId = query.attributeValue("query_id");
			}
		}
		return queryId;
	}

	public static String createContactNameFromContactDoc(
			final Document contactDoc ) {
		return ContactStatement.wrap(contactDoc)
				.getContactNameByFirstAndLastNames();
	}

}
