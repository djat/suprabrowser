package ss.server.networking.util;

import java.util.ArrayList;

import ss.server.networking.DialogsMainPeer;

public class Filter {
	private static final boolean INITIAL_PASS = true;

	private ArrayList<Expression> exp = new ArrayList<Expression>();

	private UnaryOperation uOp;

	public Filter() {
		this(UnaryOperation.NOP);
	}

	public Filter(UnaryOperation uOp) {
		this.uOp = uOp;
	}

	public void add(Expression e) {
		this.exp.add(e);
	}

	public boolean filter(DialogsMainPeer handler) {
		boolean pass = INITIAL_PASS;
		for (Expression e : this.exp) {
			pass = e.filter(handler, pass);
		}
		return this.uOp.eval(pass);
	}
}
