package ss.lab.dm3.persist;

import ss.lab.dm3.orm.entity.Entity;

/**
 * 
 * @author Dmitry Goncharov
 */
public interface IEntityConvertor {
	
	DomainObject convert(Entity entity, ObjectController.State initialState);
	
}
