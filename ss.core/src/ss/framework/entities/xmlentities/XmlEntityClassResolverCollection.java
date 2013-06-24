package ss.framework.entities.xmlentities;

import org.dom4j.Element;

import ss.framework.entities.ListEntityObject;

public class XmlEntityClassResolverCollection extends ListEntityObject<IXmlEntityClassResolver> {
	/**
	 * Find suitable resolver for specified data or null if no suitable resolver found.
	 */
	public IXmlEntityClassResolver findResolver( Element element ) {
		for( IXmlEntityClassResolver resolver : this ) {
			if ( resolver.match( element ) ) {
				return resolver;
			}
		}
		return null;
	}
	
	/**
	 * Add resolver to collection if resolver not yet in.
	 * @param resolver not null resolver 
	 */
	public void registry( IXmlEntityClassResolver resolver ) {
		if ( !internalContains( resolver ) ) {
			internalAdd(resolver );
		}
	}


	
}
