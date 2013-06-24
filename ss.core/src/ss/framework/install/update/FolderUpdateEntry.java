/**
 * 
 */
package ss.framework.install.update;

import java.util.ArrayList;
import java.util.List;

import ss.common.ArgumentNullPointerException;
import ss.framework.install.IInstalEntry;

/**
 *
 */
public class FolderUpdateEntry extends AbstractUpdateEntry {

	private final List<AbstractUpdateEntry> children = new ArrayList<AbstractUpdateEntry>();
	
	/**
	 * @param downloadBase
	 * @param installBase
	 * @param name
	 * @param remotePath
	 * @param description
	 */
	public FolderUpdateEntry(String downloadBase, String installBase, String name, String remotePath, IInstalEntry description) {
		super(downloadBase, installBase, name, remotePath, description);
	}

	/**
	 * @param parent
	 * @param description
	 */
	public FolderUpdateEntry(FolderUpdateEntry parent, IInstalEntry description) {
		super(parent, description);
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.AbstractUpdateEntry#download(ss.framework.install.update.IFilesDownloader)
	 */
	@Override
	public void collectDownloads(IFilesDownloader downloader) {
		for (AbstractUpdateEntry child : this.children )  {
			child.collectDownloads(downloader);
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.AbstractUpdateEntry#arrangeDownloaded(ss.framework.install.update.IDownloadArranger)
	 */
	@Override
	public void arrangeDownloaded(IFilesArranger arranger) {
		for (AbstractUpdateEntry child : this.children )  {
			child.arrangeDownloaded( arranger );
		}	
	}

	/**
	 * @param entry
	 */
	public void addChild(AbstractUpdateEntry entry) {
		if (entry == null) {
			throw new ArgumentNullPointerException("entry");
		}
		this.children.add(entry);
	}
	
	
}
