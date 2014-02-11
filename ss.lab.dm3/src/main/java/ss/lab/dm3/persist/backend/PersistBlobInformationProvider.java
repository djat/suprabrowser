package ss.lab.dm3.persist.backend;

import org.hibernate.Session;

import ss.lab.dm3.blob.IBlobObject;
import ss.lab.dm3.blob.backend.BlobInformation;
import ss.lab.dm3.blob.backend.BlobInformationProvider;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.hibernate.SessionManager;

public class PersistBlobInformationProvider extends BlobInformationProvider {
	
	private final SessionManager sessionManager;
	
	/**
	 * @param domain
	 */
	public PersistBlobInformationProvider( SessionManager sessionManager ) {
		super();
		this.sessionManager = sessionManager;
	}

	/**
	 * @param blobInfo
	 */
	@Override
	protected void updateBlobBy(BlobInformation blobInfo) {
		final Session session = sessionManager.begin();
		try {
			final IBlobObject obj = find( session, blobInfo.getResourceId() );
			if ( obj == null ) {
				this.log.error( "Can't find blob by " + blobInfo );
				return;
			}
			blobInfo.writePropertiresTo( obj );
		}
		finally {
			sessionManager.commit();
		}
	}

	@Override
	protected IBlobObject find( QualifiedObjectId<?> resourceId) {
		final Session session = sessionManager.begin();
		try {
			return find(session, resourceId);
		}
		finally {
			sessionManager.commit();
		}
	}

	/**
	 * @param session
	 * @param resourceId
	 * @return
	 */
	private IBlobObject find(Session session, QualifiedObjectId<?> resourceId) {
		QualifiedObjectId<? extends DomainObject> domainResourceId = QualifiedObjectId.cast( DomainObject.class, resourceId);
		final Class<? extends DomainObject> objectClazz = domainResourceId.getObjectClazz();
		DomainObject obj = objectClazz.cast( session.get( objectClazz, domainResourceId.getId() ) );
		return (IBlobObject) obj;
	}

}
