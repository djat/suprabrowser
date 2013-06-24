/**
 * 
 */
package ss.common;


import org.apache.log4j.Logger;

import ss.common.path.ClassLocation;
import ss.common.path.FolderFinder;
import ss.common.path.application.SupraJarApplicationFolderCondition;
import ss.global.SSLogger;

/**
 *
 */
public class FolderUtils {

	private static boolean INITIALIZED = false;
	
	private static String APPLICATIN_FOLDER = null;
		
	/**
	 * 
	 */
	public static String getTempFolder() {
		final String tempFolder = System.getProperty( "java.io.tmpdir" );
		if ( tempFolder == null ) {
			throw new RuntimeException( "Can't resolve temp folder" );
		}
		return tempFolder;
	}
	
	public static String getStartUpBase() {
		return System.getProperty( "user.dir" );
	}
	
	public static String getClassPath() {
		return System.getProperty( "java.class.path" );
	}
	
	public static synchronized String getApplicationFolder() {
		ensureInitialized();
		return APPLICATIN_FOLDER;
	}
	
	/**
	 * 
	 */
	private static synchronized void ensureInitialized() {
		if ( INITIALIZED ) {
			return;
		}
		INITIALIZED = true;
		APPLICATIN_FOLDER = findApplicationFolder();
		if ( APPLICATIN_FOLDER == null ) {
			getLogger().error( "Can't find application folder"  );
		}		
	}	
		
	/**
	 * @return
	 */
	private static String findApplicationFolder() {
		ClassLocation classLocation = findClassLocation();
		if ( classLocation == null ) {
			getLogger().error( "Can't find class location" );				
		}
		else {
			if ( classLocation.isJar() ) {
				return classLocation.getBaseFolder();	
			}
			else {
				// So we application is set of classes files instead of jar.
				// By default think that application folder is parent to folder with classes 
				final String baseFolder = classLocation.getBaseFolder();
				final String appFolder = PathUtils.getParentFolder(baseFolder);
				if ( getLogger().isInfoEnabled()) {
					getLogger().info("Application is running under Eclipse. Application folder is " + appFolder );
				}
				return appFolder;
			}
		}
		getLogger().error( "Can't find application folder via class location" );
		return findApplicationFolderViaFinder();
	}

	

	/**
	 * @return
	 */
	private static Logger getLogger() {
		return SSLogger.getLogger( FolderUtils.class );
	}

	public static boolean isApplicationFolderDefined() {
		return getApplicationFolder() != null;
	}
		
	private static ClassLocation findClassLocation() {
		return PathUtils.getClassLocation( FolderUtils.class );
	}
	
	private static FolderFinder createFolderFinder() {
		final FolderFinder finder = new FolderFinder();
		finder.addLookUp( getStartUpBase() );
		finder.addLookUp( getClassPath() );
		return finder;
	}
	
	private static String findApplicationFolderViaFinder() {
		final FolderFinder finder = createFolderFinder();
		finder.addCondition( new SupraJarApplicationFolderCondition() );
		return finder.find();
	}
	
	
}
