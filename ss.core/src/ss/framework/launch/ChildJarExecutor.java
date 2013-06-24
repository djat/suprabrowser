/**
 * 
 */
package ss.framework.launch;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import ss.common.ThreadUtils;
import ss.common.threads.ThreadBlocker;
import ss.common.threads.ThreadBlocker.TimeOutException;


/**
 *
 */
public class ChildJarExecutor extends AbstractJarExecutor {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChildJarExecutor.class);
	
	/**
	 * 
	 */
	private static final int TIME_OUT = 20000;

	private final IApplicationLaunchedDetector launchedDetector;
	
	private final IReleaseMarkDetector releaseMarkDetector;

	private ThreadBlocker threadBlocker = null;
	
	private volatile boolean callerBlocked = true;
	
	
	/**
	 * @param launchedDetector
	 * @param releaseMarkDetector
	 */
	public ChildJarExecutor(final IApplicationLaunchedDetector launchedDetector, final IReleaseMarkDetector releaseMarkDetector) {
		super();
		if ( launchedDetector == null && releaseMarkDetector == null ) {
			throw new NullPointerException( "launchedDetector or releaseMarkDetector should be not null" );
		}
		this.launchedDetector = launchedDetector;
		this.releaseMarkDetector = releaseMarkDetector;
	}

	@Override
	public void runUntilRelease(final Project project, final String targetName) throws CantLaunchJarException {
		if (project == null) {
			throw new NullPointerException("project");
		}
		if (targetName == null) {
			throw new NullPointerException("targetName");
		}
		this.setThreadBlocker(new ThreadBlocker(TIME_OUT));
		project.addBuildListener(new RunListener());
		try {
			setCallerBlocked(true);
			if (logger.isDebugEnabled()) {
				logger.debug("Begins caller blocking");
			}
			ThreadUtils.start(new Runnable() {
				public void run() {
					try {
						project.executeTarget(targetName);
						if (logger.isDebugEnabled()) {
							logger.debug("Target executed begin waiting to launch");
						}
						waitToLaunched();
					}
					catch (BuildException ex) {
						failed( ex );						
					}
					catch (Throwable ex) {
						failed( ex );
					}
				}

				private void failed(Throwable ex) {
					logger.error("Unexpected executing error", ex);
					setJarRunException( new CantLaunchJarException( "Unexpected executing error", ex ) );
					releaseBlock();
				}

			}, getClass());
			try {
				this.getThreadBlocker().blockUntilRelease();
			}
			catch(TimeOutException ex) {
				throw new CantLaunchJarException( "Launch waiting time is out", ex );
			}			
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("Finishing caller blocking");
			}
			setCallerBlocked(false);
		}
		throwLaunchExceptionIfHas();
	}

	private synchronized boolean isCallerBlocked() {
		return this.callerBlocked;
	}

	private synchronized void setCallerBlocked(boolean blocking) {
		this.callerBlocked = blocking;
	}
	
	private void waitToLaunched() {
		if (!hasLaunchDetector()) {
			return;
		}
		while (isCallerBlocked()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
			}
			if (this.launchedDetector.checkLaunched()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Detect application launch");
				}
				releaseBlock();
				break;
			}
		}
	}

	private boolean hasLaunchDetector() {
		return this.launchedDetector != null;
	}

	private void releaseBlock() {
		if (logger.isDebugEnabled()) {
			logger.debug("Releasing caller blocking");
		}
		final ThreadBlocker blocker = getThreadBlocker();
		if (blocker != null) {
			blocker.release();
		}
	}
	/**
	 * @param message
	 */
	@Override
	protected void processFatalMessage(String message) {
		super.processErrorMessage(message);
		releaseBlock();
	}

	/* (non-Javadoc)
	 * @see ss.framework.launch.AbstractJarExecutor#processMessage(java.lang.String)
	 */
	@Override
	protected void processMessage(String message) {
		super.processMessage(message);
		if ( hasReleaseMarkDetector() ) {
			if ( this.releaseMarkDetector.match(message) ) {
				if (logger.isInfoEnabled()) {
					logger.info( "Release mark detected in " + message );
				}
				releaseBlock();
			}
		}
	}

	/**
	 * @return
	 */
	private boolean hasReleaseMarkDetector() {
		return this.releaseMarkDetector != null;
	}

	/**
	 * @param threadBlocker
	 *            the threadBlocker to set
	 */
	private synchronized void setThreadBlocker(ThreadBlocker threadBlocker) {
		if (this.threadBlocker != null) {
			throw new IllegalStateException("JarExecutor can't be resused");
		}
		this.threadBlocker = threadBlocker;
	}

	/**
	 * @return the threadBlocker
	 */
	private synchronized ThreadBlocker getThreadBlocker() {
		return this.threadBlocker;
	}

}
