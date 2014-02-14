/**
 * 
 */
package ss.server.db.dataaccesscomponents;

/**
 * @author d!ma
 *
 */
public enum DataCommand {
	
	
		SELECT {
			protected String formatPrefixBase() {
				return "SELECT xmldata FROM";
			}
		},
		INSERT { 
			protected String formatPrefixBase() {
				return "INSERT INTO";
			}
		},
		DELETE {
			protected String formatPrefixBase() {
				return "DELETE FROM";
			}			
		};

		private static final String SUPRASPHERES_TABLE_NAME = "supraspheres";
		/**
		 * @return
		 */
		public String formatPrefix() 
		{
			return formatPrefixBase() + " " + SUPRASPHERES_TABLE_NAME;
		}
		
		
		/**
		 * @return
		 */
		protected abstract String formatPrefixBase();

		
}
