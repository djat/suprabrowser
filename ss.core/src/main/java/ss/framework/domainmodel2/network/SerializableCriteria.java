/**
 * 
 */
package ss.framework.domainmodel2.network;

import java.io.Serializable;

import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.EqualFieldCondition;

/**
 *
 */
final class SerializableCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4391877825641320873L;

	private final Class domainObjectClass;
	
	private final Class fieldDesciptorClass;
	
	private final Serializable expectedValue;

	public SerializableCriteria( Criteria criteria ) {
		this.domainObjectClass = criteria.getDomainObjectClass();
		EqualFieldCondition condition = (EqualFieldCondition) criteria.getCondition();
		this.fieldDesciptorClass = condition.getDescriptorClass();
		this.expectedValue = normalizeValue( condition.getExpectedValue() ); 
	}
	
	private static Serializable normalizeValue( Object value ) {
		if ( value instanceof DomainObject ) {
			return ((DomainObject )value).getId();
		}
		if ( value instanceof Serializable ) {
			return (Serializable)value;
		}
		throw new IllegalArgumentException( "Value should be serializable. Value " + value );		
	}
	
	@SuppressWarnings("unchecked")
	public Criteria createCriteria() {
		return CriteriaFactory.createEqual( this.domainObjectClass, this.fieldDesciptorClass, this.expectedValue);
	}
}
