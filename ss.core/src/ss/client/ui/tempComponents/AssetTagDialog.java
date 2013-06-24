/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.processing.TagActionProcessor;
import ss.client.ui.typeahead.TypeAheadManager;
import ss.common.StringUtils;
import ss.common.XmlDocumentUtils;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class AssetTagDialog extends Dialog {

	private static final String TAG_TEXT_TITLE = "Tag text: ";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AssetTagDialog.class);
	
	private final String sphereId;
	
	private final String messageId;
	
	private Text tagText;
	
	public AssetTagDialog( final String sphereId, final String messageId ) {
		super((Shell)null);
		this.sphereId = sphereId;
		this.messageId = messageId;
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		new Label(parent, SWT.LEFT).setText(TAG_TEXT_TITLE);
		
		this.tagText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		this.tagText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.tagText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.keyCode == 13 ) {
					performTagging();
				}
			}
		});
		
		final Button save = new Button(parent, SWT.PUSH);
		save.setText("Save");
		save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performTagging();
			}
		});
		TypeAheadManager.INSTANCE.addKeywordAutoComplete( this.tagText );
		
		return parent;
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Tag Asset Dialog");
	}

	private String getTagText() {
		return this.tagText.getText();
	}

	/**
	 * @return the sphereId
	 */
	private String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @return the messageId
	 */
	private String getMessageId() {
		return this.messageId;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(250, 100);
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE;
	}
	
	private void performTagging() {
		if (StringUtils.isBlank( getTagText() )) {
			logger.warn("Can not be blank keyword");
			return;
		}
		final Hashtable session = (Hashtable)SupraSphereFrame.INSTANCE.client.session.clone();
		session.put(SessionConstants.SPHERE_ID2, this.sphereId);
		final Document doc = SupraSphereFrame.INSTANCE.client.getSpecificId( session, getMessageId() );
		if (logger.isDebugEnabled()) {
			logger.debug("Specific Document recieved for tagging with keyword: " + getTagText() + " is: " + XmlDocumentUtils.toPrettyString(doc));
		}
		final TagActionProcessor processor = new TagActionProcessor( SupraSphereFrame.INSTANCE.client, getSphereId(), doc );
		processor.doTagAction( getTagText() );
		close();
	}
}
