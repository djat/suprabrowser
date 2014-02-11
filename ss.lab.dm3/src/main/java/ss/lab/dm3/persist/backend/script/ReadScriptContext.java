package ss.lab.dm3.persist.backend.script;


import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.backend.ObjectCollector;
import ss.lab.dm3.persist.backend.hibernate.ObjectSelector;
import ss.lab.dm3.persist.script.QueryScript;

/**
 * @author Dmitry Goncharov
 */
public class ReadScriptContext<T extends QueryScript> {

	private final T script;
	
	private final ObjectSelector selector;
	
	private final ObjectCollector collector;
	
	/**
	 * @param script
	 * @param selector
	 */
	public ReadScriptContext(T script, ObjectSelector selector) {
		super();
		this.script = script;
		this.selector = selector;
		this.collector = new ObjectCollector(this.selector);
	}

	public T getScript() {
		return this.script;
	}

	/**
	 * @param createEqByClass
	 */
	public void collect(TypedQuery<?> criteria) {
		this.collector.add( this.selector.select( criteria ) );
	}

	public ObjectSelector getSelector() {
		return this.selector;
	}

	public ObjectCollector getCollector() {
		return this.collector;
	}

	
}
