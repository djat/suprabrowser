/**
 * 
 */
package ss.framework.install;


import ss.common.FolderUtils;
import ss.framework.entities.xmlentities.XmlEntityObject;


/**
 *
 */
public class RootInstallEntry extends AbstractInstallEntry {

	/**
	 * Create RootInstallEntry object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static RootInstallEntry wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, RootInstallEntry.class);
	}

	/**
	 * Create RootInstallEntry object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static RootInstallEntry wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, RootInstallEntry.class);
	}	
	
	private String localBase;
	
	/**
	 * 
	 */
	public RootInstallEntry() {
		this( FolderUtils.getApplicationFolder() );
	}
	
	

	/**
	 * @param localBase
	 */
	public RootInstallEntry(final String localBase) {
		super();
		this.localBase = localBase;
	}

	public InstallEntryType getType() {
		return InstallEntryType.FOLDER;
	}

	/**
	 * 
	 */
	public String getLocalBase() {
		return this.localBase;
	}

	public boolean hasLocalBase() {
		return this.localBase != null;
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.install.IInstalEntry#getHash()
	 */
	public String getHash() {
		return null;
	}

	/**
	 * @param localBase the localBase to set
	 */
	public void setLocalBase(String localBase) {
		this.localBase = localBase;
	}

	
}
