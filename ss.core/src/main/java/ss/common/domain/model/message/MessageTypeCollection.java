/**
 * 
 */
package ss.common.domain.model.message;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ss.common.domain.model.enums.MessageType;

/**
 * @author roman
 *
 */
public class MessageTypeCollection implements Iterable<MessageType>{

	private Set<MessageType> types = new HashSet<MessageType>();
	
	public Iterator<MessageType> iterator() {
		return this.types.iterator();
	}
	
	public final void addType(MessageType type) {
		this.types.add(type);
	}
	
	public final void removeType(MessageType type) {
		this.types.remove(type);
	}
	
	public final void removeType(int index) {
		this.types.remove(index);
	}
	
	public boolean contains(MessageType type) {
		return this.types.contains(type);
	}
}
