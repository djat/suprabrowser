package ss.lab.dm3.persist.transaction.concurrent;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class RandomHelperTestCase extends TestCase {
	
	public void test() {
		List<String> items = Arrays.asList( new String[] {
				"1", "2", "3", "4", "5"
		} );
		for (int n = 0; n < 100; n++) {
			System.out.println( RandomHelper.randomItems( items, items.size() ) );
		}
	}

}
