package ss.framework.entities.xmlentities;

import org.dom4j.Element;


public final class XmlEntityObjectFactory implements IXmlEntityObjectFactory<XmlEntityObject> {

	private XmlEntityClassResolverCollection resolvers = new XmlEntityClassResolverCollection();
	
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlEntityObjectFactory#createBlankObject(org.dom4j.Element)
	 */
	public XmlEntityObject createBlankObject(Element element) throws CannotCreateEntityException {
		IXmlEntityClassResolver resolver = this.resolvers.findResolver( element );
		if ( resolver == null ) {
			throw new CannotCreateEntityException( String.format( "Cannot resolve entity class for element %s", element ) );
		}
		Class<XmlEntityObject> entityClass = resolver.getEntityClass();
		try {
			XmlEntityObject entityObject = entityClass.newInstance();
			return entityObject;
		}
		catch(Exception ex ) {
			throw new CannotCreateEntityException( ex ); 
		}		
	}
	
	/**
	 * Registry resolver in factory
	 * @param resolver not null resolver
	 */
	public void registryResolver( IXmlEntityClassResolver resolver ) {
		this.resolvers.registry(resolver);
	}
}
