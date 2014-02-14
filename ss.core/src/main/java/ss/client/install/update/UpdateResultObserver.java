/**
 * 
 */
package ss.client.install.update;

import java.util.Hashtable;

import org.eclipse.swt.widgets.Shell;

import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.WelcomeScreen;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.MapUtils;
import ss.common.UiUtils;
import ss.framework.install.update.UpdateProcessListener;
import ss.framework.install.update.UpdateResult;
import ss.global.LoggerUtils;

/**
 * 
 */
public class UpdateResultObserver implements UpdateProcessListener {

	/**
	 * 
	 */
	private static final String APPLICATION_UPDATE_MESSAGE_TITLE = "Application Update";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateResultObserver.class);

	private final Hashtable<String, String> session;

	private final WelcomeScreen welcomeScreen;

	/**
	 * @param session
	 * @param welcomeScreen
	 */
	public UpdateResultObserver(final Hashtable<String, String> session,
			final WelcomeScreen welcomeScreen) {
		super();
		this.session = session;
		this.welcomeScreen = welcomeScreen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.install.update.UpdateProcessListener#applicationIsUpToDate()
	 */
	public void applicationIsUpToDate() {
		safeContinueNormalApplicationRun();
	}

	/**
	 * 
	 */
	private void safeContinueNormalApplicationRun() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				continueNormalApplicationRun();
			}
		});
	}

	/**
	 * 
	 */
	protected void continueNormalApplicationRun() {
		LoggerUtils.enableEmbededAppender();
		final SupraSphereFrame sF = new SupraSphereFrame(UiUtils.getDisplay(), this.welcomeScreen );
		if (logger.isDebugEnabled()) {
			logger.debug("Start up session is "
					+ MapUtils.allValuesToString(this.session));
		}
		sF.openAndBlock(this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.install.update.UpdateProcessListener#askUserToProceedUpdate()
	 */
	public boolean askUserToProceedUpdate() {
		// TODO ask user about update
		final String dispalyMessage = "New versions of SupraSphere libraries will be downloaded.\r\n"
				+ "You might have to login again after the downloading has completed.";
		showMessage(dispalyMessage);
		return true;
	}



	/* (non-Javadoc)
	 * @see ss.framework.install.update.UpdateProcessListener#cantUpdate(java.lang.String, boolean)
	 */
	public void cantUpdate(String message, boolean actualClientCanWorkWithServer) {
		// FIXME some clever handling
		final String fullMessage = "Can't update application. " + message;
		logger.error(fullMessage);
		try {
			UiUtils.swtInvoke(new Runnable() {
				public void run() {
					UserMessageDialogCreator.error( getDialogParent(), fullMessage,
							APPLICATION_UPDATE_MESSAGE_TITLE);
				}
			});
		} finally {
			if ( actualClientCanWorkWithServer ) {
				safeContinueNormalApplicationRun();
			}
			else {
				exit();
			}
		}
	}
	/**
	 * 
	 */
	private void exit() {
		logger.warn( "Close Welcome Screen. Going to exit" );
		this.welcomeScreen.closeFromWithin();
		System.exit(0);
	}	

	/**
	 * @param string
	 */
	private void showMessage(final String message) {
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				UserMessageDialogCreator.info( getDialogParent(), message,
						APPLICATION_UPDATE_MESSAGE_TITLE);
			}

		});
	}
	
	/**
	 * @return
	 */
	private Shell getDialogParent() {
		final Shell active = SDisplay.display.get().getActiveShell();
		return active != null ? active : UpdateResultObserver.this.welcomeScreen.getShell();
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.UpdateProcessListener#updated(ss.framework.install.update.UpdateResult)
	 */
	public void updated(UpdateResult updateResult) {
		if ( updateResult.isShouldExit() ) {
			logger.info( "Shooting down application" );
			exit();
		}
		else {
			continueNormalApplicationRun();
		}
	}


}
