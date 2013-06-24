/**
 * 
 */
package ss.smtp.custom.clubdealinsubject;

import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
class SplittedSubject {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SplittedSubject.class);

	private static final String SEPARATOR = ":";

	private static final String CD = "CD";

	private final String originalSubject;
	
	private String newSubject = null;
	
	private int clubdealNumber = Integer.MIN_VALUE;

	SplittedSubject( final String subject ){
		this.originalSubject = subject;
		parse( this.originalSubject );
	}	
	
	private void parse( final String text ) {
		if ( StringUtils.isNotBlank(text) ) {
			final int index = text.indexOf( SEPARATOR );
			if ( index != -1 ) {
				String[] parts = text.split(SEPARATOR, 2);
				if (checkAndSetClubdeal(parts[0])){
					this.newSubject = (parts[1] != null ? parts[1].trim() : "");
				}
			}
		}
	}
	
	private boolean checkAndSetClubdeal( final String strout ) {
		if ( StringUtils.isBlank(strout) ) {
			return false;
		}
		final String str = strout.trim();
		if ( str.toUpperCase().startsWith( CD ) ){
			String number = str.substring( 2 );
			if ( number != null ) {
				try {
					this.clubdealNumber = Integer.parseInt( number );
					return true;
				} catch ( Throwable ex ){
					if (logger.isDebugEnabled()) {
						logger.debug("Not number: " + number );
					}
				}
			}
		}
		return false;
	}

	public String getOriginalSubject() {
		return this.originalSubject;
	}

	public String getNewSubject() {
		return this.newSubject;
	}

	public int getClubdealName() {
		return this.clubdealNumber;
	}
	
	public boolean isSucceded(){
		return (this.clubdealNumber != Integer.MIN_VALUE); 
	}
}
