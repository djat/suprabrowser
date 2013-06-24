package ss.server.networking.util;

public enum BinaryOperation {
	AND {
		public boolean eval(boolean first, boolean second) {
			return first & second;
		}
	},
	OR {
		public boolean eval(boolean first, boolean second) {
			return first | second;
		}
	};

	public abstract boolean eval(boolean first, boolean second);

}
