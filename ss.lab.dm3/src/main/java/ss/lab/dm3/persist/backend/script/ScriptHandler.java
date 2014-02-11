package ss.lab.dm3.persist.backend.script;

import ss.lab.dm3.persist.script.IScript;

/**
 * @author Dmitry Goncharov
 */
public class ScriptHandler<T extends IScript> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Class<T> scriptClass;
	
	/**
	 * @param scriptClass
	 */
	public ScriptHandler(Class<T> scriptClass) {
		super();
		this.scriptClass = scriptClass;
	}


	public Class<T> getScriptClass() {
		return this.scriptClass;
	}
}
