package ss.server.debug;

import ss.common.ListUtils;
import ss.common.ReflectionUtils;
import ss.common.debug.RunRemoteCommand;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;

public class RunRemoveCommandHandler extends
		RespondentCommandHandler<RunRemoteCommand, String> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RunRemoveCommandHandler.class);

	/**
	 * @param acceptableCommandClass
	 */
	public RunRemoveCommandHandler() {
		super(RunRemoteCommand.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected String evaluate(RunRemoteCommand command)
			throws CommandHandleException {
		final String name = command.getName();
		IRemoteCommand remoteCommand = findCommand(name);
		if (remoteCommand != null) {
			try {
				RemoteCommandContext context = new RemoteCommandContext( command.getArgs() );
				return remoteCommand.evaluate( context );
			} catch (Exception ex) {
				throw new CommandHandleException(ex);
			}
		} else {
			throw new CommandHandleException("Can't find command with name: \""
					+ (name != null ? name : "[null]") + "\"");
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private IRemoteCommand findCommand(String name) {
		if (name == null) {
			return null;
		}
		if (name.contains(".")) {
			return findCommandByClassName(name);
		} else {
			return findCommandByClassName( VolatileClassLoader.SS_SERVER_DEBUG_VOLATILECOMMANDS + name);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private IRemoteCommand findCommandByClassName(String name) {
		if ( VolatileClassLoader.INSTANCE.isVolatile( name ) ) {
			return loadVolatileCommand(name);
		}
		else {
			return ReflectionUtils.create( name, IRemoteCommand.class );			
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private IRemoteCommand loadVolatileCommand(String name) {
		try {
			Class<?> clazz = VolatileClassLoader.INSTANCE.loadClass(name);
			if (clazz != null) {
				if (!IRemoteCommand.class.isAssignableFrom(clazz)) {
					throw new RuntimeException("Illegal super class for "
							+ clazz.getName()
							+ ", super is  "
							+ ListUtils.allValuesToString(clazz
									.getGenericInterfaces()) + " "
							+ clazz.getGenericSuperclass());
				} else {
					return IRemoteCommand.class.cast(ReflectionUtils
							.create(clazz));
				}
			} else {
				logger.warn("Command not found. Name: " + name);
				return null;
			}
		} catch (ClassNotFoundException ex) {			 
			logger.warn("Command not found. Name: " + name, ex);
			return null;
		}
	}

}
