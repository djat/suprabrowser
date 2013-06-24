package ss.client.ui.typeahead;

import org.eclipse.swt.widgets.Text;

import ss.common.StringUtils;

public class TextContentProvider implements IControlContentProvider {

	private static final String DEFAULT_SPLIT_STRING = " ";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TextContentProvider.class);
	
	protected final Text text;

	private final String[] validPhrasePrefixes;
	
	protected final String splitString;
	
	public TextContentProvider(final Text text, final String splitString) {
		this( text, null, splitString );
	}
	
	public TextContentProvider(final Text text ) {
		this( text, null, null );
	}
	/**
	 * @param text
	 */
	public TextContentProvider(final Text text, String [] validPhrasePrefixes, final String splitString ) {
		super();
		this.text = text;		
		this.validPhrasePrefixes = validPhrasePrefixes != null ? validPhrasePrefixes : new String[] {};
		this.splitString = StringUtils.isBlank(splitString) ? DEFAULT_SPLIT_STRING : splitString;
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.typeahead.IControlTextProvider#getTextToComplette()
	 */
	public final String getTextToComplette() {
		final String phraseToComplette = getPhraseToComplette();		
		if ( canComplettePhrase(phraseToComplette)) {
			if ( logger.isDebugEnabled() ) {
				logger.debug("Phrase to complette " + phraseToComplette);
			}			
			return phraseToComplette;			
		}	
			
		if ( logger.isDebugEnabled() ) {
			logger.debug("Don't synchornize filter" );
		}
		return null;
	}

	/**
	 * @return last p
	 */
	protected String getPhraseToComplette() {
		String lastNotEmptyWord = "";
		final String typedText = this.text.getText();
		final String[] typedWords = typedText.split( this.splitString );
		for( String word : typedWords ) {
			if ( word.length() > 0 ) {
				lastNotEmptyWord = word;
			}			
		}
		return lastNotEmptyWord;
	}

	/**
	 * @param phraseToComplette
	 * @return
	 */
	private boolean canComplettePhrase(final String phraseToComplette) {
		if ( phraseToComplette == null ) {
			return false;
		}
		if (this.validPhrasePrefixes.length == 0) {
			return true;
		}
		for (String key : this.validPhrasePrefixes) {
			if (phraseToComplette.startsWith(key)) {
				return true;
			}
		}
		return false;
	}
}
