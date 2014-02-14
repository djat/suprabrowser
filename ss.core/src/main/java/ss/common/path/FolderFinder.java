/**
 * 
 */
package ss.common.path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ss.common.ListUtils;
import ss.common.PathUtils;
import ss.common.path.application.SupraJarApplicationFolderCondition;

/**
 *
 */
public final class FolderFinder {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FolderFinder.class); 
	
	
	private final List<AbstractFolderCondition> conditions = new ArrayList<AbstractFolderCondition>();
	
	private List<File> lookUpsFolders = new ArrayList<File>();
	
	
	/**
	 * 
	 */
	public FolderFinder() {
		super();
		final SupraJarApplicationFolderCondition surpaJarApplicationFolderCondition = new SupraJarApplicationFolderCondition();
		addCondition(surpaJarApplicationFolderCondition);
	}

	/**
	 * @param condition
	 */
	public void addCondition(AbstractFolderCondition condition) {
		this.conditions.add( condition );
	}

	public void addLookUp( String lookUpPath ) {
		if ( lookUpPath  != null ) {
			for( String path : PathUtils.splitMultiplePathes(lookUpPath ) ) {
				if ( !path.equals( PathUtils.SAME_FOLDER_DOT ) ) {
					addSingleLookUp( path );
				}
			}
		}
	}
	
	private void addSingleLookUp( String lookUpPath ) {
		if ( lookUpPath == null ) {
			return;
		}
		File file = new File( lookUpPath ).getAbsoluteFile();
		file = file.isFile() ? file.getParentFile() : file;
		if ( !file.exists() ) {
			return;
		}
		this.lookUpsFolders.add( file );
	}
	
	public String find() {
		if (logger.isDebugEnabled()) {
			logger.debug( "Look ups " + ListUtils.valuesToString( this.lookUpsFolders ) );
		}
		for( AbstractFolderCondition condition : this.conditions ) {
			for( File folder : this.lookUpsFolders ) {
				if ( condition.match(folder) ) {
					return folder.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void addLookUpClassPath( String classPath ) {
		
		
	}
}

