/**
 * 
 */
package ss.framework.domainmodel2;

import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.LazyFieldDescriptor;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.framework.entities.xmlentities.XmlEntityUtils;

/**
 *
 */
public final class XmlEntityFieldDescriptor<E extends XmlEntityObject> extends LazyFieldDescriptor<XmlEntityField<E>, E> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XmlEntityFieldDescriptor.class);
	
	private final Class<E> entityObjectClass;

	/**
	 * @param domainObjectClass
	 * @param name
	 * @param fieldClass
	 * @param entityClass
	 */
	@SuppressWarnings("unchecked")
	public XmlEntityFieldDescriptor(Class<? extends DomainObject> domainObjectClass, String name, Class<E> entityClass) {
		super(domainObjectClass, name, (Class)XmlEntityField.class );
		this.entityObjectClass = entityClass;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.LazyFieldDescriptor#resolve(ss.framework.domainmodel2.AbstractDomainSpace, java.lang.String)
	 */
	@Override
	public E resolve(AbstractDomainSpace space, String loadedStrValue) {
		E value = XmlEntityUtils.wrap(loadedStrValue, this.entityObjectClass );
		if (logger.isDebugEnabled() ) {
			logger.debug( this + " resolving " + loadedStrValue + " result " + XmlEntityUtils.entityToString(value) );
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.fields.FieldDescriptor#getValueToSave(ss.framework.domainmodel2.fields.Field)
	 */
	@Override
	protected String getValueToSave(XmlEntityField<E> field) {
		E value = field.get();
		return XmlEntityUtils.entityToString( value );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.FieldDescriptor#getActualValue(ss.framework.domainmodel2.Field)
	 */
	@Override
	protected E getActualValue(XmlEntityField<E> field) {
		return field.get();
	}

	/**
	 * @return the entityObjectClass
	 */
	public Class<E> getEntityObjectClass() {
		return this.entityObjectClass;
	}
	
	
	
	
	

}
