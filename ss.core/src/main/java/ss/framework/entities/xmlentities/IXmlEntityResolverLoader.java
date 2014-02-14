package ss.framework.entities.xmlentities;

public interface IXmlEntityResolverLoader {

	/**
	 * Registry resolver in specified factory
	 */
	void loadTo( XmlEntityObjectFactory targetFactory );
	
}
