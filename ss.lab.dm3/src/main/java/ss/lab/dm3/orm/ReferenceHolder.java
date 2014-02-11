package ss.lab.dm3.orm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class ReferenceHolder {
	
	private final Class<? extends MappedObject> ownerEntityType;
	
	private final Class<? extends MappedObject> targetEntityType;
	
	private final String propertyName;
	
	private Long targetEntityId;
	
	
	/**
	 * @param from
	 * @param targetEntityType
	 * @param targetEntityId
	 */
	public ReferenceHolder(Class<? extends MappedObject> ownerEntityType, Class<? extends MappedObject> targetEntityType, String propertyName, Long targetEntityId) {
		super();
		if ( ownerEntityType == null ) {
			throw new NullPointerException("ownerEntityType");
		}
		if (targetEntityType == null) {
			throw new NullPointerException("targetEntityType");
		}
		if (propertyName == null) {
			throw new NullPointerException("name");
		}
		this.ownerEntityType = ownerEntityType;
		this.targetEntityType = targetEntityType;
		this.propertyName = propertyName;  
		this.targetEntityId = targetEntityId;		 
	}
	
	public Class<? extends MappedObject> getOwnerEntityType() {
		return this.ownerEntityType;
	}

	public Class<? extends MappedObject> getTargetEntityType() {
		return this.targetEntityType;
	}

	public Long getTargetEntityId() {
		return this.targetEntityId;
	}

	public void setTargetEntityId(Long targetEntityId) {
		this.targetEntityId = targetEntityId;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "targetEntityType", this.targetEntityType.getSimpleName() );
		tsb.append( "targetEntityId", this.targetEntityId );
		return tsb.toString();
	}
		

	
	
}
