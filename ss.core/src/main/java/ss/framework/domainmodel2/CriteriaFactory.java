/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public class CriteriaFactory {

	/**
	 * @param name
	 * @param name2
	 * @param memberOwner
	 * @return
	 */
	public static <D extends DomainObject,FD extends FieldDescriptor<?,V>,V> Criteria<D> createEqual( Class<D> domainObjectClass, Class<FD> fieldDesciptorClass, V expectedValue ) {
		FieldCondition fieldCondition = DescriptorManager.INSTANCE.get(fieldDesciptorClass).createEqualFieldCondition(expectedValue);
		return new Criteria<D>(domainObjectClass, fieldCondition);
	}

}
