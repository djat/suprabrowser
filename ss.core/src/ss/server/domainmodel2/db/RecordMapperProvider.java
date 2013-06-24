/**
 * 
 */
package ss.server.domainmodel2.db;

import ss.common.domainmodel2.*;
import ss.framework.domainmodel2.DomainObject;

import java.util.*;

/**
 * 
 */
public class RecordMapperProvider implements IRecordMapperProvider {

	/**
	 * 
	 */
	public static class MapperNotFoundException extends
			IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3652990988563772533L;

		/**
		 * @param domainObjectClass
		 */
		public MapperNotFoundException(Class domainObjectClass) {
			super("Cannot find record mapper for " + domainObjectClass);
		}

	}

	private final Map<Class, RecordMapper> classToMapper = new Hashtable<Class, RecordMapper>();

	/**
	 * 
	 */
	public RecordMapperProvider() {
		super();
		addMapper(Sphere.class, "sphere", new String[] { "id", "system_name", "title", "preferences_xml" });
		addMapper(Member.class, "member", new String[] { "id", "login", "first_name", "core_sphere_id", "preferences_xml" });
		addMapper(InvitedMember.class, "invited_member", new String[] { "id",
				"sphere_id", "member_id", "preferences_xml" });
		addMapper(Configuration.class, "configuration", new String[] { "id",
			"name", "value_xml" });
	}

	/**
	 * @param name
	 * @param string
	 * @param strings
	 */
	private void addMapper(Class<? extends DomainObject> domainObjectClass,
			String tableName, String[] fieldNames) {
		RecordMapper mapper = new RecordMapper(
				domainObjectClass, tableName, fieldNames);
		this.classToMapper.put(mapper.getDomainObjectClass(), mapper);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.server.domainmodel2.IRecordMapperProvider#getMapper(java.lang.Class)
	 */
	public RecordMapper getMapper(Class domainObjectClass) {
		RecordMapper mapper = this.classToMapper
				.get(domainObjectClass);
		if (mapper == null) {
			throw new MapperNotFoundException(domainObjectClass);
		}
		return mapper;
	}

}
