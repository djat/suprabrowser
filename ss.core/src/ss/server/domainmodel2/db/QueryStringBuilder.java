/**
 * 
 */
package ss.server.domainmodel2.db;

import java.util.Map;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.DescriptorManager;
import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.EqualFieldCondition;
import ss.framework.domainmodel2.FieldDescriptor;
import ss.framework.domainmodel2.Record;

/**
 * 
 */
public final class QueryStringBuilder {

	private final RecordMapper mapper;

	private final StringBuilder sb = new StringBuilder();

	private boolean separatorSupressed;

	/**
	 * @param mapper
	 */
	public QueryStringBuilder(final RecordMapper mapper) {
		super();
		if (mapper == null) {
			throw new ArgumentNullPointerException("mapper");
		}
		this.mapper = mapper;
	}

	/**
	 * @param clause
	 */
	public QueryStringBuilder add(String clause) {
		flushSpace();
		this.sb.append(clause);
		desireSeparator();
		return this;
	}

	/**
	 * 
	 */
	private void flushSpace() {
		flushSeparator(" ");
	}

	/**
	 * @param separator
	 */
	private void flushComaSpace() {
		flushSeparator(", ");
	}

	/**
	 * @param separator
	 */
	private void flushSeparator(String separator) {
		if (!isSeparatorSupressed()) {
			this.sb.append(separator);
		}
		suppressSeparator();
	}

	/**
	 * @return the separatorSupressed
	 */
	private boolean isSeparatorSupressed() {
		return this.separatorSupressed;
	}

	private void suppressSeparator() {
		this.separatorSupressed = true;
	}

	private void desireSeparator() {
		this.separatorSupressed = false;
	}

	public QueryStringBuilder openBrace() {
		add("(");
		suppressSeparator();
		return this;
	}

	public QueryStringBuilder closeBrace() {
		suppressSeparator();
		add(")");
		return this;
	}

	/**
	 * 
	 */
	private void addWhere() {
		add("WHERE");
	}

	private void addEqual(String fieldName, Object fieldValue) {
		addName(fieldName);
		add("=");
		addValue(fieldValue);
	}

	/**
	 * @param record
	 */
	public QueryStringBuilder addWhereById(Record record) {
		addWhere();
		addEqual(this.mapper.getIdFieldName(), record.getId());
		return this;
	}

	/**
	 * @param string
	 */
	public QueryStringBuilder addWhere(Criteria criteria) {
		addWhere();
		addCondition(criteria);
		return this;
	}

	/**
	 * @param criteria
	 */
	private void addCondition(Criteria criteria) {
		EqualFieldCondition condition = (EqualFieldCondition) criteria
				.getCondition();
		Object expectedValue = condition.getExpectedValue();
		if (expectedValue instanceof DomainObject) {
			expectedValue = ((DomainObject) expectedValue).getId();
		}
		FieldDescriptor descriptor = DescriptorManager.INSTANCE.get(condition
				.getDescriptorClass());
		addEqual(descriptor.getName(), expectedValue);
	}

	/**
	 * 
	 */
	public QueryStringBuilder addTableName() {
		addName(this.mapper.getTableName());
		return this;
	}

	/**
	 * @param record
	 */
	public QueryStringBuilder addValuesUpdate(Record record) {
		Map<String, Object> fieldNameToValue = this.mapper
				.getFieldNameToValue(record);
		flushSpace();
		for (String fieldName : fieldNameToValue.keySet()) {
			flushComaSpace();
			addEqual(fieldName, fieldNameToValue.get(fieldName));
		}
		return this;
	}

	/**
	 * @param names
	 */
	private QueryStringBuilder addNames(Iterable<String> names) {
		flushSpace();
		for (String name : names) {
			flushComaSpace();
			addName(name);
		}
		return this;
	}

	/**
	 * 
	 */
	public QueryStringBuilder addFieldNames() {
		return addNames(this.mapper.getFieldNames());
	}

	/**
	 * @param record
	 */
	public QueryStringBuilder addFieldNames(Record record) {
		return addNames(this.mapper.getFieldNames(record));
	}

	/**
	 * @param record
	 */
	public QueryStringBuilder addValues(Record record) {
		flushSpace();
		for (Object value : this.mapper.getFieldValues(record)) {
			flushComaSpace();
			addValue(value);
		}
		return this;
	}

	/**
	 * @param value
	 */
	private void addValue(Object value) {
		String strValue = value != null ? value.toString() : "";
		StringBuilder sb = new StringBuilder(strValue.length() + 2);
		sb.append('\'');
		for (int n = 0; n < strValue.length(); n++) {
			char ch = strValue.charAt(n);
			switch (ch) {
			case '\n':
				sb.append("\\n");
				break;
			case '\'':
				sb.append("''");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		sb.append('\'');
		add(sb.toString());

	}

	/**
	 * @param name
	 */
	private void addName(String name) {
		add("`" + name + "`");
	}

	/**
	 * @return
	 * @see java.lang.StringBuilder#toString()
	 */
	public String toString() {
		return this.sb.toString();
	}

}
