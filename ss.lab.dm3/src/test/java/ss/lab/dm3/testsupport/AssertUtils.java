package ss.lab.dm3.testsupport;

import java.util.Collection;

import junit.framework.Assert;

public class AssertUtils {

	public static void assertSetSame( Object[] expectedSet, Collection<?> items) {
		Assert.assertNotNull( items );
		Assert.assertEquals( "Items size", expectedSet.length, items.size() );
		for( Object object : expectedSet ) {
			Assert.assertTrue( "Items " + items + " does not contains " + object, items.contains(object ) );
		}
	}
	
}
