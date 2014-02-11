package ss.lab.dm3.security2.backend;

import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.backend.vote.Voter;
import ss.lab.dm3.security2.backend.vote.Voter.Access;

/**
 * Allow access to object if one or more voters said Access.GRANTED and no voter
 * said Access.DENIED
 * 
 * @author Dmitry Goncharov
 * 
 */
public class AccessDecisionStrategy {

	private List<Voter> voters = new ArrayList<Voter>();

	/**
	 * @param authentication
	 * @param object
	 * @param permission
	 * @return
	 */
	public boolean hasAccess(Authentication authentication, Object object,
			Permission permission) {
		// Predefined condition that system can anything
		if ( authentication.getAuthorities().contains(Authority.SYSTEM) ) {
			return true;
		} else {
			// Start voting
			int votes = 0;
			for (Voter voter : this.voters) {
				Access access = voter.voteAccess(authentication, object,
						permission);
				if (access == Access.DENIED) {
					return false;
				}
				votes += access.toInt();
			}
			return votes > 0;
		}
	}

	public List<Voter> getVoters() {
		return this.voters;
	}

	public void setVoters(List<Voter> voters) {
		this.voters = voters;
	}
	
	

}
