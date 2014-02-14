/**
 * 
 */
package ss.common.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

import ss.common.FolderUtils;
import ss.common.PathUtils;
import ss.global.SSLogger;
import ss.util.InitializedReference;

/**
 * @author dankosedin
 * 
 */
public class UnPackTask implements Runnable {
	/**
	 * 
	 */
	private static final String LIB_FOLDER = "lib";

	private static Logger logger = SSLogger.getLogger(UnPackTask.class);

	/**
	 * 
	 */
	private String tempFolder;

	private String jar;

	private InitializedReference<String> result;

	/**
	 * @param tempFolder
	 */
	public UnPackTask(String jar, String tempFolder,
			InitializedReference<String> result) {
		this.tempFolder = tempFolder;
		this.jar = jar;
		this.result = result;
	}

	public void run() {
		String applicationFolder = FolderUtils.getApplicationFolder();
		// TODO replace by find via class path
		String jar = PathUtils.combinePath(applicationFolder, LIB_FOLDER,
			this.jar);
		boolean success = false;
		if (this.tempFolder != null) {
			try {
				success = unpack(jar, this.tempFolder);
			} catch (IOException ex) {
				logger.error("IO Exeption while unpacking jar", ex);
			}
			setProperties(success, this.tempFolder);
		}
		if (!success) {
			try {
				success = unpack(jar, applicationFolder);
			} catch (IOException ex) {
				logger.error("IO Exeption while unpacking jar", ex);
			}
			setProperties(success, applicationFolder);
		}
		if (!success) {
			try {
				success = unpack(jar, FolderUtils.getStartUpBase());
			} catch (IOException ex) {
				logger.error("IO Exeption while unpacking jar", ex);
			}
			setProperties(success, FolderUtils.getStartUpBase());
		}
		if (!success) {
			setProperties(success, null);
		}
	}

	protected void setProperties(boolean success, String tempFolder) {
		if ((success) || (tempFolder == null)) {
			this.result.initialize((tempFolder == null) ? null : tempFolder
					+ File.separator);
		}
	}

	private static boolean unpack(String sJar, String sFolder)
			throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Unpacking jar " + sJar + " to " + sFolder);
		}
		File folder = new File(sFolder);
		if (!folder.exists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("IO Exeption while unpacking jar",
					new IOException("Folder " + sFolder + " does not exists"));
			}
			return false;
		}
		if (!folder.canRead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("IO Exeption while unpacking jar",
					new IOException("Folder " + sFolder
							+ " could not be readed"));
			}
			return false;
		}
		if (!folder.canWrite()) {
			if (logger.isDebugEnabled()) {
				logger.debug("IO Exeption while unpacking jar",
					new IOException("Folder " + sFolder
							+ " could not be writed"));
			}
			return false;
		}
		File jar = new File(sJar);
		if (!jar.exists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("IO Exeption while unpacking jar",
					new IOException("Jar file " + sJar + " does not exists"));
			}
			return false;
		}
		if (!jar.canRead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("IO Exeption while unpacking jar",
						new IOException("Jar file " + sJar
								+ " could not be readed"));
			}
			return false;
		}
		final JarInputStream jis = new JarInputStream(new FileInputStream(jar));
		try {
			JarEntry entry = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Unpacking jar started");
			}
			while ((entry = jis.getNextJarEntry()) != null) {
				String path = sFolder + File.separator + entry.getName();
				if (logger.isDebugEnabled()) {
					logger.debug("Processing entry " + entry.getName());
					logger.debug("Unpacking to " + path);
				}
				if (entry.isDirectory()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Entry is a folder");
					}
					File dir = new File(path);
					dir.mkdirs();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Entry is a file");
					}
					final FileOutputStream fous = new FileOutputStream(path);
					try {
						byte[] buffer = new byte[10240];
						for (int i = jis.read(buffer); i > -1; i = jis.read(buffer)) {
							fous.write(buffer, 0, i);
						}
						fous.flush();
					} finally {
						fous.close();
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Processing entry " + entry.getName()
							+ " finished");
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Unpacking jar finished");
			}
		} finally {
			jis.close();
		}
		return true;
	}

}