package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedReference;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.QualifiedReferenceAccessor;
import ss.lab.dm3.orm.mapper.property.converter.QualifiedReferenceTypeConverter;
import ss.lab.dm3.orm.mapper.property.converter.TypeConverter;

/**
 * @author Dmitry Goncharov
 *
 * @param <T>
 */
public final class QualifiedReferenceDescriptor<T extends MappedObject> extends ReferenceDescriptor<T,QualifiedReference<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 713386825325425751L;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 * @param targetEntityClass
	 */
	public QualifiedReferenceDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> targetEntityClass, Multiplicity multiplicity) {
		super(beanClazz, name, QualifiedReference.wrap( targetEntityClass ), targetEntityClass);
		setMultiplicity(multiplicity);
	}
	
	@Override
	public IAccessor createPropertyAccessor() {
		return new QualifiedReferenceAccessor( super.createMethodAccessor(), this.name, this.targetEntityClass );
	}

	@Override
	public TypeConverter<QualifiedReference<T>> createTypeConverter() {
		return new QualifiedReferenceTypeConverter<T>( this.targetEntityClass );
	}
}
