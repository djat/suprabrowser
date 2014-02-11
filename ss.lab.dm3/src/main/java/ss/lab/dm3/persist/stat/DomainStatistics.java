package ss.lab.dm3.persist.stat;

/**
 * @author dmitry
 *
 */
public class DomainStatistics {

	private int transactionChangeSetsSize;
	
	private int listenersSize;
	
	private int cleanObjectsSize;
	
	private int spacesSize;
	
	private int proxyObjectSetSize;

	public DomainStatistics(int transactionChangeSetsSize, int listenersSize,
			int cleanObjectsSize, int spacesSize, int proxyObjectSetSize) {
		this.transactionChangeSetsSize = transactionChangeSetsSize;
		this.listenersSize = listenersSize;
		this.cleanObjectsSize = cleanObjectsSize;
		this.spacesSize = spacesSize;
		this.proxyObjectSetSize = proxyObjectSetSize;
	}

	public int getTransactionChangeSetsSize() {
		return this.transactionChangeSetsSize;
	}

	public void setTransactionChangeSetsSize(int transactionChangeSetsSize) {
		this.transactionChangeSetsSize = transactionChangeSetsSize;
	}

	public int getListenersSize() {
		return this.listenersSize;
	}

	public void setListenersSize(int listenersSize) {
		this.listenersSize = listenersSize;
	}

	public int getCleanObjectsSize() {
		return this.cleanObjectsSize;
	}

	public void setCleanObjectsSize(int cleanObjectsSize) {
		this.cleanObjectsSize = cleanObjectsSize;
	}

	public int getSpacesSize() {
		return this.spacesSize;
	}

	public void setSpacesSize(int spacesSize) {
		this.spacesSize = spacesSize;
	}

	public int getProxyObjectSetSize() {
		return this.proxyObjectSetSize;
	}

	public void setProxyObjectSetSize(int proxyObjectSetSize) {
		this.proxyObjectSetSize = proxyObjectSetSize;
	}
	
}
