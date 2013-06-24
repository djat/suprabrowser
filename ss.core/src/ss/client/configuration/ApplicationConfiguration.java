/**
 * 
 */
package ss.client.configuration;

import java.io.File;
import ss.client.ui.IllegalSphereUrlException;
import ss.common.ArgumentNullPointerException;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.framework.entities.xmlentities.XmlEntityUtils;
import ss.util.VariousUtils;

/**
 * 
 */
public class ApplicationConfiguration extends XmlEntityObject {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ApplicationConfiguration.class);

	private final File bindedFile;

	private final ISimpleEntityProperty address = super
			.createAttributeProperty("address/@value");

	private final ISimpleEntityProperty port = super
			.createAttributeProperty("port/@value");

	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("supra_sphere/@value");

	private final ISimpleEntityProperty showInvitationUi = super
			.createAttributeProperty("show_invitation_gui/@value");

	private final ISimpleEntityProperty xulrunnerRegistered = super
		.createAttributeProperty("xulrunnerRegistered/@value");

	/**
	 * @param bindedFile
	 */
	public ApplicationConfiguration(final File bindedFile) {
		super("dyn_client");
		if (bindedFile == null) {
			throw new ArgumentNullPointerException("bindedFile");
		}
		this.bindedFile = bindedFile;
		reload();
	}

	/**
	 * @param sphereURL
	 */
	public void setConnectionUrl(String sphereUrl) {
		SphereConnectionUrl sphereConnectionUrl;
		try {
			sphereConnectionUrl = new SphereConnectionUrl(sphereUrl);
		} catch (IllegalSphereUrlException ex) {
			logger.error("Can't parse connection url, use default instead");
			sphereConnectionUrl = new SphereConnectionUrl();
		}
		setConnectionUrl(sphereConnectionUrl);
	}

	/**
	 * @param sphereConnectionUrl
	 */
	public void setConnectionUrl(SphereConnectionUrl sphereConnectionUrl) {
		if ( sphereConnectionUrl == null ) {
			throw new NullPointerException( "sphereConnectionUrl" );
		}
		setAddress(sphereConnectionUrl.getServer());
		setPort(sphereConnectionUrl.getPort());
		setSphereId(sphereConnectionUrl.getSphereId());
	}

	/**
	 * @param sphereId2
	 */
	private void setSphereId(String value) {
		this.sphereId.setValue(value);
	}

	/**
	 * @param server
	 */
	private void setAddress(String server) {
		this.address.setValue(server);
	}

	/**
	 * 
	 */
	public void save() {
		XmlEntityUtils.safeSave(this.bindedFile, this);		
	}

	/**
	 * @return
	 */
	public static ApplicationConfiguration loadUserConfiguration() {
		final ApplicationConfiguration configuration = createConfiguration();
		configuration.reload();
		return configuration;
	}

	/**
	 * @return
	 */
	private static ApplicationConfiguration createConfiguration() {
		final File file = VariousUtils.getSupraFile("dyn_client.xml");
		final ApplicationConfiguration configuration = new ApplicationConfiguration( file );
		return configuration;
	}

	/**
	 * @return
	 */
	public static ApplicationConfiguration createBlankUserConfiguration() {
		final ApplicationConfiguration configuration = createConfiguration();
		configuration.setAddress( "suprasecure.com" );
		configuration.setPort( 3000 );
		configuration.setSphereId( "" );
		return configuration;
	}

	private void reload() {
		XmlEntityUtils.safeLoad(this.bindedFile, this);
	}

	/**
	 * @return the initialSphereUrl
	 */
	public String getConnectionUrl() {
		SphereConnectionUrl url = new SphereConnectionUrl();
		url.setServer(getAddress());
		url.setPort(getPort());
		url.setSphereId(getSphereId());
		return url.toString();
	}

	/**
	 * @return
	 */
	private String getAddress() {
		return this.address.getValue();
	}

	/**
	 * @return
	 */
	private String getSphereId() {
		return this.sphereId.getValue();
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port.getIntValue();
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port.setIntValue(port);
	}

	public boolean isShowInvitationUi() {
		return this.showInvitationUi.getBooleanValue();
	}

	/**
	 * @return 
	 * 
	 */
	public XulRunnerRegisterState getXulrunnerRegistered() {
		return this.xulrunnerRegistered.getEnumValue( XulRunnerRegisterState.class );
	}
	
	/**
	 * @return 
	 * 
	 */
	public void setXulRunnerRegistered( XulRunnerRegisterState value) {
		this.xulrunnerRegistered.setEnumValue(value);
	}

	
}
