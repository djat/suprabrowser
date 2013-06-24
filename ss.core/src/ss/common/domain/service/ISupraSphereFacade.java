package ss.common.domain.service;

import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.SphereReferenceList;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;


public interface ISupraSphereFacade {

	/**
	 * @return
	 */
	SphereEmailCollection getSpheresEmails();
	
	SupraSphereMember getSupraMemberByLoginName(String userLogin);
	SupraSphereMember findMemberByLogin(String memberLogin);
	SupraSphereMember findMemberByContactName(String contactName);
		
	Iterable<SupraSphereMember> getSupraMembers();

	String getName();
	String getSystemName();
	
	@Refactoring(classify=SupraSphereRefactor.class, message = "Refactor users of this method" )
	Document getBindedDocumentForSaveToDb();

	ISupraSphereFacade duplicate();

	/**
	 * @return
	 */
	List<SupraSphereMember> getAllMembers();

	/**
	 * @param contactName
	 *
	 * @return
	 */
	String getPrivateForSomeoneElse(String contactName);
	
	
	String getP2PSphere(String login, String peerContactName);

	/**
	 * 		
	 * @param contactName
	 * @return
	 */
	String getLoginForContact(String contactName);

	/**
	 * @param checkSphereId
	 * @param loginName
	 * @param contactName
	 * 
	 */
	boolean isPersonal(String checkSphereId, String loginName, String contactName);

	/**
	 * @param loginName
	 * @return


		
	 * 
	 */
	String getPersonalSphereFromLogin(String loginName);

	/**
	 * 
	 * 
	 * @return
	 */
	SphereReferenceList getAllSpheres();

	/**
	 * 
	 * @param login
	 * @return
	 */
	SphereReferenceList getAllEnabledSpheresByLogin(String login);

	/**
	 * 
	 * @param contact
	 * @return
	 */
	SphereReferenceList getAllEnabledSpheresByContactName(String contact);

	/**
	 * 
	 * @param contactName
	 * @param loginName
	 * @return
	 */
	boolean isAdmin(String contactName, String loginName);
	
	boolean isPrimaryAdmin(String contactName, String loginName);

	/**
	 * 
	 * @param loginName
	 * @return
	 */
	String getContactNameByLogin(String loginName);

	/**
	 * @param firstContact
	 * @param secondContact
	 * @return
	 * 
	 */
	String getSharedSphereIdForContactPair(String firstContact,
			String secondContact);

	/**
	 * @param system_name
	 * @return

	 */
	String getSphereType(String system_name, String contactName);

	/**
	 * @param display_name
	 * @param contact_name		
	 */
	String getSystemName(String display_name, String contact_name);

	/**
	 * @param loginName
	 * @return
	 */
	String getLoginSphere(String loginName);

	/**
	 * @param displayName
	 * @param contactName
	 * @return
	 */
	boolean isSphereEnabledForContact(String displayName, String contactName);

	/**
	 * @param contactName 
	 * @return
	 * 
	 */
	Vector<String> getAvailableGroupSpheres(String contactName);

	/**
	 * @param contactName
	 * @return
	 * 
	 */
	Vector<String> getAvailableSpheres(String contactName);

	/**
	 * @param login
	 * @return
	 * 
	 */
	boolean isUserExists(String login);

	/**
	 * @return
	 */
	String getDomains();

	/**
	 * @return
	 * 
	 */
	List<String> getAvailableGroupSpheresId( String contactName );

	/**
	 * @return
	 */
	String getInformationForDump();

	/**
	 * @param display_name
	 * @return
	 * 
	 */
	boolean isSphereExists(String display_name,String contactName);

	/**
	 * 
	 * @param sphere
	 * @return
	 */
	boolean isSpherePersonal(SphereStatement sphere);

	/**
	 * @param contact_name
	 * @return
	 */
	Vector<String> getMembersFor(String contact_name);

	/**
	 * 
	 * @return
	 */
	String getSphereCoreId( String contactName );

	/**
	 * @return
	 */
	String getSupraSphereName();

	/**
	 * @param contactName
	 */
	String getSphereCoreDisplayNameFor(String contactName);

	/**
	 * @param system_name
	 * 
	 */
	String getDisplayNameWithoutRealName(String system_name);

	/**
	 * 
	 * @param system_name
	 * @param contactName
	 * @return
	 */
	String getSphereDisplayName(String contactName, String system_name);

	/**	
	 * @param display_name
	 * @param contact_name
	 * @return
	 */
	String getSphereSystemNameByContactAndDisplayName(String display_name,
			String contact_name);

	/**
	 * @param login
	 * @return
	 * 
	 */
	List<SphereReference> getAllAvailablePrivateSpheres(String login);

	/**
	 * @param sphereId
	 * @return
	 */
	List<MemberReference> getMembersForSphere(String sphereId);

	/**
	 * @param sphereId
	 * @param memberLogin
	 * @return
	 */
	boolean isSphereEnabledForMember(String sphereId, String memberLogin);

	/**
	 * @param sphereId
	 * @return
	 */
	Vector<String> getLoginsForMemberEnabled(String sphereId);

	/**
	 * @param contactName
	 * @return		
	 */
	Vector<String> getContactsForMemberEnabled(String contactName);
	
	String getAdminLogin();
	

	String getAdminName();
	

}
