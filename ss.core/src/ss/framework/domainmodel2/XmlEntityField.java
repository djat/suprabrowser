/**
 * 
 */
package ss.framework.domainmodel2;

import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.framework.entities.xmlentities.XmlEntityUtils;

/**
 *
 */
public class XmlEntityField<E extends XmlEntityObject> extends LazyField<E>{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XmlEntityField.class);
	
	/**
	 * @param objectOwner
	 * @param descriptor
	 */
	public XmlEntityField(LazyFieldDescriptor<?, E> descriptor, FieldMap fieldMap) {
		super(descriptor, fieldMap);
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.LazyField#get()
	 */
	@Override
	public synchronized E get() {
		E ret = super.get();
		if ( logger.isDebugEnabled() ) {
			logger.debug( this + " = " +  XmlEntityUtils.entityToString(ret) );
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.Field#getComparableValue()
	 */
	@Override
	public Object getComparableValue() {
		return null;
	}

}
