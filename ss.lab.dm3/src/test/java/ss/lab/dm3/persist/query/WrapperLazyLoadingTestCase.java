package ss.lab.dm3.persist.query;


import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.ObjectController;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;

/**
 * 
 * @author Dmitry Goncharov
 */
public class WrapperLazyLoadingTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		// Remove all from domain
		domain.getRepository().unloadAll();
		// Check Simple propery
		Sphere sphere2 = domain.resolve( QueryHelper.eq(Sphere.class, 2L ) );
		assertNotNull( sphere2 );
		ObjectController sphere2Ctrl = ObjectController.get( sphere2 );
		assertTrue( sphere2Ctrl.isProxy() );
		assertEquals( new Long(2), sphere2.getId() ); 
		assertTrue( sphere2Ctrl.isProxy() );
		assertEquals( "Sphere_Display#2", sphere2.getDisplayName() ); 
		assertTrue( sphere2Ctrl.isClean() );
		assertSame( sphere2, domain.resolve( QueryHelper.eq(Sphere.class, 2L ) ) );
		// Check related proxy object 
		Sphere sphere1 = sphere2.getParentSphere();
		assertNotNull( sphere1 );
		assertEquals( new Long(1), sphere1.getId() );
		assertSame( sphere1, domain.resolve( Sphere.class, 1L	) );
		ObjectController sphere1Ctrl = ObjectController.get( sphere1 );
		assertTrue( sphere1Ctrl.isProxy() );
		assertEquals( "Sphere_Display#1", sphere1.getDisplayName() );
		assertTrue( sphere1Ctrl.isClean() );
	}
}
