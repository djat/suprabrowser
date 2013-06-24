/**
 * 
 */
package ss.common.domain.model.enums;

/**
 * @author roman
 *
 */
public enum Role {

	NONE {
		public String toString() {
			return "none";
		}
	},
	
	MANAGER {
		public String toString() {
			return "Portfolio Manager";
		}
	},
	
	TRADER {
		public String toString() {
			return "Trader";
		}
	};
	
	public String toString() {
		return null;
	}
}
