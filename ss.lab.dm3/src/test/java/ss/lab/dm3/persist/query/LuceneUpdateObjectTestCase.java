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
public class LuceneUpdateObjectTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		update(domain);
		check(domain);
	}
	
	public void update(Domain domain) {
		domain.beginTrasaction();
		Sphere sphere = domain.resolve( Sphere.class, 1L );
		sphere.setDisplayName( "Lucene update sphere" );	
		domain.commitTrasaction();
	}
	
	public void check(Domain domain) {
		List<Sphere> spheres = domain.find( QueryHelper.luceneSearch( Sphere.class, "+(displayName:update)"  ) ).toList();
		assertEquals( 1, spheres.size() );
		assertEquals(  domain.resolve( Sphere.class, 1L ), spheres.get(0));
	}
	
}
