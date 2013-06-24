/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.Hashtable;

import ss.common.ReflectionUtils;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 *
 */
public class DomainObjectDescriptor {

	private final Class<? extends DomainObject> domainObjectClass;
	
	private final Hashtable<String, FieldDescriptor> nameToDescriptor = new Hashtable<String, FieldDescriptor>();
	
	/**
	 * @param domainObjectClass
	 */
	public DomainObjectDescriptor(final Class<? extends DomainObject> domainObjectClass) {
		super();
		this.domainObjectClass = domainObjectClass;
	}

	private FieldDescriptor get( String name ) {
		return this.nameToDescriptor.get( name );
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <E extends XmlEntityObject> XmlEntityFieldDescriptor<E> get(Class<E> valueClass, String name) {
		FieldDescriptor rawDescriptor = get( name );
		if ( rawDescriptor == null ) {
			rawDescriptor = new XmlEntityFieldDescriptor<E>( this.domainObjectClass, name, valueClass );
			add( rawDescriptor );
		}
		if ( !XmlEntityFieldDescriptor.class.isInstance( rawDescriptor ) ) {
			throw new InvalidFieldDescriptorClassException( rawDescriptor );
		}
		final XmlEntityFieldDescriptor<E> descriptor = (XmlEntityFieldDescriptor)rawDescriptor;
		if ( descriptor.getEntityObjectClass() != valueClass ) {
			throw new InvalidFieldDescriptorException( rawDescriptor, "Illegal entity class." );	
		}		
		return descriptor;
	}

	@SuppressWarnings("unchecked")
	public <D extends DomainObject> ReferenceFieldDescriptor<D> get(Class<D> valueClass, String name) {
		FieldDescriptor rawDescriptor = get( name );
		if ( rawDescriptor == null ) {
			// TODO think about this creation
			rawDescriptor = new ReferenceFieldDescriptor<D>( this.domainObjectClass, name, valueClass );
			add( rawDescriptor );
		}
		if ( !ReferenceFieldDescriptor.class.isInstance( rawDescriptor ) ) {
			throw new InvalidFieldDescriptorClassException( rawDescriptor );
		}
		final ReferenceFieldDescriptor<D> descriptor = (ReferenceFieldDescriptor) rawDescriptor;
		if ( descriptor.getTargetDomainObjectClass() != valueClass ) {
			throw new InvalidFieldDescriptorException( rawDescriptor, "Illegal domain class." );	
		}
		return descriptor; 
	}
	
	@SuppressWarnings("unchecked")
	public <F extends Field> FieldDescriptor<F, ?> get(Class<F> fieldClass, String name) {
		FieldDescriptor rawDescriptor = get( name );
		final Class descriptorClass = FieldToDescriptorMap.INSTANCE.get(fieldClass);		
		if ( rawDescriptor == null ) {
			rawDescriptor = createDescriptor( descriptorClass, name );
		}		
		if ( !descriptorClass.isInstance( rawDescriptor ) ) {
			throw new InvalidFieldDescriptorException( rawDescriptor, "Illegal descriptor class." );
		}
		return rawDescriptor;
	}
	
	/**
	 * @param descriptorClass
	 * @param name 
	 */
	private <F extends FieldDescriptor> F createDescriptor(Class<F> descriptorClass, String name) {
		return ReflectionUtils.create( descriptorClass, this.domainObjectClass, name );
	}

	
	public synchronized void add(FieldDescriptor fieldDescriptor) {
		if ( this.nameToDescriptor.contains( fieldDescriptor.getName() ) ) {
			throw new DuplicateDescriptorException( fieldDescriptor );
		}
		this.nameToDescriptor.put(fieldDescriptor.getName(), fieldDescriptor);
	}
	
	/**
	 * @return the domainObjectClass
	 */
	public Class<? extends DomainObject> getDomainObjectClass() {
		return this.domainObjectClass;
	}
	
	/**
	 *
	 */
	public static class InvalidFieldDescriptorException extends
			IllegalStateException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2650594043735823931L;

		/**
		 * @param rawDescriptor
		 */
		public InvalidFieldDescriptorException(FieldDescriptor rawDescriptor ) {
			this( rawDescriptor, null );
		}

		/**
		 * @param rawDescriptor
		 */
		public InvalidFieldDescriptorException(FieldDescriptor rawDescriptor, String details  ) {
			super( "Illegal descriptor " + rawDescriptor +  ( details != null ? ".Details: " + details : "" ) );
		}
	}
	
	/**
	 *
	 */
	public static class InvalidFieldDescriptorClassException extends
		InvalidFieldDescriptorException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1636759033273786459L;

		/**
		 * @param rawDescriptor
		 */
		public InvalidFieldDescriptorClassException(FieldDescriptor rawDescriptor ) {
			super( rawDescriptor, "Illegal descriptor class." );
		}
	}
	
	public static class DuplicateDescriptorException extends InvalidFieldDescriptorException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2423208629366948069L;

		/**
		 * @param rawDescriptor
		 */
		public DuplicateDescriptorException(FieldDescriptor rawDescriptor) {
			super(rawDescriptor, "Descriptor with given name already exists." );
		}		
	
	}
}
