/**
 * 
 */
package ss.lab.dm3.persist.changeset;

import java.io.Serializable;

/**
 *
 */
public class ChangeSetId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7965880670125846941L;

	private final String contextId;
	
	private final String changeId;

	/**
	 * @param contextId
	 * @param changeId
	 */
	public ChangeSetId(String contextId, String changeId) {
		super();
		this.contextId = contextId;
		this.changeId = changeId;
	}

	/**
	 * @return the contextId
	 */
	public String getContextId() {
		return this.contextId;
	}

	/**
	 * @return the changeId
	 */
	public String getChangeId() {
		return this.changeId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.changeId == null) ? 0 : this.changeId.hashCode());
		result = prime * result
				+ ((this.contextId == null) ? 0 : this.contextId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ChangeSetId other = (ChangeSetId) obj;
		if (this.changeId == null) {
			if (other.changeId != null)
				return false;
		} else if (!this.changeId.equals(other.changeId))
			return false;
		if (this.contextId == null) {
			if (other.contextId != null)
				return false;
		} else if (!this.contextId.equals(other.contextId))
			return false;
		return true;
	}

	
}
