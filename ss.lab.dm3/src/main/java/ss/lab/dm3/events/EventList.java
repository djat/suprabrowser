package ss.lab.dm3.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class EventList implements Iterable<Event>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4451273238953412969L
	;
	private final List<Event> items = new ArrayList<Event>();
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Event> iterator() {
		return this.items.iterator();
	}

	/**
	 * @param event
	 */
	public void add(Event event) {
		this.items.add( event );
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "itemsSize", this.items.size() )
		.toString();
	}

	
}
