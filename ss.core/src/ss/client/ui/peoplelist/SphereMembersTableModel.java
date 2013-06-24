/**
 * 
 */
package ss.client.ui.peoplelist;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import ss.client.ui.PeopleTable;
import ss.common.ListUtils;

/**
 * 	TODO change from sync root to 
 *  Disply.sync/async exec
 *  
 */
public final class SphereMembersTableModel implements ISphereMembersModel {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereMembersTableModel.class);

	private final PeopleTable peopleTableOwner;

	private final List<SphereMember> members = new ArrayList<SphereMember>();;

	private final Hashtable<String, SphereMember> nameToMember = new Hashtable<String, SphereMember>();

	public SphereMembersTableModel(final PeopleTable owner) {
		this.peopleTableOwner = owner;
	}

	public Object getSyncRoot() {
		return this;
	}

	/**
	 * Returns member by name or throw exception if no member found
	 * 
	 * @param memberName
	 * @return
	 */
	public SphereMember requireMember(String memberName) {
		SphereMember member = findMember(memberName);
		if (member == null) {
			throw new IndexOutOfBoundsException("Cannot find member "
					+ memberName);
		}
		return member;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#isTyping(java.lang.String)
	 */
	public boolean isTyping(String memberName) {
		return requireMember(memberName).getState().isTyping();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#findMember(java.lang.String)
	 */
	public SphereMember findMember(final String memberName) {
		return this.nameToMember.get(SphereMember.normalizeName(memberName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#getReplyMessageId(java.lang.String)
	 */
	public String getReplyMessageId(String memberName) {
		return requireMember(memberName).getReplyMessageId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#setAsNotTyping(java.lang.String)
	 */
	public boolean setAsNotTyping(String memberName) {
		if (logger.isDebugEnabled()) {
			logger.debug("setAsNotTyping " + memberName);
		}
		final SphereMember member = findMember(memberName);
		if (member != null) {
			member.resetTyping();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.models.peoplelist.ISphereMembersModel#setAsTyping(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean setAsTyping(String memberName, String replyMessageId) {
		if (logger.isDebugEnabled()) {
			logger.debug("setAsTyping " + memberName);
		}
		final SphereMember member = findMember(memberName);
		if (member != null) {
			member.updateState(SphereMemberState.TYPING, replyMessageId);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#getMembersNames()
	 */
	public Vector<String> getMembersNames() {
		synchronized (getSyncRoot()) {
			Vector<String> memberNames = new Vector<String>(this.members.size());
			for (SphereMember member : this.members) {
				memberNames.add(member.getQualifiedName());
			}
			return memberNames;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#addMemberAndNotify(java.lang.String)
	 */
	public SphereMember addMemberAndNotify(final String memberName) {
		synchronized (getSyncRoot()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding user " + memberName + " to "
						+ ListUtils.valuesToString(this.members));
			}
			final int addedIndex = addMemberWithoutNotification(memberName);
			SphereMember member = this.members.get(addedIndex);
			notifyExtensiveMemberChange();
			return member;
		}
	}

	private int addMemberWithoutNotification(final String memberName) {
		synchronized (getSyncRoot()) {
			final SphereMember newMember = new SphereMember(this, memberName);
			final int insertionIndex = getInsertionIndex(newMember);
			this.members.add(insertionIndex, newMember);
			this.nameToMember.put(newMember.getName(), newMember);
			return insertionIndex;
		}
	}

	private int getInsertionIndex(final SphereMember newMember) {
		synchronized (getSyncRoot()) {
			for (int n = 0; n < this.members.size(); n++) {
				final SphereMember inListMember = this.members.get(n);
				if (inListMember.compareTo(newMember) > 0) {
					return n;
				}
			}
			return this.members.size();
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#removeMemberAndNotify(java.lang.String)
	 */
	public SphereMember removeMemberAndNotify(final String memberName) {
		synchronized (getSyncRoot()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Removing user " + memberName + " from "
						+ ListUtils.valuesToString(this.members));
			}
			final SphereMember member = findMember(memberName);
			if (member != null) {
				this.members.remove(member);
				notifyExtensiveMemberChange();
			}
			return member;
		}
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#setMembers(java.util.List)
	 */
	public void setMembers(final List<String> newMembersNames) {
		synchronized (getSyncRoot()) {
			final int count = this.members.size();
			if (count > 0) {
				this.nameToMember.clear();
				this.members.clear();
			}
			if (newMembersNames.size() > 0) {
				for (String rawMemberName : newMembersNames) {
					addMemberWithoutNotification(rawMemberName);
				}
			}
			notifyExtensiveMemberChange();
		}
	}
	

	/**
	 * @param index
	 * @return
	 */
	public SphereMember getElementAt(int index) {
		return this.members.get(index);
	}

	private void notifyExtensiveMemberChange() {
		this.peopleTableOwner.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#notifyLocalMemberChange(ss.client.ui.peoplelist.SphereMember)
	 */
	public void updateMemberLabel(SphereMember member) {
		 this.peopleTableOwner.updateMember(member);
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return this.members.toArray();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.peoplelist.ISphereMembersModel#notifyExtensiveMemberChange(ss.client.ui.peoplelist.SphereMember)
	 */
	public void updateMemberOrder(SphereMember member) {
		if ( member != null ) {
			synchronized (getSyncRoot()) {
				final int oldIndex = this.members.indexOf(member);
				if (oldIndex >= 0) {
					this.members.remove(oldIndex);					
				}
				final int newIndex = getInsertionIndex(member);
				this.members.add(newIndex, member);				
			}
		}	
		notifyExtensiveMemberChange();
	}

	
}
