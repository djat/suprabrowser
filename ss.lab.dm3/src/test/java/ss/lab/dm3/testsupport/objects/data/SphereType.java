/**
 * 
 */
package ss.lab.dm3.testsupport.objects.data;

/**
 * @author roman
 *
 */
public enum SphereType {
	
	GROUP {
		@Override
		public String toString() {
			return "group";
		}
	},
	
	MEMBER {
		@Override
		public String toString() {
			return "member";
		}
	};
	
	@Override
	public abstract String toString();
	
}
