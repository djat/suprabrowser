package ss.lab.dm3.persist.init;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.ChildrenDomainObjectList;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;
import ss.lab.dm3.testsupport.objects.data.SphereType;

public class LoadTestCase extends AbstractDomainTestCase {

	public void test() {
		Domain domain = getDomain();
		final SupraSphere supraSphere = domain.resolve(SupraSphere.class, 1L);
		assertNotNull(supraSphere);
		assertEquals(new Long(1), supraSphere.getId());
		Sphere sphere1 = supraSphere.getSphere();
		assertNotNull(sphere1);
		assertEquals(new Long(1), sphere1.getId());
		final UserAccount userAccount1 = domain.resolve(UserAccount.class, 1L);
		assertNotNull(userAccount1);
		assertEquals(new Long(1), userAccount1.getId());
		final ChildrenDomainObjectList<UserInSphere> spheres = userAccount1.getSpheres();
		assertEquals(1, spheres.size());
		UserInSphere user1UserInSphere1 = spheres.findById(1L);
		assertNotNull(user1UserInSphere1);
		assertEquals(new Long(1L), user1UserInSphere1.getId());
		assertSame(userAccount1, user1UserInSphere1.getUserAccount());
		assertSame(sphere1, user1UserInSphere1.getSphere());
	
		
		Sphere sphereF = domain.resolve( Sphere.class, 1L );
		
		assertEquals( new Long(1L), sphereF.getId());
		assertEquals( "Sphere#1", sphereF.getSystemName());
		assertEquals(SphereType.GROUP, sphereF.getSphereType());
		assertEquals(DeliveryType.NORMAL, sphereF.getDefaultDeliveryType());
		assertEquals(true, sphereF.isEmailAliasEnabled());
		assertEquals("abc@mail.ru", sphereF.getEmailAliasAddresses());
		assertEquals( 1, sphereF.getUsers().size());
		UserInSphere sphereUserInSpheresF = sphereF.getUsers().findById( 1L );
		assertEquals( "sphere_display_name#1", sphereUserInSpheresF.getSphereDisplayName() );
		
		
		Sphere sphereS = domain.resolve( Sphere.class, 2L );
		assertEquals( new Long(2L), sphereS.getId());
		assertEquals( "Sphere#2", sphereS.getSystemName());
		assertEquals(SphereType.MEMBER, sphereS.getSphereType());
		assertEquals(DeliveryType.POLL, sphereS.getDefaultDeliveryType());
		assertEquals(false, sphereS.isEmailAliasEnabled());
		assertEquals("def@list.ru", sphereS.getEmailAliasAddresses());
		assertEquals( 1, sphereS.getUsers().size());
		UserInSphere sphereUserInSpheresS = sphereS.getUsers().findById( 2L);
		assertEquals( "sphere_display_name#2", sphereUserInSpheresS.getSphereDisplayName() );
		
		/*******************************************************/
		
		SupraSphere supraSphereF = domain.resolve(SupraSphere.class, 1L);
		assertEquals( new Long(1L), supraSphereF.getId());
		assertEquals("domain#1", supraSphereF.getDomainNames());
		Sphere sphereData = supraSphereF.getSphere();
		assertEquals("Sphere#1", sphereData.getSystemName());
		
		SupraSphere supraSphereS = domain.resolve(SupraSphere.class, 2L);
		assertEquals( new Long(2L), supraSphereS.getId());
		assertEquals("domain#2", supraSphereS.getDomainNames());
		Sphere sphereDataS = supraSphereS.getSphere();
		assertEquals("Sphere#2", sphereDataS.getSystemName());
		
		/*******************************************************/
		
		UserAccount userDataF = domain.resolve(UserAccount.class, 1L);
		assertEquals( new Long(1L), userDataF.getId());
		assertEquals("jack", userDataF.getLogin());
		assertEquals("Jack", userDataF.getContactName());
		Sphere sphereDataUserF = userDataF.getHomeSphere(); 
		assertEquals("Sphere#1", sphereDataUserF.getSystemName());
		
		UserAccount userDataS = domain.resolve(UserAccount.class, 2L);
		assertEquals( new Long(2L), userDataS.getId());
		assertEquals("bill", userDataS.getLogin());
		assertEquals("Bill", userDataS.getContactName());
		Sphere sphereDataUserS = userDataS.getHomeSphere(); 
		assertEquals("Sphere#2", sphereDataUserS.getSystemName());
		
		/*******************************************************/
		
		UserInSphere userInSphereDataF = domain.resolve(UserInSphere.class, 1L);
		assertEquals( new Long(1L), userInSphereDataF.getId());
		assertEquals("sphere_display_name#1", userInSphereDataF.getSphereDisplayName());
		Sphere chekRefUserF = userInSphereDataF.getSphere();
		assertEquals("Sphere#1", chekRefUserF.getSystemName());
		UserAccount checkUserRefDataF = userInSphereDataF.getUserAccount();
		assertEquals("jack", checkUserRefDataF.getLogin());
		
		UserInSphere userInSphereDataS = domain.resolve(UserInSphere.class, 2L);
		assertEquals( new Long(2L), userInSphereDataS.getId());
		assertEquals("sphere_display_name#2", userInSphereDataS.getSphereDisplayName());
		Sphere chekRefUserS = userInSphereDataS.getSphere();
		assertEquals("Sphere#2", chekRefUserS.getSystemName());
		UserAccount checkUserRefDataS = userInSphereDataS.getUserAccount();
		assertEquals("bill", checkUserRefDataS.getLogin());

	}

}
