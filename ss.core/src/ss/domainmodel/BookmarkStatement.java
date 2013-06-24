package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class BookmarkStatement extends Statement {
	
	private final ISimpleEntityProperty status = super
		.createAttributeProperty("status/@value");
	
	public BookmarkStatement() {
		super("email");
	}
	
	/**
	  Create bookmark object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static BookmarkStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, BookmarkStatement.class);
	}

	/**
	 * Create bookmark object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static BookmarkStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, BookmarkStatement.class);
	}
	
	/**
	 * Gets the bookmark status
	 */
	public final String getStatus() {
		return this.status.getValue();
	}

	/**
	 * Sets the bookmark status
	 */
	public final void setStatus(String value) {
		this.status.setValue(value);
	}
}
