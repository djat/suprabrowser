package ss.common.debug;

import ss.framework.networking2.Command;

public class RunRemoteCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6136393884904461130L;

	private final String name;
	
	private final String args;

	/**
	 * @param name
	 * @param args
	 */
	public RunRemoteCommand(final String name, final String args) {
		super();
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the args
	 */
	public String getArgs() {
		return this.args;
	}

	@Override
	public String toString() {
		return super.toString() + ", name: " + this.name;
	}
	
	
	
	
}
