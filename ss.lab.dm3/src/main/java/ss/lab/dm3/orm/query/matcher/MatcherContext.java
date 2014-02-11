package ss.lab.dm3.orm.query.matcher;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.query.index.ICollector;

public class MatcherContext implements ICollector<MappedObject>{
	
	protected Iterable<?extends MappedObject> source;
	
	protected Set<MappedObject> collected = new HashSet<MappedObject>();
	
	/**
	 * @param source
	 */
	public MatcherContext(Iterable<? extends MappedObject> source) {
		this.source = source;
	}

	/**
	 * @param addContext
	 */
	public void add(Iterable<? extends MappedObject> objects) {
		for( MappedObject object : objects ) {
			add( object );
		}
	}

	/**
	 * 
	 */
	public Iterable<? extends MappedObject> getSource() {
		return this.source;
	}

	/**
	 * @param object
	 */
	public void add(MappedObject object) {
		this.collected.add( object );
	}

	public Iterable<MappedObject> getCollected() {
		return this.collected;
	}
	
	public int getSize() {
		return this.collected.size();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "source", this.source );
		tsb.append( "collected", this.collected.size() );
		return tsb.toString();
	}
	
	
}
