package ss.framework.entities.xmlentities;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.dom4j.Document;

import ss.common.XmlDocumentUtils;

final class SerializableXmlEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 762903863185482054L;

	private final Class<? extends XmlEntityObject> objectClazz;
	
	private final String xml;

	/**
	 * @param objectClazz
	 * @param xml
	 */
	public SerializableXmlEntity(Class<? extends XmlEntityObject> objectClazz, Document xml ) {
		super();
		this.objectClazz = objectClazz;
		this.xml = xml != null ? XmlDocumentUtils.toCompactString( xml ) : null;
	}
	
	Object readResolve() throws ObjectStreamException {
		Document doc = XmlDocumentUtils.parse(this.xml); 
		return XmlEntityUtils.safeWrap( doc, this.objectClazz );
	}
}
