package ss.lab.dm3.orm.entity;

import java.io.Serializable;

import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.map.BeanMap;
import ss.lab.dm3.utils.CantFindPropertyWithNameException;

/**
 * @author Dmitry Goncharov
 */
public class EntityBuilder {

	private final BeanMap map;
	
	private final Serializable[] values;

	/**
	 * @param map
	 */
	public EntityBuilder(BeanMap map) {
		super();
		this.map = map;
		this.values = new Serializable[ this.map.getSerializeblePropertiesCount() ];
	}
	
	/**
	 * @param userInSphereEntity
	 */
	public EntityBuilder(Mapper<?> mapper, Entity entity) {
		this( mapper.get( entity ).getMap() );
		System.arraycopy( entity.getValues(), 0, this.values, 0, this.values.length	);
	}

	public void setValue( String propertyName, Serializable value ) {
		int index = indexOf(propertyName);
		if ( index < 0 ) {
			throw new CantFindPropertyWithNameException( this, propertyName );
		}
		this.values[ index ] = value;
	}
	
	/**
	 * @param accessorName
	 */
	public int indexOf(String accessorName) {
		return this.map.getPropertySerializableIndex(accessorName);
	}

	public Entity create() {
		return new Entity( this.map.getId(), this.values );
	}

	/**
	 * @param string
	 * @return
	 */
	public Serializable getValue(String propertyName) {
		int index = indexOf(propertyName);
		if ( index < 0 ) {
			throw new CantFindPropertyWithNameException( this, propertyName );
		}
		return this.values[ index ];
	}
}
