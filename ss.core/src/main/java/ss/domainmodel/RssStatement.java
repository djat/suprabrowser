package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlEntityObject;

public class RssStatement extends Statement {
	
	public RssStatement() {
		super("email");
	}
	
	/**
	  Create rss object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static RssStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, RssStatement.class);
	}

	/**
	 * Create rss object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static RssStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, RssStatement.class);
	}	
	
}
