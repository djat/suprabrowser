package ss.lab.dm3.persist.backend.script;

import ss.lab.dm3.persist.script.IScript;

/**
 * @author Dmitry Goncharov
 */
public class CantFindScriptHandlerException extends ScriptException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1926019840311261551L;
	
	/**
	 * @param script
	 */
	public CantFindScriptHandlerException(IScript script) {
		super( "Can't find script handler for " + script );
	}

}
