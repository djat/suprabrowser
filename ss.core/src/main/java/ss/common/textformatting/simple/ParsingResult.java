/**
 * 
 */
package ss.common.textformatting.simple;

import ss.common.StringUtils;
import ss.domainmodel.clubdeals.ClubDealUtils;

/**
 * @author zobo
 *
 */
public class ParsingResult {
	
	private static final String[] KEYWORDS = {"#type:","#note:"};

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ParsingResult.class);
	
	private String note = null;
	
	private String type = null;
	
	private String remaindedSubject = null;
	
	public ParsingResult( final String subject ){
		parse( subject );
	}
	
	public String getNote(){
		return this.note;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getRemaindedSubject(){
		return (this.remaindedSubject != null) ? this.remaindedSubject : "";
	}
	
	private void parse( final String text ){
		if ( StringUtils.isNotBlank(text) ) {
			setType( findFirst(text,0) );
			setNote( findFirst(text,1) );
			setSubjetc( removeKeywordRemainders(text) );
		}
	}
	
	private String findFirst( final String text, final int keywordIndex ){
		if (StringUtils.isBlank(text)){
			return null;
		}
		final int index = text.toLowerCase().indexOf(KEYWORDS[keywordIndex]);
		if (index == -1) {
			return null;
		}
		final String str = text.substring( index + KEYWORDS[keywordIndex].length() );
		final String result = removeKeywordRemainders( str );
		return (StringUtils.isNotBlank(result) ? result.trim() : null);
	}
	
	private String removeKeywordRemainders( final String text ){
		String rem = text;
		for ( String key : KEYWORDS ) {
			if (StringUtils.isBlank(rem)) {
				return null;
			}
			final int index = rem.toLowerCase().indexOf(key);
			if (index != -1) {
				rem = rem.substring(0, index);
			}
		}
		return rem;
	}
	
	private void setNote( final String newNote ){
		if ( this.note == null ) {
			this.note = newNote;
			if (logger.isDebugEnabled()) {
				logger.debug("Note setted: " + this.note);
			}
		}
	}
	
	private void setType( final String newType ){
		if ( this.type == null ) {
			if ( StringUtils.isNotBlank(newType) && (!ClubDealUtils.getAllContactTypes().contains(newType))) {
				logger.error("Type : \"" + newType + "\" is not allowed");
				return;
			}
			this.type = newType;
			if (logger.isDebugEnabled()) {
				logger.debug("Type setted: " + this.type);
			}
		}
	}
	
	private void setSubjetc( final String newSubj ){
		if ( this.remaindedSubject == null ) {
			this.remaindedSubject = newSubj;
			if (logger.isDebugEnabled()) {
				logger.debug("RemaindedSubject setted: " + this.remaindedSubject);
			}
		}
	}
}
