package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;

/**
 * @author dmitry
 *
 */
public class OrdersTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		check1( domain );
		check2( domain );
		check3( domain );
		check4( domain );
	}
	
	public void check1( Domain domain ) {
		domain.getRepository().unloadAll();
		final TypedQuery<Sphere> criteriaEq = QueryHelper.eq( Sphere.class ).addOrderByDesc( "displayName" );
		checkCriteriaForSphere(domain, criteriaEq);
	}
	
	public void check2( Domain domain ) {
		domain.getRepository().unloadAll();
		final TypedQuery<Sphere> queryHql = QueryHelper.hql( Sphere.class, "from Sphere").addOrderByDesc( "displayName" );
		checkCriteriaForSphere(domain, queryHql);
	}
	
	public void check3( Domain domain ) {
		domain.getRepository().unloadAll();
		final TypedQuery<UserAccount> criteriaEq = QueryHelper.eq( UserAccount.class ).addOrderByAsc( "contactName" );
		checkCriteriaForUser(domain, criteriaEq);
	}
	
	public void check4( Domain domain ) {
		domain.getRepository().unloadAll();
		final TypedQuery<UserAccount> queryHql = QueryHelper.hql( UserAccount.class, "from UserAccount" ).addOrderByAsc( "contactName" );
		checkCriteriaForUser(domain, queryHql);
	}

	private void checkCriteriaForSphere(Domain domain,
			final TypedQuery<Sphere> typedQuery) {
		List<Sphere> spheres = domain.find( typedQuery ).toList();
		assertEquals( domain.resolve( Sphere.class, 3L ) , spheres.get(0) );
		assertEquals( domain.resolve( Sphere.class, 2L ) , spheres.get(1) );
		assertEquals( domain.resolve( Sphere.class, 1L ) , spheres.get(2) );
	}
	
	private void checkCriteriaForUser(Domain domain,
			final TypedQuery<UserAccount> typedQuery) {
		List<UserAccount> users = domain.find( typedQuery ).toList();
		assertEquals( domain.resolve( UserAccount.class, 7L ) , users.get(0) );
		assertEquals( domain.resolve( UserAccount.class, 10L ) , users.get(1) );
	}
}