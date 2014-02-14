/**
 * 
 */
package ss.framework.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline;

/**
 *
 */
public class JarLauncher {

	/**
	 * 
	 */
	private static final String RUN_TARGET_NAME = "run";

	private final File jarFile;
	
	private final File baseFolder;
	
	private final List<String> arguments = new ArrayList<String>();
	
	private String javaLibraryPath;
	
	private boolean spawn = false;

	private IApplicationLaunchedDetector launchDetector = null;
	
	/**
	 * @param jarFile
	 */
	public JarLauncher(final File jarFile) {
		super();
		this.jarFile = jarFile;
		this.baseFolder = jarFile.getParentFile();
	}
	
	/**
	 * @param string
	 */
	public JarLauncher(String fileName) {
		this( new File( fileName) );
	}

	public void launch() throws CantLaunchJarException {
		if ( !this.jarFile.exists() ) {
			throw new CantLaunchJarException( "Jar file not found " + this.jarFile );
		}
		if ( !this.jarFile.isFile() ) {
			throw new CantLaunchJarException( "Target jar file path is not file " + this.jarFile );
		}
		final Project antProject = new Project();
		antProject.init();
		final Target target = new Target();
		target.setName(RUN_TARGET_NAME);
		final Java task = createRunTask(antProject);
		target.addTask( task );
		antProject.addTarget( target);
		antProject.setBaseDir( this.baseFolder );
		AbstractJarExecutor executor = createExecutor();
		executor.runUntilRelease(antProject, RUN_TARGET_NAME);
	}

	/**
	 * @return
	 */
	private AbstractJarExecutor createExecutor() {
		if ( this.isSpawn() ) {
			return new FireAndForgetJarExecutor();
		}
		else {
			return new ChildJarExecutor( this.launchDetector, new ReleaseMarkDetector() );
		}
	}

	/**
	 * @param antProject
	 * @return
	 */
	private Java createRunTask(final Project antProject) {
		final Java task = new Java();
		task.setProject(antProject);
		task.setTaskName( this.jarFile.getName() );
		task.setJar( this.jarFile );
		task.setFork(true);
		task.setSpawn(this.spawn);
		if ( this.javaLibraryPath != null ) {
			task.createJvmarg().setLine( "-Djava.library.path=\"" + this.javaLibraryPath + "\"");
		}
		for( String argument : this.arguments ) {
			Commandline.Argument antArgument = task.createArg();
			antArgument.setValue(argument);
		}
		return task;
	}

	/**
	 * @param downloadBase
	 */
	public void addArg(String argument) {
		this.arguments.add(argument);
	}

	/**
	 * @return the spawn
	 */
	public boolean isSpawn() {
		return this.spawn;
	}

	/**
	 * @param spawn the spawn to set
	 */
	public void setSpawn(boolean spawn) {
		this.spawn = spawn;
	}

	/**
	 * @return the javaLibraryPath
	 */
	public String getJavaLibraryPath() {
		return this.javaLibraryPath;
	}

	/**
	 * @param javaLibraryPath the javaLibraryPath to set
	 */
	public void setJavaLibraryPath(String javaLibraryPath) {
		this.javaLibraryPath = javaLibraryPath;
	}

	/**
	 * @return the releaseMarkDetector
	 */
	public IApplicationLaunchedDetector getLaunchDetector() {
		return this.launchDetector;
	}

	/**
	 * @param releaseMarkDetector the releaseMarkDetector to set
	 */
	public void setLaunchDetector(IApplicationLaunchedDetector releaseMarkDetector) {
		this.launchDetector = releaseMarkDetector;
	}
	
	
}
