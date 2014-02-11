package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;

/**
 * @author dmitry
 *
 */
public class LuceneDeleteObjectTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		delete(domain);
		check(domain);
	}
	
	public void delete(Domain domain) {
		domain.beginTrasaction();
		Sphere sphere = domain.resolve( Sphere.class, 1L );
		sphere.delete();
		domain.commitTrasaction();
	}
	
	public void check(Domain domain) {
		List<Sphere> spheres = domain.find( QueryHelper.luceneSearch( Sphere.class, "+(displayName:sphere)" ) ).toList();
		assertEquals( 2, spheres.size() );
		assertEquals(  domain.resolve( Sphere.class, 2L ), spheres.get(0));
		assertEquals(  domain.resolve( Sphere.class, 3L ), spheres.get(1));
	}
	
}
