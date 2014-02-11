package ss.lab.dm3.persist.data;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;
import ss.lab.dm3.testsupport.TestConfigurationProvider;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;
import ss.lab.dm3.testsupport.objects.UserInSphere;

public class BatchObjectCreationTest {

	
	public static void main(String[] args) {
		SessionFactory sessionFactory = HibernateUtils.createSessionFactory( TestConfigurationProvider.INSTANCE.get() );
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	
		Object existedUserInSphere = session.get(UserInSphere.class, 10L);
		if ( existedUserInSphere != null ) {
			session.delete(existedUserInSphere );
		}
		
		Object existedSphere = session.get(Sphere.class, 10L);
		if ( existedSphere != null ) {
			session.delete( existedSphere );
		}
		
		
		session.getTransaction().commit();
		
		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		UserAccount userAccount = (UserAccount) session.load( UserAccount.class, new Long(1) );
		Sphere sphere = new Sphere();
		sphere.setId( 10L );
		sphere.setSystemName( String.valueOf( sphere.getId() ) );
		sphere.setDisplayName( "New Sphere" );
		
		UserInSphere userInSphere = new UserInSphere();
		userInSphere.setId( 10L );
		userInSphere.setSphere(sphere);
		userInSphere.setUserAccount(userAccount);
		
		session.save( userInSphere );
		
		session.save( sphere );
		
		session.getTransaction().commit();
//		
//		ManagerBackEnd backEnd = new ManagerBackEnd( , eventManager );
//		Sphere sphere = new Sphere();
//		UserInSphere userInSphere = new UserInSphere();
//		
//		userInSphere.setSphere( sphere );
//		userInSphere.setUserAccount( );
//		
//		EntityList created = new EntityList();
//		created.add(  );
//		EntityList updated = new EntityList();
//		EntityList deleted = new EntityList();
//		ChangeSet changeSet = new ChangeSet( null, created, updated, deleted );
//		
//		backEnd.commit(dataChanges);
		
	}
	
	
}
