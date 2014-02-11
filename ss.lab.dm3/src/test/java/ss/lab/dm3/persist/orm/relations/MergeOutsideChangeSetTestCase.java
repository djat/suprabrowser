package ss.lab.dm3.persist.orm.relations;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityBuilder;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.changeset.DataChangeSet;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;

public class MergeOutsideChangeSetTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		load(domain);
		externalChange(domain);
		check(domain);
	}
	

	public void testIngoringLocalChange() {
		final Domain domain = getDomain();
		externalChange(domain);
		checkIgnoring(domain);
	}
	
	public void load(Domain domain) {
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		assertEquals( 1, sphere1.getUsers().size() );
		Sphere sphere2 = domain.resolve( Sphere.class, 2L );
		assertEquals( 1, sphere2.getUsers().size() );
		UserAccount user1 = domain.resolve( UserAccount.class, 1L );
		assertEquals( 1, user1.getSpheres().size() );
		UserAccount user2 = domain.resolve( UserAccount.class, 2L );
		assertEquals( 1, user2.getSpheres().size() );
	}
	
	public void externalChange( Domain domain ) {
		UserInSphere userInSphere = domain.resolve( UserInSphere.class, 1L );
		Entity userInSphereEntity = userInSphere.toEntity();
		EntityBuilder userInSphereEb = new EntityBuilder( domain.getMapper(), userInSphereEntity ); 
		userInSphereEb.setValue( "sphere", QualifiedObjectId.create( Sphere.class, 2L ) );
		userInSphereEb.setValue( "userAccount", QualifiedObjectId.create( UserAccount.class, 2L ) );
		DataChangeSet dataChangeSet = new DataChangeSet();
		dataChangeSet.getUpdated().add( userInSphereEb.create() );
		domain.onExternalChanges( dataChangeSet );
		// Check change set result
		UserAccount user = domain.resolve( UserAccount.class, 2L );
		Sphere sphere = domain.resolve( Sphere.class, 2L );
		assertSame( user, userInSphere.getUserAccount() );
		assertSame( sphere, userInSphere.getSphere() );
	}
	
	public void check(Domain domain) {
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		Sphere sphere2 = domain.resolve( Sphere.class, 2L );
		UserAccount user1 = domain.resolve( UserAccount.class, 1L );
		UserAccount user2 = domain.resolve( UserAccount.class, 2L );
		UserInSphere userInSphere1 = domain.resolve( UserInSphere.class, 1L );
		UserInSphere userInSphere2 = domain.resolve( UserInSphere.class, 2L );
		assertSame( user2, userInSphere1.getUserAccount() );
		assertSame( sphere2, userInSphere1.getSphere() );
		assertListSame( new DomainObject[] { userInSphere1, userInSphere2 }, user2.getSpheres() );
		assertListSame( new DomainObject[] { userInSphere1, userInSphere2 }, sphere2.getUsers() );
		assertEquals( 0, sphere1.getUsers().size() );
		assertEquals( 0, user1.getSpheres().size() );
	}
	
	public void checkIgnoring( Domain domain) {
		load( domain );
		UserAccount user = domain.resolve( UserAccount.class, 1L );
		Sphere sphere = domain.resolve( Sphere.class, 1L );
		UserInSphere userInSphere = domain.resolve( UserInSphere.class, 1L );
		assertSame( user, userInSphere.getUserAccount() );
		assertSame( sphere, userInSphere.getSphere() );		
	}
}
