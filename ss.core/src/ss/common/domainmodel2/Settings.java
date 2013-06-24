/**
 * 
 */
package ss.common.domainmodel2;

import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.DomainValueSet;
import ss.framework.domainmodel2.Record;

/**
 *
 */
public class Settings extends DomainValueSet  {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Settings.class);
	
	/**
	 * 
	 */
	private static final String SETTINGS_XML = "settings_xml";
	
	private String settingsXml;

	/**
	 * @param domainObjectOwner
	 */
	public Settings(DomainObject domainObjectOwner) {
		super(domainObjectOwner);
	}

	/**
	 * @return the settingXml
	 */
	public String getSettingsXml() {
		return this.settingsXml;
	}

	/**
	 * @param settingsXml the settingXml to set
	 */
	public void setSettingsXml(String settingsXml) {
		markDirty();
		if ( logger.isDebugEnabled() ) {
			logger.debug( "updating settings " + settingsXml );
		}
		this.settingsXml = settingsXml;
	}

	

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.DomainValueSet#load(ss.framework.domainmodel2.Record)
	 */
	@Override
	public synchronized void load(Record record) {
		super.load(record);
		this.settingsXml = record.getText( SETTINGS_XML );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.DomainValueSet#save(ss.framework.domainmodel2.Record)
	 */
	@Override
	public synchronized void save(Record record) {
		super.save(record);
		record.setText( SETTINGS_XML, this.settingsXml );
	}
		 
}
