/**
 * 
 */
package ss.client.ui.email;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.models.autocomplete.ResultListener;
import ss.client.ui.typeahead.IControlContentProvider;
import ss.client.ui.typeahead.TextContentProvider;
import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class EmailSimpleAddShellCompositeUnit extends
        EmailSimpleShellCompositeUnit {
	
	private static final String SPLIT_STRING = ",";
	
	private class EmailTextContentProvider extends TextContentProvider {

		private static final char SPLIT_CHAR = ',';
		
		EmailTextContentProvider(Text text){
			super(text, SPLIT_STRING);
		}

		@Override
		protected String getPhraseToComplette() {
			String lastNotEmptyWord = "";
			final String typedText = this.text.getText();
			if (StringUtils.isBlank(typedText)){
				return lastNotEmptyWord;
			}
			if (typedText.lastIndexOf(SPLIT_CHAR) == (typedText.length()-1)) {
				return lastNotEmptyWord;
			}
			final String[] typedWords = typedText.split( this.splitString );
			for( String word : typedWords ) {
				lastNotEmptyWord = word;
			}
			return lastNotEmptyWord.trim();
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailSimpleAddShellCompositeUnit.class);

    public EmailSimpleAddShellCompositeUnit(Composite parent, int style, String labelText, String defaultText, boolean enabled) {
        super(parent, style, labelText, defaultText, enabled);
    }

    @Override
    protected ResultListener<String> getResultListener() {
        return new ResultAdapter<String>() {
            @Override
            public void processListSelection(String selection, String realData) {
				if (logger.isDebugEnabled()){
					logger.debug("processListSelection in Email Add Composite, selection is: " + selection + ", and realData: " + realData);
				}
                String str = EmailSimpleAddShellCompositeUnit.this.text.getText();
				if (logger.isDebugEnabled()){
					logger.debug("Text taken from Text field: " + str);
				}
                String toSet = realData;
                if (StringUtils.isNotBlank(str)){
    				if (logger.isDebugEnabled()){
    					logger.debug("Will try to block selection due to Text in field is not blank");
    				}
                    SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(str);
                    set.cleanUp();
                    if (!set.getParsedEmailNames().contains(SpherePossibleEmailsSet.parseSingleAddress(realData))){
                        set.addAddresses( realData );
        				if (logger.isDebugEnabled()){
        					logger.debug("RealData is not in field, adding");
        				}
                    } else {
        				if (logger.isDebugEnabled()){
        					logger.debug("RealData is in field, not adding");
        				}
                    }
                    toSet = set.getSingleStringEmails();
                } else {
    				if (logger.isDebugEnabled()){
    					logger.debug("No need to block selection, Text in field is blank");
    				}
                }
				if (logger.isDebugEnabled()){
					logger.debug("Returning string: " + toSet);
				}
				toSet += SPLIT_STRING;
                EmailSimpleAddShellCompositeUnit.this.text.setText(toSet);
                EmailSimpleAddShellCompositeUnit.this.text.setSelection(toSet.length()+1);
            }
        };
    }

	@Override
	protected IControlContentProvider getContentProvider(Text text) {
		return new EmailTextContentProvider(text);
	}
}
