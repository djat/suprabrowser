package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;

public class SeachableFieldTestCase extends AbstractDomainTestCase {
	
	public void test() {
		final Domain domain = getDomain();
		luceneReindexBase();
		searchByQuery(domain, "+displayName:#1");
		searchByQuery(domain, "+id:1");
		searchByQuery(domain, "+id:1 + qualifier:Sphere");
		searchByQuery(domain, "+systemName:1");
	}

	private void searchByQuery(Domain domain, String query) {
		List<Sphere> objects = domain.find( QueryHelper.luceneSearch( Sphere.class, query ) ).toList();
		assertEquals(1, objects.size());
		assertEquals( domain.resolve( Sphere.class, 1L ), objects.get(0));
	}
	
}
