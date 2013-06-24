/**
 * 
 */
package ss.framework.install.update;

import java.io.File;
import java.io.IOException;

import ss.common.ArgumentNullPointerException;
import ss.common.CompareUtils;
import ss.common.FileUtils;
import ss.common.FolderUtils;
import ss.common.PathUtils;
import ss.framework.install.InstallEntry;
import ss.framework.install.InstallEntryCollection;
import ss.framework.install.InstallEntryType;
import ss.framework.install.InstallationDescription;
import ss.framework.install.RootInstallEntry;

/**
 * 
 */
public class UpdateBuilder {

	/**
	 * 
	 */
	private static final String SS_UPDATE_DATA_FOLDER_NAME = "ss.update.data";
	
	private static final String SS_UPDATE_DATA_BACKUP_FOLDER_NAME = "ss.update.data.backup";

	/**
	 * 
	 */
	private static final String FILES_DOWNLOAD_SUBFOLDER = "files";

	public static final String INSTALLER_TEMP_FOLDER = SS_UPDATE_DATA_FOLDER_NAME;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateBuilder.class);

	private final InstallEntryCollection emptyCollection;

	private final InstallationDescription current;

	private final InstallationDescription target;

	private String installBase;

	private String downloadBase;

	/**
	 * @param current
	 * @param target
	 */
	public UpdateBuilder(InstallationDescription current,
			InstallationDescription target) {
		super();
		this.current = current;
		this.target = target;
		this.emptyCollection = new InstallEntryCollection();
		this.emptyCollection.standalone();
	}

	/**
	 * @return the installBase
	 */
	public String getInstallBase() {
		return this.installBase;
	}

	/**
	 * @param installBase
	 *            the installBase to set
	 */
	public void setInstallBase(String installBase) {
		this.installBase = installBase;
	}

	/**
	 * @return the downloadBase
	 */
	public String getDownloadBase() {
		return this.downloadBase;
	}

	/**
	 * @param downloadBase
	 *            the downloadBase to set
	 */
	public void setDownloadBase(String downloadBase) {
		this.downloadBase = downloadBase;
	}

	/**
	 * @throws CantUpdateApplicationException
	 * 
	 */
	public Update getResult() throws CantUpdateApplicationException {
		if (this.installBase == null) {
			setUpInstallBase();
		}
		if (this.downloadBase == null) {
			setUpDownloadBase();
		}
		final RootInstallEntry targetRoot = this.target.getRootEntry();
		final RootInstallEntry currentRoot = this.current.getRootEntry();
		String downloadFilesFolder = PathUtils.combinePath(this.downloadBase,
				FILES_DOWNLOAD_SUBFOLDER);
		FolderUpdateEntry updateRoot = new FolderUpdateEntry(
				downloadFilesFolder, this.installBase, "", "", targetRoot);
		generateUpdate(updateRoot, targetRoot.getChildren(), currentRoot
				.getChildren());
		return new Update(new File(this.downloadBase), updateRoot, this.target);
	}

	/**
	 * @throws CantUpdateApplicationException
	 */
	private void setUpDownloadBase() throws CantUpdateApplicationException {
		File downloadFolder = tryCreateDownloadBaseInInstallerFolder();
		if (downloadFolder == null) {
			downloadFolder = createTempDownloadFolder();
		}
		this.downloadBase = downloadFolder.getAbsolutePath();
	}

	/**
	 * @return
	 */
	private File tryCreateDownloadBaseInInstallerFolder() {
		final File downloadBaseCandidate = new File(FolderUtils.getApplicationFolder(),
				SS_UPDATE_DATA_FOLDER_NAME);
		if ( downloadBaseCandidate.exists() ) {
			final File backup = new File(FolderUtils.getApplicationFolder(),
					SS_UPDATE_DATA_BACKUP_FOLDER_NAME );
			FileUtils.deleteFolder( backup );
			if ( !downloadBaseCandidate.renameTo( backup ) ) {
				return null;
			}
			FileUtils.deleteFolder( backup );
		}
		downloadBaseCandidate.mkdirs();
		return downloadBaseCandidate;
	}

	/**
	 * @return
	 * @throws CantUpdateApplicationException
	 */
	private static File createTempDownloadFolder()
			throws CantUpdateApplicationException {
		final File downloadFolder;
		try {
			downloadFolder = FileUtils.createTempFolder(INSTALLER_TEMP_FOLDER);
		} catch (IOException ex) {
			throw new CantUpdateApplicationException(
					"Can't create download folder", ex);
		}
		return downloadFolder;
	}

	/**
	 * 
	 */
	private void setUpInstallBase() {
		this.installBase = this.target.getRootEntry().getLocalBase();
	}

	private void generateUpdate(FolderUpdateEntry parent,
			InstallEntryCollection targets, InstallEntryCollection currents) {
		for (InstallEntry target : targets) {
			final InstallEntry current = currents.findEntry(target.getName());
			if (InstallEntryType.FILE.equals(target.getType())) {
				if (newOrCurrentVersion(target, current)
						&& !haveSameFileContent(target, current)) {
					parent.addChild(new FileUpdateEntry(parent, target));
				}
			} else if (InstallEntryType.FOLDER.equals(target.getType())) {
				FolderUpdateEntry updateChild = new FolderUpdateEntry(parent,
						target);
				generateUpdate(updateChild, target.getChildren(),
						current != null ? current.getChildren()
								: this.emptyCollection);
				parent.addChild(updateChild);
			} else if (InstallEntryType.UNKNOWN.equals(target.getType())) {
				logger.error("Unexpected install entry " + target);
			}
		}
	}

	/**
	 * @param other
	 * @return
	 */
	private final boolean newOrCurrentVersion(InstallEntry first,
			InstallEntry second) {
		if (first == null) {
			throw new ArgumentNullPointerException("first");
		}
		if (second == null) {
			return true;
		}
		checkNames(first, second);
		return first.getVersionObj().compareTo(second.getVersionObj()) >= 0;
	}

	/**
	 * @param first
	 * @param second
	 */
	private void checkNames(InstallEntry first, InstallEntry second) {
		if (!first.getName().equals(second.getName())) {
			throw new IllegalArgumentException(
					"first and second have different names" + first.getName()
							+ ", " + second.getName());
		}
	}

	/**
	 * @param other
	 * @return
	 */
	private final boolean haveSameFileContent(InstallEntry first,
			InstallEntry second) {
		if (first == null) {
			throw new ArgumentNullPointerException("first");
		}
		if (second == null) {
			return false;
		}
		checkNames(first, second);
		if (!first.hasHash() || !second.hasHash()) {
			return false;
		}
		return first.getSize() == second.getSize()
				&& CompareUtils.equals(first.getHash(), second.getHash());
	}

}
