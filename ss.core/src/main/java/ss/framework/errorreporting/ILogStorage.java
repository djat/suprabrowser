package ss.framework.errorreporting;

public interface ILogStorage {

	void store( ILogEvent logEvent );

	/**
	 * @return
	 */
	SessionInformation createSession( ICreateSessionInformation createSessionInfo ) throws CantCreateSessionException;
	
}
