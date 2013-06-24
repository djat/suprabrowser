package ss.framework.launch;

import java.io.File;
import java.util.List;

import ss.common.ListUtils;
import ss.common.PathUtils;

public class JarLauncherSwtHelper {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(JarLauncherSwtHelper.class);

	private static final String LIB_WIN32_SWT_PATH = "lib-win32/swt";

	private static final String LIB_LINUX_SWT_PATH = "lib-linux/swt";

	static final String DRY_RUN_ARG = "-dry-run";

	private static void launch(File jarFile, String... args)
			throws CantLaunchJarException {
		jarFile = jarFile.getAbsoluteFile();
		JarLauncher launcher = createLauncher(jarFile, ListUtils.toList( args ) );
		launcher.setSpawn(true);
		launcher.launch();
	}

	/**
	 * @param baseFolder
	 * @param jarApplicationFileName
	 * @return
	 */
	private static JarLauncher createLauncher(File jarFile, List<String> args) {
		JarLauncher launcher = new JarLauncher(jarFile);
		File baseFolder = jarFile.getParentFile();
		if (baseFolder == null) {
			throw new NullPointerException("baseFolder is null for " + jarFile);
		}
		launcher.setJavaLibraryPath(createSwtLibraryPath(baseFolder));
		if (args != null) {
			for (String arg : args) {
				launcher.addArg(arg);
			}
		}
		return launcher;
	}

	public static void dryLaunchAndLaunch(File jarFile, String... args)
			throws CantLaunchJarException {
		dryLaunch(jarFile, args);
		launch(jarFile, args);
	}

	/**
	 * @param jarFile
	 * @throws CantLaunchJarException
	 */
	private static void dryLaunch(File jarFile, String... args)
			throws CantLaunchJarException {
		List<String> argsList = ListUtils.toList( args );
		argsList.add( 0, DRY_RUN_ARG);
		JarLauncher launcher = createLauncher(jarFile, argsList );
		launcher.launch();
	}

	/**
	 * @param baseFolder
	 * @return
	 */
	private static String createSwtLibraryPath(File baseFolder) {
		StringBuilder libPath = new StringBuilder();
		libPath.append( createSwtPath(baseFolder, LIB_LINUX_SWT_PATH).getAbsolutePath() );
		libPath.append( File.pathSeparator );
		libPath.append( createSwtPath(baseFolder, LIB_WIN32_SWT_PATH).getAbsolutePath() );
		final String currentLibraryPath = System.getProperty( "java.library.path" );
		if ( currentLibraryPath != null &&  currentLibraryPath.length() > 0 ) {
			libPath.append( File.pathSeparator );
			libPath.append( currentLibraryPath );
		}
		return libPath.toString();
	}

	/**
	 * @param baseFolder
	 * @param swtRelativePath
	 * @return
	 */
	private static File createSwtPath(File baseFolder,
			final String swtRelativePath) {
		return new File(baseFolder, PathUtils
				.unifiedPathToLocalPath(swtRelativePath)).getAbsoluteFile();
	}
}
