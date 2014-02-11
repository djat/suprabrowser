package ss.lab.dm3.persist.query;

import java.util.List;

import ss.lab.dm3.context.InjectionUtils;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.ObjectController;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserInSphere;
import ss.lab.dm3.utils.DebugUtils;

/**
 * @author dmitry
 * 
 */
public class CascadePropertiesTestCase extends AbstractDomainTestCase {

	public void test() {
		InjectionUtils.push( true, TypedQueryMatcher.DEBUG_KEY );
		final Domain domain = getDomain();
		final TypedQuery<Sphere> query = QueryHelper.eq(Sphere.class).addOrderByDesc("displayName");
		List<Sphere> spheres = domain.find(
				query).toList();
		for (Sphere sphere : spheres) {
			assertTrue(sphere.getUsers().isFetched());
			for (UserInSphere userInSphere : sphere.getUsers()) {
				final ObjectController objectController = ObjectController.get(userInSphere);
				assertTrue( objectController.isClean() );
			}
		}		
		assertListSame( new DomainObject[] { domain.resolve( UserInSphere.class, 1L ) }, spheres.get(2).getUsers());
		assertListSame( new DomainObject[] { domain.resolve( UserInSphere.class, 2L ) }, spheres.get(1).getUsers());
		DebugUtils.trace(spheres.get(1).getUsers());		
		assertListSame( new DomainObject[] {}, spheres.get(0).getUsers());
	}
}