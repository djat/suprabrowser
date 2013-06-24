/**
 * 
 */
package ss.smtp.custom.clubdealinsubject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.domainmodel.Statement;
import ss.domainmodel.clubdeals.ClubDeal;
import ss.server.db.XMLDB;
import ss.smtp.custom.ICustomStrategy;
import ss.smtp.reciever.MailData;
import ss.smtp.reciever.RecieveList;
import ss.smtp.reciever.Reciever;

/**
 * @author zobo
 *
 */
public class ClubDealInEmailSubjectStrategy implements ICustomStrategy {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClubDealInEmailSubjectStrategy.class);
	
	public static final ClubDealInEmailSubjectStrategy INSTANCE = new ClubDealInEmailSubjectStrategy();
	
	private ClubDealInEmailSubjectStrategy(){
		
	}
	
	public void applyStrategy( final MailData data, final XMLDB xmldb  ){
		final Statement st = Statement.wrap( data.getBody() );
		final SplittedSubject subject = new SplittedSubject( st.getSubject() );
		if ( !subject.isSucceded() ) {
			return;
		}
		final String sysname = getClubdealSystemName( subject.getClubdealName(), getAllClubdeals( xmldb ) );
		if (StringUtils.isBlank( sysname )) {
			return;
		}
		String emailAdress = "";
		if (!data.getRecieveList().getRecievers().isEmpty()){
			emailAdress = data.getRecieveList().getRecievers().get(0).getEmailAdress();
		}
		Reciever reciever = new Reciever( sysname, null, emailAdress);
		
		st.setSubject( subject.getNewSubject() );
		RecieveList list = new RecieveList();
		list.addReciever(reciever);
		data.setRecieveList( list  );
	}
	
	private String getClubdealSystemName( final int number, final List<ClubDeal> deals ){
		String numberString = "" + number;
		for ( ClubDeal deal : deals ) {
			if ( deal.getName().startsWith( numberString ) ){
				return deal.getSystemName();
			}
		}
		return null;
	}
	
	private List<ClubDeal> getAllClubdeals( final XMLDB xmldb ){
		List<ClubDeal> deals = new ArrayList<ClubDeal>();
		final Vector<Document> allSphereDocs = xmldb.getAllSpheres();
		if ( allSphereDocs == null ) {
			return deals;
		}
		for(Document doc : allSphereDocs) {
			ClubDeal sphere = ClubDeal.wrap(doc);
			if( sphere.isClubDeal()) {
				deals.add( sphere );
			}
		}		
		return deals;
	}
}
