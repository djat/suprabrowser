package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;

/**
 * @author dmitry
 *
 */
public class LimitTestCase extends AbstractDomainTestCase {

	private static final int LIMIT_FOR_SPHERES = 2;
	private static final int LIMIT_FOR_USERS = 1;
	private static final int LIMIT_OFFSET = 2;

	public void test() {
		final Domain domain = getDomain();	
		check1( domain );
		check2( domain );
		check3( domain );
		check4( domain );
	}
	
	public void check1( Domain domain ) {
		List<Sphere> spheres = domain.find( QueryHelper.eq( Sphere.class ).addLimit( LIMIT_FOR_SPHERES ) ).toList();
		assertEquals( LIMIT_FOR_SPHERES, spheres.size() );
	}
	
	public void check2( Domain domain ) {
		List<UserAccount> users = domain.find( QueryHelper.hql( UserAccount.class , "from UserAccount").addLimit( LIMIT_FOR_USERS ) ).toList();
		assertEquals( LIMIT_FOR_USERS, users.size() );
	}
	
	public void check3( Domain domain ) {
		List<UserAccount> users = domain.find( QueryHelper.sql( UserAccount.class , "select * from user_account where home_sphere_id=?", 1).addLimit( LIMIT_OFFSET, LIMIT_FOR_USERS ) ).toList();
		assertEquals( LIMIT_FOR_USERS, users.size() );
	}
	
	public void check4( Domain domain ) {
		List<UserAccount> users = domain.find( QueryHelper.hql( UserAccount.class , "from UserAccount u where u.homeSphere.systemName=?", "Sphere#1" ).setLimitOffset( LIMIT_OFFSET ) ).toList();
		assertEquals( 5, users.size() );
	}
	
	public void check5( Domain domain ) {
		domain.getRepository().unloadAll();
		List<UserAccount> users = domain.find( QueryHelper.hql( UserAccount.class , "from UserAccount u where u.homeSphere.systemName=?", "Sphere#1" ).addLimit( LIMIT_OFFSET, LIMIT_FOR_USERS ) ).toList();
		assertEquals( LIMIT_FOR_USERS, users.size() );
	}
}
