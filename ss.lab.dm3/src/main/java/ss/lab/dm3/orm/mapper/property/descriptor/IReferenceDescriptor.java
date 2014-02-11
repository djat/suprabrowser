/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.ReferenceHolder;

/**
 * @author Dmitry Goncharov
 */
public interface IReferenceDescriptor extends ISerializableDescriptor {

	public enum Multiplicity {
		Unknown,
		OneToOne,
		OneToMany;	
	}

	/**
	 * @return
	 */
	ReferenceHolder createReferenceHolder();

	Class<? extends MappedObject> getTargetEntityClass();
	
	Multiplicity getMultiplicity();
}
