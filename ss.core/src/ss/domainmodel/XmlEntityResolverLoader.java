package ss.domainmodel;

import ss.framework.entities.xmlentities.IXmlEntityResolverLoader;
import ss.framework.entities.xmlentities.XmlEntityObjectFactory;

public class XmlEntityResolverLoader implements IXmlEntityResolverLoader {

	private XmlEntityObjectFactory currentFactory;
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlEntityResolverLoader#loadTo(ss.framework.entities.xmlentities.XmlEntityFactory)
	 */
	public void loadTo(XmlEntityObjectFactory targetFactory) {
		this.currentFactory = targetFactory;
		hardCodeLoading();
	}

	/**
	 * Create {@link XmlEntityClassResolver} and add it to factory resolvers
	 */	
	@SuppressWarnings("unchecked")
	private void registry( String elementName, Class entityClass ) {
		//TODO: controls abmigous resolvers
		this.currentFactory.registryResolver( new XmlEntityClassResolver( elementName, entityClass ) );
	}
	
	/**
	 * Function initialize object resolvers
	 */
	private void hardCodeLoading() {
		registry( "sphere", SphereStatement.class );
	}
	
	
	

	
}
