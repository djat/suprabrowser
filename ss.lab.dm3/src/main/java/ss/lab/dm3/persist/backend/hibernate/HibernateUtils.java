package ss.lab.dm3.persist.backend.hibernate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.proxy.HibernateProxyHelper;

import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 *
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtils {

	@SuppressWarnings("unused")
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(HibernateUtils.class);
	
	public static SessionFactory createSessionFactory(Configuration configuration ) {
		final AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.setNamingStrategy( new DataNamingStrategy() );
		for( Class<? extends DomainObject> dataClazz : configuration.getDomainDataClasses() ) {
			annotationConfiguration.addAnnotatedClass( dataClazz );
		}
		
		for( Class<?> dataClazz : configuration.getInternalDataClassList() ) {
			annotationConfiguration.addAnnotatedClass( dataClazz );
		}
		annotationConfiguration.setProperty( "hibernate.connection.url", configuration.getDbUrl() );
		annotationConfiguration.setProperty( "hibernate.connection.username", configuration.getDbUser() );
		annotationConfiguration.setProperty( "hibernate.connection.password", configuration.getDbPassword() );
//		annotationConfiguration.setEntityNotFoundDelegate( new EntityNotFoundDelegate() {
//
//			public void handleEntityNotFound(String entityName, Serializable id) {
//				try {
//					OrmManager manager = OrmManagerResolveHelper.resolve( null );
//					ObjectResolver resolver = manager.getObjectResolver();
//					if ( resolver instanceof CreatedObjectResolver ) {
//						CreatedObjectResolver createdObjectResolver = ((CreatedObjectResolver) resolver);
////						if ( createdObjectResolver.contains( entityName, id ) ) {
////							
////						}
//						return;
//					}
//				}
//				catch( RuntimeException ex ) {
//					log.warn( "Can't resolver orm manager", ex );
//				}
//				System.out.println( "Entity not found " + entityName + " id " + id );
//			}
//			
//		});
		org.hibernate.cfg.Configuration hibConfiguration = annotationConfiguration.configure( configuration.getBaseHibernateConfigurationName() );
		return hibConfiguration.buildSessionFactory();
	}
	/**
	 * @param datclass1
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends DomainObject> getClassWoProxy(DomainObject dataObject ) {
		return HibernateProxyHelper.getClassWithoutInitializingProxy( dataObject );
	}

	public static Session getCurrentSession() {
		return SessionManager.getCurrentSession();
	}
	
//	public static ObjectSelector createObjectSelectorWithCurrentSession() {
//		return new ObjectSelector( getCurrentSession(), OrmManagerResolveHelper.resolve().getBeanMapperProvider().get() );
//	}

}
