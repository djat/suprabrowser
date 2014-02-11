/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.NativeReferenceAccessor;
import ss.lab.dm3.orm.mapper.property.converter.MappedObjectTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;

/**
 * @author Dmitry Goncharov
 */
public class NativeReferenceDescriptor<T extends MappedObject> extends ReferenceDescriptor<T,T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1729645366614539588L;


	/**
	 * @param beanClazz
	 * @param name
	 * @param targetEntityClass
	 */
	public NativeReferenceDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> targetEntityClass, Multiplicity multiplicity) {
		super(beanClazz, name, targetEntityClass, targetEntityClass);
		setMultiplicity(multiplicity);
	}

	@Override
	public IAccessor createPropertyAccessor() {
		return new NativeReferenceAccessor( super.createMethodAccessor(), this.name, this.targetEntityClass );
	}

	@Override
	public TypeConverter<T> createTypeConverter() {
		return new MappedObjectTypeConverter<T>( this.targetEntityClass );
	}
	
}
