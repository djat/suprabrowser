/**
 * 
 */
package ss.common.file.vcf;

import java.io.ByteArrayOutputStream;
import java.util.List;

import net.wimpi.pim.Pim;
import net.wimpi.pim.contact.basicimpl.ContactImpl;
import net.wimpi.pim.contact.basicimpl.PersonalIdentityImpl;
import net.wimpi.pim.contact.basicimpl.SimpleExtension;
import net.wimpi.pim.contact.io.ContactMarshaller;
import net.wimpi.pim.contact.io.vcard.GenericExtensionItemHandler;
import net.wimpi.pim.contact.io.vcard.ItemHandlerManager;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.PersonalIdentity;
import net.wimpi.pim.factory.ContactIOFactory;
import net.wimpi.pim.util.versitio.versitException;
import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class VCardCreater {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardCreater.class);
	
	public static final VCardCreater INSTANCE = new VCardCreater();
	
	private final ContactIOFactory ciof;

	private final ContactMarshaller marshaller;
	
	private VCardCreater(){
		final String encoding = "ISO-8859-1";//"UTF-8";

		this.ciof = Pim.getContactIOFactory();
		this.marshaller = this.ciof.createContactMarshaller();
		this.marshaller.setEncoding(encoding);

		SimpleExtension ext = new SimpleExtension("X-KIDS");
		try {
			ItemHandlerManager.getReference().addExtensionHandler(
					ext.getIdentifier(), new GenericExtensionItemHandler(ext));
		} catch (versitException ex) {
			logger.error("Error initializing vcf converter", ex);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("VCardOperator initialized");
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public AttachedFile create( final ContactStatement contactSt ){
		if ( contactSt == null ) {
			logger.error("Contact statement is null");
			return null;
		}
		final String contactName = contactSt.getContactNameByFirstAndLastNames();
		if ( StringUtils.isBlank(contactName) ) {
			logger.error("Contact name is blank");
			return null;
		}
		final String fileName = contactName.trim() + ".vcf";
		final Contact contact = createContact( contactSt );
		if ( contact == null ) {
			logger.error("Can not create contact");
			return null;
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.marshaller.marshallContact(out, contact);
		return new AttachedFile( fileName , out.toByteArray() );
	}

	/**
	 * @param contactSt
	 * @return
	 */
	private Contact createContact( ContactStatement contactSt ) {
		final Contact contact = new ContactImpl();
		
		VCardPersonalMerger.INSTANCE.collectPersonalFromContactStatement(contactSt, contact);
		VCardAddressMerger.INSTANCE.collectAddressFromContactStatement(contactSt, contact);
		VCardOrganizationMerger.INSTANCE.collectOrganizationFromContactStatement(contactSt , contact);
		VCardComunicationsMerger.INSTANCE.collectComunicationsFromContactStatement(contactSt, contact);
		
		return contact;
	}

	public static void test( final ContactStatement st ){
		String messageId = VariousUtils.createMessageId();
		EmailAddressesContainer addressesContainer = new EmailAddressesContainer("myfriend@gmail.com", "me@localhost");		
		AttachedFileCollection files = new AttachedFileCollection();
		files.add( INSTANCE.create(st) );
		String sphereId = st.getCurrentSphere();
		EmailBody emailBody = new EmailBody("Email title", "And its body");
		List<SendingElement> elements = SendingElementFactory.createCreated(addressesContainer, files, emailBody, messageId , sphereId, null);
		if ( (elements != null) && (!elements.isEmpty()) ) {
			for ( SendingElement element : elements ) {
				Mailer.INSTANCE.send( element );
			}
		} else {
			logger.error("Sending elements list is empty");
		}
	}
}
