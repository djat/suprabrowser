/**
 * 
 */
package ss.framework.install;

import java.io.File;

import org.dom4j.DocumentException;

import ss.common.ArgumentNullPointerException;
import ss.common.XmlDocumentUtils;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * 
 */
public class InstallationDescription extends XmlEntityObject {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InstallationDescription.class);
	
	/**
	 * 
	 */
	private static final String ROOT_ELEMENT_NAME = "installation_description";

	/**
	 * Create InstallationDescription object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static InstallationDescription wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, InstallationDescription.class);
	}

	private final ISimpleEntityProperty applicationName = super
			.createAttributeProperty("application_name/@value");

	private final ISimpleEntityProperty applicationVersion = super
			.createAttributeProperty("application_version/@value");

	private final IComplexEntityProperty<RootInstallEntry> rootEntry = super
			.createComplexProperty(RootInstallEntry.ROOT_ELEMENT_NAME,
					RootInstallEntry.class);

	/**
	 * Construct InstallationDescription
	 */
	public InstallationDescription() {
		super(ROOT_ELEMENT_NAME);
	}

	/**
	 * @param entry
	 */
	public InstallationDescription(RootInstallEntry entry) {
		this();
		this.rootEntry.setValue(entry);
	}

	/**
	 * Gets the applicationVersion
	 */
	public final String getApplicationVersion() {
		return this.applicationVersion.getValue();
	}

	/**
	 * Sets the applicationVersion
	 */
	public final void setApplicationVersion(String value) {
		this.applicationVersion.setValue(value);
	}

	/**
	 * Gets the application name
	 */
	public final String getApplicationName() {
		return this.applicationName.getValue();
	}

	/**
	 * Sets the application name
	 */
	public final void setApplicationName(String value) {
		this.applicationName.setValue(value);
	}

	/**
	 * @return the applicationVersion
	 */
	public QualifiedVersion getApplicationVersionObj() {
		return QualifiedVersion.safeParse(getApplicationVersion());
	}

	/**
	 * @return the root
	 */
	public RootInstallEntry getRootEntry() {
		return this.rootEntry.getValue();
	}

	/**
	 * @param version
	 */
	public void setApplicationVersion(QualifiedVersion version) {
		setApplicationVersion(version.toString());
	}

	public void save( File file ) throws CantSaveInstallationDescriptionException {
		if (file == null) {
			throw new ArgumentNullPointerException("file");
		}
		try {
			XmlDocumentUtils.save(file, getDocumentCopy() );
		} catch (DocumentException ex) {
			throw new CantSaveInstallationDescriptionException( "Can't save installation description to " + file.getAbsolutePath(), ex );
		}
	}
	
	public boolean verifyAndFixOsName() {
		final QualifiedVersion version = getApplicationVersionObj();
		final OperationSystemName os = version.getOperationSystem();
		final OperationSystemName systemOs = OperationSystemName.getFromSystem();
		if ( !os.equals( systemOs ) ) {
			logger.error( "Iconsisten OS name set description os to system os. Description os: " + os + ", System os " + systemOs );
			version.setOperationSystem( systemOs );
			setApplicationVersion( version );
			return false;
		}
		else {
			return true;
		}
	}
}
