package ss.client.ui.typeahead;

import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.models.autocomplete.DataListener;
import ss.client.ui.models.autocomplete.DataModel;
import ss.client.ui.models.autocomplete.Proposal;
import ss.client.ui.models.autocomplete.ProposalCollection;
import ss.client.ui.models.autocomplete.ResultListener;
import ss.common.ArgumentNullPointerException;
import ss.common.IdentityUtils;
import ss.common.UiUtils;

/**
 * This class is purposed for adding type-ahead auto-complete functionality to
 * SWT Text widget.<br>
 * This is an example of using its <code>
 //create new text widget
 textField = new Text(getShell(), SWT.LEFT | SWT.SINGLE); 
 //create new typeAhead component    
 TypeAheadComponentNew typeAhead = new TypeAheadComponentNew( 
 textField, model, new TypeAheadComponentNew.ResultListener() {

 public void processResult(String string) {
 //insert here code that process result of autocompletition 

 }});

 </code> If you want to remove type-ahead auto-complete functionality from
 * text field use <code>dispose();</code> This component is implemented as
 * PopupDialog. When user uses Up or Down arrow keys dialog shows up.
 * 
 * @author dankosedin
 */

final class TypeAheadComponentNew<T> implements DataListener {

	private static final String AUTO_COMPLETE = "AUTO.COMPLETE";

	private static final String NO_ITEMS_MATCHED = "NO.ITEMS.MATCHED";

