package ss.lab.dm3.orm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class EntityList implements Serializable, Iterable<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5005329200788744477L;

	private final List<Entity> items = new ArrayList<Entity>();

	public Iterator<Entity> iterator() {
		return this.items.iterator();
	}

	public void add(Entity item) {
		this.items.add( item );
	}
	
	/**
	 * 
	 */
	public void clear() {
		this.items.clear();
	}

	/**
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "size", this.items.size() );
		return tsb.toString();
	}

	/**
	 * @param entity
	 */
	public boolean contains(Entity entity) {
		return this.items.contains( entity );
	}

	
}
