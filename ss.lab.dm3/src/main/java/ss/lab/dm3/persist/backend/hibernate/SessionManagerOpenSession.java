package ss.lab.dm3.persist.backend.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class SessionManagerOpenSession extends SessionManager {

	public SessionManagerOpenSession(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected void closeSessionSilently(Session session) {
		if ( session != null ) {
			try {
				session.close();
			}
			catch( Throwable ex ) {
				this.log.warn( "Can't close session " + session, ex );
			}
		}
		else {
			this.log.warn( "Session is null" );
		}
	}

	@Override
	protected Session openSession() {
		return this.sessionFactory.openSession();
	}

}
