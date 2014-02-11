package ss.lab.dm3.persist.query;

import java.util.Set;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;

public class QueryTestCase extends AbstractDomainTestCase {
	
	public void test() {
		final Domain domain = getDomain();
		// Find signle object
		Sphere sphere1 = domain.resolve( QueryHelper.eq( Sphere.class, 1L ) );
		assertNotNull( sphere1 );
		assertEquals( "Sphere_Display#1", sphere1.getDisplayName() );
		Sphere sphere2 = domain.resolve( QueryHelper.eq( Sphere.class, 2L ) );
		assertNotNull( sphere2 );
		assertEquals( "Sphere_Display#2", sphere2.getDisplayName() );
		Sphere shere2ByDisplayName = domain.resolve( QueryHelper.eq( Sphere.class, "displayName", "Sphere_Display#2" ) );
		assertSame( sphere2, shere2ByDisplayName );
		Sphere sphere3 = domain.resolve( QueryHelper.eq( Sphere.class, "emailAliasAddresses", "sphere3@list.ru" ) );
		assertNotNull( sphere3 );
		assertEquals( "Sphere_Display#3", sphere3.getDisplayName() );
		// Should not exists
		Sphere sphere1000 = domain.find( QueryHelper.eq( Sphere.class, 1000L ) ).getFirstOrNull();
		assertNull( sphere1000 );
		// Find several objects
		// Find by long foreign key  
		Set<Sphere> sphere1ChildrenByLong = domain.find( QueryHelper.eq( Sphere.class, "parentSphere", 1L ) ).toSet();
		assertEquals( 2, sphere1ChildrenByLong.size());
		assertTrue( sphere1ChildrenByLong.contains( sphere2 ) );
		assertTrue( sphere1ChildrenByLong.contains( sphere3 ) );
		// Find by object foreign key 
		Set<Sphere> sphere1ChildrenByObject = domain.find( QueryHelper.eq( Sphere.class, "parentSphere", sphere1 ) ).toSet();
		assertEquals( 2, sphere1ChildrenByObject .size());
		assertTrue( sphere1ChildrenByObject.contains( sphere2 ) );
		assertTrue( sphere1ChildrenByObject.contains( sphere3 ) );		

	}

}
