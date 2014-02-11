package ss.lab.dm3.persist.query;

import java.math.BigInteger;

import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.testsupport.objects.Sphere;

public class GenericQueryTestCase extends AbstractDomainTestCase {

	public void test() {
		final Domain domain = getDomain();
		// Remove all from domain
		domain.getRepository().unloadAll();
		// Check
		Number evaluate = domain.evaluate( Number.class, "select count(*) from sphere" );
		assertEquals( 3, evaluate.intValue() );
		
		String[] genericArray = domain.evaluate( String[].class, "select display_name from sphere" );
		assertEquals( domain.find( Sphere.class, 1L ).getDisplayName(), genericArray[0] );
		assertEquals( domain.find( Sphere.class, 2L ).getDisplayName(), genericArray[1] );
		assertEquals( domain.find( Sphere.class, 3L ).getDisplayName(), genericArray[2] );
		
		Number[] array = domain.evaluate( Number[].class, "select id from sphere" );
		assertEquals( 1L, array[0].longValue() );
		assertEquals( 2L, array[1].longValue() );
		assertEquals( 3L, array[2].longValue() );
		
		Object[] arrayObj = domain.evaluate( Object[].class, "select id,display_name from sphere" );
		for (int i = 0; i < arrayObj.length; i++) {
			Object[] result = (Object[]) arrayObj[i];
			BigInteger integer = BigInteger.valueOf(i+1);
			assertEquals( integer, result[0] );
			assertEquals( domain.find( Sphere.class, new Long(i+1) ).getDisplayName(), result[1] );
		}
	}
}
