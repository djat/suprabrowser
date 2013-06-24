package ss.framework.entities.xmlentities;

public interface IXmlEntityObjectFindCondition<E extends XmlEntityObject> {

	boolean macth( E entityObject );
	
}
