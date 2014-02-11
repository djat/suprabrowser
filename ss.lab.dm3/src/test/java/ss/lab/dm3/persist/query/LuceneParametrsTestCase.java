package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.WikiSphereExtension;

public class LuceneParametrsTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		luceneReindexBase();
		List<DomainObject> objects = domain.find( QueryHelper.luceneSearch( DomainObject.class, "+ id:3 + qualifier:(Sphere or UserAccount)" ) ).toList();
		List<UserAccount> users = domain.find( QueryHelper.luceneSearch( UserAccount.class, "+qualifier:UserAccount" ) ).toList();
		List<WikiSphereExtension> extensions = domain.find( QueryHelper.luceneSearch( WikiSphereExtension.class, "+description:wiki*" ) ).toList();
		assertTrue(objects.contains(domain.resolve(Sphere.class, 3L)));
		assertTrue(objects.contains(domain.resolve(UserAccount.class, 3L)));
		assertEquals(2, objects.size());
		assertEquals( 20, users.size() );
		assertEquals( 2, extensions.size() );
	}
}
