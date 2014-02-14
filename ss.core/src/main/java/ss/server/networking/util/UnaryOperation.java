package ss.server.networking.util;

public enum UnaryOperation {
	NOP {
		public boolean eval(boolean operand) {
			return operand;
		}
	},
	NOT {
		public boolean eval(boolean operand) {
			return !operand;
		}
	};
	public abstract boolean eval(boolean operand);
}
