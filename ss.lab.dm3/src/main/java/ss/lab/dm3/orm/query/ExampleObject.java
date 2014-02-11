package ss.lab.dm3.orm.query;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;

public class ExampleObject implements MappedObject {

	private Long id;
	
	private Long parentId;
	
	private Long ownerId;
	
	private String title;

	
	/**
	 * @param id
	 * @param parentId
	 * @param ownerId
	 * @param title
	 */
	public ExampleObject(Long id, Long parentId, Long ownerId, String title) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.ownerId = ownerId;
		this.title = title;
	}

	public Long getId() {
		return this.id;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString( this );
	}

	
	
}
