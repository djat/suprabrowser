/**
 * 
 */
package ss.common;

import java.io.File;

import org.apache.log4j.Logger;

import ss.common.path.CantCreateClassLocationException;
import ss.common.path.ClassLocation;
import ss.common.path.ClassLocationBuilder;
import ss.global.SSLogger;

/**
 *
 */
public class PathUtils {

	public static final char SLASH = File.separatorChar;

	private static final char SEMICOLON = File.pathSeparatorChar;

	private static final String SEMICOLON_SPLIT = String.valueOf(SEMICOLON);

	public static final String SAME_FOLDER_DOT = ".";

	public static final char UNIFIED_SLASH = '/';

	public static final String UNIFIED_SLASH_FOR_SPLIT = String.valueOf('/');
	
	private static final char EXTENSION_DOT = '.';
	
	private final static String baseDir = System.getProperty("user.dir");

	public static final String PARENT_DIRECTORY = "..";
		
	public static String getFileNameOnly( String path ) {
		final int fileNamePos = getFileNameStartPos(path, false);
		if ( fileNamePos > 0 ) {
			return path.substring( fileNamePos );
		}
		else {
			return path;
		}
	}

	/**
	 * @return
	 */
	public static String getBaseDir() {
		return baseDir;
	}
	
	

	public static String combinePath(String... parts) {
		return combinePath(SLASH, parts);
	}

	public static String combinePath(char slash, String... parts) {
		return combinePath(slash, false, parts);
	}

	public static String unifiedPathToLocalPath(String path) {
		return path != null ? path.replace(UNIFIED_SLASH, SLASH) : null;
	}

	public static String localPathToUnifiedPath(String path) {
		return path != null ? path.replace(SLASH, UNIFIED_SLASH) : null;
	}

	private static String combinePath(char slash, boolean addLastSlash,
			String... parts) {
		if (parts == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int n = 0; n < parts.length; ++n) {
			final String part = parts[n];
			if (part == null || part.equals(SAME_FOLDER_DOT)
					|| part.length() == 0) {
				continue;
			}
			if (sb.length() > 0) {
				ensureHasLastSlash(sb, slash);
			}
			sb.append(part);
		}
		if (sb.length() > 0) {
			if (addLastSlash) {
				ensureHasLastSlash(sb, slash);
			} else {
				ensureHasNoLastSlash(sb, slash);
			}
		}
		return sb.toString();
	}

	/**
	 * @param sb
	 */
	private static void ensureHasNoLastSlash(StringBuilder sb, char slash) {
		int firstNotSlashFromTheEnd = sb.length() - 1;
		while (firstNotSlashFromTheEnd >= 0
				&& sb.charAt(firstNotSlashFromTheEnd) == slash) {
			--firstNotSlashFromTheEnd;
		}
		sb.delete(firstNotSlashFromTheEnd + 1, sb.length());
	}
	
	/**
	 * @param path
	 * @return
	 */
	public static String getParentFolder(String path) {
		if (path == null || path.length() == 0) {
			return null;
		}
		File file = new File(path);
		return file.getParent();
	}
	
	private static String tryGetRelativePath(String path, String basePath) {
		if (path.equals(basePath)) {
			return "";
		}
		basePath = ensureHasLastSlash(basePath);
		if (path.startsWith(basePath)) {
			return path.substring(basePath.length());
		} else {
			return null;
		}
	}

	/**
	 * @param startFolder
	 * @return
	 */
	private static String ensureHasLastSlash(String path) {
		final StringBuilder sb = new StringBuilder(path);
		ensureHasLastSlash(sb, SLASH);
		return sb.toString();
	}

	public static String[] splitPathParts(String path) {
		if (path == null) {
			return null;
		}
		return path.split(File.separator);
	}

	public static String[] splitUnifiedPathParts(String path) {
		if (path == null) {
			return null;
		}
		return path.split(UNIFIED_SLASH_FOR_SPLIT);
	}

