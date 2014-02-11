package ss.lab.dm3.persist.backend.script;

import ss.lab.dm3.persist.script.QueryScript;

/**
 * @author Dmitry Goncharov
 */
public abstract class ReadScriptHandler<T extends QueryScript> extends ScriptHandler<T> {

	/**
	 * @param scenarioClass
	 */
	public ReadScriptHandler(Class<T> scenarioClass) {
		super(scenarioClass);
	}

	public abstract void handle( ReadScriptContext<T> context );
	
}
