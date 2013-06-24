package ss.framework.dm3.backend;

import java.io.Serializable;
import java.util.Hashtable;

import ss.framework.dm3.service.ServiceCommand;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;
import ss.lab.dm3.connection.service.IServiceBackEndFactory;
import ss.lab.dm3.connection.service.IServiceInvokator;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.connection.service.proxy.ReflectionServiceInvokator;

/**
 * @author Dmitry Goncharov
 */
public class ServiceCommandHandler extends RespondentCommandHandler<ServiceCommand,Serializable>{

	private final IServiceBackEndFactory serviceFactory;
	
	private final Hashtable<Class<?>, IServiceInvokator> serviceClassToInvokator = new Hashtable<Class<?>, IServiceInvokator>();
	
	/**
	 */
	public ServiceCommandHandler(IServiceBackEndFactory  serviceFactory) {
		super(ServiceCommand.class);
		this.serviceFactory = serviceFactory;
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected Serializable evaluate(ServiceCommand command)
			throws CommandHandleException {
		IServiceInvokator serviceInvokator = getServiceInvokator( command.getServiceClazz() );
		try {
			return serviceInvokator.invoke( command.getMethodName(), command.getParameterTypes(), command.getArgs() );
		}
		catch( ServiceException ex ) {
			throw new CommandHandleException( "Service invokation failed. Command " + command, ex );
		}
	}

	/**
	 * @param serviceClazz
	 * @return
	 */
	private synchronized IServiceInvokator getServiceInvokator(
			Class<? extends ServiceAsync> serviceClazz) {
		IServiceInvokator serviceInvokator = this.serviceClassToInvokator.get( serviceClazz );
		if ( serviceInvokator == null ) {
			Service serviceImplementation = this.serviceFactory.create( serviceClazz );
			serviceInvokator = new ReflectionServiceInvokator( serviceImplementation );
			this.serviceClassToInvokator.put( serviceClazz, serviceInvokator );
		}
		return serviceInvokator;
	}



}
