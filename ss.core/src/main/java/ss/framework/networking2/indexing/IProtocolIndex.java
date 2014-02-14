package ss.framework.networking2.indexing;

import ss.framework.networking2.Protocol;

public interface IProtocolIndex {

	void add(Protocol protocol );

	/**
	 * @param protocol
	 */
	void remove(Protocol protocol);

	/**
	 * @return
	 */
	boolean isEmpty();
		
}
