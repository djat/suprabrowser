package ss.lab.dm3.persist.query;

import java.util.Set;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.Sphere;

public class HqlQueryTestCase extends AbstractDomainTestCase {
	
	public void test() {
		final Domain domain = getDomain();
		// Remove all from domain
		domain.getRepository().unloadAll();
		// Check
		Sphere sphere1 = domain.resolve( QueryHelper.eq(Sphere.class, 1L ) );
		final TypedQuery<Sphere> hqlQuery = QueryHelper.hql( Sphere.class, "from Sphere s where s.parentSphere=?", sphere1 );
		Set<Sphere> spheres = hqlQuery.find().toSet();
		assertEquals( 2, spheres.size() );
		assertTrue( spheres.contains( domain.resolve( QueryHelper.eq(Sphere.class, 2L ) ) ) );
		assertTrue( spheres.contains( domain.resolve( QueryHelper.eq(Sphere.class, 3L ) ) ) );
	}
	
}
