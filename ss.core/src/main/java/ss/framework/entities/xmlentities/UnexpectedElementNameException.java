package ss.framework.entities.xmlentities;

import ss.common.UnexpectedRuntimeException;

public class UnexpectedElementNameException extends UnexpectedRuntimeException {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5065913811001616440L;

	public UnexpectedElementNameException( XmlElementCollectionDataProvider elements, String expected, String actual ) {
        super( String.format( "Unexpected element name in %s. Expected %s. Actual %s", elements, expected, actual ) );
    }
}
