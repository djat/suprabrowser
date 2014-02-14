package ss.framework.entities.xmlentities;

import java.util.HashMap;

import org.dom4j.Document;

public class CachedWrapper {

	protected transient final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(getClass());
	
	private final HashMap<Document, XmlEntityObject> documentToEntity = new HashMap<Document, XmlEntityObject>();
	

	public final synchronized <T extends XmlEntityObject> T wrap(Document document,
			Class<T> xmlEntityClazz ) {
		if (document == null) {
			return null;
		}
		T entity = xmlEntityClazz.cast( this.documentToEntity.get(document) );
		if (entity == null) {			
			entity = add( document, xmlEntityClazz);			
		}
		return entity;
	}
	
	protected <T extends XmlEntityObject> T add(Document document, Class<T> xmlEntityClazz ) {
		T entity = XmlEntityUtils.wrap(document, xmlEntityClazz);
		this.documentToEntity.put( document, entity );
		return entity;
	}
	
	public final synchronized XmlEntityObject evict(Document document ) {
		if ( document == null ) {
			return null;
		}
		XmlEntityObject entity = this.documentToEntity.get( document );
		if ( entity != null ) {
			remove(document, entity);
		}
		return entity;		
	}

	/**
	 * @param document
	 * @param entity
	 */
	protected void remove(Document document, XmlEntityObject entity) {
		this.documentToEntity.remove( document );		
	}
	
}
