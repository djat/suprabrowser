package ss.framework.dm3.service;

import java.io.Serializable;

import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.Protocol;
import ss.lab.dm3.connection.service.IServiceInvokator;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.ServiceAsync;

/**
 * @author Dmitry Goncharov
 */
public class NetworkServiceInvokator implements IServiceInvokator {

	private final Protocol protocol;
	
	private final Class<? extends ServiceAsync> serviceClass;
		
	/**
	 * @param protocol
	 * @param serviceClass
	 */
	public NetworkServiceInvokator(Protocol protocol,
			Class<? extends ServiceAsync> serviceClass) {
		super();
		this.protocol = protocol;
		this.serviceClass = serviceClass;
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.connection.service.IServiceInvokator#invoke(java.lang.String, java.lang.Class<?>[], java.io.Serializable[])
	 */
	public Serializable invoke(String methodName, Class<?>[] parameterTypes,
			Serializable[] args) throws ServiceException {
		ServiceCommand command = new ServiceCommand( this.serviceClass, methodName, parameterTypes, args ); 
		try {
			return command.execute( this.protocol, Serializable.class );
		} catch (CommandExecuteException ex) {
			throw new ServiceException( "Service command failed " +  command, ex );
		}
	}

}