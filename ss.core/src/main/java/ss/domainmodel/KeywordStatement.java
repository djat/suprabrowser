package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class KeywordStatement extends Statement {
	
	private final ISimpleEntityProperty status = super
		.createAttributeProperty( "status/@value" );
	
	private final ISimpleEntityProperty uniqueId = super
		.createAttributeProperty( "unique_id/@value" );
	
	private final ISimpleEntityProperty multiLocSphere = super
		.createAttributeProperty( "multi_loc_sphere/@value" );
	
	private final ISimpleEntityProperty numberOfTags = super
		.createAttributeProperty( "stats/number_of_tags/@value" );
	
	private final ISimpleEntityProperty numberWithThisTag = super
		.createAttributeProperty( "stats/number_with_this_tag/@value" );
	
	public KeywordStatement() {
		super( "email" );
	}
	
	/**
	  Create keyword object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static KeywordStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, KeywordStatement.class);
	}

	/**
	 * Create keyword object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static KeywordStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, KeywordStatement.class);
	}
	
	/**
	 * Gets the keyword status
	 */
	public final String getStatus() {
		return this.status.getValue();
	}

	/**
	 * Sets the keyword status
	 */
	public final void setStatus(String value) {
		this.status.setValue(value);
	}
	
	/**
	 * Gets the keyword unique id
	 */
	public final String getUniqueId() {
		return this.uniqueId.getValue();
	}

	/**
	 * Sets the keyword unique id
	 */
	public final void setUniqueId(String value) {
		this.uniqueId.setValue(value);
	}
	
	/**
	 * Gets the keyword multi loc sphere
	 */
	public final String getMultiLocSphere() {
		return this.multiLocSphere.getValue();
	}

	/**
	 * Sets the keyword multi loc sphere
	 */
	public final void setMultiLocSphere(String value) {
		this.multiLocSphere.setValue(value);
	}
	
	/**
	 * Gets the keyword number of tags
	 */
	public final String getNumberOfTags() {
		return this.numberOfTags.getValue();
	}

	/**
	 * Sets the keyword number of tags
	 */
	public final void setNumberOfTags(String value) {
		this.numberOfTags.setValue(value);
	}
	
	
	public final String getNumberWithThisTag() {
		return this.numberWithThisTag.getValue();
	}

	public final void setNumberWithThisTag(String value) {
		this.numberWithThisTag.setValue(value);
	}

}
