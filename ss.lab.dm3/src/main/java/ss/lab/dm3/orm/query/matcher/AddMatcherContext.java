package ss.lab.dm3.orm.query.matcher;

import java.util.HashSet;

import ss.lab.dm3.orm.MappedObject;

public class AddMatcherContext extends MatcherContext {
	
	/**
	 * @param parent
	 */
	public AddMatcherContext(MatcherContext parent) {
		super( parent.getSource() );
	}

	/**
	 * 
	 */
	public void replaceSourceByCollectedAndResetCollected() {
		this.source = this.collected;
		this.collected = new HashSet<MappedObject>();
	}

}
