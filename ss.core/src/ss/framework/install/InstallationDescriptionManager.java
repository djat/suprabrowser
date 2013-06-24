package ss.framework.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.ArgumentNullPointerException;
import ss.common.InstallUtils;
import ss.common.PathUtils;
import ss.common.XmlDocumentUtils;

public class InstallationDescriptionManager {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InstallationDescriptionManager.class);

	public final static String INSTALLATION_DESCRIPTION_FILE_NAME = "instdesc.xml";

	/**
	 * Singleton instance
	 */
	public final static InstallationDescriptionManager INSTANCE = new InstallationDescriptionManager();

	private InstallationDescriptionManager() {
	}

	public InstallationDescription safeLoad(InputStream stream) {
		if (stream != null) {
			try {
				return load(stream);
			} catch (DocumentException ex) {
				logger.error("Can't load installation description", ex);
			}
		} else {
			logger.warn("Input stream is null");
		}
		return createBlank();
	}

	public InstallationDescription load(InputStream stream)
			throws DocumentException {
		if (stream == null) {
			throw new ArgumentNullPointerException("stream");
		}
		final Document document = XmlDocumentUtils.load(stream);
		if (document == null) {
			return createBlank();
		}
		final InstallationDescription description = InstallationDescription
				.wrap(document);

		return description;
	}

	/**
	 * @return
	 */
	public InstallationDescription createBlank() {
		RootInstallEntry entry = InstallUtils.createRootInstallEntry();
		final InstallationDescription blank = new InstallationDescription(entry);
		final String applicationVersion = InstallUtils.getApplicationVersion();
		if (applicationVersion != null) {
			final QualifiedVersion qualifiedApplicationVersion = QualifiedVersion
					.safeParse(applicationVersion);
			if (!qualifiedApplicationVersion.toString().equals(
					applicationVersion)) {
				logger
						.error("Qualified version and version are different. Qaulified "
								+ qualifiedApplicationVersion
								+ ", version "
								+ applicationVersion);
			}
			blank.setApplicationVersion(qualifiedApplicationVersion);
			blank.verifyAndFixOsName();
		} else {
			final QualifiedVersion qualifiedApplicationVersion = new QualifiedVersion(
					OperationSystemName.getFromSystem());
			logger.error("Can't get application version. Use blank version "
					+ qualifiedApplicationVersion);
			blank.setApplicationVersion(qualifiedApplicationVersion);
		}
		return blank;
	}

	/**
	 * @param applicationFolder
	 * @throws CantLoadInstallationDescriptionException
	 */
	public InstallationDescription loadFromApplicationFolder(
			String applicationFolder)
			throws CantLoadInstallationDescriptionException {
		final File descriptionFile = getDecriptionFileName(applicationFolder);
		return load(descriptionFile);
	}

	public boolean hasInstallationDescription(String applicationFolder) {
		final File decriptionFile = getDecriptionFileName(applicationFolder);
		return decriptionFile != null && decriptionFile.exists();
	}

	/**
	 * @param applicationFolder
	 * @return
	 */
	private File getDecriptionFileName(String applicationFolder) {
		return getDefaultDescriptionFile(applicationFolder);
	}

	/**
	 * @param descriptionFileName
	 * @return
	 * @throws CantLoadInstallationDescriptionException
	 * @throws DocumentException
	 */
	public InstallationDescription load(final String descriptionFileName)
			throws CantLoadInstallationDescriptionException {
		final File targetFile = new File(descriptionFileName);
		return load(targetFile);
	}

	/**
	 * @param file
	 * @return
	 * @throws CantLoadInstallationDescriptionException
	 */
	public InstallationDescription load(final File file)
			throws CantLoadInstallationDescriptionException {
		if (file == null) {
			throw new ArgumentNullPointerException("file");
		}
		if (file.exists()) {
			final FileInputStream stream;
			try {
				stream = new FileInputStream(file);
			} catch (FileNotFoundException ex) {
				throw new CantLoadInstallationDescriptionException(
						"Installation description not found. " + file, ex);
			}
			try {
				InstallationDescription description = load(stream);
				description.getRootEntry().setLocalBase(file.getParent());
				return description;
			} catch (DocumentException ex) {
				throw new CantLoadInstallationDescriptionException(
						"Installation description is unreadable in " + file, ex);
			} finally {
				try {
					stream.close();
				} catch (IOException ex) {
					logger.error("Can't close file: " + file, ex);
				}
			}
		} else {
			throw new CantLoadInstallationDescriptionException(
					"Installation description not found. " + file);
		}
	}

	/**
	 * @param applicationFolder
	 * @throws CantSaveInstallationDescriptionException
	 */
	public void saveToApplicationFolder(String applicationFolder,
			InstallationDescription installDescription)
			throws CantSaveInstallationDescriptionException {
		save(getDefaultDescriptionFile(applicationFolder), installDescription);
	}

	/**
	 * @param applicationFolder
	 * @return
	 */
	public File getDefaultDescriptionFile(String applicationFolder) {
		return new File(PathUtils.combinePath(applicationFolder,
				INSTALLATION_DESCRIPTION_FILE_NAME));
	}

	public void save(String descriptionFileName,
			InstallationDescription installDescription)
			throws CantSaveInstallationDescriptionException {
		final File file = new File(descriptionFileName);
		save(file, installDescription);
	}

	/**
	 * @param installDescription
	 * @param targetFile
	 * @throws CantSaveInstallationDescriptionException
	 */
	private void save(File targetFile,
			InstallationDescription installDescription)
			throws CantSaveInstallationDescriptionException {
		if (installDescription == null) {
			throw new ArgumentNullPointerException("installDescription");
		}
		installDescription.save(targetFile);
	}

}
