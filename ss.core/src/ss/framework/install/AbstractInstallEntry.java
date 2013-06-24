package ss.framework.install;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public abstract class AbstractInstallEntry extends XmlEntityObject implements IInstalEntry {

	public static final String ROOT_ELEMENT_NAME = "entry";
	
	private final ISimpleEntityProperty name = super
			.createAttributeProperty("@name");

	private final InstallEntryCollection children = super.bindListProperty( new InstallEntryCollection() );

	/**
	 * 
	 */
	public AbstractInstallEntry() {
		super( ROOT_ELEMENT_NAME );
	}

	/**
	 * Gets the name
	 */
	public final String getName() {
		return this.name.getValue();
	}

	/**
	 * Sets the name
	 */
	public final void setName(String value) {
		this.name.setValue(value);
	};
	
	public final InstallEntryCollection getChildren() {
		return this.children;
	}
}
