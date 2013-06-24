/**
 * 
 */
package ss.framework.install.update;

import java.io.File;
import ss.framework.install.IInstalEntry;

/**
 * 
 */
public class FileUpdateEntry extends AbstractUpdateEntry {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileUpdateEntry.class);

	/**
	 * @param parent
	 * @param description
	 */
	public FileUpdateEntry(FolderUpdateEntry parent, IInstalEntry description) {
		super(parent, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.install.update.AbstractUpdateEntry#collectDownloads(ss.framework.install.update.IFilesDownloader)
	 */
	@Override
	public void collectDownloads(IFilesDownloader downloader) {
		downloader.addToQueue(getDownloadDesctination().getAbsolutePath(),
				this.remotePath, this.description.getHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.install.update.AbstractUpdateEntry#arrangeDownloaded(ss.framework.install.update.IDownloadArranger)
	 */
	@Override
	public void arrangeDownloaded(IFilesArranger arranger) {
		final File downloadedFile = getDownloadDesctination();
		final File targetFile = getInstallDestination();
		arranger.addArrangement( targetFile, downloadedFile );		
	}

}
