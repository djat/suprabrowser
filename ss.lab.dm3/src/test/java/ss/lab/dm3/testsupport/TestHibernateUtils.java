package ss.lab.dm3.testsupport;

import org.hibernate.*;

import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;

/**
 * @author Dmitry Goncharov
 *
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class TestHibernateUtils extends HibernateUtils {

	private static SessionFactory sessionFactory;

	static {
		try {
			sessionFactory = HibernateUtils.createSessionFactory( TestConfigurationProvider.INSTANCE.get() );
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, we could look up in JNDI here
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}
