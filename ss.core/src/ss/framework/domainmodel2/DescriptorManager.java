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
public final class DescriptorManager {

	/**
	 * Singleton instance
	 */
	public final static DescriptorManager INSTANCE = new DescriptorManager();

	private final Hashtable<Class, FieldDescriptor> indexedDescriptorClassToInstance = new Hashtable<Class, FieldDescriptor>();
	
	private final Hashtable<Class, DomainObjectDescriptor> domainClassToDescriptor = new Hashtable<Class, DomainObjectDescriptor>();

	private DescriptorManager() {
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends FieldDescriptor> T get(Class<T> indexedDescriptorClass) {
		T fieldDescriptor = indexedDescriptorClass
				.cast(this.indexedDescriptorClassToInstance
						.get(indexedDescriptorClass));
		if (fieldDescriptor == null) {
			fieldDescriptor = ReflectionUtils.create(indexedDescriptorClass);
			DomainObjectDescriptor objectDescriptor = getDomainObjectDescriptor( fieldDescriptor.getBaseDomainObjectClass() );
			objectDescriptor.add( fieldDescriptor );
			this.indexedDescriptorClassToInstance.put(indexedDescriptorClass,
					fieldDescriptor);
		}
		return fieldDescriptor;
	}
	
	private DomainObjectDescriptor getDomainObjectDescriptor( Class<? extends DomainObject> domainObjectClass ) {
		DomainObjectDescriptor descriptor = this.domainClassToDescriptor.get(domainObjectClass);
		if ( descriptor == null ) {
			descriptor = new DomainObjectDescriptor( domainObjectClass );
			this.domainClassToDescriptor.put(descriptor.getDomainObjectClass(), descriptor );
		}
		return descriptor;
	}

	public synchronized <E extends XmlEntityObject> XmlEntityFieldDescriptor<E> get(
			Class<? extends DomainObject> domainObjectOwnerClass,
			Class<E> valueClass, String name) {
		return getDomainObjectDescriptor(domainObjectOwnerClass).get( valueClass, name );
	}

	public synchronized <F extends Field> FieldDescriptor<F, ?> get(
			Class<? extends DomainObject> domainObjectOwnerClass,
			Class<F> fieldClass, String name) {
		return getDomainObjectDescriptor(domainObjectOwnerClass).get( fieldClass, name );
	}

	public synchronized <D extends DomainObject> ReferenceFieldDescriptor<D> get(
			Class<? extends DomainObject> domainObjectOwnerClass,
			Class<D> valueClass, String name) {
		return getDomainObjectDescriptor(domainObjectOwnerClass).get( valueClass, name ); 
	}

}
