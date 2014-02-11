package ss.lab.dm3.persist.backend;

import org.hibernate.Session;

import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.hibernate.HibernateUtils;

/**
 *
 */
public class DomainObjectConverter {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private Session session;
	
	private Mapper<DomainObject> mapper;

	public DomainObjectConverter(org.hibernate.classic.Session session,
			Mapper<DomainObject> mapper) {
		this.session = session;
		this.mapper = mapper;
	}

	/**
	 * @param object 
	 * @return
	 */
	public Entity toEntity(DomainObject object) {
		Class<? extends DomainObject> objectClazz = HibernateUtils.getClassWoProxy( object );
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Adding " + object  + " by class " + objectClazz );
		}
		BeanMapper<DomainObject> beanMapper = this.mapper.get( objectClazz );
		if ( beanMapper.isInheritanceBase() ) {
//			if ( this.log.isInfoEnabled() ) {
//				this.log.info( "Found base class instead of subclass " + object + " by " + query );
//			}
			// Evict object from session
			this.session.evict(object);
			object = (DomainObject) this.session.get( objectClazz, object.getId() );
			beanMapper = this.mapper.get( object );
			if ( beanMapper.isInheritanceBase() ) {
				// baseObjects.add( object.getQualifiedId() );
				this.log.warn( "Can't get child class by " + objectClazz );
				this.session.evict(object);
				// continue;
			}
		}
		return beanMapper.toEntity(object);
	}
	
	
//	private void addBaseObjects(EntitiesSelectResult entityList, List<QualifiedObjectId<? extends DomainObject>> baseObjects) {
//		final Session session = getSession();
//		session.beginTransaction();
//		session.clear();
//		for ( QualifiedObjectId<? extends DomainObject> baseObjectId : baseObjects ) {
//			DomainObject object = (DomainObject) session.get( baseObjectId.getObjectClazz(), baseObjectId.getId() ); 
//			BeanMapper<DomainObject> beanMapper = this.mapper.get( object );
//			if ( beanMapper.isInheritanceBase() ) {
//				this.log.error( "Can't get child class by " + baseObjectId );
//				entityList.add( beanMapper.toEntity( object ) );
//			}
//			else {
//				this.log.warn( "Find subclass of " + baseObjectId.getObjectClazz() + " object is " + object );
//				entityList.add( beanMapper.toEntity( object ) );
//			}
//		}
//		session.getTransaction().commit();
//	}
	
}
