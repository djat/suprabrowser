package ss.lab.dm3.persist.init;

import junit.framework.TestCase;
import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.persist.AbstractDomainTestCase;

public class SetUpTestCase extends TestCase {

	private class Mock extends AbstractDomainTestCase {
		
		public Mock() {
			super(false);
		}
		
//		@Override
//		protected String getSetUpBatchFileName() {
//			return null;
//		}

		public SystemConnectionProvider createSystemConnectionProvider() {
			try {
				super.setUp();
				SystemConnectionProvider systemConnectionProvider = getSystemConnectionProvider();
				super.tearDown();
				return systemConnectionProvider;
			} catch (Exception ex) {
				throw new RuntimeException( "Can't create domain", ex );
			}			
		}
	}
	
	public void testOne() {
		Mock mock = new Mock();
		mock.createSystemConnectionProvider();
	}
	
	public void testTwo() {
		Mock mock = new Mock();
		mock.createSystemConnectionProvider();
	}
}
