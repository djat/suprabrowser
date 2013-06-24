/**
 * 
 */
package ss.client.ui.viewers.actions;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.viewers.NewContact;
import ss.global.SSLogger;
import ss.util.NameTranslation;

/**
 * @author zobo
 *
 */
public class NewContactEntitleActionListener implements Listener {
    private NewContact newContact;
    private Logger logger = SSLogger.getLogger(this.getClass());

    /**
     * @param newContact
     */
    public NewContactEntitleActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    @SuppressWarnings("unchecked")
	public void handleEvent(Event event) {

        String sphereId = (String) this.newContact.getSession().get("sphere_id");

        String sphereType = this.newContact.getClient().getVerifyAuth()
                .getSphereType(sphereId);
        boolean isPersonal = this.newContact.getClient().getVerifyAuth()
                .isPersonal(sphereId,
                        (String) this.newContact.getSession().get("username"),
                        (String) this.newContact.getSession().get("real_name"));

        Vector members = this.newContact.getClient()
                .getMembersFor(this.newContact.getSession());

        boolean existsAlready = false;
        String existingContactName = NameTranslation
                .createContactNameFromContactDoc(this.newContact.getOrigDoc());
        for (int i = 0; i < members.size(); i++) {

            Document oneMember = (Document) members.get(i);

            String existingMemberContactName = oneMember.getRootElement()
                    .element("contact_name").attributeValue("value");
            if (existingContactName.equals(existingMemberContactName)) {
                existsAlready = true;

            }

        }
        if (existsAlready == false) {

            if (sphereType.equals("group") || isPersonal) {

				//@SuppressWarnings("unused")
				String commonSphereId = this.newContact.getClient()
                        .entitleContactForSphere(
                                this.newContact.getSession(),
                                this.newContact.getOrigDoc(),
                                this.newContact.getClient()
                                        .getVerifyAuth()
                                        .getSphereType(
                                                (String) this.newContact.getSession()
                                                        .get("sphere_id")));

                if (isPersonal) {

                    // String commonSphereId =
                    // mP.sF.client.verifyAuth.getSharedSphereIdForContactPair((String)session.get("real_name"),NameTranslation.createContactNameFromContactDoc(origDoc));

                    // Hashtable toSend = (Hashtable)session.clone();
                    // toSend.put("session_id",commonSphereId);

                    // Document sendDoc =
                    // XMLSchemaTransform.addLocationToDoc(origDoc,origDoc,(String)session.get("sphereURL"),(String)session.get("sphere_id"),(String)session.get("real_name"),commonSphereId,NameTranslation.createContactNameFromContactDoc(origDoc));

                    // mP.sF.client.publishTerse(toSend, sendDoc);
                    // mP.sF.client.addLocationsToDoc(session,origDoc,commonSphereId,NameTranslation.createContactNameFromContactDoc(origDoc),sendDoc.getRootElement().element("message_id").attributeValue("value"));

                }

            } else {

                this.logger
                        .warn("Entitle new contact for each of the members in the sphere...");

                for (int i = 0; i < members.size(); i++) {

                    Document oneMember = (Document) members.get(i);

                    String existingMemberLogin = oneMember.getRootElement()
                            .element("login_name").attributeValue("value");
                    String existingMemberContact = oneMember
                            .getRootElement().element("contact_name")
                            .attributeValue("value");

                    String personalSphere = this.newContact.getClient()
                            .getVerifyAuth().getPrivateForSomeoneElse(
                                    existingMemberContact);

                    Hashtable toSend = (Hashtable) this.newContact.getSession()
                            .clone();

                    toSend.put("sphere_id", personalSphere);

                    this.newContact.getClient()
                            .entitleContactForOneSphere(toSend,
                                    this.newContact.getOrigDoc(), oneMember,
                                    existingMemberLogin,
                                    existingMemberContact);

                    // Document sendDoc =
                    // XMLSchemaTransform.addLocationToDoc(origDoc,origDoc,(String)session.get("sphereURL"),(String)session.get("sphere_id"),mP.sF.client.verifyAuth.getDisplayName((String)session.get("sphere_id")),personalSphere,personalDisplay);

                    // mP.sF.client.publishTerse(toSend, sendDoc);

                    // mP.sF.client.addLocationsToDoc(session,origDoc,personalSphere,
                    // personalDisplay,sendDoc.getRootElement().element("message_id").attributeValue("value"));

                    // String commonSphereId =
                    // mP.sF.client.verifyAuth.getSystemNameForOther(existingMemberContact,NameTranslation.createContactNameFromContactDoc(origDoc));

                    // mP.sF.client.publishTerse(toSend, sendDoc);

                    // mP.sF.client.addLocationsToDoc(session,origDoc,personalSphere,
                    // personalDisplay,sendDoc.getRootElement().element("message_id").attributeValue("value"));

                    /*
                     * 
                     * String supraSphere =
                     * (String)session.get("supra_sphere");
                     * 
                     * Document oneMember = (Document)members.get(i);
                     * 
                     * String existingMemberLogin =
                     * oneMember.getRootElement().element("login_name").attributeValue("value");
                     * String existingMemberContact =
                     * oneMember.getRootElement().element("contact_name").attributeValue("value");
                     * String personalSphere =
                     * mP.sF.client.verifyAuth.getPrivateForSomeoneElse(existingMemberContact);
                     * 
                     * String cname =
                     * NameTranslation.createContactNameFromContactDoc(origDoc);
                     * String username =
                     * origDoc.getRootElement().element("login").attributeValue("value");
                     * 
                     * mP.client.registerMember(session,supraSphere,origDoc,existingMemberLogin,
                     * existingMemberContact, personalSphere,
                     * sphereId,cname,username, "member");
                     */

                }

            }

            NewContact.getSShell().dispose();

        }
    }
}