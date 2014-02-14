/**
 * 
 */
package ss.util;

import ss.client.ui.PreviewHtmlTextCreator;

/**
 * @author roman
 *
 */
public class TextQuoter {

	public static TextQuoter INSTANCE = new TextQuoter();
	
	protected static final String BREAK = "<br>";
	
	protected static final String QUOTE = "<span style=\"color:black\">&gt;&nbsp;</span>";
	
	private TextQuoter() {
		super();
	}
	
	public String breakAndMakeQuoted(final String text) {
		return breakAndMakeQuoted( text, true );
	}
	
	private String breakAndMakeQuoted(final String text, final boolean isQuote) {
		if(text==null) {
			return null;
		}
		
		String origBody = text.replace("\n", BREAK);
		
		origBody = PreviewHtmlTextCreator.excludeReferences(origBody);
		origBody = PreviewHtmlTextCreator.excludePreformatted(origBody);
		origBody = PreviewHtmlTextCreator.excludeParagraphs(origBody);
		
		if ( !isQuote ) {
			return origBody;
		}
		
		String[] tokens = origBody.split(BREAK);
		
		StringBuffer buffer = new StringBuffer();
		
		for(String token : tokens) {
			if(realTokenLength(token)<=0) {
				continue;
			}
			StringBuffer subBuffer = new StringBuffer();
			prepareToken(token, subBuffer);
			buffer.append(subBuffer.toString());
		}

		return buffer.toString();
	}
	
	public String breakAndCleanUp( final String text ){
		return breakAndMakeQuoted( text, false );
	}
	
	private String prepareToken(String token, StringBuffer subBuffer) {
		if(realTokenLength(token)>70 && !token.startsWith(QUOTE)) {
			int endTokenIndex = Math.min(90, realTokenLength(token));
			int index = lastIndexOfSpace(getSubstring(token, 70, endTokenIndex));
			if(index<0) {
				index = Math.min(20, realTokenLength(getSubstring(token, 70)));
			} else {
				index++;
			}
			
			String tokenTail = getSubstring(token, 70+index);
			token = QUOTE+getSubstring(token, 70, endTokenIndex)+BREAK;
			
			subBuffer.append(token);
			if (realTokenLength(tokenTail)>0){
				prepareToken(tokenTail, subBuffer);
			}
		} else {
			token = QUOTE+token+BREAK;
			subBuffer.append(token);
		}
		return token;
	}
	
	/**
	 * @param token
	 * @param i
	 * @return
	 */
	private String getSubstring(String token, int start) {
		return getSubstring(token, start, realTokenLength(token));
	}

	/**
	 * @param token
	 * @param i
	 * @param endTokenIndex
	 * @return
	 */
	private String getSubstring(String token, int startIndex, int endTokenIndex) {
		if(startIndex>=endTokenIndex) {
			return null;
		}
		int i=0;
		boolean intoTag = false;
		boolean shouldInsert = (startIndex==0);
		char[] chars = token.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(char ch : chars) {
			if(shouldInsert) {
				buffer.append(ch);
			}
			if(ch=='<') {
				intoTag = true;
				continue;
			}
			if(ch=='>') {
				intoTag = false;
				continue;
			}
			if(!intoTag) {
				i++;
			}
			shouldInsert = (i>=startIndex && i<endTokenIndex);
			if(i>=endTokenIndex) {
				return buffer.toString();
			}
		}
		return buffer.toString();
	}

	private int realTokenLength(String token) {
		int i=0;
		boolean intoTag = false;
		char[] chars = token.toCharArray();
		for(char ch : chars) {
			if(ch=='<') {
				intoTag = true;
				continue;
			}
			if(ch=='>') {
				intoTag = false;
				continue;
			}
			if(!intoTag) {
				i++;
			}
		}
		return i;
	}
	
	private int lastIndexOfSpace(String token) {
		int i = 0;
		int spaceIndex = -1;
		boolean intoTag = false;
		char[] chars = token.toCharArray();
		for(char ch : chars) {
			if(ch=='<') {
				intoTag = true;
				continue;
			}
			if(ch=='>') {
				intoTag = false;
				continue;
			}
			if(ch==' ') {
				spaceIndex = i;
			}
			if(!intoTag) {
				i++;
			}
		}
		return spaceIndex;
	}
	
	public static void main(String[] args) {
		String token = "<p style=\"\"> some textsome text some text some text some textsome<a></a> text some text some <b>text some<br> text some textsome<span> text</span></p><br>";
		String subStr = TextQuoter.INSTANCE.getSubstring(token, 20, 60);
		System.out.println(subStr);
		System.out.println(TextQuoter.INSTANCE.lastIndexOfSpace(subStr));
	}
}
