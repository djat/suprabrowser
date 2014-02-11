package ss.lab.dm3.connection.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.persist.backend.script.ScriptHandler;

public class ScriptHandlerList implements Iterable<ScriptHandler<?>>{

	private List<ScriptHandler<?>> items = new ArrayList<ScriptHandler<?>>(); 
	
	/**
	 * @param startupLoaderScriptHandler
	 */
	public void add(ScriptHandler<?> scriptHandler) {
		this.items.add(scriptHandler);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ScriptHandler<?>> iterator() {
		return this.items.iterator();
	}

}
