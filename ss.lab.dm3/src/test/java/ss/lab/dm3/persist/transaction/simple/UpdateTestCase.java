package ss.lab.dm3.persist.transaction.simple;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.testsupport.TestDataLoader;
import ss.lab.dm3.testsupport.objects.Sphere;

public class UpdateTestCase extends AbstractDomainTestCase {

	public void testWithDomainReset() {
		TestDataLoader.load();
		final Domain domain = getDomain();
		editData(domain);
		evictDomainObjects();
		TestDataLoader.load();
		check(domain);
	}

	public void testWoDomainReset() {
		TestDataLoader.load();
		final Domain domain = getDomain();
		editData(domain);
		check(domain);
	}

	protected void editData(Domain domain) {
		domain.beginTrasaction();
		Sphere sphere = domain.resolve(Sphere.class, 1L);
		assertNotNull(sphere);
		assertEquals(true, sphere.isEmailAliasEnabled());
		assertEquals("Sphere_Display#1", sphere.getDisplayName());
		sphere.setEmailAliasEnabled(false);
		sphere.setDisplayName("Edited display name");
		domain.commitTrasaction();
	}

	public void check(Domain domain) {
		Sphere sphere = domain.resolve(Sphere.class, 1L);
		assertNotNull(sphere);
		assertEquals(false, sphere.isEmailAliasEnabled());
		assertEquals("Edited display name", sphere.getDisplayName());
	}

}
