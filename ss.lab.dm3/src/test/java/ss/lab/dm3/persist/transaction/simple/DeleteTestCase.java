package ss.lab.dm3.persist.transaction.simple;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.ObjectNotFoundException;
import ss.lab.dm3.testsupport.TestDataLoader;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;

public class DeleteTestCase extends AbstractDomainTestCase {

	private Long createSphereId;
	private Long createSupraSphereId;
	private Long createUserAccountId;
	private Long createUserInSphereId;

	public void test() {
		final Domain domain = getDomain();
		TestDataLoader.load();
		deleteData(domain);
		evictDomainObjects();
		TestDataLoader.load();
		check(domain);
	}

	protected void deleteData(Domain domain) {
		domain.beginTrasaction();
		Sphere sphere3 = domain.find(Sphere.class, 3L);
		sphere3.delete();

		Sphere parentSphere = domain.resolve(Sphere.class, 1L);
		Sphere sphere = domain.createObject(Sphere.class);
		sphere.setDefaultDeliveryType(DeliveryType.CONFIRM_RECEIPT);
		sphere.setDisplayName("Hello, Sphere");
		sphere.setSystemName("Hello, SystemName");
		DeleteTestCase.this.createSphereId = sphere.getId();
		parentSphere.getChildrenSpheres().add(sphere);
		sphere.delete();

		SupraSphere supraSphere = domain.createObject(SupraSphere.class);
		supraSphere.setDomainNames("Hello, domain");
		supraSphere.setSphere(sphere);
		DeleteTestCase.this.createSupraSphereId = supraSphere.getId();
		supraSphere.delete();

		UserAccount userAccount = domain.createObject(UserAccount.class);
		userAccount.setLogin("ivan");
		userAccount.setContactName("Ivan");
		userAccount.setContactCardId("card#ivan");
		userAccount.setHomeSphere(sphere);
		DeleteTestCase.this.createUserAccountId = userAccount.getId();
		userAccount.delete();

		UserInSphere userInSphere = domain.createObject(UserInSphere.class);
		userInSphere.setSphereDisplayName("Hello, sphere display name");
		userInSphere.setSphere(sphere);
		userInSphere.setUserAccount(userAccount);
		DeleteTestCase.this.createUserInSphereId = userInSphere.getId();
		userInSphere.delete();
		domain.commitTrasaction();
	}

	public void check(Domain domain) {
		Sphere sphere3 = domain.find(Sphere.class, 3L);
		assertNull(sphere3);

		Sphere sphere = domain.find(Sphere.class, this.createSphereId);
		assertNull(sphere);

		SupraSphere supraSphere = domain.find(SupraSphere.class, this.createSupraSphereId);
		assertNull(supraSphere);

		UserAccount userAccount = domain.find(UserAccount.class, this.createUserAccountId);
		assertNull(userAccount);

		UserInSphere userInSphere = domain.find(UserInSphere.class, this.createUserInSphereId);
		assertNull(userInSphere);

		Sphere sphere3resolved = domain.resolve(Sphere.class, 3L);
		try {
			sphere3resolved.getDisplayName();
			fail("Load of not existed object does not fail.");
		}
		catch (ObjectNotFoundException ex) {
		}

	}
}
