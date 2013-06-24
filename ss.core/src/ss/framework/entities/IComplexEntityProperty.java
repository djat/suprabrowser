package ss.framework.entities;

import ss.framework.entities.xmlentities.XmlEntityObject;

public interface IComplexEntityProperty<T extends XmlEntityObject> extends
		IEntityProperty {

	/**
	 * Returns value
	 */
	T getValue();
	
	void setValue( T value );
	
}
