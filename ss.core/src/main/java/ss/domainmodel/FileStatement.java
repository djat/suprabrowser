package ss.domainmodel;

import org.apache.log4j.Logger;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.global.SSLogger;

public class FileStatement extends Statement {

	private static final Logger logger = SSLogger.getLogger(FileStatement.class);
	
	private final ISimpleEntityProperty bytes = super
		.createAttributeProperty( "bytes/@value" );
	
	private final ISimpleEntityProperty originalDataId = super
		.createAttributeProperty( "original_data_id/@value" );
	
	private final ISimpleEntityProperty dataId = super
		.createAttributeProperty( "data_id/@value" );
	
	public FileStatement() {
		super( "email" );
	}

	/**
	  Create file object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static FileStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, FileStatement.class);
	}

	/**
	 * Create file object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static FileStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, FileStatement.class);
	}
	
	/**
	 * Gets the file bytes
	 */
	public final String getBytes() {
		return this.bytes.getValue();
	}

	/**
	 * Sets the file bytes
	 */
	public final void setBytes(String value) {
		this.bytes.setValue(value);
	}
	
	/**
	 * Gets the file original data id
	 */
	public final String getOriginalDataId() {
		return this.originalDataId.getValue();
	}

	/**
	 * Sets the file original data id
	 */
	public final void setOriginalDataId(String value) {
		this.originalDataId.setValue(value);
	}
	
	/**
	 * Gets the file data id
	 */
	public final String getDataId() {
		return this.dataId.getValue();
	}

	/**
	 * Sets the file data id
	 */
	public final void setDataId(String value) {
		this.dataId.setValue(value);
	}

	/**
	 * 
	 */
	public String getFilename() {
		if(getDataId()==null) {
			return null;
		}
		try {
			return getDataId().split("_____")[1];
		} catch(RuntimeException ex) {
			logger.warn("null data_id in file:"+getSubject());
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getSizeToString() {
		Long fileSize = Long.parseLong(getBytes());
		if(fileSize<1024) {
			return fileSize+" bytes";
		}
		long kylobytes = fileSize/1024;
		if(kylobytes<1024) {
			String rest = ((fileSize%1024)/100) > 0 ? ","+(fileSize%1024)/100 : "";
			return kylobytes+rest+" Kb";
		}
		long megabytes = kylobytes/1024;
		if(megabytes<1024) {
			String rest = ((kylobytes%1024)/10) > 0 ? ","+(kylobytes%1024)/10 : "";
			return megabytes+rest+" Mb";
		}
		long gigabytes = megabytes/1024;
		String rest = ((megabytes%1024)/10) > 0 ? ","+(megabytes%1024)/10 : "";
		return gigabytes+rest+" Gb";
	}


}
