/**
 * 
 */
package ss.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class FileUtils {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileUtils.class);

	private static final int MAX_TRY_COUNT = 1024;

	public static String loadText(Class owner, String fileName) {
		try {
			StringBuilder sb = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(owner
					.getResourceAsStream(fileName));
			try {
				char[] cbuf = new char[2048];
				for (;;) {
					int count = reader.read(cbuf);
					if (count <= 0) {
						break;
					}
					sb.append(cbuf, 0, count);
				}
			} finally {
				reader.close();
			}
			return sb.toString();
		} catch (IOException ex) {
			throw new RuntimeException("Cant read", ex);
		}
	}

	public static void deleteFolder(String folder) {
		if (folder == null) {
			return;
		}
		deleteFolder(new File(folder));
	}

	public static void deleteFolder(File dir) {
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				deleteFolder(f);
			}
		}
		dir.delete();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static File createTempFolder(String prefix, String suffix)
			throws IOException {
		for (int n = 0; n < MAX_TRY_COUNT; ++n) {
			File file = File.createTempFile(prefix, suffix);
			file.delete();
			if (file.mkdir()) {
				return file;
			}
		}
		throw new RuntimeException("Can't create temp folder");
	}

	/**
	 * @param string
	 * @return
	 * @throws IOException
	 */
	public static File createTempFolder(String prefix) throws IOException {
		return createTempFolder(prefix, null);
	}

	public static boolean ensureFolerExists(String folder) {
		if (folder == null) {
			throw new ArgumentNullPointerException("folder");
		}
		final File file = new File(folder);
		if (file.exists()) {
			return file.isDirectory();
		} else {
			return file.mkdirs();
		}
	}

	public static String getCanonicalPath(String path) {
		if (path == null) {
			return null;
		}
		if (path.length() == 0) {
			return path;
		}
		return getCanonicalPath(new File(path));
	}

	

	/**
	 */
	public static List<String> listAll(String path) {
		if (path == null || path.length() == 0) {
			return new ArrayList<String>();
		} else {
			return listAll(new File(path));
		}
	}

	/**
	 * @param path
	 * @return
	 */
	private static List<String> listAll(File file) {
		final List<String> result = new ArrayList<String>();
		if (file.exists()) {
			result.add(file.getAbsolutePath());
			final File[] listFiles = file.listFiles();
			if (listFiles != null) {
				for (File child : listFiles) {
					result.addAll(listAll(child));
				}
			}
		}
		return result;
	}

	/**
	 * @param filePath
	 */
	public static boolean createFile(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			logger.error("Can't create file. File path is empty");
			return false;
		} else {
			File file = new File(filePath);
			try {
				return file.createNewFile();
			} catch (IOException ex) {
				logger.error("Can't create file " + filePath, ex);
				return false;
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public static String getCanonicalPath(File file) {
		if (file == null) {
			return null;
		}
		try {
			return file.getCanonicalPath();
		} catch (IOException ex) {
			logger.error("Can't normalize path for: " + file, ex);
			return file.getAbsolutePath();
		}
	}

	/**
	 * @param file
	 */
	public static boolean ensureParentFolderExists(File file) {
		final File parent = file.getParentFile();
		if (parent != null) {
			return ensureFolerExists(parent.getAbsolutePath());
		} else {
			return true;
		}
	}

	/**
	 * @param blankClientFolder
	 * @return
	 */
	public static boolean isFolderExist(String folder) {
		if (folder == null || folder.length() == 0) {
			return false;
		}
		File file = new File(folder);
		return file.isDirectory() && file.exists();
	}

	/**
	 * @param decriptionFileName
	 * @return
	 */
	public static boolean isFileExist(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return false;
		}
		File file = new File(fileName);
		return file.exists() && file.isFile();
	}

	/**
	 */
	public static void replace(File source, File destination)
			throws IOException {
		if (destination == null) {
			throw new ArgumentNullPointerException("destination");
		}
		if (source == null) {
			throw new ArgumentNullPointerException("source");
		}
		if (!source.exists() || !source.isFile()) {
			throw new FileNotFoundException( "Can't find source file " + source.getAbsolutePath() );
		}
		destination.delete();
		if ( destination.exists() ) {
			throw new IOException( "Can't delete destination file " + destination );
		}
		if (!source.renameTo(destination)) {
			destination.createNewFile();
			final byte[] buffer = new byte[8192];
			final FileInputStream in = new FileInputStream(source);
			try {
					final FileOutputStream out = new FileOutputStream(destination);
				try {
					int bytesReadCount;
					do {
						bytesReadCount = in.read(buffer);
						if ( bytesReadCount > 0 ) {
							out.write(buffer, 0, bytesReadCount);
						}
					} while (bytesReadCount >= 0);
				} finally {
					try {
						out.close();
					} catch (IOException ex) {
						logger.error("Can't close file " + destination, ex);
					}
				}
			} finally {
				try {
					in.close();
				} catch (IOException ex) {
					logger.error("Can't close file " + source, ex);
				}
			}
		}

	}

	/**
	 * 
	 */
	public static URI toUri( String localFilePath) {
		File file = new File( getCanonicalPath( localFilePath ) );
		return file.toURI();
	}
	
	private static final int BYTES_TO_READ = 255;
	
	private static final double PROBABILITY = 0.2;

	public static boolean isBinary( final File f ) {
		FileInputStream in = null;
		try {
			in = new FileInputStream( f );
			byte [] cc = new byte[BYTES_TO_READ];
			int read = in.read(cc,0,BYTES_TO_READ);

			if ( read <= 0 ) {
				return false;
			}
			
			double prob_bin=0;

			for(int i=0; i < cc.length; i++){
				int j = (int)cc[i];
				if( (j<32 || j>127) && ( j != 9) && (j != 10) && (j != 13) ){
					prob_bin++;
				}
			}

			double pb = prob_bin / read;
			if(pb > PROBABILITY){
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			logger.error( "Error in determing isBinary file for file: " + f.getName(),e );
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				logger.error("Can't close file stream for file: " + f.getName(), ex);
			}
		}
		return false;
	}
}
