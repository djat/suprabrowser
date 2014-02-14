/**
 * 
 */
package ss.common.domain.model.enums;

/**
 * @author roman
 *
 */
public enum MessageType {

	TERSE {
		public String toString() {
			return "terse";
		}
	},
	
	EMAIL {
		public String toString() {
			return "email";
		}
	},
	
	FILE {
		public String toString() {
			return "file";
		}
	},
	
	KEYWORD {
		public String toString() {
			return "keyword";
		}
	},
	
	BOOKMARK {
		public String toString() {
			return "bookmark";
		}
	},
	
	CONTACT {
		public String toString() {
			return "contact";
		}
	},
	
	SPHERE {
		public String toString() {
			return "sphere";
		}
	},
	
	SUPRASPHERE {
		public String toString() {
			return "suprasphere";
		}
	},
	
	RESULT {
		public String toString() {
			return "result";
		}
	},
	
	COMMENT {
		public String toString() {
			return "comment";
		}
	},
	
	RSS {
		public String toString() {
			return "rss";
		}
	},
	
	SYSTEM_MESSAGE {
		public String toString() {
			return "system_message";
		}
	},
	
	MESSAGE {
		public String toString() {
			return "message";
		}
	};
	
	public String toString() {
		return null;
	}
}
