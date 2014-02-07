package ss.common.domainmodel2;

import ss.domainmodel.configuration.ConfigurationValue;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.StringField;
import ss.framework.domainmodel2.StringFieldDescriptor;
import ss.framework.domainmodel2.XmlEntityField;

public class Configuration extends DomainObject {
	
	public static class NameDescriptor extends StringFieldDescriptor {
		public NameDescriptor() {
			super(Configuration.class,"name");
		}
	}
	
	private final StringField name = createField( NameDescriptor.class );
	
	private final XmlEntityField<ConfigurationValue> value = createFieldXmlEntityField(ConfigurationValue.class, "value_xml" );
		
	/**
	 * @return
	 */
	public static Configuration getByName(AbstractDomainSpace spaceOwner, String name) {
		return spaceOwner.getSingleObject( CriteriaFactory.createEqual( Configuration.class, NameDescriptor.class, name ) );
	}
	
	/**
	 * @return
	 */
	public static Configuration createNew(AbstractDomainSpace spaceOwner, String name) {
		if ( name == null || name.length() == 0 ) {
			throw new IllegalArgumentException( "name" );
		}
		Configuration settings = new Configuration( spaceOwner );
		settings.name.setSilently(name);
		settings.markNew();
		return settings;
	}
	/**
	 * @param space
	 */
	public Configuration(AbstractDomainSpace space) {
		super(space);
	}

	public void setName(String name) {
		this.name.set(name );
	}

	public String getName() {
		return this.name.get();
	}
		
	/**
	 * @return the configuration value
	 */
	public ConfigurationValue getValue() {
		return this.value.get();
	}

	/**
	 */
	public void setValue(ConfigurationValue value) {
		this.value.set(value);
	}
	
}
