package ss.framework.domainmodel2;


public final class Criteria<D extends DomainObject> {

	private final Class<D> domainObjectClass;
	
	private final FieldCondition condition;
	
	/**
	 * @param domainObjectClass
	 * @param condition
	 */
	public Criteria(Class<D> domainObjectClass, FieldCondition condition) {
		super();
		this.domainObjectClass = domainObjectClass;
		this.condition = condition;
	}

	/**
	 * @param object
	 * @return
	 */
	public boolean match(DomainObject object) {
		if ( object == null ) {
			return false;
		}
		if ( this.domainObjectClass != object.getClass() ) {
			return false;
		}
		return this.condition.match( object );
	}

	/**
	 * @return
	 */
	public final Class<D> getDomainObjectClass() {
		return this.domainObjectClass;
	}

	/**
	 * @return the condition
	 */
	public final FieldCondition getCondition() {
		return this.condition;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Criteria [" 
		+ this.domainObjectClass.getName() 
		+ ", " + this.condition.toString()  
		+ "]";
	}
	
	
}
