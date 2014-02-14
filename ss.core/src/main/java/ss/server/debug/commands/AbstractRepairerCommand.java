/**
 * 
 */
package ss.server.debug.commands;

import ss.common.ReflectionUtils;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.debug.ssrepair.AbstractRepairer;
import ss.server.debug.ssrepair.Context;

/**
 *
 */
public abstract class AbstractRepairerCommand implements IRemoteCommand {

	/**
	 * 
	 */
	private static final String COMMIT = "commit";
	
	private final Class<? extends AbstractRepairer> repairerClass;
	
	/**
	 * @param repairerClass
	 */
	public AbstractRepairerCommand(final Class<? extends AbstractRepairer> repairerClass) {
		super();
		this.repairerClass = repairerClass;
	}

	/**
	 * (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		final Context repairerContext = new Context( this.repairerClass.getSimpleName(), COMMIT.equals( context.getArgs() ) );
		repaire(repairerContext);
		return repairerContext.getReport();
	}

	/**
	 * @param repairerContext
	 */
	protected void repaire(final Context repairerContext) {
		final AbstractRepairer repairer = ReflectionUtils.create( this.repairerClass, repairerContext );
		repairer.repair();
	}
	
}
