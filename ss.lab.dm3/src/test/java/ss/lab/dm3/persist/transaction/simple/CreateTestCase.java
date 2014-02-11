package ss.lab.dm3.persist.transaction.simple;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.ObjectController;
import ss.lab.dm3.testsupport.TestDataLoader;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;

public class CreateTestCase extends AbstractDomainTestCase {

	private Long createSphereId;
	private Long createSupraSphereId;
	private Long createUserAccountId;
	private Long createUserInSphereId;

	public void test() {
		TestDataLoader.load();
		Domain domain = getDomain();
		dataCreator( domain );
		evictDomainObjects();
		TestDataLoader.load();
		check(domain);
	}

	public void testWoPreload() {
		TestDataLoader.load();
		Domain domain = getDomain();
		dataCreator(domain);
		evictDomainObjects();
		check(domain);
	}

	protected void dataCreator(Domain domain) {
		domain.beginTrasaction();
		Sphere parentSphere = domain.resolve(Sphere.class, 1L);
		assertTrue(ObjectController.get(parentSphere).isClean());
		Sphere sphere = domain.createObject(Sphere.class);
		sphere.setDefaultDeliveryType(DeliveryType.CONFIRM_RECEIPT);
		sphere.setDisplayName("Hello, Sphere");
		sphere.setSystemName("Hello, SystemName");
		CreateTestCase.this.createSphereId = sphere.getId();
		parentSphere.getChildrenSpheres().add(sphere);
		assertSame(parentSphere, sphere.getParentSphere());

		SupraSphere supraSphere = domain.createObject(SupraSphere.class);
		supraSphere.setDomainNames("Hello, domain");
		supraSphere.setSphere(sphere);
		CreateTestCase.this.createSupraSphereId = supraSphere.getId();

		UserAccount userAccount = domain.createObject(UserAccount.class);
		userAccount.setLogin("ivan");
		userAccount.setContactName("Ivan");
		userAccount.setContactCardId("card#ivan");
		userAccount.setHomeSphere(sphere);
		CreateTestCase.this.createUserAccountId = userAccount.getId();

		UserInSphere userInSphere = domain.createObject(UserInSphere.class);
		userInSphere.setSphereDisplayName("Hello, sphere display name");
		userInSphere.setSphere(sphere);
		userInSphere.setUserAccount(userAccount);
		CreateTestCase.this.createUserInSphereId = userInSphere.getId();
		domain.commitTrasaction();
	}

	public void check(Domain domain) {
		Sphere sphere = domain.resolve(Sphere.class, this.createSphereId);
		assertEquals("Hello, Sphere", sphere.getDisplayName());
		assertEquals("Hello, SystemName", sphere.getSystemName());
		assertEquals(DeliveryType.CONFIRM_RECEIPT, sphere.getDefaultDeliveryType());
		Sphere sphere1 = domain.resolve(Sphere.class, 1L);
		assertSame(sphere1, sphere.getParentSphere());

		SupraSphere supraSphere = domain.resolve(SupraSphere.class, this.createSupraSphereId);
		SupraSphere supraSphere1 = domain.resolve(SupraSphere.class, 1L);
		assertEquals("Hello, domain", supraSphere.getDomainNames());
		assertSame(supraSphere1.getSphere(), supraSphere.getSphere().getParentSphere());

		this.log.debug("List all users");
		for (UserAccount userAccount : domain.find(UserAccount.createEqByClass())) {
			this.log.debug("User " + userAccount);
		}
		UserAccount userAccount = domain.resolve(UserAccount.class, this.createUserAccountId);
		assertNotNull(userAccount);
		assertEquals("ivan", userAccount.getLogin());
		assertEquals("Ivan", userAccount.getContactName());
		assertEquals("card#ivan", userAccount.getContactCardId());
		UserAccount userAccount1 = domain.resolve(UserAccount.class, 1L);
		assertSame(userAccount1.getHomeSphere(), userAccount.getHomeSphere().getParentSphere());

		UserInSphere userInSphere = domain.resolve(UserInSphere.class, this.createUserInSphereId);
		UserInSphere userInSphere1 = domain.resolve(UserInSphere.class, 1L);
		assertEquals("Hello, sphere display name", userInSphere.getSphereDisplayName());
		assertEquals(userInSphere1.getSphere(), userInSphere.getSphere().getParentSphere());
		assertEquals(userInSphere1.getUserAccount().getHomeSphere(), userInSphere.getUserAccount()
				.getHomeSphere().getParentSphere());
	}
}
