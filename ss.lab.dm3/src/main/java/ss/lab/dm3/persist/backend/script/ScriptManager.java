package ss.lab.dm3.persist.backend.script;

import java.util.HashMap;

import ss.lab.dm3.persist.script.IScript;
import ss.lab.dm3.persist.script.ModifyScript;
import ss.lab.dm3.persist.script.QueryScript;

public class ScriptManager {

	private final HashMap<Class<? extends IScript>, ScriptHandler<?>> scriptClazzToHandler = new HashMap<Class<? extends IScript>, ScriptHandler<?>>();
	
	
	/**
	 * @param script
	 */
	@SuppressWarnings("unchecked")
	public <T extends QueryScript> ReadScriptHandler<T> getHandler(T script) {
		ScriptHandler<?> handler = getRawHandler(script);
		return (ReadScriptHandler<T>) handler;
	}

	/**
	 * @param script
	 */
	@SuppressWarnings("unchecked")
	public <T extends ModifyScript> ModifyScriptHandler<T> getHandler(T script) {
		ScriptHandler<?> handler = getRawHandler(script);
		return (ModifyScriptHandler<T>) handler;
	}

	/**
	 * @param script
	 * @return
	 */
	private ScriptHandler<?> getRawHandler(IScript script) {
		ScriptHandler<?> handler = findHandler(script);
		if ( handler == null ) {
			throw new CantFindScriptHandlerException( script ); 
		}
		return handler;
	}

	public ScriptHandler<?> findHandler(IScript script) {
		Class<? extends IScript> scriptClazz = script.getClass();
		ScriptHandler<?> handler = this.scriptClazzToHandler.get( scriptClazz );
		return handler;
	}

	/**
	 * @param scriptHandlers
	 */
	public void add(Iterable<ScriptHandler<?>> scriptHandlers) {
		for( ScriptHandler<?> scriptHandler : scriptHandlers ) {
			this.scriptClazzToHandler.put( scriptHandler.getScriptClass(), scriptHandler );
		}
	}

}
