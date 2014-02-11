package ss.lab.dm3.connection.configuration;

public abstract class ConfigurationProvider implements IConfigurationProvider {

	private Configuration configuration = null;

	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.configuration.IConfigurationProvider#get()
	 */
	public synchronized Configuration get() {
		if ( this.configuration == null ) {
			this.configuration = new Configuration();
			setUp( this.configuration );
		}
		return this.configuration;
	}

	protected void setUp(Configuration configuration) {
	}

}
