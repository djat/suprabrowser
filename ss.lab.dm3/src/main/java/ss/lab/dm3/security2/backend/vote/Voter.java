package ss.lab.dm3.security2.backend.vote;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.SecurityIdentifierProvider;


public abstract class Voter {


	public enum Access {
		GRANTED(1),
		ABSTAIN(0),
		DENIED(-1);

		private final int intValue;
		
		/**
		 * @param intValue
		 */
		private Access(int intValue) {
			this.intValue = intValue;
		}

		/**
		 * @return
		 */
		public int toInt() {
			return this.intValue;
		}
	};

	protected SecurityIdentifierProvider securityIdentifierProvider;
	
	public SecurityIdentifierProvider getSecurityIdentifierResolver() {
		return this.securityIdentifierProvider;
	}

	public void setSecurityIdentifierResolver(
			SecurityIdentifierProvider securityIdentifierResolver) {
		this.securityIdentifierProvider = securityIdentifierResolver;
	}

	public abstract Access voteAccess( Authentication authentication, Object object, Permission permission );
	
}
