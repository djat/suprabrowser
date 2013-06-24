/**
 * 
 */
package ss.common.file.vcf;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import net.wimpi.pim.Pim;
import net.wimpi.pim.contact.basicimpl.SimpleExtension;
import net.wimpi.pim.contact.io.ContactUnmarshaller;
import net.wimpi.pim.contact.io.vcard.GenericExtensionItemHandler;
import net.wimpi.pim.contact.io.vcard.ItemHandlerManager;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.PersonalIdentity;
import net.wimpi.pim.factory.ContactIOFactory;
import net.wimpi.pim.util.versitio.versitException;

import org.dom4j.Document;

import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.common.file.AbstractSpecificFileData;
import ss.common.file.AbstractSpecificFileProcessing;
import ss.common.file.DefaultDataForSpecificFileProcessingProvider;
import ss.common.file.ISpecificFileData;
import ss.common.file.ParentStatementData;
import ss.common.file.ReturnData;
import ss.common.file.SpecificFileProcessor;
import ss.domainmodel.ContactStatement;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 * 
 */
public class VCardOperator extends AbstractSpecificFileProcessing {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VCardOperator.class);

	public static final VCardOperator INSTANCE = new VCardOperator();

	private final ContactIOFactory ciof;

	private final ContactUnmarshaller unmarshaller;

	private final VCardComunicationsMerger comunicationsMerger = VCardComunicationsMerger.INSTANCE;

	private final VCardOrganizationMerger organizationMerger = VCardOrganizationMerger.INSTANCE;

	private final VCardPersonalMerger personalMerger = VCardPersonalMerger.INSTANCE;
	
	private final VCardAddressMerger addressMerger = VCardAddressMerger.INSTANCE;
	
	private final VCardPublisher publisher = VCardPublisher.INSTANCE;

	private VCardOperator() {
		final String encoding = "ISO-8859-1";//"UTF-8";

		this.ciof = Pim.getContactIOFactory();
		this.unmarshaller = this.ciof.createContactUnmarshaller();
		this.unmarshaller.setEncoding(encoding);

		// Add handler for the simple extension kids
		SimpleExtension ext = new SimpleExtension("X-KIDS");
		// add the handler, so the marshalling will work
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

	/* (non-Javadoc)
	 * @see ss.common.file.AbstractSpecificFileProcessing#process(ss.common.file.ISpecificFileData)
	 */
	@Override
	protected ReturnData process(final ISpecificFileData dat) {
		final VCardOperatorData data = VCardOperatorData.class.cast( dat );
		try {
			if (StringUtils.isBlank(data.getSystemFileName())) {
				logger.error("systemFileName is null");
				return null;
			}

			final FileInputStream inStream = new FileInputStream(data.getSystemFileName());
			final TreeSet<Contact> contacts = cleanUpContacts( this.unmarshaller.unmarshallContacts(inStream) );
			if ( contacts.isEmpty() ) {
				return null;
			}
			
			ReturnData returnData = null;
			for ( Contact c : contacts ) {
				if ( returnData != null ) {
					returnData.addData( processSingleContact( c, data ) );
				} else {
					returnData = processSingleContact( c, data );
				}
			}
			
			inStream.close();

			return returnData;
		} catch (Exception ex) {
			logger.error("Error processing file: " + data.getSystemFileName()
					+ " in vcf converter");
			return null;
		}
	}
	
	private TreeSet<Contact> cleanUpContacts( final Contact[] unmarshallContacts ) {
		if ( unmarshallContacts == null ) {
			return null;
		}
		final TreeSet<Contact> set = new TreeSet<Contact>(new Comparator<Contact>(){

			public int compare(Contact contact1, Contact contact2) {
				String name1 = getName( contact1.getPersonalIdentity() );
				String name2 = getName( contact2.getPersonalIdentity() );
				if ( StringUtils.isBlank(name1) || StringUtils.isBlank(name2) ) {
					return 0;
				}
				return name1.compareTo( name2 );
			}
			
			private String getName( final PersonalIdentity personal ){
				if ( personal == null ) {
					return null;
				}
				StringBuilder sb = new StringBuilder();
				sb.append( StringUtils.getNotNullString(personal.getFirstname()) );
				final String lastName = StringUtils.getNotNullString(personal.getLastname());
				if ( sb.length() > 0 &&
					 lastName != null &&
					 lastName.length() > 0) {
					sb.append( " " );			 
				}
				sb.append( lastName );
				return sb.toString();
			}
			
		});
		for ( Contact c : unmarshallContacts ) {
			set.add( c );
		}
		return set;
	}

	private final ReturnData processSingleContact( final Contact contact, final VCardOperatorData data ){
		try {

			final ContactStatement statement = createStatement(contact, data.getGiver());
			if (StringUtils.isBlank(statement.getContactNameByFirstAndLastNames())) {
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Contact statement: " + statement.getBindedDocument().asXML());
			}
			
			final List<String> spheresIdToPublish = new ArrayList<String>();
			final List<String> spheresIdNotToPublishEmail = new ArrayList<String>();
			final List<String> spheresNotToIndex = new ArrayList<String>();
			for (String sphereId : data.getSphereIds()) {
				if (logger.isDebugEnabled()) {
					logger.debug(" sphereId : " + sphereId);
				}
				Document existedDoc = data.getPeer().getXmldb().getContactExists(statement, sphereId);
				if (existedDoc == null) {
					if (logger.isDebugEnabled()) {
						logger.debug(" existedDoc is null, publishing ");
					}
					spheresIdToPublish.add( sphereId );
					spheresNotToIndex.add( sphereId );
					if ( data.getNote().isNotEmpty() ) {
						spheresIdNotToPublishEmail.add( sphereId );
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(" existedDoc is not null ");
					}
					if ( data.getNote().isNotEmpty() ) {
						spheresIdNotToPublishEmail.add( sphereId );
						this.publisher.publishNoteForExistingContact( data.getNote(), data.getGiver(), existedDoc, data.getPeer() );
					}
					if ( StringUtils.isNotBlank( data.getNote().getNewType() ) ) {
						if ( !spheresIdNotToPublishEmail.contains( sphereId ) ) {
							spheresIdNotToPublishEmail.add( sphereId );
						}
						this.publisher.changeTypeForContact(existedDoc, sphereId, data.getNote().getNewType(), data.getPeer().getXmldb());
					}
				}
			}
			
			this.publisher.publishAllContacts( statement, spheresIdToPublish, data.getNote(), data.getPeer().getXmldb() );
			return new ReturnData(spheresIdNotToPublishEmail, spheresNotToIndex);
		} catch (Exception ex) {
			logger.error("Error processing file: " + data.getSystemFileName()
					+ " in vcf converter");
			return null;
		}	
	}

	private ContactStatement createStatement(final Contact contact, final String giver) {
		ContactStatement st = new ContactStatement();

		addSystemInformation(st, giver);
		st.setBody((contact.getNote() == null) ? "" : contact.getNote());

		this.personalMerger.addPersonalToContactStatement(st, contact
				.getPersonalIdentity());
		
		this.addressMerger.addAddressToContactStatement(st, contact);

		this.organizationMerger.addOrganizationToContactStatement(st, contact
				.getOrganizationalIdentity());

		this.comunicationsMerger.addComunicationsToContactStatement(st, contact
				.getCommunications());

		return st;
	}

	/**
	 * @param giver 
	 * @param st
	 * @param communications
	 */
	private void addSystemInformation( final ContactStatement contact, final String giver ) {

		String moment = DialogsMainPeer.getCurrentMoment();

		contact.setType("contact");
		contact.setThreadType("contact");
		
		contact.setGiver( (giver == null) ? "" : giver );
		
		contact.setLogin("");

		contact.setMoment(moment);
		
		addVotingTemplate( contact );
	}

	/**
	 * @param contact
	 */
	private void addVotingTemplate( final ContactStatement contact ) {
		contact.setTallyNumber( "0.0" );
		contact.setTallyValue( "0.0" );
		contact.setVotingModelType( "absolute" );
		contact.setVotingModelDesc( "Absolute without qualification" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.smtp.reciever.file.ISpecificFileProcessing#getExtention()
	 */
	public String getExtention() {
		return "vcf";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.smtp.reciever.file.ISpecificFileProcessing#getFileTypeDescription()
	 */
	public String getFileTypeDescription() {
		return "vCard form converter to Contact Document";
	}

	/* (non-Javadoc)
	 * @see ss.common.file.AbstractSpecificFileProcessing#getFileDataClass()
	 */
	@Override
	protected Class<? extends AbstractSpecificFileData> getFileDataClass() {
		return VCardOperatorData.class;
	}

	public static void test( final String sphereId, final DialogsMainPeer peer, final String real_name){
		final List<String> sphereIds = new ArrayList<String>();
		sphereIds.add( sphereId );
		
		final String subject1 = "Hello to all #note: very important persons #type: Investor";
		final String body1 = "go go go persons!";
		
		//final String defaultVcfFileName = "vCard.vcf";
		//final String gunter = "Günther Reibling.vcf";
		final String uta = "Uta Ackermans-Meynen.vcf";
		final String filename = uta;
			
		final DefaultDataForSpecificFileProcessingProvider dataProvider = new DefaultDataForSpecificFileProcessingProvider(
				"/home/zobo/income/supraSphere/9/" + filename, filename, real_name, peer, 
				sphereIds, new ParentStatementData( body1, subject1 ));
		try {
			logger.error( ListUtils.allValuesToString( SpecificFileProcessor.INSTANCE.process( dataProvider ).getSpheresNotToIndex() ) );
		} catch (ClassNotFoundException ex1) {
			logger.error( "TODO error message",ex1);
		}
	}
}
