package ss.lab.dm3.persist.query;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.ForumSphereExtension;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SphereExtension;
import ss.lab.dm3.testsupport.objects.WikiSphereExtension;

/**
 * 
 * @author Dmitry Goncharov
 */
public class ObjectHierarchyTestCase extends AbstractDomainTestCase {
	
	public void testByProperties() {
		checkProperties( getDomain() );		
	}
	
	public void testByResolve() {
		evictDomainObjects();
		checkResolve(getDomain());		
	}
	
	public void testFind() {
		checkFind(getDomain());
	}
	
	public void checkProperties(Domain domain) {
		Sphere sphere1 = domain.resolve( Sphere.class, 1L );
		assertNull( sphere1.getExtension() );
		Sphere sphere2 = domain.resolve( Sphere.class, 2L );
		final SphereExtension extension1 = sphere2.getExtensionObject();
		assertNotNull( extension1  );
		assertTrue( extension1 instanceof WikiSphereExtension );
		assertEquals( "Wiki sphere", ((WikiSphereExtension) extension1).getDescription() );
		
		Sphere sphere3 = domain.resolve( Sphere.class, 3L );
		final SphereExtension extension2 = sphere3.getExtensionObject();
		assertNotNull( extension2 );		
		assertTrue( extension2 instanceof ForumSphereExtension );
		assertEquals( "Forum sphere", ((ForumSphereExtension) extension2).getDescription() );		
	}

	public void checkResolve(Domain domain) {
		final SphereExtension extension1 = domain.resolve( SphereExtension.class, 1L );
		assertNotNull( extension1  );
		assertTrue( extension1 instanceof WikiSphereExtension );
		assertEquals( "Wiki sphere", ((WikiSphereExtension) extension1).getDescription() );
		
		System.out.println( extension1.getRelatedSpheres().size() );
		
		final SphereExtension extension2 = domain.resolve( SphereExtension.class, 3L );
		assertNotNull( extension2 );
		assertTrue( extension2 instanceof ForumSphereExtension );
		assertEquals( "Forum sphere", ((ForumSphereExtension) extension2).getDescription() );
	}

	public void checkFind(Domain domain) {
		final SphereExtension extension = domain.resolve( SphereExtension.class, 1L );
		System.out.println( extension );
		final TypedQuery<Sphere> query = QueryHelper.eq( Sphere.class, "extension", extension );
		Sphere sphere = domain.resolve( query );		
		System.out.println( sphere );
	}
}
