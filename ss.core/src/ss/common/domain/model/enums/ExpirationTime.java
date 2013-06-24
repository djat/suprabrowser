/**
 * 
 */
package ss.common.domain.model.enums;

/**
 * @author roman
 *
 */
public enum ExpirationTime {
	
	ALL {
		public String toString() {
			return "all";
		}
	},
	DAY_1 {
		public String toString() {
			return "1 day";
		}
	},
	DAYS_2 {
		public String toString() {
			return "2 days";
		}
	},
	DAYS_3 {
		public String toString() {
			return "3 days";
		}
	},
	DAYS_4 {
		public String toString() {
			return "4 days";
		}
	},
	DAYS_5 {
		public String toString() {
			return "5 days";
		}
	},
	HOUR_1 {
		public String toString() {
			return "1 hour";
		}
	},
	HOURS_2 {
		public String toString() {
			return "2 hours";
		}
	},
	HOURS_3 {
		public String toString() {
			return "3 hours";
		}
	},
	HOURS_6 {
		public String toString() {
			return "6 hours";
		}
	},
	WEEK_1 {
		public String toString() {
			return "1 week";
		}
	},
	WEEKS_2 {
		public String toString() {
			return "2 weeks";
		}
	},
	WEEKS_4 {
		public String toString() {
			return "4 weeks";
		}
	},
	NONE {
		public String toString() {
			return "none";
		}
	};


	public String toString() {
		return null;
	}
}
