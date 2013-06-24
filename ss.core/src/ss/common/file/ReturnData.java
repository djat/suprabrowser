/**
 * 
 */
package ss.common.file;

import java.util.List;

import ss.common.ListUtils;

/**
 * @author zobo
 *
 */
public class ReturnData {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReturnData.class);
	private List<String> spheresNotToPublish;
	
	private List<String> spheresNotToIndex;

	public ReturnData(List<String> spheresNotToPublish,
			List<String> spheresNotToIndex) {
		super();
		this.spheresNotToPublish = spheresNotToPublish;
		this.spheresNotToIndex = spheresNotToIndex;
	}
	
	public void addData( final ReturnData data ){
		if ( data == null ) {
			return;
		}
		if ( data.getSpheresNotToIndex() != null ) {
			if (getSpheresNotToIndex() == null) {
				this.spheresNotToIndex = data.getSpheresNotToIndex();
			} else {
				for ( String sphereId : data.getSpheresNotToIndex() ) {
					if ( !getSpheresNotToIndex().contains(sphereId) ) {
						getSpheresNotToIndex().add( sphereId );
					}
				}
			}
		}
		if ( data.getSpheresNotToPublish() != null ) {
			if ( getSpheresNotToPublish() == null ) {
				this.spheresNotToPublish = data.getSpheresNotToPublish();
			} else {
				for ( String sphereId : data.getSpheresNotToPublish() ) {
					if ( !getSpheresNotToPublish().contains(sphereId) ) {
						getSpheresNotToPublish().add( sphereId );
					}
				}
			}
		}
	}

	public List<String> getSpheresNotToPublish() {
		return this.spheresNotToPublish;
	}

	public List<String> getSpheresNotToIndex() {
		return this.spheresNotToIndex;
	}
}
