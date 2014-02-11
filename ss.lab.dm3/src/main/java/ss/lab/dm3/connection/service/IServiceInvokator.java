package ss.lab.dm3.connection.service;

import java.io.Serializable;

import ss.lab.dm3.connection.ICallbackHandler;

/**
 * @author Dmitry Goncharov
 */
public interface IServiceInvokator {

	/**
	 * 	
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 */
	void ainvoke(String methodName, Class<?>[] parameterTypes, Serializable[] args, ICallbackHandler resultHandler) throws ServiceException;
}