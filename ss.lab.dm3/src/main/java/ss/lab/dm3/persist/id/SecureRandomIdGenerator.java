package ss.lab.dm3.persist.id;

import java.security.SecureRandom;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainIdGenerator;

public class SecureRandomIdGenerator implements DomainIdGenerator {

	private final SecureRandom random = new SecureRandom();
	
	/**
	 * @return
	 */
	private Long nextPositiveLong() {
		for( int loopGuard = 0; loopGuard < 1000 ; ++ loopGuard  ) {
			final long nextLong = this.random.nextLong();
			if ( nextLong > 0 ) {
				return nextLong;
			}
			if ( nextLong < 0 ) {
				return -nextLong;
			}
		}
		throw new IllegalStateException( "Can't generate secure random not zero Long value" );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.persist.backend.hibernate.DataIdGenerator#createId(ss.lab.dm3.persist.backend.hibernate.DataObject)
	 */
	public Long createId(DomainObject object) {
		return nextPositiveLong();
	}

}
