package ss.lab.dm3.orm;

import java.util.HashMap;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;

public class MockObjectResolver extends ObjectResolver {

	private final HashMap<QualifiedObjectId<?>,MappedObject> objects = new HashMap<QualifiedObjectId<?>, MappedObject>();
	
	public void add( MappedObject obj ) {
		QualifiedObjectId<?> id = QualifiedObjectId.create( obj.getClass(), obj.getId() );
		this.objects.put( id, obj );
	}
	
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.ObjectResolver#resolve(java.lang.Class, java.lang.Long)
	 */
	@Override
	public <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
		QualifiedObjectId<?> qualifiedId = QualifiedObjectId.create( entityClass, id );
		return entityClass.cast(this.objects.get( qualifiedId ));
	}

}
