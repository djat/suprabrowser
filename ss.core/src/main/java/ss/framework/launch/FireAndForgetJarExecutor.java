/**
 * 
 */
package ss.framework.launch;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * 
 */
class FireAndForgetJarExecutor extends AbstractJarExecutor {

	@SuppressWarnings("unused")
	static org.apache.log4j.Logger logger =  ss.global.SSLogger
			.getLogger(FireAndForgetJarExecutor.class);

	/**
	 * @param releaseDetector
	 */
	public FireAndForgetJarExecutor() {
		super();
	}

	public void runUntilRelease(final Project project, final String targetName)
			throws CantLaunchJarException {
		if (project == null) {
			throw new NullPointerException("project");
		}
		if (targetName == null) {
			throw new NullPointerException("targetName");
		}
		project.addBuildListener(createBuildLogger());
		try {
			project.executeTarget(targetName);
		}
		catch( BuildException ex ) {
			throw new CantLaunchJarException( "Can't launch run due build exception", ex );
		}
		throwLaunchExceptionIfHas();
	}


}
