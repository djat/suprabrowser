package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SphereLocation extends XmlEntityObject {
		
		public static final String ITEM_ROOT_ELEMENT_NAME = "sphere";
	
		private final ISimpleEntityProperty url = super
							.createAttributeProperty( "@URL");
		
		private final ISimpleEntityProperty exDisplay = super
							.createAttributeProperty( "@ex_display");	
		
		private final ISimpleEntityProperty exSystem = super
							.createAttributeProperty( "@ex_system");	
		
		private final ISimpleEntityProperty exMessage = super
							.createAttributeProperty( "@ex_message");	
		
		
		/**
		 * 
		 */
		public SphereLocation() {
			super( ITEM_ROOT_ELEMENT_NAME );
		}
	
		public String getDisplay() {
			return this.exDisplay.getValue();
		}

		
		public void setDisplay(String value) {
			this.exDisplay.setValue( value );
		}
		
		public String getUrl() {
			return this.url.getValue();
		}

		
		public void setUrl(String value) {
			this.url.setValue( value );
		}
		
		public String getSystem() {
			return this.exSystem.getValue();
		}

		
		public void setSystem(String value) {
			this.exSystem.setValue( value );
		}
		
		public String getMessage() {
			return this.exMessage.getValue();
		}

		
		public void setMessage(String value) {
			this.exMessage.setValue( value );
		}

	

}
