package ss.server.networking.util;

import ss.server.networking.DialogsMainPeer;

public class Expression {

	private HandlerKey key;

	private String value;

	private UnaryOperation uOp;

	private BinaryOperation bOp;

	public Expression(HandlerKey id, String value) {
		this(id, value, UnaryOperation.NOP, BinaryOperation.AND);
	}

	public Expression(HandlerKey id, String value, UnaryOperation uOp) {
		this(id, value, uOp, BinaryOperation.AND);
	}

	public Expression(HandlerKey id, String value, BinaryOperation bOp) {
		this(id, value, UnaryOperation.NOP, bOp);
	}

	public Expression(HandlerKey id, String value, UnaryOperation uOp,
			BinaryOperation bOp) {
		this.key = id;
		this.value = value;
		this.uOp = uOp;
		this.bOp = bOp;
	}

	public boolean filter(DialogsMainPeer handler) {
		String hValue = handler.get(this.key);
		boolean equals = hValue.equals(this.value);
		return this.uOp.eval(equals);
	}

	public boolean filter(DialogsMainPeer handler, boolean accumulator) {
		return this.bOp.eval(accumulator, filter(handler));
	}

}
