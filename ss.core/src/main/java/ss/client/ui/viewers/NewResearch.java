/*
 * Created on May 31, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui.viewers;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.ImagesPaths;


public class NewResearch {
	
	
	final Display display = new Display(); 
	Shell shell= null;
	SupraSphereFrame sF = null;
	MessagesPane mP = null;
	Image im = null;
	private Logger logger = SSLogger.getLogger(this.getClass());
	
	private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWRESEARCH);
	
	private static final String SYMBOL = "NEWRESEARCH.SYMBOL";
	private static final String PRICE = "NEWRESEARCH.PRICE";
	private static final String SECTOR = "NEWRESEARCH.SECTOR";
	private static final String RETAIL = "NEWRESEARCH.RETAIL";
	private static final String IT = "NEWRESEARCH.IT";
	private static final String BIOTECH = "NEWRESEARCH.BIOTECH";
	private static final String PHARMA = "NEWRESEARCH.PHARMA";
	private static final String MANUFACTURING = "NEWRESEARCH.MANUFACTURING";
	private static final String COMMODITY = "NEWRESEARCH.COMMODITY";
	private static final String CONTACT = "NEWRESEARCH.CONTACT";
	private static final String CLOSE = "NEWRESEARCH.CLOSE";
	private static final String NOTE = "NEWRESEARCH.NOTE";
							
	
	public NewResearch(SupraSphereFrame sF, MessagesPane mP) {
		
		this.sF = sF;
		this.mP = mP;
		//OS.OleInitialize(0);
				
	}
	public NewResearch() {
		
		
		
	}
	
	public static void main(String[] args) {
		
		NewResearch nr = new NewResearch();
		nr.layoutGUI();
		nr.runEventLoop();
		
	}
	public void forceActive() {

		//synchronized (mp.popups) {
			this.logger.info("Closing from withing");

			Thread t =
				new Thread() {
					public void run() {

						NewResearch.this.shell.forceActive();
						NewResearch.this.shell.forceFocus();

					}
				};
				UiUtils.swtBeginInvoke(t);
		//}

	}
	
	public void layoutGUI() {
		
				
		
		this.shell = new Shell(this.display, SWT.TITLE|SWT.RESIZE|SWT.BORDER);
		GridLayout gl = new GridLayout();
		gl.numColumns =  2;
		
		
		RowLayout rowLayout = new RowLayout();

		   rowLayout.wrap = true;

		   rowLayout.pack = true;

		   rowLayout.justify = true;

		   rowLayout.type = SWT.VERTICAL;
		this.shell.setLayout(gl);
		
		//GridData shellData = new GridData();
		//shellData.grabExcessHorizontalSpace = true;
		
		//shell.setLayoutData(shellData);
		
		Composite leftComp = new Composite(this.shell,SWT.NONE);
		
		
		GridData leftData = new GridData();
		leftData.grabExcessVerticalSpace = true;
		leftData.grabExcessHorizontalSpace = false;
		leftData.horizontalAlignment = GridData.BEGINNING;
		leftData.verticalAlignment = GridData.BEGINNING;
		leftComp.setLayoutData(leftData);
		
		
		GridLayout lC = new GridLayout();
		
		
		
		lC.numColumns =  2;
		
		leftComp.setLayout(lC);
		

		GridData lD = new GridData();
		lD.grabExcessHorizontalSpace = false;
		lD.horizontalAlignment = GridData.BEGINNING;
		lD.verticalAlignment = GridData.VERTICAL_ALIGN_END;

		Label label = new Label(leftComp,SWT.NONE);
		
		label.setLayoutData(lD);
		label.setText(this.bundle.getString(SYMBOL));
		


		StyledText txt = new StyledText(leftComp,SWT.BORDER);
		
		
		
		GridData tD = new GridData();
		//tD.grabExcessHorizontalSpace = false;
		//tD.horizontalAlignment = GridData.BEGINNING;
		//tD.verticalAlignment = GridData.VERTICAL_ALIGN_END;
		//tD.heightHint = 200;
		
		tD.widthHint = 100;
		txt.setLayoutData(tD);
		//txt.setSize(100,100);
		
		
		Label priceLabel = new Label(leftComp,SWT.NONE);
		
		
		priceLabel.setText(this.bundle.getString(PRICE));
		
		GridData pD = new GridData();
		
		
		//pD.verticalAlignment = 0;
		//pD.verticalSpan = 4;
		//pD.horizontalAlignment = 0;
		
		//pD.grabExcessHorizontalSpace = false;
		//pD.horizontalAlignment = GridData.BEGINNING;
		//pD.verticalAlignment = GridData.VERTICAL_ALIGN_END;
		
		
		
		StyledText price = new StyledText(leftComp,SWT.BORDER);
		
		price.setLayoutData(pD);
		pD.widthHint = 100;
		
		Label sector = new Label(leftComp,SWT.NONE);
		sector.setText(this.bundle.getString(SECTOR));
		
		final Combo combo = new Combo(leftComp,SWT.DROP_DOWN);
		
		GridData cD = new GridData();
		cD.horizontalAlignment = GridData.BEGINNING;
		//cD.grabExcessHorizontalSpace = true;
		//cD.horizontalAlignment = GridData.FILL;
		cD.widthHint = 100;
		combo.setLayoutData(cD);
		
		combo.add(this.bundle.getString(RETAIL),0);
		combo.add(this.bundle.getString(IT),1);
		combo.add(this.bundle.getString(BIOTECH),2);
		combo.add(this.bundle.getString(PHARMA),3);
		combo.add(this.bundle.getString(MANUFACTURING),4);
		combo.add(this.bundle.getString(COMMODITY),5);
		
		
		
		GridData conD = new GridData();
		Label contact = new Label(leftComp,SWT.NONE);
		
		contact.setText(this.bundle.getString(CONTACT));
		
		contact.setLayoutData(conD);
		//cD.grabExcessHorizontalSpace = true;
		//cD.horizontalAlignment = GridData.FILL;
		
		StyledText st = new StyledText(leftComp,SWT.BORDER);
		
		GridData stD = new GridData();
		stD.widthHint = 100;
		
		st.setLayoutData(stD);
		
		Button close = new Button(leftComp,SWT.PUSH);
		close.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				
				
				closeFromWithin();
				
			}
		});
		
		
		close.setText(this.bundle.getString(CLOSE));
		
		GridData cData = new GridData();
		
		cData.verticalAlignment = GridData.CENTER;
		cData.grabExcessVerticalSpace = true;
		close.setLayoutData(cData);
		
		
		
		Composite rightComp = new Composite(this.shell,SWT.NONE);
		
		GridData rightData = new GridData();
		rightData.grabExcessHorizontalSpace = true;
		rightData.grabExcessVerticalSpace = true;
		rightData.verticalAlignment = GridData.FILL;
		rightData.horizontalAlignment = GridData.FILL;
		
		rightComp.setLayoutData(rightData);
		
		GridLayout rC = new GridLayout();
		rC.numColumns = 2;
		GridData rCD = new GridData();
		rCD.grabExcessHorizontalSpace = true;
		rCD.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		
		rCD.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		
		rightComp.setLayout(rC);
		//rightComp.setLayoutData(rCD);
		
		
		Label note = new Label(rightComp,SWT.NONE);
		
		note.setText(this.bundle.getString(NOTE));
		GridData nD = new GridData();
		nD.verticalAlignment = GridData.BEGINNING;
		note.setLayoutData(nD);
		
		
		
		StyledText noteText = new StyledText(rightComp,SWT.BORDER|SWT.WRAP|SWT.V_SCROLL);
		
		
		GridData nLD = new GridData();
		
		nLD.grabExcessHorizontalSpace = true;
		
		
		nLD.horizontalAlignment = GridData.FILL;
		
		
		nLD.grabExcessVerticalSpace = true;
		nLD.verticalAlignment = GridData.FILL;
		
		noteText.setLayoutData(nLD);
		
		

		
//		String fsep = System.getProperty("file.separator");
//		String bdir = System.getProperty("user.dir");

		try {
			this.im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			this.shell.setImage(this.im);
		} catch (IOException ex) {
			this.logger.error("can't create supra icon", ex);
		}
				
		
		
		this.shell.setSize(700,400);
		
		
		
		
	}
	public void closeFromWithin() {

		//synchronized (mp.popups) {
			this.logger.info("Closing from withing");

			Thread t =
				new Thread() {
					public void run() {

						NewResearch.this.im.dispose();
						NewResearch.this.shell.dispose();

					}
				};
				UiUtils.swtBeginInvoke(t);
		//}

	}
	public void runEventLoop() {

		this.shell.layout();
		this.shell.open();
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}

		
	}
	
}