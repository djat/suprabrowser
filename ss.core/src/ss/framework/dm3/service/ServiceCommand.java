package ss.framework.dm3.service;

import java.io.Serializable;

import ss.framework.networking2.Command;
import ss.lab.dm3.connection.service.ServiceAsync;

/**
 * @author Dmitry Goncharov
 */
public class ServiceCommand extends Command {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6346839522293963442L;

	private final Class<? extends ServiceAsync> serviceClazz;
	private final String methodName;
	private final Class<?>[] parameterTypes;
	private final Serializable[] args;

	/**
	 * @param serviceClass
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 */
	public ServiceCommand(Class<? extends ServiceAsync> serviceClazz,
			String methodName, Class<?>[] parameterTypes, Serializable[] args) {
		super();
		this.serviceClazz = serviceClazz;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}

	public Class<? extends ServiceAsync> getServiceClazz() {
		return this.serviceClazz;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public Class<?>[] getParameterTypes() {
		return this.parameterTypes;
	}

	public Serializable[] getArgs() {
		return this.args;
	}
	
}