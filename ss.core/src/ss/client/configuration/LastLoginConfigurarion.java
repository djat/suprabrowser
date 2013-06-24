/**
 * 
 */
package ss.client.configuration;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;
import ss.common.XmlDocumentUtils;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.util.LocationUtils;

/**
 * 
 */
public class LastLoginConfigurarion extends XmlEntityObject {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LastLoginConfigurarion.class);
	
	/**
	 * 
	 */
	private static final String DEFAULT_PROFILE_ID = "0000000000000000000";

	private final File bindedFile;
	
	private String user;

	private String profileId;

	private String pass;

	private boolean userNameAndPasswordLoaded = false;

	
	/**
	 * @param bindedFile
	 */
	public LastLoginConfigurarion(final File bindedFile) {
		super();
		if (bindedFile == null) {
			throw new ArgumentNullPointerException("bindedFile");
		}
		this.bindedFile = bindedFile;
	}

	/**
	 * @return
	 */
	public static LastLoginConfigurarion loadUserConfiguration() {
		final LastLoginConfigurarion configuration = createConfiguration();
		configuration.reload();
		return configuration;
	}

	/**
	 * @return
	 */
	private static LastLoginConfigurarion createConfiguration() {
		final LastLoginConfigurarion configuration = new LastLoginConfigurarion( LocationUtils.getLastLoginFile() );
		return configuration;
	}

	/**
	 * @return
	 */
	public static LastLoginConfigurarion createBlankUserConfiguration() {
		LastLoginConfigurarion configuration = createConfiguration();
		return configuration;
	}

	/**
	 * 
	 */
	public void reload() {
		if ( this.bindedFile.exists() ) {
			try {
				final Document doc = XmlDocumentUtils.load(this.bindedFile);
				parse( doc );
			} catch (DocumentException ex) {
				logger.error( "Can't load xml document from " + this.bindedFile,  ex );
			}
		}
		else {
			logger.warn( "File not found " + this.bindedFile );
		}
	}

	/**
	 * @param doc
	 */
	private void parse(Document doc) {
		final Element root = doc.getRootElement();
		if ( root == null ) {
			logger.warn( "Root element is null" );
			return;
		}
		final Element prevLogins = root.element("prev_logins");
		if ( prevLogins == null) {
			logger.warn( "prev_logins element is null" );
			return;
		}
		final Element login = prevLogins.element(
							"login");
		if ( login == null ) {
			logger.warn( "login element is null" );
			return;
		}
		this.user = login.attributeValue("username");
		this.pass = login.attributeValue("passphrase");
		this.userNameAndPasswordLoaded = true;
		this.profileId = login.attributeValue("profile_id");
		if (this.profileId == null ) {
			this.profileId = DEFAULT_PROFILE_ID;
		}
	}
		
	
	public boolean isUserNameAndPasswordLoaded() {
		return this.userNameAndPasswordLoaded;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return this.pass;
	}

	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return this.profileId;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @param profileId
	 *            the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}
