package ss.framework.io.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ss.common.FileUtils;
import ss.common.PathUtils;

public final class FileList {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileList.class);
	
	private final String basePath;
	
	private final IFileNamePattern folderFilter;
	
	private final IFileNamePattern fileFilter; 
	
	private final List<String> fullPathes = new ArrayList<String>();
	
	private final List<String> relativeUris = new ArrayList<String>();
	
	/**
	 * @param basePath
	 * @param folderFilter
	 * @param fileFilter
	 */
	public FileList(final String basePath, final IFileNamePattern folderFilter, final IFileNamePattern fileFilter) {
		super();
		this.basePath = FileUtils.getCanonicalPath( basePath );
		this.folderFilter = folderFilter;
		this.fileFilter = fileFilter;
		process( new File( this.basePath ) );
	}

	private void process( File file ) {
		final String absolutePath = FileUtils.getCanonicalPath( file );
		final String relativeUri = PathUtils.localPathToUnifiedPath( PathUtils.getRelativePath( absolutePath, this.basePath ) );
		if ( file.isDirectory() ) {
			final boolean folderMatch = this.folderFilter.match( relativeUri );
			if (logger.isDebugEnabled()) {
				logger.debug( "Test Folder [" + relativeUri + "] = " + folderMatch );
			}
			if ( folderMatch ) {
				for( File child : file.listFiles() ) {
					process( child );
				}
			}
		}
		else {
			final boolean fileMatch = this.fileFilter.match( relativeUri );
			if (logger.isDebugEnabled()) {
				logger.debug( "Test File [" + relativeUri + "] = " + fileMatch );
			}
			if ( fileMatch ) {
				this.fullPathes.add( absolutePath );
				this.relativeUris.add( relativeUri );
			}			
		}
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return this.basePath;
	}

	public List<String> getFullPathes() {
		return this.fullPathes;
	}
	
	public List<String> getRelativeUris() {
		return this.relativeUris;
	}
}
