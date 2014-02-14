/**
 * 
 */
package ss.client.preferences;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.ManagePreferencesCommonShell;
import ss.client.ui.preferences.SetPreferencesCommonShell;
import ss.client.ui.progressbar.DownloadProgressBar;
import ss.common.ThreadUtils;
import ss.common.TimeLogWriter;
import ss.common.UiUtils;

/**
 * @author zobo
 * 
 */
public class PreferencesUILoader {

	private static final String LOADING_PREFERENCES = "UILOADER.LOADING_PREFERENCES";

	private static final String LOADING_ADMINISTRATE = "UILOADER.LOADING_ADMINISTRATE";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_PREFERENCES_UILOADER);

	private enum OptionsLoadingState {
		LOADING, LOADED, IDLE
	}

	private enum BarState {
		BOTH, ADMIN, PREF, IDLE
	}

	public enum OptionsTypes {
		PREFERENCES, ADMINISTRATE
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesUILoader.class);

	public static PreferencesUILoader INSTANCE = new PreferencesUILoader();

	private OptionsLoadingState preferencesLoading = OptionsLoadingState.IDLE;

	private OptionsLoadingState administrateLoading = OptionsLoadingState.IDLE;

	private DownloadProgressBar bar = null;

	private BarState barState = BarState.IDLE;

	private static Object sync = new Object();

	private PreferencesAdminController adminController;

	private PreferencesController prefController;

	private PreferencesUILoader() {
		logger.info("Preferences UI Loader created");
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void load(OptionsTypes optionType) {
		load(optionType, null);
	}

	public void load(OptionsTypes optionType, String sphereId) {
		if (optionType == OptionsTypes.ADMINISTRATE) {
			if (this.administrateLoading == OptionsLoadingState.IDLE) {
				this.administrateLoading = OptionsLoadingState.LOADING;
				loadPreform(optionType, sphereId);
				activateBar(optionType);
			} else if (this.administrateLoading == OptionsLoadingState.LOADED) {
				if (ManagePreferencesCommonShell.INSTANCE != null) {
					ManagePreferencesCommonShell.INSTANCE.setFocus();
				}
			} else if (this.administrateLoading == OptionsLoadingState.LOADING) {
				// Do nothing
			}
		} else if (optionType == OptionsTypes.PREFERENCES) {
			if (this.preferencesLoading == OptionsLoadingState.IDLE) {
				this.preferencesLoading = OptionsLoadingState.LOADING;
				loadPreform(optionType, sphereId);
				activateBar(optionType);
			} else if (this.preferencesLoading == OptionsLoadingState.LOADED) {
				if (SetPreferencesCommonShell.INSTANCE != null) {
					SetPreferencesCommonShell.INSTANCE.showSphere(sphereId,
							true);
				}
			} else if (this.preferencesLoading == OptionsLoadingState.LOADING) {
				// Do nothing
			}
		}
	}

	private void loadPreform(final OptionsTypes optionType,
			final String sphereId) {
		Thread t = new Thread() {
			@Override
			public void run() {
				final TimeLogWriter timeLogWriter = new TimeLogWriter( PreferencesUILoader.class, "Load preferences started" );
				loadData(optionType);
				timeLogWriter.logAndRefresh("Data preloded");
				createGUI(optionType, sphereId);
				timeLogWriter.logAndRefresh("Window opened");
			}
		};
		ThreadUtils.start(t, "Options Loader");
	}

	public void finished(OptionsTypes optionType) {
		if (optionType == OptionsTypes.ADMINISTRATE) {
			this.administrateLoading = OptionsLoadingState.LOADED;
		} else if (optionType == OptionsTypes.PREFERENCES) {
			this.preferencesLoading = OptionsLoadingState.LOADED;
		}
		deactivateBar(optionType);
	}

	public void destroyed(OptionsTypes optionType) {
		if (optionType == OptionsTypes.ADMINISTRATE) {
			this.administrateLoading = OptionsLoadingState.IDLE;
		} else if (optionType == OptionsTypes.PREFERENCES) {
			this.preferencesLoading = OptionsLoadingState.IDLE;
		}
		deactivateBar(optionType);
	}

	private void activateBar(OptionsTypes optionType) {
		synchronized (sync) {
			if (this.barState == BarState.IDLE) {
				if (optionType == OptionsTypes.ADMINISTRATE) {
					this.barState = BarState.ADMIN;
					UiUtils.swtBeginInvoke(new Runnable() {
						public void run() {
							if (logger.isDebugEnabled()) {
								logger.debug("Creating Download progress bar");
							}
							PreferencesUILoader.this.bar = new DownloadProgressBar(
									PreferencesUILoader.this.bundle
											.getString(LOADING_ADMINISTRATE),
									SWT.TOP);
						}
					});
				} else if (optionType == OptionsTypes.PREFERENCES) {
					this.barState = BarState.PREF;
					UiUtils.swtBeginInvoke(new Runnable() {
						public void run() {
							if (logger.isDebugEnabled()) {
								logger.debug("Creating Download progress bar");
							}
							PreferencesUILoader.this.bar = new DownloadProgressBar(
									PreferencesUILoader.this.bundle
											.getString(LOADING_PREFERENCES),
									SWT.TOP);
						}
					});
				}
			} else if (this.barState == BarState.ADMIN) {
				this.barState = BarState.BOTH;
			} else if (this.barState == BarState.PREF) {
				this.barState = BarState.BOTH;
			}
		}
	}

	private void deactivateBar(OptionsTypes optionType) {
		synchronized (sync) {
			if (optionType == OptionsTypes.ADMINISTRATE) {
				if (this.barState == BarState.ADMIN) {
					this.barState = BarState.IDLE;
					if (logger.isDebugEnabled()) {
						logger.debug("Destroying Download progress bar");
					}
					this.bar.destroyDownloadBar();
				} else if (this.barState == BarState.BOTH) {
					this.barState = BarState.PREF;
					this.bar.setTitle(this.bundle
							.getString(LOADING_PREFERENCES));
				}
			} else if (optionType == OptionsTypes.PREFERENCES) {
				if (this.barState == BarState.PREF) {
					this.barState = BarState.IDLE;
					if (logger.isDebugEnabled()) {
						logger.debug("Destroying Download progress bar");
					}
					this.bar.destroyDownloadBar();
				} else if (this.barState == BarState.BOTH) {
					this.barState = BarState.ADMIN;
					this.bar.setTitle(this.bundle
							.getString(LOADING_ADMINISTRATE));
				}
			}
		}
	}

	private void loadData(OptionsTypes optionType) {
		if (optionType == OptionsTypes.ADMINISTRATE) {
			if (logger.isDebugEnabled()) {
				logger.debug("Loading preferences data for Administrate shell");
			}
			
			this.adminController = SupraSphereFrame.INSTANCE.client
					.getPreferencesAdminController();
			this.adminController.preloadData(SupraSphereFrame.INSTANCE.client.getForwardingController());
			if (logger.isDebugEnabled()) {
				logger.debug("Data for Administrate shell has been loaded");
			}
		} else if (optionType == OptionsTypes.PREFERENCES) {
			if (logger.isDebugEnabled()) {
				logger.debug("Loading preferences data for Preferences shell");
			}
			this.prefController = SupraSphereFrame.INSTANCE.client
					.getPreferencesController();
			this.prefController.preloadData(SupraSphereFrame.INSTANCE.client.getForwardingController());
			if (logger.isDebugEnabled()) {
				logger.debug("Data for Preferences shell has been loaded");
			}
		}
	}

	private void createGUI(final OptionsTypes optionType, final String sphereId) {
		if (optionType == OptionsTypes.ADMINISTRATE) {
			if (ManagePreferencesCommonShell.INSTANCE == null) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						if (logger.isDebugEnabled()) {
							logger
									.debug("Administrate shell createGUI performing");
						}
						try {
							new ManagePreferencesCommonShell(
								SupraSphereFrame.INSTANCE,
								PreferencesUILoader.this.adminController);
						}
						catch( Throwable ex ) {
							PreferencesUILoader.INSTANCE.destroyed( optionType );
							logger.error( "Can't create admin dialog", ex );
						}
					}
				});
			}
		} else if (optionType == OptionsTypes.PREFERENCES) {
			if (SetPreferencesCommonShell.INSTANCE == null) {
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						if (logger.isDebugEnabled()) {
							logger
									.debug("Preferences shell createGUI performing");
						}
						try {
							new SetPreferencesCommonShell(
								SupraSphereFrame.INSTANCE,
								PreferencesUILoader.this.prefController,
								sphereId);
						}
						catch( Throwable ex ) {
							PreferencesUILoader.INSTANCE.destroyed( optionType );
							logger.error( "Can't create prefference dialog", ex );
						}
						
					}
				});
			}
		}
	}
}
