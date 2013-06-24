package ss.framework.domainmodel2;

import ss.common.ArgumentNullPointerException;
import ss.framework.entities.xmlentities.XmlEntityObject;


public abstract class DomainObject {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(IdDescriptor.class);
	
	public static class IdDescriptor extends LongFieldDescriptor{
		public IdDescriptor() {
			super( DomainObject.class, "id" );
		}
	}
	private final FieldMap fieldMap = new FieldMap( this );
		
	private final LongField id = createField( IdDescriptor.class );
	
	private final AbstractDomainSpace spaceOwner;

	/**
	 * @param space 
	 */
	public DomainObject(final AbstractDomainSpace space ) {
		super();
		if ( space == null ) {
			throw new ArgumentNullPointerException( "space" );
		}
		this.spaceOwner = space; 
	}
	
	/**
	 * @param indexedDescriptor
	 * @return
	 */
	private final <F extends Field> F createField(FieldDescriptor<F,?> fieldDescriptor) {
		return this.fieldMap.add( fieldDescriptor );
	}
	
	/**
	 * @param indexedDescriptor
	 * @return
	 */
	protected final <FD extends FieldDescriptor<F,?>,F extends Field> F createField(Class<FD> indexedDescriptor) {
		final FD fieldDescriptor = DescriptorManager.INSTANCE.get( indexedDescriptor );
		return createField( fieldDescriptor );
	}

	/**
	 * @param descriptor
	 * @return
	 */
	protected final <D extends DomainObject> ReferenceField<D> createField(Class<ReferenceFieldDescriptor<D>> indexedDescriptor) {
		final ReferenceFieldDescriptor<D> fieldDescriptor = DescriptorManager.INSTANCE.get( indexedDescriptor );
		return createField( fieldDescriptor );
	}
	
	/**
	 * @param name
	 * @return
	 */
	protected final <E extends XmlEntityObject> XmlEntityField<E> createField(Class<E> entityClass, String name ) {
		final XmlEntityFieldDescriptor<E> fieldDescriptor = DescriptorManager.INSTANCE.get( getClass(), entityClass, name );
		return createField( fieldDescriptor );
	}

	/**
	 * @param name
	 * @param string
	 * @return
	 */
	protected final <F extends Field> F createField(Class<F> fieldClass, String name) {
		FieldDescriptor<F,?> fieldDescriptor = DescriptorManager.INSTANCE.get( getClass(), fieldClass, name );
		return createField(fieldDescriptor);
	}
	
	/**
	 * @param name
	 * @param string
	 * @return
	 */
	protected final <D extends DomainObject> ReferenceField<D> createField(Class<D> targetClass, String name) {
		ReferenceFieldDescriptor<D> fieldDescriptor = DescriptorManager.INSTANCE.get( getClass(), targetClass, name );
		return createField(fieldDescriptor);
	}
	
	/**
	 * Returns object space owner
	 */
	public final synchronized AbstractDomainSpace getSpaceOwner() {
		return this.spaceOwner;
	}
	
	/**
	 * Mark object as removed.
	 * If object is new than it will be not added in transaction commit.
	 * In other cases it will be marked as removed.
	 */
	public final void markRemoved() {
		this.spaceOwner.markRemoved( this );
	}
	
	/**
	 * Mark object as dirty.
	 * Only clean object can became dirty.
	 */
	protected final void markDirty() {
		this.spaceOwner.markDirty(this );
	}
	
	/**
	 * Mark object as new. 
	 * It will be added to db on transaction commit.
	 */
	protected final void markNew( long id ) {
		this.setId(id);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Marked as new " + this );
		}
		this.spaceOwner.markNew(this );
	}	

	/**
	 * Mark object as new. 
	 * It will be added to db on transaction commit.
	 */
	protected final void markNew() {
		markNew( getSpaceOwner().createNewId( getClass() ) );
	}	
	
	/**
	 * @return the id
	 */
	public final long getId() {
		return this.id.get();
	}
	
	final void setId( long id ) {
		this.id.setSilently( id );
	}

	final synchronized void load( Record record ) {
		this.fieldMap.load( record );
	}
	
	final synchronized void save( Record record ) {
		this.fieldMap.save( record );
	}	

	public final boolean isOutOfDate() {
		throw new TbiException( "isOutOfDate" );
	}
	
	/**
	 * @param fieldName
	 * @return
	 */
	public final <F extends Field> F getField(FieldDescriptor<F,?> fieldDescriptor) {
		return this.fieldMap.requireField( fieldDescriptor );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " id: " + getId();
	}

	/**
	 * @return
	 */
	public final ObjectMark getMark() {
		return this.spaceOwner.getMark( this );
	}

	public final String allFieldsToString() {
		return this.fieldMap.allFieldsToString();
	}
	
}
