package ss.framework.install.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ss.common.ArgumentNullPointerException;
import ss.common.FileUtils;
import ss.common.FolderUtils;
import ss.framework.install.CantSaveInstallationDescriptionException;
import ss.framework.install.InstallationDescription;
import ss.framework.install.InstallationDescriptionManager;
import ss.framework.install.update.arrangement.InstallerFilesArranger;
import ss.framework.launch.CantLaunchJarException;
import ss.framework.launch.JarLauncherSwtHelper;

public class Update {

	/**
	 * 
	 */
	private static final String UPDATE_ARG = "-update";

	/**
	 * 
	 */
	private static final String SUPRA_LAUNCH_FILE_NAME = "supra_launch.jar";

	/**
	 * 
	 */
	private static final String LOCK_FILE_NAME = "lock.txt";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Update.class);

	private final File downloadBase;

	private final FolderUpdateEntry root;

	private final InstallationDescription resultDescription;

	private final File applicationFolder;

	private FileInputStream installerLock;

	/**
	 * @param downloadBase
	 * @param root
	 * @param description
	 */
	public Update(File downloadBase, FolderUpdateEntry root,
			InstallationDescription resultDescription) {
		super();
		if (downloadBase == null) {
			throw new ArgumentNullPointerException("downloadBase");
		}
		if (root == null) {
			throw new ArgumentNullPointerException("root");
		}
		if (resultDescription == null) {
			throw new ArgumentNullPointerException("resultDescription");
		}
		this.downloadBase = downloadBase;
		this.root = root;
		this.resultDescription = resultDescription;
		this.applicationFolder = new File(FolderUtils.getApplicationFolder());
	}

	public void cleanup() {
		logger.info("Cleaning up " + this.downloadBase);
		if (this.installerLock != null) {
			try {
				this.installerLock.close();
				this.installerLock = null;
			} catch (IOException ex) {
				logger.error("Can't close installer lock", ex);
			}
		}
		FileUtils.deleteFolder(this.downloadBase);
	}

	public FolderUpdateEntry getRoot() {
		return this.root;
	}

	private void downloadAndArrange(IFilesDownloader downloader,
			IFilesArranger arranger) throws CantUpdateApplicationException {
		logger.info("Collecting downloads");
		this.root.collectDownloads(downloader);
		logger.info("Begin download");
		downloader.downloadAll();
		logger.info("Download done");
		this.root.arrangeDownloaded(arranger);
		logger.info("Arrange downloads");
	}

	/**
	 * @return the downloadBase
	 */
	public File getDownloadBase() {
		return this.downloadBase;
	}

	/**
	 * @param downloader
	 * @throws CantUpdateApplicationException
	 * 
	 */
	public UpdateResult perform(IFilesDownloader filesDownloader)
			throws CantUpdateApplicationException {
		final IFilesArranger filesArranger = new InstallerFilesArranger(
				this.applicationFolder, this.downloadBase,
				getUpdateVersion());
		try {
			filesDownloader.initialize(this.resultDescription
					.getApplicationVersionObj());
			downloadAndArrange(filesDownloader, filesArranger);
			addUpdateResultDescriptionToArrangement(filesArranger,
					this.resultDescription);
		} catch (CantDownloadFileException ex) {
			logger.error("Can't download files " + ex.getMessage());
			cleanup();
			throw ex;
		}
		try {
			filesArranger.arrangeAll();
			attemptInstallerLock();
			launchInstaller();
			return new UpdateResult(this.installerLock);
		} catch (CantArrangeFileException ex) {
			logger.error("Can't arrange files " + ex.getMessage());
			cleanup();
			throw ex;
		}
	}

	/**
	 * @return
	 */
	private String getUpdateVersion() {
		return this.resultDescription.getApplicationVersion();
	}

	/**
	 * @param downloadBase
	 * @throws CantUpdateApplicationException
	 */
	private void launchInstaller() throws CantUpdateApplicationException {
		final File installerFile = new File(FolderUtils.getApplicationFolder(),
				SUPRA_LAUNCH_FILE_NAME);
		try {
			JarLauncherSwtHelper.dryLaunchAndLaunch( installerFile,
					UPDATE_ARG,
					this.downloadBase.getAbsolutePath() );
		} catch (CantLaunchJarException ex) {
			throw new CantUpdateApplicationException(
					"Can't launch application installer " + installerFile, ex);
		}
	}

	/**
	 * @param downloadBase
	 * @throws CantUpdateApplicationException
	 */
	private void attemptInstallerLock() throws CantUpdateApplicationException {
		final File fileLock = new File(this.downloadBase, LOCK_FILE_NAME);
		FileUtils.ensureParentFolderExists(fileLock);
		try {
			fileLock.createNewFile();
			this.installerLock = new FileInputStream(fileLock);
		} catch (IOException ex) {
			throw new CantUpdateApplicationException("Can't create lock file "
					+ fileLock, ex);
		}
	}

	/**
	 * @param filesArranger
	 * @param downloadBase
	 * @param resultDescription
	 * @throws CantUpdateApplicationException
	 */
	private void addUpdateResultDescriptionToArrangement(
			final IFilesArranger filesArranger,
			InstallationDescription resultDescription)
			throws CantUpdateApplicationException {
		final File updateResultDescriptionFile = new File(
				this.downloadBase,
				InstallationDescriptionManager.INSTALLATION_DESCRIPTION_FILE_NAME);
		FileUtils.ensureParentFolderExists(updateResultDescriptionFile);
		try {
			resultDescription.save(updateResultDescriptionFile);
		} catch (CantSaveInstallationDescriptionException ex) {
			throw new CantUpdateApplicationException(
					"Can't save result description to file "
							+ updateResultDescriptionFile, ex);
		}
		final File applicationDescriptionFile = InstallationDescriptionManager.INSTANCE
				.getDefaultDescriptionFile(this.applicationFolder
						.getAbsolutePath());
		filesArranger.addArrangement(applicationDescriptionFile,
				updateResultDescriptionFile);
	}

}
