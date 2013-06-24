package ss.client.ui.peoplelist;

import java.util.StringTokenizer;

import ss.common.ArgumentNullPointerException;

public class SphereMember implements Comparable {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereMember.class);

	public static String normalizeName(String memberName) {
		if (isNameContainAsterisk(memberName)) {
			return memberName.substring(1, memberName.length() - 1);
		} else {
			return memberName;
		}
	}

	public static boolean isNameContainAsterisk(String memberName) {
		return (memberName.charAt(0) == '*')
				&& (memberName.charAt(memberName.length() - 1) == '*');
	}

	private final ISphereMembersModel modelOwner;

	private final String name;

	private SphereMemberState state;

	private String replyMessageId;

	/**
	 * @param name
	 * @param state
	 */
	public SphereMember(ISphereMembersModel modelOwner, String name) {
		super();
		if (modelOwner == null) {
			throw new ArgumentNullPointerException( "modelOwner" );
		}
		this.modelOwner = modelOwner;
		this.name = normalizeName(name);
		this.state = isNameContainAsterisk(name) ? SphereMemberState.ONLINE
				: SphereMemberState.OFFLINE;
	}
	
	public String getNameWithoutSphere() {
		String newName = null;
		logger.warn("name: " + this.name);
		StringTokenizer st = new StringTokenizer(this.name, ":");
		newName = st.nextToken();
		if (st.hasMoreTokens()) {
			newName = st.nextToken();
			logger.warn("name now: " + this.name);
			newName = newName.substring(1, newName.length());
			logger.warn("Returning : " + newName);
		}
		return newName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the state
	 */
	public SphereMemberState getState() {
		return this.state;
	}

	/**
	 * @param newState
	 *            the state to set
	 */
	public synchronized void updateState(final SphereMemberState newState,
			final String newReplyMessageId) {
		updateState(newState);
		this.replyMessageId = newReplyMessageId;
	}

	/**
	 * @param newState
	 *            the state to set
	 */
	public synchronized boolean updateState(final SphereMemberState newState) {
		if (this.state == newState || this.state.equals(newState)) {
			logger.debug("cancel updating State, cause old " + this.state
					+ " equals to new " + newState);
			return false;
		}
		final boolean isOnlineChanged = this.state.isOnline() != newState
				.isOnline();
		this.state = newState;
		logger.debug("notify state changed for " + this + ". Is online changed " + isOnlineChanged);
		if (isOnlineChanged) {
			this.modelOwner.updateMemberOrder( this );
		} else {
			this.modelOwner.updateMemberLabel(this);
		}
		return true;
	}

	/**
	 * @return the replyMessageId
	 */
	public String getReplyMessageId() {
		return this.replyMessageId;
	}

	/**
	 * @return If user is online than returns name with asterisks otherwise
	 *         returns name
	 */
	public String getQualifiedName() {
		return this.state.isOnline() ? "*" + getName() + "*" : getName();
	}

	public boolean updatePresence(boolean isOnline) {
		return updateState(isOnline ? SphereMemberState.ONLINE
				: SphereMemberState.OFFLINE);
	}

	public boolean isOnline() {
		return this.state.isOnline();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		final SphereMember other = (SphereMember) o;
		if (isOnline() != other.isOnline()) {
			if (isOnline()) {
				return -1;
			} else {
				return 1;
			}
		}
		return getName().compareTo(other.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + ", state " + this.state;
	}

	/**
	 * 
	 */
	public synchronized void resetTyping() {
		if ( this.state == SphereMemberState.TYPING ) {
			updateState( SphereMemberState.ONLINE );
		}
	}

}
