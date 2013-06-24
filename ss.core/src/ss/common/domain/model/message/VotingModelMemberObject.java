/**
 * 
 */
package ss.common.domain.model.message;

import java.util.Date;

import ss.common.domain.model.suprasphere.MemberReferenceObject;

/**
 * @author roman
 *
 */
public class VotingModelMemberObject extends MemberReferenceObject {

	private Date votingMoment;

	/**
	 * @return the votingMoment
	 */
	public Date getVotingMoment() {
		return this.votingMoment;
	}

	/**
	 * @param votingMoment the votingMoment to set
	 */
	public void setVotingMoment(Date votingMoment) {
		this.votingMoment = votingMoment;
	}
}
