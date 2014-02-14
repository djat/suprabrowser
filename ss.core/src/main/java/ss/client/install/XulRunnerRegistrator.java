package ss.client.install;

import java.io.File;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline;

import ss.common.FolderUtils;
import ss.common.PathUtils;
import ss.framework.install.OperationSystemName;
import ss.framework.install.OperationSystemName.OsFamily;

public class XulRunnerRegistrator {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XulRunnerRegistrator.class);

	private static final String REGISTER_USER_ARG = "--register-user";
	
	private static final String UNREGISTER_USER_ARG = "--unregister-user";
	
	private static final String XULRUNNER_FOLDER = "xulrunner";

	private static final String WIN32_XUL_RUNNER_APP_NAME = "xulrunner.exe";
	
	private static final String DEFAULT_XUL_RUNNER_APP_NAME = "xulrunner";

	private static final String REGISTER_TARGET_NAME = "register";

	
	private final File xulRunnerFile;
	
	/**
	 * @param xulRunnerFile
	 */
	public XulRunnerRegistrator() {
		super();
		String xulRunnerExecutableName = getXulRunnerExecutableName();
		this.xulRunnerFile = new File( PathUtils.combinePath( FolderUtils.getStartUpBase(), XULRUNNER_FOLDER, xulRunnerExecutableName ) ).getAbsoluteFile();
	}

	/**
	 * @return
	 */
	private String getXulRunnerExecutableName() {
		String xulRunnerExecutableName;
		OperationSystemName osName = OperationSystemName.getFromSystem();
		if ( osName.getFamily().equals( OsFamily.WIN32 ) ) {
			xulRunnerExecutableName = WIN32_XUL_RUNNER_APP_NAME;
		}
		else {
			xulRunnerExecutableName = DEFAULT_XUL_RUNNER_APP_NAME;
		}
		return xulRunnerExecutableName;
	}

	public void register() throws CantRegisterXulRunnerException {
		logger.warn("Registering!!");
		if ( !canRunXulrunnerRegister() ) {
			throw new CantRegisterXulRunnerException( "Can't find xulrunner " + this.xulRunnerFile );
		}
		final Project antProject = new Project();
		antProject.init();
		final Target target = new Target();
		target.setName(REGISTER_TARGET_NAME);
		target.addTask( createRunTask(antProject, false ) );
		target.addTask( createRunTask(antProject, true ) );
		antProject.addTarget( target);
		logger.warn("Dir: "+this.xulRunnerFile.getParentFile());
		antProject.setBaseDir( this.xulRunnerFile.getParentFile() );
		antProject.addBuildListener( new RunListener() );
		antProject.executeTarget( REGISTER_TARGET_NAME );
	}

	/**
	 * @return
	 */
	public boolean canRunXulrunnerRegister() {
		return this.xulRunnerFile .isFile() && this.xulRunnerFile.exists();
	}
	
	/**
	 * @param antProject
	 * @return
	 */
	private Task createRunTask(final Project antProject, boolean register ) {
		ExecTask task = new ExecTask();
		task.setProject(antProject);
		task.setTaskName( this.xulRunnerFile.getName() );
		task.setExecutable( this.xulRunnerFile.getAbsolutePath() );
		task.setFailonerror( true );
		Commandline.Argument antArgument = task.createArg();
		antArgument.setValue( register ? REGISTER_USER_ARG : UNREGISTER_USER_ARG );
		return task;
	}

	/**
	 * @param message
	 */
	private void processMessage(String message) {
		logger.info( message );
	}
	
	
	@Override
	public String toString() {
		return this.xulRunnerFile.toString();
	}


	class RunListener extends DefaultLogger {
		
		/**
		 * 
		 */
		public RunListener() {
			super();
			setOutputPrintStream(System.out);
			setErrorPrintStream(System.err);
			setMessageOutputLevel(Project.MSG_INFO);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.tools.ant.DefaultLogger#messageLogged(org.apache.tools.ant.BuildEvent)
		 */
		@Override
		public void messageLogged(BuildEvent event) {
			super.messageLogged(event);
			String message = event.getMessage();
			processMessage(message != null ? message : "");
		}
	}
	
}
