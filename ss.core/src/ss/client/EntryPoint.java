/**
 * 
 */
package ss.client;

import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import ss.client.configuration.ApplicationConfiguration;
import ss.client.configuration.StartUpArgs;
import ss.client.configuration.XulRunnerRegisterState;
import ss.client.event.SsDomainChangesListener;
import ss.client.install.XulRunnerRegistrator;
import ss.client.ui.WelcomeScreen;
import ss.common.FolderUtils;
import ss.common.InstallUtils;
import ss.common.StringUtils;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.domainmodel2.SsDomain;
import ss.common.domainmodel2.SupraSphereFrameBasedDataProviderConnector;
import ss.framework.launch.LaunchUtils;
import ss.global.SSLogger;
import ss.global.LoggerConfiguration;

/**
 * 
 */
public class EntryPoint {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger;

	
	public static void main(String[] args) {		
		// Basicaly initialize as client
		SSLogger.initialize(LoggerConfiguration.CLIENT);
		logger = SSLogger.getLogger(EntryPoint.class);
		Locale.setDefault(Locale.US);
		final StartUpArgs startUpArgs = new StartUpArgs( args );
		if (!FolderUtils.isApplicationFolderDefined()) {
			final String message = "Can't find application folder. Terminating.";
			logger.error(message);
			return;
		}
		try {
			ThreadUtils.initializeDefaultExceptionHandler();
			LaunchUtils.printInitializationMark();
			if ( LaunchUtils.isDryRun(args) ) {
				logger.info( "Dry run. Exit" );
				return;
			}
			if ( startUpArgs.isShowVersionDefined() ) {
				if ( java.awt.GraphicsEnvironment.isHeadless() ) {
					System.out.println( InstallUtils.getApplicationInfo() );
				}
				else {
					showInfo( InstallUtils.getApplicationInfo() );
				}
				return;
			}
			final String startType = startUpArgs.getStartType();
			SsDomain.initialize(new SupraSphereFrameBasedDataProviderConnector());
			SsDomain.addChangesListener(new SsDomainChangesListener());
			UiUtils.setCheckUnsafeUiCalls(true);			
			setUpSwingColors();
			//registerXulrunnerIfNeeded();
			if ( startUpArgs.hasCorrectStartUpType() ) {
				new WelcomeScreen( startUpArgs );
			} else if (startType.equals("server")) {
				showError("Please use SupraServer.main to start server.");
			} else if (startType.equals("servant")) {
				showError("Please use sparate processes for client and server, use SupraServer.main to start server");
			} else {
				showError("Unknown start up arg '" + startType + "'" 
						+ StringUtils.getLineSeparator() + "Known args: client, invite.");
			}
		} catch (Throwable ex) {
			final StringBuilder message = new StringBuilder();
			message.append(	"Unexpected exception. Please see logs for details." );
			message.append(	StringUtils.getLineSeparator() ).append("Exception: ").append( ex );
			showError( message.toString());						
		}
	}

	/**
	 * 
	 */
	private static void setUpSwingColors() {
		ColorUIResource control;
		ColorUIResource controlShadow;
		ColorUIResource controlHighlight;
		ColorUIResource controlDkShadow;

		control = new ColorUIResource(new java.awt.Color(239, 239, 239));
		controlHighlight = new ColorUIResource(
				java.awt.SystemColor.controlLtHighlight);
		ColorUIResource controlDarkHighlight = new ColorUIResource(
				java.awt.Color.lightGray);
		controlShadow = new ColorUIResource(
				java.awt.SystemColor.controlShadow);
		controlDkShadow = new ColorUIResource(java.awt.Color.darkGray);

		UIDefaults defaults = UIManager.getDefaults();
		defaults.put("ScrollBar.background", control); // Background to
		// slider
		defaults.put("ScrollBar.foreground", control); // Dots, I think
		defaults.put("ScrollBar.track", controlDarkHighlight);
		defaults.put("ScrollBar.trackHighlight", controlDkShadow);

		defaults.put("ScrollBar.thumb", new ColorUIResource(
				new java.awt.Color(214, 214, 206))); // Actual slider
		defaults.put("ScrollBar.thumbHighlight", controlHighlight);
		defaults.put("ScrollBar.thumbDarkShadow", controlDkShadow);
		defaults.put("ScrollBar.thumbLightShadow", controlShadow);
	}

	/**
	 * 
	 */
	private static void registerXulrunnerIfNeeded() {
		ApplicationConfiguration conCfg = ApplicationConfiguration.loadUserConfiguration();
		if ( conCfg.getXulrunnerRegistered() == XulRunnerRegisterState.REGISTERED ) {
			return;
		}
		else {
			final XulRunnerRegistrator registrator = new XulRunnerRegistrator();
			if ( !registrator.canRunXulrunnerRegister() ) {
				logger.warn( "Can't register xul xunner by " + registrator );
				return;
			}
			try {
				registrator.register();
				conCfg.setXulRunnerRegistered( XulRunnerRegisterState.REGISTERED );
			} catch (Throwable ex) {
				if ( conCfg.getXulrunnerRegistered() != XulRunnerRegisterState.REGISTRATION_FAILED ) {
					conCfg.setXulRunnerRegistered( XulRunnerRegisterState.REGISTRATION_FAILED );				
					logger.error( "Can't register xulrunner", ex );
				}
				else {
					logger.warn( "Can't register xulrunner " + ex);
				}
			}
			conCfg.save();
		}
		
	}
	
	/**
	 * @param message
	 */
	private static void showError(String message) {
		logger.fatal(message);
		JOptionPane.showMessageDialog( null, message, "SupraSphere", JOptionPane.ERROR_MESSAGE );
	}
	
	/**
	 * @param string
	 */
	private static void showInfo(String message) {
		logger.info(message);
		JOptionPane.showMessageDialog( null, message, "SupraSphere", JOptionPane.INFORMATION_MESSAGE );
	}
	
	
}
