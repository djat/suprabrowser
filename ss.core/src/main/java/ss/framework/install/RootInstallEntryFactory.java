/**
 * 
 */
package ss.framework.install;

import java.util.List;
import java.util.TreeSet;

import ss.common.PathUtils;

/**
 *
 */
public class RootInstallEntryFactory {

	/**
	 * 
	 */
	private static final String ROOT_NAME = "application-folder";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootInstallEntryFactory.class);
	
	/**
	 * Singleton instance
	 */
	public final static RootInstallEntryFactory INSTANCE = new RootInstallEntryFactory();

	private RootInstallEntryFactory() {
	}
	
	public RootInstallEntry create( String basePath, List<String> relativeUris, boolean evaluateHash  ) {
		final RootInstallEntry rootEntry = new RootInstallEntry( basePath );
		rootEntry.setName( ROOT_NAME );
		final TreeSet<String> sortedRelativePathesWithoutDups = new TreeSet<String>( relativeUris );
		for( String relativeUri : sortedRelativePathesWithoutDups ) {
			InstallEntry entry = rootEntry.getChildren().findOrCreate( relativeUri );
			String fileName = PathUtils.combinePath( basePath, PathUtils.unifiedPathToLocalPath( relativeUri ) );
			entry.setType(InstallEntryType.FILE);
			if ( !entry.setUpFileAttributes( fileName, evaluateHash ) ) {
				logger.error( "Can't set up file attribute for " + fileName );
			}
//			if (logger.isDebugEnabled()) {
//				logger.debug( "Entry created " + entry  + " by " + relativeUri );
//			}
		}		
		return rootEntry;
	}

}
