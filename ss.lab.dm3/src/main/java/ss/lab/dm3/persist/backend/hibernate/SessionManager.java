package ss.lab.dm3.persist.backend.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import ss.lab.dm3.persist.DomainException;

public abstract class SessionManager {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	protected final SessionFactory sessionFactory;
	
	private final static ThreadLocal<Session> currentSession = new ThreadLocal<Session>();
	
	public SessionManager(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public Session begin() {
		Session session = getCurrentSession();
		if ( session != null ) {
			if ( session.isOpen() ) {
				throw new IllegalStateException( "Can't open new session because already has opened " + session );
			}
		}
		session = openSession();
		try {
			Transaction transaction = session.beginTransaction();
			if (log.isDebugEnabled()) {
				log.debug("Start transaction " + transaction );
			}
		}
		catch( Throwable ex ) {
			closeSessionSilently(session);
			throw new DomainException( "Can't open transaction", ex );
		}
		setCurrentSession(session);
		return session;
	}

	protected abstract void closeSessionSilently(Session session);

	protected abstract Session openSession();

	private void setCurrentSession(Session session) {
		currentSession.set( session );
	}
	
	public void commit() {
		commit(true);
	}
	
	public void commit(boolean clear) {
		Session session = getCurrentSession();
		if ( session != null ) {
			session.flush();
			if ( clear ) {
				session.clear();
			}
			Transaction transaction = session.getTransaction();
			if (log.isDebugEnabled()) {
				log.debug("Commiting transaction " + transaction );
			}
			transaction.commit();			
			closeSessionSilently( session );
			setCurrentSession( null );
		}
		else {
			throw new IllegalStateException( "Can't find current session" );
		}
	}
	
	public void rollback( boolean throwIfError ) {
		Session session = getCurrentSession();
		if ( session != null ) {
			Transaction transaction = session.getTransaction();
			if ( transaction == null ) {
				if ( throwIfError ) {
					throw new IllegalStateException( "Current session has not transaction. Current session " + session );
				}
			}
			else {
				transaction.rollback();
			}
			closeSessionSilently( session );
			setCurrentSession(null);
		}
		else {
			if ( throwIfError ) {
				throw new IllegalStateException( "Can't find current session" );
			}
		}
	}
	
	public static Session getCurrentSession() {
		return currentSession.get();
	}

	public void end() {
		Session session = getCurrentSession();
		if ( session != null ) {
			log.warn( "Current session is null. Close it " + session );
			Transaction transaction = null; 
			try {
				transaction = session.getTransaction();
				if ( transaction != null ) {
					log.warn( "Current transaction is not null. Rollback it " + transaction );
						transaction.rollback();
				}
			}
			catch( Throwable ex ) {
				log.warn( "Can't rollback curremt transaction " + transaction );
			}
			closeSessionSilently(session);
			setCurrentSession(null);
		}				
	}
	
}
