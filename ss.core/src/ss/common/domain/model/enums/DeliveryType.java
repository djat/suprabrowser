/**
 * 
 */
package ss.common.domain.model.enums;

/**
 * @author roman
 *
 */
public enum DeliveryType {

	CONFIRM_RECEIPT {
		public String toString() {
			return "confirm_receipt";
		}
	},
	
	NORMAL {
		public String toString() {
			return "normal";
		}
	},
	
	POLL {
		public String toString() {
			return "poll";
		}
	},
	
	DECISIVE {
		public String toString() {
			return "decisive";
		}
	};
	
	public String toString() {
		return null;
	}
}
