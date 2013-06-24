package ss.domainmodel;

import ss.domainmodel.SphereItem.SphereType;


public class MemberRelation {

	private String firstContactName;

	private String secondContactName;

	/**
	 * @param firstContactName
	 * @param secondContactName
	 */
	public MemberRelation(String firstContactName,
			String secondContactName) {
		super();
		this.firstContactName = firstContactName;
		this.secondContactName = secondContactName;
	}

	/**
	 * @return the firstContactName
	 */
	public String getFirstContactName() {
		return this.firstContactName;
	}

	/**
	 * @return the secondContactName
	 */
	public String getSecondContactName() {
		return this.secondContactName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME
				* result
				+ ((this.firstContactName == null) ? 0
						: this.firstContactName.hashCode());
		result = PRIME
				* result
				+ ((this.secondContactName == null) ? 0
						: this.secondContactName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final MemberRelation other = (MemberRelation) obj;
		if (this.firstContactName == null) {
			if (other.firstContactName != null)
				return false;
		} else if (!this.firstContactName.equals(other.firstContactName))
			return false;
		if (this.secondContactName == null) {
			if (other.secondContactName != null)
				return false;
		} else if (!this.secondContactName.equals(other.secondContactName))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.firstContactName + " -> " + this.secondContactName;
	}

	/**
	 * @param supraSphere
	 * @param sphereId
	 */
	public void createSphere(SupraSphereStatement supraSphere,
			String sphereId) {
		SupraSphereMember member = supraSphere.getSupraMembers()
				.findMemberByContactName(getFirstContactName());
		member.addItem(new SphereItem(sphereId, getSecondContactName(),
				SphereType.MEMBER, true));
	}

}