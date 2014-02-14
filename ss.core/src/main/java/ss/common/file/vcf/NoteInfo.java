/**
 * 
 */
package ss.common.file.vcf;

import java.util.List;

import ss.common.StringUtils;
import ss.common.textformatting.TextCustomFormat;
import ss.common.textformatting.simple.ParsingResult;

/**
 * @author zobo
 *
 */
public class NoteInfo {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NoteInfo.class);

	private static final String ERROR = "ERROR";

	private String subject;
	
	private String body;
	
	private String newType;
	
	private boolean message = false;

	public NoteInfo( final String subject, final String body ) {
//		this.subject = parseNote( subject );
//		this.newType = parseType( subject );
//		if ( StringUtils.isBlank( this.newType ) ) {
//			this.newType = parseType( body );
//		}
//		if (StringUtils.isNotBlank( this.subject )) {
//			this.body = body;
//			if ( StringUtils.isBlank( this.body )) {
//				this.message = false;
//			} else {
//				this.message = true;
//			}
//		} else {
//			this.message = false;
//			this.body = parseNote( body );
//		}
		ParsingResult result = new ParsingResult( subject );
		this.subject = result.getNote();
		this.newType = result.getType();
		if (StringUtils.isNotBlank( this.subject )) {
			this.body = body;
			if ( StringUtils.isBlank( this.body )) {
				this.message = false;
			} else {
				this.message = true;
			}
		} else {
			this.body = null;
		}
	}

	private String parseType( final String text ) {
		final List<String> list = TextCustomFormat.INSTANCE.getSingleTypeFromText( text );
		if ( (list == null) || (list.isEmpty()) ) {
			return null;
		}
		return list.get(0);
	}

	public String getSubject() {
		if ( isMessage() ) {
			return this.subject;
		} else {
			return getTextForTerse();
		}
	}

	public String getBody() {
		return this.body;
	}
	
	public boolean isMessage() {
		return this.message;
	}
	
	private String getTextForTerse(){
		if ( StringUtils.isNotBlank( this.subject ) ) {
			return this.subject;
		}
		if ( StringUtils.isNotBlank( this.body ) ) {
			return this.body;
		}		
		return ERROR;
	}
	
	private String parseNote( final String text ) {
		final List<String> list = TextCustomFormat.INSTANCE.getNotesFromText( text );
		if ( (list == null) || (list.isEmpty()) ) {
			return null;
		}
		return list.get(0);
	}

	public boolean isNotEmpty() {
		return ( StringUtils.isNotBlank( this.subject ) || StringUtils.isNotBlank( this.body ) );
	}

	public String getNewType() {
		return this.newType;
	}

	@Override
	public String toString() {
		return "SUBJECT: " + this.subject + " , BODY: " + this.body;
	}
	
	public void setSubject( final String subject ){
		this.subject = subject;
	}
	
	public void setBody( final String body ){
		this.body = body;
	}
	
	public void setIsMessage( final boolean ismessage ){
		this.message = ismessage;
	}
}
