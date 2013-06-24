package ss.domainmodel;

import ss.common.ArgumentNullPointerException;
import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

public class SphereEmailCollection extends XmlListEntityObject<SphereEmail> {

	/**
	 * 
	 */
	public SphereEmailCollection() {
		super( SphereEmail.class, "sphere-email" );
	}

	public final SphereEmail getSphereEmailBySphereId( final String sphereId ) {
		return findFirst( new IXmlEntityObjectFindCondition<SphereEmail>() {
			public boolean macth(SphereEmail entityObject) {
				return sphereId.equals( entityObject.getSphereId() );
			}			
		});		
	}

	public final SphereEmail getSphereEmailByEmailName( final String emailName ) {
		return findFirst( new IXmlEntityObjectFindCondition<SphereEmail>() {
			public boolean macth(SphereEmail entityObject) {
                return entityObject.getEmailNames().getParsedEmailNames().contains(emailName);
			}			
		});			
	}
	
	public void put( final SphereEmail sphereEmail ) {
		if ( sphereEmail == null ) {
			throw new ArgumentNullPointerException( "sphereEmail" );
		}
		final SphereEmail existedSphereEmailBySphereId = getSphereEmailBySphereId( sphereEmail.getSphereId() );
		if ( existedSphereEmailBySphereId != null ) {
			remove(existedSphereEmailBySphereId);
		}
		
		/*final SphereEmail existedSphereByEmailName = getSphereEmailByEmailName( sphereEmail.getEmailNames() );
		if ( existedSphereByEmailName != null ) {
			// TODO: solve what todo
		}*/
		super.internalAdd( sphereEmail );
	}
	
	public void remove( final SphereEmail sphereEmail ) {
		super.internalRemove( sphereEmail );
	}
	
}
