package ss.lab.dm3.persist.data;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ss.lab.dm3.testsupport.TestHibernateUtils;
import ss.lab.dm3.testsupport.objects.data.DeliveryType;
import ss.lab.dm3.testsupport.objects.data.SphereType;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.SupraSphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import junit.framework.TestCase;

public class ObjectsTestCase extends TestCase {

	public void test() {
		Session session = TestHibernateUtils.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		Sphere sphereF = (Sphere) session.load( Sphere.class, 1L );
		assertEquals( new Long(1L), sphereF.getId());
		assertEquals( "Sphere#1", sphereF.getSystemName());
		assertEquals(SphereType.GROUP, sphereF.getSphereType());
		assertEquals(DeliveryType.NORMAL, sphereF.getDefaultDeliveryType());
		assertEquals(true, sphereF.isEmailAliasEnabled());
		assertEquals("abc@mail.ru", sphereF.getEmailAliasAddresses());
		// assertEquals( 1, sphereF.getUsers().size());
		// UserInSphere[] sphereUserInSpheresF = sphereF.getUsers().toArray( new UserInSphere[]{} );
		// assertEquals( "sphere_display_name#1", sphereUserInSpheresF[ 0 ].getSphereDisplayName() );
		
		
		Sphere sphereS = (Sphere) session.load( Sphere.class, 2L );
		assertEquals( new Long(2L), sphereS.getId());
		assertEquals( "Sphere#2", sphereS.getSystemName());
		assertEquals(SphereType.MEMBER, sphereS.getSphereType());
		assertEquals(DeliveryType.POLL, sphereS.getDefaultDeliveryType());
		assertEquals(false, sphereS.isEmailAliasEnabled());
		assertEquals("def@list.ru", sphereS.getEmailAliasAddresses());
		// assertEquals( 1, sphereS.getUsers().size());
		// UserInSphere[] sphereUserInSpheresS = sphereS.getUsers().toArray( new UserInSphere[]{} );
		// assertEquals( "sphere_display_name#2", sphereUserInSpheresS[ 0 ].getSphereDisplayName() );
		
		/*******************************************************/
		
		SupraSphere supraSphereF = (SupraSphere) session.load(SupraSphere.class, 1L);
		assertEquals( new Long(1L), supraSphereF.getId());
		assertEquals("domain#1", supraSphereF.getDomainNames());
		Sphere sphere = supraSphereF.getSphere();
		assertEquals("Sphere#1", sphere.getSystemName());
		
		SupraSphere supraSphereS = (SupraSphere) session.load(SupraSphere.class, 2L);
		assertEquals( new Long(2L), supraSphereS.getId());
		assertEquals("domain#2", supraSphereS.getDomainNames());
		Sphere supraSphereSphere = supraSphereS.getSphere();
		assertEquals("Sphere#2", supraSphereSphere.getSystemName());
		
		/*******************************************************/
		
		UserAccount userF = (UserAccount) session.load(UserAccount.class, 1L);
		assertEquals( new Long(1L), userF.getId());
		assertEquals("jack", userF.getLogin());
		assertEquals("Jack", userF.getContactName());
		Sphere sphereUserF = userF.getHomeSphere(); 
		assertEquals("Sphere#1", sphereUserF.getSystemName());
		
		UserAccount userS = (UserAccount) session.load(UserAccount.class, 2L);
		assertEquals( new Long(2L), userS.getId());
		assertEquals("bill", userS.getLogin());
		assertEquals("Bill", userS.getContactName());
		Sphere sphereUserS = userS.getHomeSphere(); 
		assertEquals("Sphere#2", sphereUserS.getSystemName());
		
		/*******************************************************/
		
		UserInSphere userInSphereF = (UserInSphere) session.load(UserInSphere.class, 1L);
		assertEquals( new Long(1L), userInSphereF.getId());
		assertEquals("sphere_display_name#1", userInSphereF.getSphereDisplayName());
		Sphere chekRefUserF = userInSphereF.getSphere();
		assertEquals("Sphere#1", chekRefUserF.getSystemName());
		UserAccount checkUserRefF = userInSphereF.getUserAccount();
		assertEquals("jack", checkUserRefF.getLogin());
		
		UserInSphere userInSphereS = (UserInSphere) session.load(UserInSphere.class, 2L);
		assertEquals( new Long(2L), userInSphereS.getId());
		assertEquals("sphere_display_name#2", userInSphereS.getSphereDisplayName());
		Sphere chekRefUserS = userInSphereS.getSphere();
		assertEquals("Sphere#2", chekRefUserS.getSystemName());
		UserAccount checkUserRefS = userInSphereS.getUserAccount();
		assertEquals("bill", checkUserRefS.getLogin());
		
		tx.commit();		
	}
}
