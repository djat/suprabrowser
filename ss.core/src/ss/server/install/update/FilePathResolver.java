/**
 * 
 */
package ss.server.install.update;

import ss.common.FileUtils;
import ss.common.PathUtils;
import ss.framework.install.update.loader.DownloadFileHeader;
import ss.framework.install.update.loader.IFilePathResolver;

/**
 *
 */
public class FilePathResolver implements IFilePathResolver {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FilePathResolver.class);
	
	private final String baseFolder;
	
	/**
	 * @param baseFolder
	 */
	public FilePathResolver(final String baseFolder) {
		super();
		this.baseFolder = baseFolder;
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.loader.IFilePathResolver#resolve(ss.framework.install.update.loader.DownloadFileHeader)
	 */
	public String resolve(DownloadFileHeader fileHeader) {
		final String localRelative = PathUtils.unifiedPathToLocalPath( fileHeader.getRemotePath() );
		final String localFullPath = PathUtils.combinePath( this.baseFolder, localRelative );
		final String localCanonicalPath = FileUtils.getCanonicalPath( localFullPath );
		if ( FileUtils.isFileExist( localCanonicalPath ) ) {
			final String relativeToCheck = PathUtils.getRelativePath( localCanonicalPath, this.baseFolder );
			if ( !relativeToCheck.equals(localRelative) ) {
				logger.error( "Forbidden download of " + localCanonicalPath + " by " + fileHeader + ", relativeToCheck " + localRelative );
				return null;
			}
			return localCanonicalPath;
		}
		else {
			logger.error( "Target download file not found " + localCanonicalPath + ", by " + fileHeader );
			return null;
		}
	}
}
