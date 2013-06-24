/**
 * 
 */
package ss.client.debug;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.client.configuration.SphereConnectionUrl;
import ss.client.networking.CustomStartUpSessionFactory;
import ss.client.networking.IStartUpSessionFactory;
import ss.client.ui.IllegalSphereUrlException;
import ss.common.XmlDocumentUtils;

/**
 * 
 */
public class SetLoginParamsDebugCommand extends AbstractDebugCommand {

	/**
	 * 
	 */
	private static final String DEFAULT_LOGIN_PARAMS_FILE_NAME = "./debug_console_login_params.xml";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetLoginParamsDebugCommand.class);

	private String login;

	private String password;

	private String sphereUrl;

	/**
	 * @param displayName
	 */
	protected SetLoginParamsDebugCommand() {
		super("set-login-params",
				"Set up login params for debug functionality.");
	}

	/**
	 * 
	 */
	private void initByDefault() {
		final File file = new File(DEFAULT_LOGIN_PARAMS_FILE_NAME);
		if (!file.exists()) {
			return;
		}
		final Document document;
		try {
			document = XmlDocumentUtils.load(file);
		} catch (DocumentException ex) {
			logger.warn("Can't restore login params", ex);
			return;
		}
		LoginParamsXmlEntity loginParams = LoginParamsXmlEntity.wrap(document);
		this.login = loginParams.getLogin();
		this.password = loginParams.getPassword();
		this.sphereUrl = loginParams.getSphereUrl();
		try {
			createStartUpSessionFactory();
		} catch (DebugCommanRunntimeException ex) {
			logger.warn("Can't create startup session factory", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		if (this.login == null) {
			initByDefault();
		}
		else {
			createStartUpSessionFactory();
			getCommandOutput().appendln("OK");
		}
	}

	/**
	 * @throws DebugCommanRunntimeException
	 */
	private void createStartUpSessionFactory()
			throws DebugCommanRunntimeException {
		SphereConnectionUrl sphereConnectionUrl;
		try {
			sphereConnectionUrl = new SphereConnectionUrl(this.sphereUrl);
		} catch (IllegalSphereUrlException ex) {
			throw new DebugCommanRunntimeException(ex);
		}
		final IStartUpSessionFactory startUpSessionFactory = new CustomStartUpSessionFactory(
				this.login, this.password, sphereConnectionUrl);
		DebugProtocolFactory.INSTANCE
				.setExplicitStartUpSessionFactory(startUpSessionFactory);
		if (logger.isDebugEnabled()) {
			logger.debug( "Start up session factory created " + startUpSessionFactory);
		}
		// saveDefaultLoginParams();
	}

	/**
	 * 
	 */
	protected void saveDefaultLoginParams() {
		final File file = new File(DEFAULT_LOGIN_PARAMS_FILE_NAME);
		LoginParamsXmlEntity loginParams = new LoginParamsXmlEntity();
		loginParams.setLogin( this.login );
		loginParams.setPassword( this.password );
		loginParams.setSphereUrl( this.sphereUrl );
		try {
			XmlDocumentUtils.save(file, loginParams.getDocumentCopy() );
		} catch (DocumentException ex) {
			logger.error( "Can't save default login params to " + file, ex );
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.debug.AbstractDebugCommand#processCommandLine(ss.client.debug.ParsedDebugCommandLine)
	 */
	@Override
	protected void processCommandLine(
			ParsedDebugCommandLine parsedDebugCommandLine) {
		super.processCommandLine(parsedDebugCommandLine);
		if (parsedDebugCommandLine.getCount() == 0) {
			getCommandOutput()
			.appendln( "Loading login params from: " + DEFAULT_LOGIN_PARAMS_FILE_NAME );
		}
		else if (parsedDebugCommandLine.getCount() != 3) {
			getCommandOutput()
					.appendln(
							"Please use syntax: set-login-params <login> <human-password> <connection-url>");
			this.login = null;
			this.password = null;
			this.sphereUrl = null;
		} else {
			this.login = parsedDebugCommandLine.getArg0();
			this.password = parsedDebugCommandLine.getArg1();
			this.sphereUrl = parsedDebugCommandLine.getArg2();
		}
	}

}
