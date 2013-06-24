package ss.client.ui;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

public class DateDialog extends BaseDialog {

	private SWTCalendar swtcal;

	private SWTCalendarListener listener;

	private Date start;

	public DateDialog(SWTCalendarListener listener, Date start) {
		this.listener = listener;
		this.start = start;
	}

	@Override
	protected void initializeControls() {
		super.initializeControls();

		getShell().setLayout(new RowLayout());

		this.swtcal = new SWTCalendar(getShell());		
		this.swtcal.addSWTCalendarListener(this.listener);
		if (this.start != null) {
			setDate(this.start);
		}
	}

	@Override
	protected int getStartUpDialogStyle() {
		return SWT.APPLICATION_MODAL | SWT.CLOSE;
	}

	public Calendar getCalendar() {
		return this.swtcal.getCalendar();
	}

	@Override
	protected void layoutDialog() {
		getShell().pack();
		super.layoutDialog();
	}

	private void setDate(Date date) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		this.swtcal.setCalendar(calendar);

	}

}
