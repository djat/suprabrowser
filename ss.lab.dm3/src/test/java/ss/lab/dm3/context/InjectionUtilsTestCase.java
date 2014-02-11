package ss.lab.dm3.context;

import junit.framework.TestCase;

public class InjectionUtilsTestCase extends TestCase {

	public static class Bean {
		
		protected String fieldProperty = "initialFieldValue";
		
		private String myMethodProperty = "initialMethodValue";

		public String getMethodProperty() {
			return this.myMethodProperty;
		}

		public void setMethodProperty(String methodProperty) {
			this.myMethodProperty = methodProperty;
		}

		public String getFieldProperty() {
			return this.fieldProperty;
		}	
		
	}
	
	public void testInject() {
		Bean bean = new Bean();
		{
			final ValueRestorer inject = InjectionUtils.inject(bean, "fieldProperty",  "fieldInjection" );
			try {
				assertEquals( "fieldInjection", bean.fieldProperty );
			}
			finally {
				inject.restore();
			}
			assertEquals( "initialFieldValue", bean.fieldProperty );
		}
		{
			assertEquals( "initialMethodValue", bean.myMethodProperty );
			final ValueRestorer inject = InjectionUtils.inject(bean, "methodProperty",  "methodInjection" );
			try {
				assertEquals( "methodInjection", bean.myMethodProperty );
			}
			finally {
				inject.restore();
			}
			assertEquals( "initialMethodValue", bean.myMethodProperty );
		}
	}
}
