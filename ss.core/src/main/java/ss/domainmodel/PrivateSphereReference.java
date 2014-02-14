package ss.domainmodel;

import ss.common.StringUtils;

public class PrivateSphereReference {

	private final String sphereId;

	private final MemberRelation forwardRelation;

	private MemberRelation backwarkRelation = null;

	/**
	 * @param sphereId
	 * @param firstContactName
	 * @param secondContactName
	 */
	public PrivateSphereReference(String sphereId, String firstContactName,
			String secondContactName, boolean bothRelations ) {
		super();
		this.sphereId = sphereId;
		this.forwardRelation = new MemberRelation(firstContactName,
				secondContactName);
		if ( bothRelations ) {
			this.backwarkRelation = new MemberRelation(secondContactName,
				firstContactName);
		}
	}

	/**
	 * @param nextTableId
	 * @param missedSphereKey
	 */
	public PrivateSphereReference(String sphereId, MemberRelation missedSphereKey, boolean bothRelations ) {
		this(sphereId, missedSphereKey.getFirstContactName(),
				missedSphereKey.getSecondContactName(), bothRelations );
	}

	/**
	 * @param supraSphere
	 */
	public void createSphere(SupraSphereStatement supraSphere) {
		this.forwardRelation.createSphere(supraSphere, this.sphereId);
		if (!isPersonalSphere()) {
			this.backwarkRelation.createSphere(supraSphere, this.sphereId);
		}
	}

	/**
	 * @return
	 */
	private boolean isPersonalSphere() {
		return this.forwardRelation.equals(this.backwarkRelation);
	}

	/**
	 * @return the firstKey
	 */
	public MemberRelation getForwardRelation() {
		return this.forwardRelation;
	}

	/**
	 * @return the secondKey
	 */
	public MemberRelation getBackwarkRelation() {
		return this.backwarkRelation;
	}

	public void setBackwarkRelation(MemberRelation backwarkRelation) {
		this.backwarkRelation = backwarkRelation;
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("p2p sphere ").append(this.sphereId).append(
				StringUtils.getLineSeparator());
		sb.append(this.forwardRelation).append(StringUtils.getLineSeparator());
		sb.append(this.backwarkRelation);
		return sb.toString();
	}

}