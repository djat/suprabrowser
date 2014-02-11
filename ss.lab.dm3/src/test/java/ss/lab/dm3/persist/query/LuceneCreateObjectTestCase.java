package ss.lab.dm3.persist.query;

import java.util.Set;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;

/**
 * @author dmitry
 * 
 */
public class LuceneCreateObjectTestCase extends AbstractDomainTestCase {

	public void test() {

		final Domain domain = getDomain();
		
		luceneReindexBase();
		
		createSphereObject(domain);
		check(domain);
	}

	public void check(Domain domain) {
		Set<Sphere> set = (Set<Sphere>) domain
				.find(
						QueryHelper.luceneSearch(Sphere.class,
								"+(displayName:sphere)")).toSet();
		assertEquals(4, set.size());
	}
	
	public void createSphereObject(Domain domain) {
		domain.beginTrasaction();
		Sphere sphere = domain.createObject(Sphere.class, 123L);
		sphere.setSystemName("123");
		sphere.setDisplayName("Lucene sphere");
		sphere.setDefaultDeliveryType(DeliveryType.CONFIRM_RECEIPT);
		domain.commitTrasaction();
	}
}
