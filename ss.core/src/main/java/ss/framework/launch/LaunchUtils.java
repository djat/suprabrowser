/**
 * 
 */
package ss.framework.launch;

import java.util.List;
import ss.common.ListUtils;

/**
 *
 */
public class LaunchUtils {

	public static boolean isDryRun( String [] args ) {
		List<String> argsList = ListUtils.toList(args );
		return argsList != null && argsList.contains( JarLauncherSwtHelper.DRY_RUN_ARG );
	}
	
	public static void printInitializationMark() {
		System.out.flush();
		System.out.println( ReleaseMarkDetector.INITIALIZATION_MARK );
		System.out.flush();
	}
}
