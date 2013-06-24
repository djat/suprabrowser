/**
 * 
 */
package ss.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import ss.common.path.ClassLocation;
import ss.framework.install.CantLoadInstallationDescriptionException;
import ss.framework.install.InstallationDescription;
import ss.framework.install.InstallationDescriptionManager;
import ss.framework.install.OperationSystemName;
import ss.framework.install.QualifiedVersion;
import ss.framework.install.RootInstallEntry;
import ss.framework.install.RootInstallEntryFactory;
import ss.framework.io.checksum.CantCreateCheckSumException;
import ss.framework.io.checksum.CheckSumFactory;
import ss.framework.io.structure.FileList;
import ss.framework.io.structure.FileListBuilder;

/**
 * 
 */
public class InstallUtils {

	/**
	 * 
	 */
	private static final String MANIFEST_IMPLEMENTATION_VERSION = "Implementation-Version";
	
	private static final String MANIFEST_IMPLEMENTATION_TITLE = "Specification-Title";

	/**
	 * 
	 */
	private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";

	/**
	 * 
	 */
	private static final String COMMON_SECTION_NAME = "common";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InstallUtils.class);

	public final static String UPDATE_PROTOCOL_NAME = "ApplicationUpdate";

	public static final String UPDATE_FILE_TRANSFER_PROTOCOL_NAME = "ApplicationUpdateFileTransfer";

	public static FileListBuilder createApplicationFileListBuilder() {
		return createApplicationFileListBuilder(getApplicationFolder());
	}

	/**
	 * @return
	 */
	private static String getApplicationFolder() {
		return FolderUtils.getApplicationFolder();
	}

	public static FileListBuilder createApplicationFileListBuilder(
			String applicationFolder) {
		final FileListBuilder listBuilder = new FileListBuilder(
				applicationFolder);
		listBuilder.excludeFolders(".svn;*/.svn;");
		listBuilder.excludeFiles("supra_launch.jar");
		listBuilder.includeFiles("supra*.jar");
		listBuilder.includeFiles("lib/*.jar");
		listBuilder.includeFiles("lib-win32/*.jar");
		listBuilder.includeFiles("lib-linux/*.jar");
		return listBuilder;
	}

	public static FileListBuilder createApplicationFileListBuilderOld(
			String applicationFolder) {
		final FileListBuilder listBuilder = new FileListBuilder(
				applicationFolder);
		listBuilder.excludeFolders(".svn;*/.svn;");
		listBuilder.includeFiles("libs/*.jar;libs/*.dll;libs/*.so");
		listBuilder.includeFiles("microblog/*.*");
		listBuilder.includeFiles("tinymce/*.*");
		listBuilder.includeFiles("xulrunner/*.*");
		listBuilder.includeFiles("build.xml");
		listBuilder.includeFiles("dyn_client.xml");
		listBuilder.includeFiles("last_login.xml");
		listBuilder.includeFiles("INSTALL.txt");
		listBuilder.includeFiles("LICENSE.txt");
		listBuilder.includeFiles("logger.conf");
		listBuilder.includeFiles("logger.conf.client");
		listBuilder.includeFiles("logger.conf.server");
		listBuilder.includeFiles("supra*.jar");
		// listBuilder.includeFiles("supra.ico");
		return listBuilder;
	}

	public static RootInstallEntry createRootInstallEntry() {
		return createRootInstallEntry(getApplicationFolder(), true);
	}

	/**
	 * 
	 */
	public static RootInstallEntry createRootInstallEntry(String baseFolder,
			boolean evaluateHash) {
		FileListBuilder fileListBuilder = createApplicationFileListBuilder(baseFolder);
		FileList fileList = fileListBuilder.getResult();
		return RootInstallEntryFactory.INSTANCE.create(fileList.getBasePath(),
				fileList.getRelativeUris(), evaluateHash);
	}

	public static Manifest getApplicationManifest() {
		final InputStream manifestIn = PathUtils.getClassLocation( InstallUtils.class ).getResourceAsStream( MANIFEST_PATH );
		if (manifestIn != null) {
			Manifest manifest;
			try {
				manifest = new Manifest(manifestIn);
			} catch (IOException ex) {
				logger.error("Can't load manifest", ex);
				return null;
			}
			return manifest;
		}
		else {
			logger.error( "Can't find manifest" );
			return null;
		}
	}
	
	public static String getApplicationVersion() {
		return getApplicationVersion( getApplicationManifest() );		
	}

	/**
	 * @param manifest
	 * @return
	 */
	public static String getApplicationVersion(Manifest manifest) {		
		final Attributes atts = getCommonSection(manifest);
		if ( atts == null ) {
			return null;
		}
		final String version = atts.getValue( MANIFEST_IMPLEMENTATION_VERSION );
		if ( version == null ) {
			logger.error("Can't find version attribute" );
		}
		return version;
	}

	/**
	 * @param manifest
	 * @return
	 */
	private static Attributes getCommonSection(Manifest manifest) {
		if ( manifest == null ) {
			return null;
		}
		final Attributes atts = manifest.getEntries().get( COMMON_SECTION_NAME );
		if ( atts == null ) {
			final String entries = MapUtils.allValuesToString( manifest.getEntries() );
			logger.error("Can't find common section " + entries  );
			return null;
		}
		return atts;
	}
	
	public static String getApplicationJarFileName() {
		ClassLocation location = PathUtils.getClassLocation(InstallUtils.class) ;
		if ( location.isJar() ) {
			return location.getCompilationUnitPath();
		}
		else {
			return null;
		}
	}
	
	public static String getApplicationInfo() {
		StringBuilder sb = new StringBuilder();
		final Attributes atts = getCommonSection( getApplicationManifest() );
		if (atts == null ) {
			sb.append( "Manifest information not found" );
		}
		else {
			sb.append( atts.getValue(MANIFEST_IMPLEMENTATION_TITLE ) );
			sb.append( " version " );
			sb.append( atts.getValue( MANIFEST_IMPLEMENTATION_VERSION ) );
			sb.append( StringUtils.getLineSeparator() );
			final String applicationFolder = getApplicationFolder();
			if ( InstallationDescriptionManager.INSTANCE.hasInstallationDescription(applicationFolder) ) {
				try {
					InstallationDescription instdesc = InstallationDescriptionManager.INSTANCE.loadFromApplicationFolder(applicationFolder);
					QualifiedVersion originalInstdescVersion = instdesc.getApplicationVersionObj();
					if ( !instdesc.verifyAndFixOsName() ) {
						sb.append( "Installation description have invalid version " + originalInstdescVersion + ", os name is " + OperationSystemName.getFromSystem() );
					}
					else {
						sb.append( "Installation description version " + instdesc.getApplicationVersionObj() );
					}
					sb.append( StringUtils.getLineSeparator() );
					final String jarFileName = getApplicationJarFileName();
					if ( jarFileName != null ) {
						try {
							sb.append( "Checksum " );
							sb.append( CheckSumFactory.INSTANCE.createFileChecksum(jarFileName) );
						} catch (CantCreateCheckSumException ex) {
							final String message = "Can't calc jar checksum " + jarFileName;
							logger.error( message, ex );
							sb.append( message );
						}
					}
					else {
						sb.append( "Can't find application jar" );
					}
				} catch (CantLoadInstallationDescriptionException ex) {
					final String message = "Can't load " + InstallationDescriptionManager.INSTALLATION_DESCRIPTION_FILE_NAME;
					logger.error(message, ex);
					sb.append( message );
				}
			}
			else {
				sb.append( "Can't find installation description " + InstallationDescriptionManager.INSTALLATION_DESCRIPTION_FILE_NAME );
			}
		}
		return sb.toString();
	}
}
