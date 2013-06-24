package ss.util;

import ss.common.StringUtils;
import ss.global.LoggerConfiguration;
import ss.global.SSLogger;

public class CheckFileExtension {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CheckFileExtension.class);
	
	private final static String[] KNOWN_FILES = {"pdf", "gz", "zip", "jar",
		"doc", "xls", "ppt", "mp3", "ogg", "wav", "java", "txt",
		"xsc", "odp", "odt"};
	
	private final static String[] KNOWN_EDITIBLE_FILES = {"doc", 
		"ppt", "odt", "xls", "txt", "xsc", "odp"};
	
	private final static String[] KNOWN_VIEWABLE_FILES = {"doc",
		"ppt", "odt", "xls", "txt", "xsc", "odp", "pdf"};
	
	private final static String[] KNOWN_CONVERTABLE_FILES = {"doc",
		"ppt", "xls", "rtf", "sxc", "odp", "odt"};
	
	private final static String[] HTML_FILES = {"htm", "html" };
	
	private final static String PDF = "pdf"; 
	
	public static boolean isHTMLDocument( final String fileName ) {
		
		return isKnown( fileName, HTML_FILES );
		
	}

	public static boolean isKnownConvertableDocument( final String fileName ) {
		
		return isKnown( fileName, KNOWN_CONVERTABLE_FILES );
		
	}
	
	public static boolean isKnownFile( final String fileName ) {

		return isKnown( fileName, KNOWN_FILES );

	}

	public static boolean isKnownEditableDocument( final String fileName ) {
		
		return isKnown( fileName, KNOWN_EDITIBLE_FILES );
		
	}

	public static boolean isKnownViewableDocument( final String fileName ) {
		
		return isKnown( fileName, KNOWN_VIEWABLE_FILES );
		
	}

	public static boolean isPDF(String name) {
		
		return isKnown( name, PDF );
		
	}
	
	public static String getExtention( final String str ){
		if ( str == null ) {
			logger.error( "str is null" );
			return null;
		}
		final int index = str.lastIndexOf('.');
		if (index != -1){
			return str.substring(index + 1).toLowerCase();
		}
		return null;
	}
	
	private static boolean contains( final String toCheck, final String[] container ){
		for ( String inner : container ) {
			if (toCheck.equals( inner )){
				return true;
			}
		}
		return false;
	}
	
	private static boolean isKnown( final String fileName, final String[] container){
		final String extention = getExtention( fileName );
		
		if ( StringUtils.isBlank( extention ) ){
			return false;
		}

		if (contains(extention, container)) {
			return true;
		}
		return false;
	}
	
	private static boolean isKnown( final String fileName, final String singleExtention){
		final String extention = getExtention( fileName );
		
		if ( StringUtils.isBlank( extention ) ){
			return false;
		}

		if ( extention.equals( singleExtention ) ) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String[] fileNames = {
				"test.vcf", "test.", "test.doc", "/home/test/test.mp3", "qweqw/as", "asd/asdsad/wqe.asd.xsc"
		};
		showAllInfo(fileNames );
	}
	
	private static void showAllInfo( final String[] fileNames ){
		SSLogger.initialize(LoggerConfiguration.DEFAULT);
		for (String fileName : fileNames) {
			System.out.println(" --- ");
			showInfo_KNOWN_FILES(fileName);
			showInfo_EDITIBLE_FILES(fileName);
			showInfo_VIEWABLE_FILES(fileName);
		}
	}
	
	private static void showInfo_KNOWN_FILES( final String fileName ){
		showInfo(isKnownFile(fileName), fileName, "KNOWN_FILES");
	}
	
	private static void showInfo_EDITIBLE_FILES( final String fileName ){
		showInfo(isKnownEditableDocument(fileName), fileName, "EDITIBLE_FILES");
	}

	private static void showInfo_VIEWABLE_FILES( final String fileName ){
		showInfo(isKnownViewableDocument(fileName), fileName, "VIEWABLE_FILES");
	}
	
	private static void showInfo( final boolean existed, final String fileName, final String message ){
		System.out.println("File name " + fileName + (existed ? " is in " : " is not in ") + message);
	}

}
