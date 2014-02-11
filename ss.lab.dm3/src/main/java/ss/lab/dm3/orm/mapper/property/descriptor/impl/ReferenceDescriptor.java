/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ReferenceHolder;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public class ReferenceDescriptor<T extends MappedObject,V> extends PropertyDescriptor<V> implements IReferenceDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1999859657263684449L;

	protected final Class<T> targetEntityClass;

	protected Multiplicity multiplicity = Multiplicity.Unknown;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 * @param targetEntityClass
	 */
	public ReferenceDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<V> valueClazz, Class<T> targetEntityClass) {
		super(beanClazz, name, valueClazz );
		if ( targetEntityClass == null ) {
			throw new NullPointerException( "targetEntityClass" );
		}
		this.targetEntityClass = targetEntityClass;
	}

	public ReferenceHolder createReferenceHolder() {
		return new ReferenceHolder( this.beanClazz, this.targetEntityClass, this.name, null );
	}


	public Class<? extends MappedObject> getTargetEntityClass() {
		return this.targetEntityClass;
	}

	
	/**
	 * @return the multiplicity
	 */
	public Multiplicity getMultiplicity() {
		return this.multiplicity;
	}

	/**
	 * @param multiplicity the multiplicity to set
	 */
	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( getBeanClazz().getSimpleName() + "." + getName() + "->" + getTargetEntityClass().getSimpleName() );
		return tsb.toString();
	}
	
}
