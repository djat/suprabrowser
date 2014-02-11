/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor;

import java.io.Serializable;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.FieldAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.MethodAccessor;
import ss.lab.dm3.orm.mapper.property.converter.ConverterFactory;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;
import ss.lab.dm3.utils.ReflectionHelper;

/**
 * @author Dmitry Goncharov
 */
public abstract class PropertyDescriptor<T> implements Serializable, IPropertyDescriptor {

	public enum AccessType {
		FIELD, METHOD
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -798128624008119233L;

	protected final Class<? extends MappedObject> beanClazz;

	protected final String name;

	protected final Class<T> valueClazz;
	
	private final String persistentName;
	
	private AccessType preferedAccessType = AccessType.METHOD;
	
	private boolean cascadeFetch = false;
	
	private boolean searchableField = false;


	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 */
	public PropertyDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz) {
		super();
		if ( beanClazz == null ) {
			throw new NullPointerException( "beanClazz" );
		}
		if ( name == null ) {
			throw new NullPointerException( "name" );
		}
		if ( valueClazz == null ) {
			throw new NullPointerException( "valueClazz" );
		} 
		this.beanClazz = beanClazz;
		this.name = name;
		this.persistentName = toPersistentName( name );
		this.valueClazz = valueClazz;
	}

	public boolean isCascadeFetch() {
		return this.cascadeFetch;
	}

	public void setCascadeFetch(boolean cascadeFetch) {
		this.cascadeFetch = cascadeFetch;
	}
	
	public boolean isSearchableField() {
		return searchableField;
	}
	
	public void setSearchableField(boolean searchableField) {
		this.searchableField = searchableField;
	}
	/**
	 * @param name2
	 * @return
	 */
	private static String toPersistentName(String name) {
		if ( name.length() > 0 ) {
			final char firstChar = name.charAt( 0 );
			return Character.toLowerCase(firstChar ) + name.substring( 1 );
		}
		else {
			return name; 
		}
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the valueClazz
	 */
	public final Class<T> getValueClazz() {
		return this.valueClazz;
	}

	/**
	 * @return
	 */
	public IAccessor createPropertyAccessor() {
		return createDefaultPropertyAccessor();
	}

	/**
	 * @return
	 */
	protected final IAccessor createDefaultPropertyAccessor() {
		if (this.preferedAccessType == AccessType.FIELD) {
			return createFieldAccessor();
		} else {
			return createMethodAccessor();
		}
	}

	/**
	 * @return
	 */
	protected FieldAccessor createFieldAccessor() {
		return new FieldAccessor(ReflectionHelper.getPropertyDeclaration(
				this.beanClazz, this.name));
	}

	/**
	 * @return
	 */
	protected MethodAccessor createMethodAccessor() {
		return new MethodAccessor(ReflectionHelper.getGetter(
				this.beanClazz, this.name), ReflectionHelper.getSetter(
				this.beanClazz, this.name));
	}

	/**
	 * @return
	 */
	public TypeConverter<T> createTypeConverter() {
		return ConverterFactory.INSTANCE.create(this.valueClazz);
	}

	public AccessType getPreferedAccessType() {
		return this.preferedAccessType;
	}

	public void setPreferedAccessType(AccessType preferedAccessType) {
		this.preferedAccessType = preferedAccessType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.beanClazz == null) ? 0 : this.beanClazz.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PropertyDescriptor<?> other = (PropertyDescriptor<?>) obj;
		if (this.beanClazz == null) {
			if (other.beanClazz != null)
				return false;
		} else if (!this.beanClazz.equals(other.beanClazz))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}
	
	public void validate() {		
	}

	/**
	 * @return the beanClazz
	 */
	public Class<? extends MappedObject> getBeanClazz() {
		return this.beanClazz;
	}

	/**
	 * @return
	 */
	public String getPersistentName() {
		return this.persistentName;
	}

}
