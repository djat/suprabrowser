package ss.lab.dm3.orm.mapper.property;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ReferenceHolder;
import ss.lab.dm3.orm.mapper.property.accessor.IManagedAccessor;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;
import ss.lab.dm3.orm.mapper.property.descriptor.ICollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.ISerializableDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public class Property<T> {

	private final PropertyDescriptor<T> descriptor;

	private final IAccessor accessor;

	private final TypeConverter<T> typeConverter;

	private final int serializationIndex;
	
	/**
	 * @param propertyDescriptor
	 * @param serializationIndex
	 */
	public Property(PropertyDescriptor<T> propertyDescriptor,
			int serializationIndex) {
		super();
		this.descriptor = propertyDescriptor;
		this.accessor = propertyDescriptor.createPropertyAccessor();
		this.typeConverter = propertyDescriptor.createTypeConverter();
		this.serializationIndex = serializationIndex;
	}

	public T getValue(Object bean) {
		return this.typeConverter.cast(this.accessor.getValue(bean));
	}

	public void setValue(Object bean, Object value) {
		this.accessor.setValue(bean, value);
	}

	public void save(Object fromBean, Serializable[] toValues) {
		if (isSerializable()) {
			Object value = this.accessor.getValue(fromBean);
			Serializable saveValue = this.typeConverter.toSerializable(value);
			toValues[this.serializationIndex] = saveValue;
		}
	}

	public void load(Object toBean, Serializable[] fromValues ) {
		if (isSerializable()) {
			Serializable loadValue = fromValues[ this.serializationIndex ];
			T value = this.typeConverter.fromSerializable( loadValue );
			this.accessor.setValue(toBean, value );
		}
	}
	
	public boolean loadOnlyIfNew(Object toBean, Serializable[] fromValues ) {
		if (isSerializable()) {
			Serializable loadValue = fromValues[ this.serializationIndex ];
			T value = this.typeConverter.fromSerializable( loadValue );
			final Object actualValue = this.accessor.getValue(toBean);
			if ( actualValue == value || 
				 (actualValue != null && actualValue.equals( value ) ) ) {
				return false;
			}
			this.accessor.setValue(toBean, value );
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getName() {
		return this.descriptor.getName();
	}
	
	public String getPersistentName() {
		return this.descriptor.getPersistentName();
	}

	/**
	 * @param owner
	 * @return
	 */
	public ReferenceHolder createReferenceHolder() {
		return ((IReferenceDescriptor)this.descriptor).createReferenceHolder();
	}
	
	/**
	 * @return
	 */
	public boolean isSerializable() {
		return this.descriptor instanceof ISerializableDescriptor;
	}
	/**
	 * @return
	 */
	public boolean isReference() {
		return this.descriptor instanceof IReferenceDescriptor;
	}

	/**
	 * @return
	 */
	public boolean isCollection() {
		return this.descriptor instanceof ICollectionDescriptor;
	}

	/**
	 * @return the serializationIndex
	 */
	public int getSerializationIndex() {
		return this.serializationIndex;
	}

	/**
	 * 
	 */
	public void setUpManagedFeatures( MappedObject bean) {
		if ( this.accessor instanceof IManagedAccessor) {
			((IManagedAccessor)this.accessor).setUpManagedFeatures( bean );
		}
	}

	/**
	 * @return the accessor
	 */
	public IAccessor getAccessor() {
		return this.accessor;
	}

	/**
	 * @param bean
	 */
	public void refresh(Object bean) {
		this.accessor.refresh(bean);
	}

	public TypeConverter<T> getTypeConverter() {
		return this.typeConverter;
	}

	public PropertyDescriptor<T> getDescriptor() {
		return this.descriptor;
	}

	/**
	 * 
	 */
	public void resetToDefault(MappedObject bean) {
		this.accessor.resetToDefault(bean);
	}
	
	public Class<T> getValueClazz() {
		return this.descriptor.getValueClazz();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "name", getName() );
		tsb.append( "typeConverter", this.typeConverter );
		return tsb.toString();
	}
	
	
}
