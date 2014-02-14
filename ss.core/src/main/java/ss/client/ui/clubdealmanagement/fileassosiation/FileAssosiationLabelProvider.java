/**
 * 
 */
package ss.client.ui.clubdealmanagement.fileassosiation;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
class FileAssosiationLabelProvider implements ITableLabelProvider {

		@SuppressWarnings("unused")
		private static final Logger logger = SSLogger.getLogger(FileAssosiationLabelProvider.class);
	
		private final Image checked;
		
		private final Image unchecked;
		
		private final IDataHashProvider provider;

		public FileAssosiationLabelProvider( final IDataHashProvider provider ) {
			super();
			this.provider = provider;
			this.checked = new Image(null,ImagesPaths.openStream(ImagesPaths.CHECKED ) );
		    this.unchecked = new Image(null, ImagesPaths.openStream(ImagesPaths.UNCHECKED ) ); 
		}
		/**
		 * Returns the image
		 * 
		 * @param element
		 *            the element
		 * @param columnIndex
		 *            the column index
		 * @return Image
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			if ( columnIndex == 0 ) {
				Boolean bool = this.provider.getHash().get((ClubdealWithContactsObject) element);
				if (bool == null) {
					bool = new Boolean(false);
				}
				return bool.booleanValue() ? this.checked : this.unchecked;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object o, int index) {
			final ClubdealWithContactsObject clubdeal = (ClubdealWithContactsObject)o;
			if(index==0) {
				return null;
			} else if(index==1) {
				return (clubdeal.getClubdeal().getName() + " (" + clubdeal.getClubdealSystemName() + ")");
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener arg0) {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener arg0) {
			
		}
		
	}
