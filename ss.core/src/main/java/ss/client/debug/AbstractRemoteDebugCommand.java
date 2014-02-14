/**
 * 
 */
package ss.client.debug;

import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.common.debug.DebugUtils;
import ss.framework.networking2.Command;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.Protocol;
import ss.framework.threads.LinearExecutors;

/**
 * 
 */
public abstract class AbstractRemoteDebugCommand extends AbstractDebugCommand {

	/**
	 * 
	 */
	private static final String DEBUG_REMOVE_COMMAND_EXECUTOR = "DebugRemoveCommandExecutor";

	/**
	 * @param displayName
	 */
	protected AbstractRemoteDebugCommand(String displayName) {
		super(displayName);
	}

	/**
	 * @param mainCommandName
	 * @param displayName
	 */
	public AbstractRemoteDebugCommand(String mainCommandName, String displayName) {
		super(mainCommandName, displayName);
	}
	
	/**
	 * @param command
	 */
	public void beginExecute(Command command) {		
		RemoteCommandRunner runner = new RemoteCommandRunner( getCurrentContext(), command );
		LinearExecutors.beginExecute( DEBUG_REMOVE_COMMAND_EXECUTOR, runner );
		final int count = LinearExecutors.INSTANCE.getLineTasksCount( DEBUG_REMOVE_COMMAND_EXECUTOR ); 
		this.getCommandOutput().appendln( "Begin command executing. Order #" + count + ". Please wait..." );
	}

	
	/**
	 *
	 */
	private final class RemoteCommandRunner implements Runnable {
		
		private StringBuilder output = new StringBuilder();
		
		private final IDebugCommandConext context;

		private final Command command;

		/**
		 * @param context
		 * @param command
		 */
		public RemoteCommandRunner(final IDebugCommandConext context, final Command command) {
			super();
			this.context = context;
			this.command = command;
		}

		public void run() {
			try {
				trace( "Begin protocol open" );
				Protocol protocol = DebugProtocolFactory.INSTANCE.create();
				try {
					trace( "Sending command " + this.command );
					final String result = this.command.execute(protocol, String.class);
					trace( "Result:" );
					trace( "---" );
					trace(result);
				} catch (CommandExecuteException ex) {
					trace(ex);
				} finally {
					trace( "---" );
					trace( "Begin protocol close" );
					protocol.beginClose();
				}
			} catch (Exception ex) {
				trace( "Can't create protocol", ex);
			}
		}

		private void trace(Throwable ex) {
			trace( null, ex );
		}

		private void trace(String message, Throwable ex) {
			trace( message + ( message != null ? " " : "" ) + DebugUtils.getExceptionInfo(ex) ); 
		}

		private void trace(String message) {
			this.output.append(message);
			this.output.append( StringUtils.getLineSeparator() );
			UiUtils.swtBeginInvoke( new Runnable() {
				public void run() {
					RemoteCommandRunner.this.context.handleOutput(RemoteCommandRunner.this.output.toString());
				}
			} );
		}
		
	}
}
