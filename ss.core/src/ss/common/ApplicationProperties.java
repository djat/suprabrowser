package ss.common;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class ApplicationProperties extends XmlEntityObject {

	private static final boolean DEFAULT_AUTO_UPDATE = true;
	
	private static ApplicationProperties INSTANCE = null;
	
	public static synchronized ApplicationProperties getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new ApplicationProperties();
			INSTANCE.reload();
		}
		return INSTANCE;
	}
	
	private final ISimpleEntityProperty autoUpdate = super
			.createAttributeProperty("auto-update/@value");


	private ApplicationProperties() {
	}
	/**
	 * Gets the autoUpdate
	 */
	public final boolean getAutoUpdate() {
		return this.autoUpdate.getBooleanValue( DEFAULT_AUTO_UPDATE );
	}

	/**
	 * Sets the autoUpdate
	 */
	public final void setAutoUpdate(boolean value) {
		this.autoUpdate.setBooleanValue( value);
	}
	

	/**
	 * 
	 */
	public synchronized void reload() {
//		String applicationFolder = FolderUtils.getApplicationFolder();
//		URL className = getClass().getResource( "ApplicationProperties.class" );
//		System.out.println( className );
	}
	  
}
