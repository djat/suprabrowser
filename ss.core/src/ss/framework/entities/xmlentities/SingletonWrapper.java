package ss.framework.entities.xmlentities;

import java.util.HashMap;

import org.dom4j.Document;

public class SingletonWrapper extends CachedWrapper {

	private final HashMap<Class<?>,Document> entityClassToDocument = new HashMap<Class<?>, Document>();
	
	@Override
	protected <T extends XmlEntityObject> T add(Document document,
			Class<T> xmlEntityClazz) {
		T entity = super.add(document, xmlEntityClazz);		
		Document documentForGivenEntityClass = this.entityClassToDocument.get( xmlEntityClazz );
		if ( documentForGivenEntityClass != null ) {
			evict( documentForGivenEntityClass );				
		}
		this.entityClassToDocument.put( xmlEntityClazz, document );
		return entity;	
	}

	@Override
	protected void remove(Document document, XmlEntityObject entity) {
		super.remove(document, entity);
		Class<?> entityClass = entity.getClass();
		Document documentByEntityClass = this.entityClassToDocument.get( entityClass );
		if ( documentByEntityClass != document ) {
			this.logger.error( "Document by entity and document by entity class are differ " + document + ", " + documentByEntityClass );
		}
		this.entityClassToDocument.remove( entityClass );
	}

}
