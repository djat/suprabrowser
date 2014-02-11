package ss.lab.dm3.connection.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.persist.backend.search.SearchConfiguration;

public class ConfigurationBuilder {

	protected static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
	.getLog( ConfigurationBuilder.class );
	
	private String baseFolder;
	
	private Configuration configuration;
	
	private final IConfigurationProvider configurationProvider;

	private String baseDir;
	
	public ConfigurationBuilder(IConfigurationProvider configurationProvider) {
		super();
		this.configurationProvider = configurationProvider;
	}

	public String getBaseFolder() {
		return this.baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public Configuration create(){
		this.configuration = this.configurationProvider.get();
				
		
		InputStream propertiesStream = getClass().getResourceAsStream("/domain.properties");
		if ( propertiesStream == null ) {
			throw new CantCreateConfigurationException( "Properties stream is null" );
		}
		Properties properties = new Properties();
		try {
			proccessProperties(propertiesStream, properties);
		} catch (IOException e) {
			throw new CantCreateConfigurationException( "Can't load properties from " + propertiesStream, e );
		}	
		return this.configuration;
	}
	
	private void proccessProperties(InputStream resourceAsStream,
			Properties properties) throws IOException {
		properties.load(resourceAsStream);
		String dbuser = properties.getProperty("dbuser");
		String dbpassword = properties.getProperty("dbpassword");
		String dburl = properties.getProperty("dburl");
		this.baseDir = properties.getProperty("baseDir");
		
		if (log.isDebugEnabled()) {
			log.debug("dbuser: " + dbuser + "; dbpass: " + dbpassword + "; dburl: " + dburl);
		}
		
		this.configuration.getBlobConfiguration().setBaseDir( getBlobBaseDir() );
		this.configuration.getSearchConfiguration().setBaseDir( getIndexBaseDir() );
		this.configuration.setDbUser(dbuser);
		this.configuration.setDbPassword(dbpassword);
		this.configuration.setDbUrl(dburl);
	}

	private File getIndexBaseDir() {
		File indexFile = new File(this.baseFolder, SearchConfiguration.INDEX_FOLDER);
		return this.baseDir != null ? ( !this.baseDir.equals("") ? new File(this.baseDir, SearchConfiguration.INDEX_FOLDER) : indexFile ) : indexFile;
	}

	private File getBlobBaseDir() {
		File baseFile = new File(this.baseFolder, BlobConfiguration.ATTACHMENT_FOLDER);
		return this.baseDir != null ? ( !this.baseDir.equals("") ? new File(this.baseDir + BlobConfiguration.ATTACHMENT_FOLDER) : baseFile ) : baseFile;
	}

	public IConfigurationProvider getConfigurationProvider() {
		return this.configurationProvider;
	}
}
