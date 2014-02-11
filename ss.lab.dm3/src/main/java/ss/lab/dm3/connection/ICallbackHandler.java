package ss.lab.dm3.connection;

/**
 * @author Dmitry Goncharov
 */
public interface ICallbackHandler {

	void onSuccess( Object result ) throws CallbackHandlerException;
	
	void onFail(Throwable ex);
	
}
