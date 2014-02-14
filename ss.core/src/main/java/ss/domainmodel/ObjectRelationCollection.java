package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

public class ObjectRelationCollection extends XmlListEntityObject<ObjectRelation> {

	@SuppressWarnings("unused")
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	public ObjectRelationCollection(){
		super(ObjectRelation.class, ObjectRelation.ROOT_ELEMENT_NAME);
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean contains(String sphereId) {
		return findBySphereId(sphereId) != null;
	}
	
	public ObjectRelation findBySphereId(String sphereId) {
		if ( sphereId == null ) {
			return null;
		}
		for( ObjectRelation relation : this ) {
			if ( sphereId.equals( relation.getSphereId() ) ) {
				return relation;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void clear() {
		super.internalClear();
	}

	/**
	 * @param statement
	 */
	public void add(SphereStatement statement) {
		if ( statement == null ) {
			this.log.warn( "Statement is null"  );
			return;
		}
		if ( !contains( statement.getSystemName() ) ) {
			add(statement.getSystemName(), statement.getDisplayName() );
		}
		
	}

	/**
	 * @param systemName
	 * @param displayName
	 */
	public void add(String sphereId, String displayName) {
		final ObjectRelation objectRelation = new ObjectRelation();
		objectRelation.setDisplayName( displayName );
		objectRelation.setSphereId( sphereId );
		internalAdd( objectRelation );
	}

	public void from( ObjectRelationCollection other ) {
		this.clear();
		this.interalAdd(other);
	}

	/**
	 * @param item
	 */
	public void removeBySphereId(String sphereId) {
		ObjectRelation relation = findBySphereId(sphereId);
		if ( relation != null ) {
			remove( relation );
		}
	}

	/**
	 * @param relation
	 */
	public void remove(ObjectRelation relation) {
		super.internalRemove(relation);
	}

//
//	TODG finish or remove it
//	/**
//	 * @param relations
//	 */
//	public ArbitraryChangeSet<ObjectRelation> getDifference(ObjectRelationCollection relations) {
//		ArbitraryDifferenceBuilder<String> builder = new ArbitraryDifferenceBuilder<String>();
//		builder.getFTo().add( this, new IObjectConverter<String, ObjectRelation>(){
//			public String convert(ObjectRelation obj) {
//				return obj.getSphereId();
//			}			
//		});
//		builder.getTo().add( this, new IObjectConverter<String, ObjectRelation>(){
//			public String convert(ObjectRelation obj) {
//				return obj.getSphereId();
//			}			
//		});
//		
//		
//	}
	
}
