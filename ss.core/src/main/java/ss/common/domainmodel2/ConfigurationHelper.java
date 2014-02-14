package ss.common.domainmodel2;

import ss.domainmodel.configuration.ConfigurationValue;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.AbstractHelper;
import ss.framework.domainmodel2.EditingScope;

public class ConfigurationHelper extends AbstractHelper {

	/**
	 * 
	 */
	private static final String MAIN_CONFIGURATION = "main";
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConfigurationHelper.class);
	/**
	 * @param spaceOwner
	 */
	public ConfigurationHelper(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}
	
	/**
	 */
	private synchronized Configuration getOrCreateConfiguration(String name) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( "name" );
		}
		final Configuration existed = Configuration.getByName(getSpaceOwner(), name);
		if ( existed != null ) {
			return existed;
		}
		final EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			return Configuration.createNew( getSpaceOwner(), name);
		}
		finally {
			editingScope.dispose();
		}				
	}
	
	public synchronized void setConfigurationValue(String name, ConfigurationValue configuration ) {
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			getOrCreateConfiguration(name).setValue( configuration );
		}
		finally {
			editingScope.dispose();
		}
	}
	
	public synchronized ConfigurationValue getConfigurationValue(String name) {
		return getOrCreateConfiguration(name).getValue();		
	}

	public synchronized void setMainConfigurationValue( ConfigurationValue configuration ) {
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			getOrCreateConfiguration(MAIN_CONFIGURATION).setValue( configuration );
		}
		finally {
			editingScope.dispose();
		}
	}
	
	public synchronized ConfigurationValue getMainConfigurationValue() {
		return getOrCreateConfiguration(MAIN_CONFIGURATION).getValue();		
	}
}
