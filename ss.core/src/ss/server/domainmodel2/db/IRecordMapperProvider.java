/**
 * 
 */
package ss.server.domainmodel2.db;



/**
 *
 */
public interface IRecordMapperProvider {

	RecordMapper getMapper( Class domainObjectClass );
	
}
