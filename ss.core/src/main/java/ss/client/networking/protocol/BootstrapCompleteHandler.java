/**
 * Jul 5, 2006 : 1:06:11 PM
 */
package ss.client.networking.protocol;

import java.io.File;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class BootstrapCompleteHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BootstrapCompleteHandler.class);

	public String fsep = System.getProperty("file.separator");

	private final DialogsMainCli cli;
	
	public BootstrapCompleteHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleBootstrapComplete(final Hashtable update) {
		VerifyAuth verifyAuth = (VerifyAuth) update
		.get(SessionConstants.VERIFY_AUTH);
		logger.info("got bootstrap complete, VerifyAuth is " +  verifyAuth );
		this.cli.setVeryfyAuthIfNull(verifyAuth);
		if ( SupraSphereFrame.INSTANCE != null || this.cli.getSF() != null ) {
			logger.warn( "Start default content loading" );
			startDefaultContentLoad( verifyAuth );
		}
		else {
			logger.warn( "SupraSphereFrame not found. Don't query default content." );
		}		
	}

	/**
	 * @param verifyAuth
	 */
	private void startDefaultContentLoad(VerifyAuth verifyAuth) {
		Document versionsDoc = null;
		final File file = new File(System.getProperty("user.dir") + this.fsep
				+ "supraversions.xml");
		final SAXReader saxReader = new SAXReader();
		try {
			versionsDoc = saxReader.read(file);
		} catch (Exception e) {
			logger.error( "Version read failed", e);
		}
		SupraSphereFrame frame = this.cli.getSF();
		if ( frame != null ) {
			frame.ensureClientInitialized( this.cli );
		}

		if (versionsDoc != null) {
			this.cli.checkForNewVersion(versionsDoc, verifyAuth);
		} else {
			this.cli.getSF().getWelcomeScreen().closeFromWithin();
			logger.info("bootstrapcomplete");
		}
	}

	public String getProtocol() {
		return SSProtocolConstants.BOOTSTRAP_COMPLETE;
	}

	public void handle(Hashtable update) {
		handleBootstrapComplete(update);
	}

}
