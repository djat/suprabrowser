/**
 * 
 */
package ss.client.ui.forward;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class NotifyTrayDialog extends Dialog {

	private Table memberTable;
	
	private Document doc;
	
	private final Collection<String> members = new ArrayList<String>();

	public NotifyTrayDialog(final Collection<String> members, final Document doc) {
		super(SupraSphereFrame.INSTANCE.getShell());
		this.doc = doc;
		this.members.addAll(members);
	}

	@Override
	protected Control createContents(final Composite parent) {
		parent.setLayout(new GridLayout());

		this.memberTable = new Table(parent, SWT.BORDER | SWT.CHECK);
		this.memberTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (String member : this.members) {
			TableItem item = new TableItem(this.memberTable, SWT.NONE);
			item.setText(member);
		}
		
		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(2, false));
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Button okButton = new Button(buttonComp, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new DoneNotifyTrayListener(this));
		
		Button cancelButton = new Button(buttonComp, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		return parent;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 200);
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE;
	}

	/**
	 * @return
	 */
	public Document getDoc() {
		return this.doc;
	}

	/**
	 * @return
	 */
	public List<String> getSelection() {
		List<String> selection = new ArrayList<String>();
		for(TableItem item : this.memberTable.getItems()) {
			if(!item.getChecked()) {
				continue;
			}
			selection.add(item.getText());
		}
		return selection;
	}
}
