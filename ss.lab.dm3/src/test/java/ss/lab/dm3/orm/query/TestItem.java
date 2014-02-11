package ss.lab.dm3.orm.query;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author dmitry
 * 
 */

public class TestItem implements MappedObject {

	enum QueryMark {
		Q1,
		Q2,
		Q3,
		Q4,
		Q5,
		Q6
	};
	
	private Long id;

	private Long parentId;

	private Long ownerId;

	private String title;

	private boolean enabled;

	@Transient
	private Set<QueryMark> queriesMarks = new HashSet<QueryMark>();
	
	/**
	 * 
	 */
	public TestItem() {
		super();
	}

	/**
	 * @param id
	 * @param parentId
	 * @param ownerId
	 * @param title
	 * @param enabled
	 */
	public TestItem(Long id, Long parentId, Long ownerId, String title,
			boolean enabled, QueryMark ...queriesMarks) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.ownerId = ownerId;
		this.title = title;
		this.enabled = enabled;
		for( QueryMark mark : queriesMarks ) {
			this.queriesMarks.add(mark);
		}
	}
	
	public boolean has(QueryMark queryMark){
		return this.queriesMarks.contains(queryMark);
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
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
