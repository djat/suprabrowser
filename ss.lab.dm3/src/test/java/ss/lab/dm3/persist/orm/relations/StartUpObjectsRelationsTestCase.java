package ss.lab.dm3.persist.orm.relations;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;

public class StartUpObjectsRelationsTestCase extends AbstractDomainTestCase {
	
	public void test() {
		final Domain domain = getDomain();		
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		UserAccount user1 = domain.resolve( UserAccount.class, 1L );
		UserInSphere userInSphere1 = domain.resolve( UserInSphere.class, 1L );
		assertNotNull( sphere1 );
		assertNotNull( user1 );
		assertNotNull( userInSphere1 );
		assertListSame( new DomainObject[] { userInSphere1 }, sphere1.getUsers() );
		assertSame( sphere1, userInSphere1.getSphere() );
		assertSame( user1, userInSphere1.getUserAccount() );
		assertListSame( new DomainObject[] { userInSphere1 }, user1.getSpheres() );
	}
	
	

}