	private static final String RETRIEVING_ITEMLIST = "RETRIEVING.ITEMLIST";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_TYPEAHEAD_TYPEAHEADCOMPONENT);

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TypeAheadComponentNew.class);

	private final ProposalCollection<T> EMPTY_PROPSALS = new ProposalCollection<T>();

	private class Popup extends PopupDialog {

		class ParentShellListener extends ShellAdapter {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ShellAdapter#shellClosed(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellClosed(ShellEvent e) {
				close();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ShellAdapter#shellDeactivated(org.eclipse.swt.events.ShellEvent)
			 */
			@Override
			public void shellDeactivated(ShellEvent shellevent) {
				if (Display.getDefault().getActiveShell() != Popup.this
						.getShell()) {
					// close();
				}
			}

		};

		class ParentShellControlListener extends ControlAdapter {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
			 */
			public void controlMoved(ControlEvent e) {
				close();
			}
		};

		class ListKeyListener extends KeyAdapter {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					TypeAheadComponentNew.this.complete();
				}
				if (e.character == SWT.ESC) {
					close();
					checkEcsRule();
				}
			}
		};
		
		class ListMouseListener extends MouseAdapter {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				super.mouseDoubleClick(e);
				TypeAheadComponentNew.this.complete();
			}
		}

		private final Control control;

		private final Shell parentShell;

		private final ShellListener parentShellListener = new ParentShellListener();

		private final ControlListener parentShellControlListener = new ParentShellControlListener();

		private List list;

		/**
		 * @param control
		 */
		public Popup(Control control, String infoText) {
			super(control.getShell(), SWT.RESIZE | SWT.ON_TOP, false, false,
					false, false, null, infoText);
			this.control = control;
			this.control.addFocusListener(new FocusAdapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
				 */
				@Override
				public void focusLost(FocusEvent e) {
					super.focusLost(e);
					// if (Display.getDefault().getActiveShell() != Popup.this
					// .getShell()) {
					// close();
					// }
				}

			});
			this.control.addDisposeListener(new DisposeListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
				 */
				public void widgetDisposed(DisposeEvent arg0) {
					close();
				}
			});
			this.parentShell = control.getShell();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createDialogArea(Composite parent) {
			this.list = new List(parent, SWT.CHECK | SWT.V_SCROLL
					| SWT.H_SCROLL | SWT.SINGLE);
			this.list.setSize(250, 300);
			this.list.addKeyListener( new ListKeyListener() );
			this.list.addMouseListener( new ListMouseListener() );
			this.list.setLayoutData(createListGridData());
			final AbstractProposalState state = getProposalState();
			state.activate();
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			parent.setLayout(gridLayout);
			return this.list;
		}

		private GridData createListGridData() {
			GridData gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			return gridData;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#configureShell(org.eclipse.swt.widgets.Shell)
		 */
		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			final Rectangle targetControlBounds = this.control.getBounds();
			final Point targetControlScreenPosition = getPosition(this.control);
			final int x = targetControlScreenPosition.x + 5;
			final int y = targetControlBounds.height + 29
					+ targetControlScreenPosition.y;
			shell.setBounds(x, y, targetControlBounds.width, 350);
			shell.moveAbove(this.parentShell);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#open()
		 */
		@Override
		public int open() {
			logger.info("open typeahead popup");
			int value = super.open();
			this.parentShell.addShellListener(this.parentShellListener);
			this.parentShell
					.addControlListener(this.parentShellControlListener);
			TypeAheadComponentNew.this.afterPopupShowed();
			return value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#close()
		 */
		@Override
		public boolean close() {
			return super.close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
		 */
		@Override
		protected void handleShellCloseEvent() {
			super.handleShellCloseEvent();
			dropCashedData();
			checkEcsRule();
			if (!this.parentShell.isDisposed()) {
				this.parentShell
						.removeControlListener(this.parentShellControlListener);
				this.parentShell.removeShellListener(this.parentShellListener);
			}
		}

		/**
		 * 
		 */
		public void resetSelection() {
			if (this.list != null && this.list.getItemCount() > 0) {
				this.list.select(0);
			}
		}

		/**
		 * @return
		 */
		public int getSelectionIndex() {
			return this.list != null ? this.list.getSelectionIndex() : -1;
		}

		/**
		 * @return the list
		 */
		public List getList() {
			return !this.list.isDisposed() ? this.list : null;
		}

	};

	private class ControlKeyListener extends KeyAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.character == '\r') {
				complete();
			} else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP) {
				openPopup();
			} else if (e.character == SWT.ESC) {
				closePopup();
				checkEcsRule();
			} else if (e.keyCode == ' ' && e.stateMask == SWT.CTRL) {
				completteByDefault();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {			
		}
	};

	private Popup popup = null;

	private final DataModel<T> model;

	private final ResultListener<T> resultListener;

	private final Control control;

	private final ControlKeyListener controlKeyListener = new ControlKeyListener();

	private final AtomicReference<AbstractProposalState> proposalStateReference = new AtomicReference<AbstractProposalState>(
			new EmptyProposalState());

	private final IControlContentProvider controlContentProvider;

	private boolean isEscDropComponent;

	private boolean isCleanControl = true;

	/**
	 * @param control
	 *            control widget wich would be auto-complete able
	 * @param model
	 *            data model wich supports this component with auto-complete
	 *            values
	 * @param resultListener
	 *            callback for processing auto-complete results.
	 * @param controlContentProvider
	 *            provide control text that should be autocomplete.
	 */
	public TypeAheadComponentNew(final Control control, DataModel<T> model,
			ResultListener<T> resultListener,
			IControlContentProvider controlContentProvider,
			boolean isEscDropComponent, boolean isCleanControl) {
		if (model == null) {
			throw new ArgumentNullPointerException("model");
		}
		if (resultListener == null) {
			throw new ArgumentNullPointerException("resultListener");
		}
		if (controlContentProvider == null) {
			throw new ArgumentNullPointerException("controlContentProvider");
		}
		this.control = control;
		this.model = model;
		this.model.addDataListener(this);
		this.controlContentProvider = controlContentProvider;
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				TypeAheadComponentNew.this.control.addKeyListener(TypeAheadComponentNew.this.controlKeyListener);				
			}
		});
		this.resultListener = resultListener;
		this.isEscDropComponent = isEscDropComponent;
		this.isCleanControl = isCleanControl;
	}

	public final void openPopup() {
		if (!isValid() || this.popup != null) {
			return;
		}
		this.updateDataModelFilterByControl();
		this.popup = new Popup(this.control, this.bundle
				.getString(AUTO_COMPLETE));
		this.popup.open();
		this.popup.getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				TypeAheadComponentNew.this.popup = null;
			}
		});
	}

	public final void loadingData() {
		setProposalState(new LoadingProposalState());
	}

	public final void loadAndUpdateData() {
		if (isValid() && this.control.isFocusControl() /* && this.popup != null */) {
			updateDataModelFilterByControl();
		}
	}

	/**
	 * 
	 */
	private void updateDataModelFilterByControl() {
		final String textToComplette = this.controlContentProvider
				.getTextToComplette();
		if (textToComplette != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("update filter " + textToComplette);
			}
			this.model.setFilter(textToComplette);
			if (this.popup != null) {
				this.popup.resetSelection();
			}
		}
	}

	/**
	 * 
	 */
	private void complete() {
		final Proposal<T> proposal = closePopupAndGetProposal();
		if (proposal != null) {
			logger.info("calling " + this.resultListener + ", with model "
					+ proposal.getModel());
			this.resultListener.processListSelection(proposal.getDisplayText(),
					proposal.getModel());
		}
	}

	/**
	 * @return
	 */
	private Proposal<T> closePopupAndGetProposal() {
		if (this.popup == null) {
			logger.info("Popup is null, cannot complete.");
			return null;
		}
		final int selectionIndex = this.popup.getSelectionIndex();
		final AbstractProposalState state = getProposalState();
		if (isValid()) {
			this.control.setFocus();
		}
		closePopup();
		if (!state.containsIndex(selectionIndex)) {
			return null;
		}
		final Proposal<T> proposal = state.getProposal(selectionIndex);
		logger.info("Forse focut to text. Completting by proposal " + proposal);
		return proposal;
	}

	/**
	 * @param state2
	 */
	private synchronized void setProposalState(final AbstractProposalState state) {
		final AbstractProposalState currentState = getProposalState();
		if (state.canOverride(currentState)) {
			this.proposalStateReference.set(state);
			UiUtils.swtInvoke(new Runnable() {
				public void run() {
					state.activate();
				}
			});
		}

	}

	public final void newData() {
		final ProposalCollection<T> proposals = this.model.getProposals();
		logger.info("New proposals received " + proposals.getCount());
		if (proposals.getCount() > 0) {
			setProposalState(new ReadyProposalState(proposals));
		} else {
			setProposalState(new EmptyProposalState());
		}
	}

	class PopupLifeController implements Runnable {

		public void run() {
			logger.info("Popup life controller started.");
			try {
				AbstractProposalState previousState = null;
				while (!Thread.interrupted()
						&& TypeAheadComponentNew.this.popup != null) {
					Thread.sleep(100);
					final AbstractProposalState currentState = getProposalState();
					if (currentState.shouldHidePopup(previousState)) {
						logger.info("close popup by life time controller");
						uiSafeClosePopup(currentState);
						return;
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Popup should live " + currentState);
						}
					}
					previousState = currentState;
				}
			} catch (InterruptedException e) {
			} finally {
				logger.info("Popup life controller finished.");
			}
		}

		/**
		 * 
		 */
		private void uiSafeClosePopup(final AbstractProposalState state) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					closePopup();
					state.afterClose();
				}
			});
		}

	}

	/**
	 * 
	 */
	private void startNewPopupLifeController() {
		Thread thread = new Thread(new PopupLifeController());
		thread.setName(IdentityUtils
				.getNextRuntimeIdForThread(TypeAheadComponentNew.class));
		thread.start();
	}

	/**
	 * 
	 */
	public final void dispose() {
		this.model.removeDataListener(this);
		if (!this.control.isDisposed()) {
			this.control.removeKeyListener(this.controlKeyListener);
		}
		closePopup();
	}
	
	public KeyListener getControlKeyListener() {
		return this.controlKeyListener;
	}

	/**
	 * @return
	 */
	private AbstractProposalState getProposalState() {
		return this.proposalStateReference.get();
	}

	/**
	 * 
	 */
	private void afterPopupShowed() {
		getProposalState().resetCreationDate();
		startNewPopupLifeController();
	}

	private abstract class AbstractProposalState {

		private final long lifeTime;

		private final ProposalCollection<T> proposals;

		private Date creationDate;

		private List targetList = null;

		/**
		 * @param proposals
		 */
		public AbstractProposalState(final int lifeTime,
				final ProposalCollection<T> proposals) {
			super();
			this.lifeTime = lifeTime;
			this.proposals = proposals != null ? proposals
					: TypeAheadComponentNew.this.EMPTY_PROPSALS;
			resetCreationDate();
		}

		/**
		 * @param currentState
		 * @return
		 */
		public boolean canOverride(AbstractProposalState state) {
			return true;
		}

		/**
		 * 
		 */
		public final void resetCreationDate() {
			this.creationDate = new Date();
		}

		public final void activate() {
			final Popup popup = TypeAheadComponentNew.this.popup;
			this.targetList = popup != null ? popup.getList() : null;
			if (this.targetList != null) {
				afterActivated();
			}
		}

		/**
		 * 
		 */
		protected abstract void afterActivated();

		/**
		 * @param oldState
		 * @return
		 */
		public final boolean shouldHidePopup(AbstractProposalState previousState) {
			if (this.lifeTime < 0) {
				return false;
			}
			// if (previousState != null && previousState.equals(this)) {
			// return false;
			// }
			final Date now = new Date();
			return now.after(getHideDate());
		}

		/**
		 * @return
		 */
		private Date getHideDate() {
			return new Date(this.creationDate.getTime() + this.lifeTime);
		}

		/**
		 * @param index
		 * @return
		 */
		public Proposal<T> getProposal(int index) {
			return this.proposals.get(index);
		}

		/**
		 * @param selectionIndex
		 * @return
		 */
		public final boolean containsIndex(int selectionIndex) {
			return selectionIndex >= 0
					&& selectionIndex < this.proposals.getCount();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public final boolean equals(Object obj) {
			return obj != null && obj.getClass() == getClass();
		}

		/**
		 * @return the targetList
		 */
		public final List getTargetList() {
			return this.targetList;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ProposalState " + getClass().getName() + ", hide date "
					+ getHideDate();
		}

		/**
		 * @return
		 */
		public final int getProposalsCount() {
			return this.proposals.getCount();
		}

		/**
		 * @return
		 */
		protected ProposalCollection<T> getProposals() {
			return this.proposals;
		}

		public void afterClose() {

		}

	}

	private class LoadingProposalState extends AbstractProposalState {

		public LoadingProposalState() {
			super(-1, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.client.ui.widgets.TypeAheadComponentNew.StampedState#afterActivated()
		 */
		@Override
		protected void afterActivated() {
			final List list = getTargetList();
			list.removeAll();
			list.setItems(new String[] { TypeAheadComponentNew.this.bundle
					.getString(RETRIEVING_ITEMLIST) });
			list.setEnabled(false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.client.ui.typeahead.TypeAheadComponentNew.AbstractProposalState#canOverride(ss.client.ui.typeahead.TypeAheadComponentNew.AbstractProposalState)
		 */
		@Override
		public boolean canOverride(AbstractProposalState state) {
			if (state.getClass() == ReadyProposalState.class) {
				final ReadyProposalState readyState = (ReadyProposalState) state;
				return readyState.getProposals().getCount() == 0;
			}
			return super.canOverride(state);
		}

	}

	private class EmptyProposalState extends AbstractProposalState {

		public EmptyProposalState() {
			super(700, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.client.ui.widgets.TypeAheadComponentNew.StampedState#afterActivated()
		 */
		@Override
		protected void afterActivated() {
			final List list = getTargetList();
			list.setItems(new String[] { TypeAheadComponentNew.this.bundle
					.getString(NO_ITEMS_MATCHED) });
			list.setEnabled(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.client.ui.typeahead.TypeAheadComponentNew.AbstractProposalState#afterClose()
		 */
		@Override
		public void afterClose() {
			checkEcsRule();
		}

	}

	private class ReadyProposalState extends AbstractProposalState {

		public ReadyProposalState(ProposalCollection<T> proposals) {
			super(-1, proposals);
			logger.info("Received proposals " + proposals.getCount());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.client.ui.widgets.TypeAheadComponentNew.StampedState#afterActivated()
		 */
		@Override
		protected void afterActivated() {
			final List list = super.getTargetList();
			list.setItems(this.getProposals().toDisplayTexts());
			list.setEnabled(true);
			list.setFocus();
		}
	}

	/**
	 * 
	 */
	private void closePopup() {
		if (this.popup != null) {
			logger.debug("close popup");
			if (!this.popup.getShell().isDisposed()
					&& this.popup.getShell().isFocusControl()
					&& !this.control.isDisposed()) {
				this.control.setFocus();
			}
			this.popup.close();
		}
	}

	/**
	 * @return
	 */
	private boolean isValid() {
		return !this.control.isDisposed();
	}

	/**
	 * 
	 */
	private void completteByDefault() {
		final AbstractProposalState currentState = getProposalState();
		if (currentState.getProposalsCount() == 1) {
			final Proposal<T> proposal = currentState.getProposal(0);
			logger.info("complette by default " + proposal);
			this.resultListener.processResult(proposal.getDisplayText(),
					proposal.getModel());
		} else {
			openPopup();
		}

	}

	private static Point getPosition(Control component) {
		if (component != null) {
			final Point parentPoint = getPosition(component.getParent());
			Rectangle bounds = component.getBounds();
			parentPoint.x = parentPoint.x + bounds.x;
			parentPoint.y = parentPoint.y + bounds.y;
			return parentPoint;
		} else {
			return new Point(0, 0);
		}
	}

	private void dropCashedData() {
		this.model.dropCashedData();
		if (this.isCleanControl ){
			((Text) this.control).setText("");
		}
	}

	private void checkEcsRule() {
		if(TypeAheadComponentNew.this.isEscDropComponent)
		{
			TypeAheadComponentNew.this.resultListener.processEmptyResult();
		}
	}

}
