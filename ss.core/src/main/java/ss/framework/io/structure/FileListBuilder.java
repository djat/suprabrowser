/**
 * 
 */
package ss.framework.io.structure;

import java.io.File;

import ss.common.ArgumentNullPointerException;
/**
 *
 */
public final class FileListBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileListBuilder.class);
	
	private final String basePath;
	
	private final FileNamePatternBuilder filePatternBuilder = new FileNamePatternBuilder();
	
	private final FileNamePatternBuilder folderPatternBuilder = new FileNamePatternBuilder();
	
	public FileListBuilder(File basePath) {
		this( basePath.getAbsolutePath() );
	}
	
	/**
	 * @param basePath
	 */
	public FileListBuilder(String basePath) {
		super();
		if (basePath == null) {
			throw new ArgumentNullPointerException("basePath");
		}
		this.basePath = basePath;
		boolean deep = true;
		if ( deep ) {
			this.folderPatternBuilder.include( FileNamePatternBuilder.ANY_NAME_PATTERN );
		}
	}

	/**
	 */
	public void includeFiles(String filePathPattern) {
//		String [] parts = PathUtils.splitUnifiedPathParts(filePathPattern);
//		if ( parts.length > 1 ) {
//			final String [] parentParts = new String[ parts.length - 1];
//			System.arraycopy(parts, 0, parentParts, 0, parentParts.length );
//			final String parentFolder = PathUtils.combinePath( PathUtils.SLASH, parts);
//			this.folderPatternBuilder.include(parentFolder );
//		}
		this.filePatternBuilder.include( filePathPattern );
	}
	
	public void excludeFiles(String filePathPattern) {
		this.filePatternBuilder.exclude( filePathPattern );
	}
	
	public void excludeFolders( String folderNamePattern ) {
		this.folderPatternBuilder.exclude( folderNamePattern );
	}
	
	public FileList getResult() {
		final IFileNamePattern folderPattern = createFolderPattern();
		final IFileNamePattern filePattern = createFilePattern();
		if (logger.isDebugEnabled()) {
			logger.debug( "Folder patterns: " + folderPattern );
			logger.debug( "File patterns: " + filePattern );
		}
		FileList fileList = new FileList( this.basePath, folderPattern, filePattern );
		return fileList;
	}

	/**
	 * @return
	 */
	public IFileNamePattern createFilePattern() {
		return this.filePatternBuilder.getResult();
	}

	/**
	 * @return
	 */
	public IFileNamePattern createFolderPattern() {
		return this.folderPatternBuilder.getResult();
	}
}
