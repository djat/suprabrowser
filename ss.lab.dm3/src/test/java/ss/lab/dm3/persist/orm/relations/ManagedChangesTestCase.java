package ss.lab.dm3.persist.orm.relations;

import ss.lab.dm3.context.InjectionUtils;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.DomainObjectCollector;
import ss.lab.dm3.persist.query.TypedQueryMatcher;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;

public class ManagedChangesTestCase extends AbstractDomainTestCase {

	public void testAddNew() {
		final Domain domain = getDomain();
		checkAddNew(domain);
		evictDomainObjects();
		checkUserInSphere3(domain);
	}
	
	public void checkAddNew( Domain domain ) {
		domain.beginTrasaction();
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		final UserInSphere userInSphere1 = domain.resolve( UserInSphere.class, 1L );
		assertListSame( new DomainObject[] { userInSphere1 }, sphere1.getUsers() );

		Sphere sphere2 = domain.resolve( Sphere.class, 2L );
		final UserInSphere userInSphere2 = domain.resolve( UserInSphere.class, 2L );
		assertListSame( new DomainObject[] { userInSphere2 }, sphere2.getUsers() );
		
		UserAccount user2 = domain.resolve( UserAccount.class, 2L );
		UserInSphere userInSphere3 = domain.createObject( UserInSphere.class, 3L );
		userInSphere3.setSphere( sphere1 );
		userInSphere3.setUserAccount( user2 );
		
		// Check sphere1 user in sphere
		assertListSame( new DomainObject[] { userInSphere1, userInSphere3 }, sphere1.getUsers() );
		InjectionUtils.push( true, TypedQueryMatcher.DEBUG_KEY, DomainObjectCollector.DEBUG_KEY );
		try {
			assertListSame( new DomainObject[] { userInSphere3, userInSphere2 }, user2.getSpheres() );
		}
		finally {
			InjectionUtils.pop( TypedQueryMatcher.DEBUG_KEY );
		}
		domain.commitTrasaction();
	}
	
	public void checkUserInSphere3(Domain domain) {
		UserInSphere userInSphere1 = domain.resolve( UserInSphere.class, 1L );
		UserInSphere userInSphere2 = domain.resolve( UserInSphere.class, 2L );
		UserInSphere userInSphere3 = domain.resolve( UserInSphere.class, 3L );
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		UserAccount user2 = domain.resolve( UserAccount.class, 2L );
		assertListSame( new DomainObject[] { userInSphere1, userInSphere3 }, sphere1.getUsers() );
		assertListSame( new DomainObject[] { userInSphere3, userInSphere2 }, user2.getSpheres() );
	}
	
	public void testRemoveExisted() {
		final Domain domain = getDomain();
		domain.beginTrasaction();
		UserInSphere userInSphere1 = domain.resolve( UserInSphere.class, 1L );
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		assertSame( sphere1, userInSphere1.getSphere() );
		sphere1.getUsers().clear( false );
		assertListSame( new DomainObject[] {}, sphere1.getUsers() );
		assertNull( userInSphere1.getSphere() );
		Sphere sphere2 = domain.resolve( Sphere.class, 2L );
		userInSphere1.setSphere( sphere2 );
		UserInSphere userInSphere2 = domain.resolve( UserInSphere.class, 2L );
		userInSphere2.delete();
		for( UserInSphere uin : sphere2.getUsers() ) {
			System.out.println( uin );
		}
		assertListSame( new DomainObject[] { userInSphere1 }, sphere2.getUsers() );
		domain.commitTrasaction();
	}
	
	
}
