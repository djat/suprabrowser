package ss.framework.install;

import java.io.File;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.io.checksum.CantCreateCheckSumException;
import ss.framework.io.checksum.CheckSumFactory;

public class InstallEntry extends AbstractInstallEntry {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InstallEntry.class);
	
	private final ISimpleEntityProperty type = super
			.createAttributeProperty("@type");
	
	private final ISimpleEntityProperty version = super
			.createAttributeProperty("@version");

	private final ISimpleEntityProperty size = super
			.createAttributeProperty("@size");

	private final ISimpleEntityProperty hash = super
			.createAttributeProperty("@hash");
	
	/**
	 * 
	 */
	public InstallEntry() {
		super();
	}

	/**
	 * @param name
	 */
	public InstallEntry(String name) {
		this();
		setName(name);
	}

	/**
	 * @return the hash
	 */
	public final String getHash() {
		return this.hash.getValue();
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public final void setHash(String hash) {
		this.hash.setValue(hash);
	}

	/**
	 * @return the size
	 */
	public final long getSize() {
		return this.size.getLongValue();
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public final void setSize(long size) {
		this.size.setLongValue(size);
	}

	/**
	 * Gets the version
	 */
	public final String getVersion() {
		return this.version.getValue();
	}

	/**
	 * Sets the version
	 */
	public final void setVersion(String value) {
		this.version.setValue(value);
	}


	/**
	 * @return the type
	 */
	public InstallEntryType getType() {
		if ( this.type.isValueDefined() ) {
			return this.type.getEnumValue( InstallEntryType.class );
		}
		else {
			if ( getChildren().getCount() > 0 ) {
				return InstallEntryType.FOLDER;
			}
			else if ( this.version.isValueDefined() || this.size.isValueDefined() || this.hash.isValueDefined() ) {
				return InstallEntryType.FILE; 
			}
			else {
				return InstallEntryType.UNKNOWN;
			}
		}		
	}

	/**
	 * @param type the type to set
	 */
	public void setType(InstallEntryType value) {
		this.type.setEnumValue( value );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof InstallEntry ) {
			InstallEntry other = (InstallEntry) obj;
			final InstallEntryType thisType = this.getType();
			if ( thisType.equals( other.getType() ) ) {
				if ( thisType.equals( InstallEntryType.FILE ) ) {
					return getName().equals( other.getName() ) &&
						getVersion().equals( other.getVersion() ) &&
						getSize() == other.getSize() &&
						getHash().equals( other.getHash() );
				}
			}
		}
		return false;
	}

	
	/**
	 * @return
	 */
	public final Version getVersionObj() {
		return VersionFactory.safeParse( getVersion() );
	}

	/**
	 * @param fileName
	 * @param evaluateHash 
	 */
	public boolean setUpFileAttributes(String fileName, boolean evaluateHash) {
		File file = new File( fileName );
		if ( file.exists() ) {
			setType( InstallEntryType.FILE );
			setVersion( null );
			if ( evaluateHash ) {
				try {
					setHash( CheckSumFactory.INSTANCE.createFileChecksum(fileName) );
				} catch (CantCreateCheckSumException ex) {
					logger.error( "Can't compute check sum for: " + fileName, ex );
				}
			}
			setSize( file.length() );
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return
	 */
	public boolean hasHash() {
		final String hash = getHash();
		return hash != null && hash.length() > 0 ;
	}
}