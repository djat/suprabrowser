/**
 * 
 */
package ss.lab.dm3.testsupport.objects.data;

/**
 * @author roman
 *
 */
public enum DeliveryType {
	
	NORMAL {
		@Override
		public String toString() {
			return "normal";
		}
	},

	CONFIRM_RECEIPT {
		@Override
		public String toString() {
			return "confirm_receipt";
		}
	},
	
	POLL {
		@Override
		public String toString() {
			return "poll";
		}
	},
	
	DECISIVE {
		@Override
		public String toString() {
			return "decisive";
		}
	};
	
}
