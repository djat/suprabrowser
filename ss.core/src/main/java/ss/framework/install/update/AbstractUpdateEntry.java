/**
 * 
 */
package ss.framework.install.update;

import java.io.File;


import ss.common.ArgumentNullPointerException;
import ss.common.PathUtils;
import ss.framework.install.IInstalEntry;

/**
 *
 */
public abstract class AbstractUpdateEntry {

	private final File downloadDesctination;
	
	private final File installDestination;
	
	protected final String remotePath;
	
	protected final IInstalEntry description;

	/**
	 * @param updateRoot
	 * @param entry
	 */
	public AbstractUpdateEntry( FolderUpdateEntry parent, IInstalEntry description) {
		this( parent.getDownloadDesctination().getAbsolutePath(),
			  parent.getInstallDestination().getAbsolutePath(),
			  description.getName(), 
			  PathUtils.combinePath( PathUtils.UNIFIED_SLASH, parent.remotePath, description.getName() ),
			  description );
	}
	
	/**
	 */
	public AbstractUpdateEntry( String downloadBase,
								String installBase,
								String name,
								String remotePath, IInstalEntry description) {
		super();
		if (downloadBase == null) {
			throw new ArgumentNullPointerException("downloadDesctination");
		}
		if (installBase == null) {
			throw new ArgumentNullPointerException("installDesctination");
		}
		if (name == null) {
			throw new ArgumentNullPointerException("name");
		}
		if (remotePath == null) {
			throw new ArgumentNullPointerException("remotePath");
		}
		if (description == null) {
			throw new ArgumentNullPointerException("description");
		}
		this.remotePath = remotePath;
		this.description = description;		
		this.downloadDesctination = new File( PathUtils.combinePath( downloadBase, name ) );
		this.installDestination = new File( PathUtils.combinePath( installBase, name ) );
	}

	public abstract void collectDownloads( IFilesDownloader downloader );
	
	/**
	 * 
	 */
	public abstract void arrangeDownloaded(IFilesArranger arranger);

	/**
	 * @return
	 */
	public final File getDownloadDesctination() {
		return this.downloadDesctination;
	}
	
	/**
	 * @return
	 */
	public final String getDownloadDesctinationAsString() {
		return this.downloadDesctination.getAbsolutePath();
	}

	/**
	 * @return
	 */
	public final File getInstallDestination() {
		return this.installDestination;
	}
	
	/**
	 * @return
	 */
	public final String getInstallDestinationAsString() {
		return this.installDestination.getAbsolutePath();
	}
}
