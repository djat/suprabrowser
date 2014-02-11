package ss.lab.dm3.persist.backend.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class SessionManagerCurrentSession extends SessionManager {

	public SessionManagerCurrentSession(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected Session openSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	protected void closeSessionSilently(Session session) {
	}
}
