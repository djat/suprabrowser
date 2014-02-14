/**
 * 
 */
package ss.client.ui.email;

import org.eclipse.swt.widgets.Composite;

import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.models.autocomplete.ResultListener;

/**
 * @author zobo
 *
 */
public class EmailSimpleSetShellCompositeUnit extends EmailSimpleShellCompositeUnit{

    public EmailSimpleSetShellCompositeUnit(Composite parent, int style, String labelText, String defaultText, boolean enabled) {
        super(parent, style, labelText, defaultText, enabled);
    }

    @Override
    protected ResultListener<String> getResultListener() {
        return new ResultAdapter<String>() {
            @Override
            public void processListSelection(String selection, String realData) {
                EmailSimpleSetShellCompositeUnit.this.text.setText(realData);
            }
        };
    }
}
