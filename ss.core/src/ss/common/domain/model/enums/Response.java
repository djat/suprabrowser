/**
 * 
 */
package ss.common.domain.model.enums;

/**
 * @author roman
 *
 */
public enum Response {

	YES {
		public String toString() {
			return "Yes";
		};
	}, 
	
	NO {
		public String toString() {
			return "No";
		};
	}, 
	
	UNSURE {
		public String toString() {
			return "Unsure";
		};
	};
	
	public String toString() {
		return null;
	};
}
