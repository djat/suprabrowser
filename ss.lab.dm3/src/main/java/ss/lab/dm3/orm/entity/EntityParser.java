package ss.lab.dm3.orm.entity;

import java.io.Serializable;

import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.map.BeanMap;

public class EntityParser {

	private final Entity entity;
	
	private final BeanMap map;

	/**
	 * @param entity
	 */
	public EntityParser(Mapper<?> mapper, Entity entity) {
		super();
		this.entity = entity;
		this.map = mapper.get( entity ).getMap();
	}

	public Serializable getValue(String propertyName ) {
		Serializable[] values = this.entity.getValues();
		return values[ this.map.getPropertySerializableIndex(propertyName) ];
	}
	
	
	
}