	public static String getRelativePath(File path,
			File basePath) {
		return getRelativePath(path.getAbsolutePath(), basePath.getAbsolutePath());
	}
	/**
	 * @param path
	 * @param basePath
	 * @return
	 */
	public static String getRelativePath(final String path,
			final String basePath) {
		if (path == null) {
			return null;
		}
		if (basePath == null) {
			return path;
		}
		String relativePath = tryGetRelativePath(path, basePath);
		if (relativePath == null) {
			String normalizedPath = getCanonicalPath(path);
			String normalizedBasePath = getCanonicalPath(basePath);
			relativePath = tryGetRelativePath(normalizedPath,
					normalizedBasePath);
		}
		return relativePath != null ? relativePath : path;
	}

	
	/**
	 * @param path
	 * @return
	 */
	private static String getCanonicalPath(String path) {
		return FileUtils.getCanonicalPath( path );
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static String getPathAndNameWithoutExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		final int fileNameStartPos = getFileNameStartPos(fileName,true);
		final int lastDotPos = fileName.lastIndexOf(EXTENSION_DOT);
		if (lastDotPos >= fileNameStartPos) {
			return fileName.substring(0, lastDotPos);
		} else {
			return fileName;
		}
	}

	/**
	 * Returns file extension (last '.' part) includes '.' Examples
	 * "somefolder.ext/file.txt" -> ".txt" "somefolder.ext/file." -> "."
	 * "somefolder.ext/file" -> ""
	 * 
	 * @param fileName
	 */
	public static String getExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		final int fileNameStartPos = getFileNameStartPos(fileName, true );
		final int lastDotPos = fileName.lastIndexOf(EXTENSION_DOT);
		if (lastDotPos >= fileNameStartPos) {
			return fileName.substring(lastDotPos);
		} else {
			return "";
		}
	}

	/**
	 * Returns file extension (last '.' part) includes '.' Examples
	 * "somefolder.ext/file.txt" -> "txt" "somefolder.ext/file." -> ""
	 * "somefolder.ext/file" -> ""
	 * 
	 * @param fileName
	 */
	public static String getExtension(String fileName, boolean withDot) {
		final String extension = getExtension(fileName);
		if (!withDot && extension != null
				&& extension.startsWith(String.valueOf(EXTENSION_DOT))) {
			return extension.substring(1);
		}
		return extension;
	}

	/**
	 *  
	 * @param fileName
	 * @param includeSlash
	 * @return Can resturt pos == fileName.length
	 */
	private static int getFileNameStartPos(String fileName, boolean includeSlash) {
		if (fileName == null) {
			return 0;
		}
		final int lastSlashPos = fileName.lastIndexOf(SLASH);
		if ( lastSlashPos >= 0 ) {
			return includeSlash ? lastSlashPos : lastSlashPos + 1;
		}
		else {
			return 0;
		}
	}

	public static String[] splitMultiplePathes(String pathes) {
		if (pathes == null) {
			return null;
		}
		return pathes.split(SEMICOLON_SPLIT);
	}

	/**
	 * @param sb
	 */
	private static void ensureHasLastSlash(StringBuilder sb, char slash) {
		if (sb.length() > 0) {
			if (sb.charAt(sb.length() - 1) != slash) {
				sb.append(slash);
			}
		}
	}	

	public static ClassLocation getClassLocation( Class clazz ) {
		if ( clazz == null ) {
			return null;
		}
		ClassLocationBuilder builder = new ClassLocationBuilder( clazz );
		try {
			return builder.getResult();
		} catch (CantCreateClassLocationException ex) {
			getLogger().error( "Can't locate class base folder", ex );
			return null;
		}
	}

	/**
	 * @return
	 */
	private static Logger getLogger() {
		return SSLogger.getLogger( PathUtils.class );
	}

	/**
	 * @return
	 */
	public static String addPathToPathList(String pathList, String path) {
		if ( path == null || path.length() == 0 ) {
			return pathList;
		}
		if ( pathList == null ) {
			pathList = "";
		}
		return pathList + SEMICOLON + path;
	}


	
}
