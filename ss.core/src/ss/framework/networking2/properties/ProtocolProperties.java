/**
 * 
 */
package ss.framework.networking2.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import ss.common.CompareUtils;
import ss.common.IdentityUtils;
import ss.common.ListUtils;

/**
 *
 */
public final class ProtocolProperties implements Serializable {
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ProtocolProperties.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 3515005141029820922L;

	private final Hashtable<Class, ProtocolProperty> properties = new  Hashtable<Class, ProtocolProperty>();
	
	private List<Class> classificableProperties;
	
	public ProtocolProperties( String displayName ) {
		this( IdentityUtils.generateUuid(), displayName );
	}

	/**
	 */
	private ProtocolProperties( UUID uuid, String displayName ) {
		addProperty( new ProtocolRuntimeId(uuid.toString()) );
		addProperty( new ProtocolDisplayName(displayName) );		
	}

	/**
	 */
	final void addProperty(ProtocolProperty property ) {
		if ( this.properties.contains( property.getClass() ) ) {
			throw new IllegalArgumentException( "Protocol identity contains property " + property );
		}
		this.properties.put( property.getClass(), property );
	}

	/**
	 * @return the uniqueId
	 */
	public final String getRuntimeId() {
		return requireProperty( ProtocolRuntimeId.class ).getValue();
	}
	
	/**
	 * @return the uniqueId
	 */
	public final <P extends ProtocolProperty> P requireProperty( Class<P> propertyClass ) {
		final P property = getProperty( propertyClass );
		if ( property == null ) {
			throw new IllegalArgumentException( "Cannot find property " + propertyClass );
		}
		return property;
	}
	
	@SuppressWarnings("unchecked")
	public final <P extends ProtocolProperty> P getProperty( Class<P> propertyClass ) {
		return (P) this.properties.get( propertyClass );
	}

	/**
	 * @param otherProperty
	 * @return
	 */
	public boolean containsProperty(ProtocolProperty otherProperty) {
		final ProtocolProperty property = getProperty(otherProperty.getClass());
		return property.equals( otherProperty );
	}
	
	public boolean constainsProperty(Class propertyClass) {
		return this.properties.contains(propertyClass);
	}

	/**
	 * @return
	 */
	public Iterable<Class> getClassificableProperties() {
		if (this.classificableProperties == null) {
			this.classificableProperties = createClassificablePropertiesList();
			if (logger.isDebugEnabled()) {
				logger.debug( "Classificable properties " + ListUtils.allValuesToString( this.classificableProperties ) + " for " + this );
			}
		}
		return this.classificableProperties;
	}
	
	@SuppressWarnings("unchecked")
	private List<Class> createClassificablePropertiesList() {
		ArrayList<Class> classificableProperties = new ArrayList<Class>(); 
		for(Enumeration<Class> propertiesClasses = this.properties.keys();propertiesClasses.hasMoreElements();) {
			Class propertyClass = propertiesClasses.nextElement();
			if ( IClassificableProperty.class.isAssignableFrom( propertyClass ) ) {
				classificableProperties.add( propertyClass );
			}
		}
		return classificableProperties;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getRuntimeId().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProtocolProperties other = (ProtocolProperties) obj;
		return CompareUtils.equals(this.getRuntimeId(), other.getRuntimeId() );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( ProtocolProperty property : this.properties.values() ) {
			if ( sb.length() > 0 ) {
				sb.append( ", " );
			}
			sb.append( property );
		}
		return sb.toString();
	}
	
	
	
	
	
	
}
