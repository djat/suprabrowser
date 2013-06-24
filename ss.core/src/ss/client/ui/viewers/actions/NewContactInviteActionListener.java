/**
 * 
 */
package ss.client.ui.viewers.actions;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.viewers.NewContact;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class NewContactInviteActionListener implements Listener {
    private NewContact newContact;
    private Logger logger = SSLogger.getLogger(this.getClass());
    
    /**
     * @param newContact
     */
    public NewContactInviteActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    public void handleEvent(Event event) {

        Thread t = new Thread() {
            private NewContactInviteActionListener listener = NewContactInviteActionListener.this;
            private NewContact newContact = NewContactInviteActionListener.this.newContact;

            public void run() {

                String cname = null;

                this.listener.logger.info("origdoc in invite: "
                        + this.newContact.getOrigDoc().asXML());
                if (this.newContact.getOrigDoc().getRootElement().element(
                        "last_name").attributeValue("value").length() > 0) {
                    cname = this.newContact.getOrigDoc().getRootElement()
                            .element("first_name").attributeValue("value")
                            + " "
                            + this.newContact.getOrigDoc().getRootElement()
                                    .element("last_name").attributeValue(
                                            "value");
                } else {

                    cname = this.newContact.getOrigDoc().getRootElement()
                            .element("first_name").attributeValue("value");

                }
                NewContactInviteActionListener.this.logger.info("cname???? " + cname);
                /*
                 * final String toEmail = (String)
                 * (mP.sF.client.getEmailInfo( session,
                 * cname)).get("email_address");
                 */

                final String toEmail = this.newContact.getOrigDoc()
                        .getRootElement().element("email_address")
                        .attributeValue("value");

                final String fromEmail = (String) (this.newContact.getClient()
                        .getEmailInfo(this.newContact.getSession(),
                                (String) this.newContact.getSession()
                                        .get("real_name")))
                        .get("email_address");

                final String fromDomain = (String) (this.newContact.getClient()
                        .getEmailInfo(this.newContact.getSession(),
                                (String) this.newContact.getSession()
                                        .get("real_name")))
                        .get("sphere_domain");

                this.listener.logger.info("to,from,fromdomain: " + toEmail + " : "
                        + fromEmail + " : " + fromDomain);

                // String subject = "A Personal Invitation From "
                // + (String) this.newContact.session.get("real_name");


                final String name = this.newContact.getClient()
                        .getVerifyAuth().getDisplayName(
                                (String) this.newContact.getSession()
                                        .get("sphere_id"));

                /*
                 * final String replySphere = (String)
                 * this.newContact.session .get("sphere_id") + "." +
                 * this.newContact.origDoc.getRootElement()
                 * .element("message_id").attributeValue( "value") + "@" +
                 * fromDomain;
                 */
                // String body = null;
                this.newContact.getClient().getAndSendInviteText(
                        this.newContact.getSession(), this.newContact.getOrigDoc(),
                        fromDomain, fromEmail);

                /*
                 * if (body.length()==0) {
                 * 
                 * if
                 * (origDoc.getRootElement().element("login").attributeValue("value").length()==0) {
                 * 
                 * body = ("Here are the steps to download a new type of
                 * product called a 'browsing engine':\n\n 1. If you do not
                 * have java 1.5, or if you are unsure, please visit
                 * http://java.com and click 'Download Now'.\n\n2. Please
                 * download and install
                 * http://www.suprasphere.com/SupraSphere.exe\n\n3. Please
                 * copy and paste this URL when you first run the
                 * application. \n\n" + "invite::" + (String)
                 * session.get("address") + ":" + (String)
                 * session.get("port") + "," + id); } else {
                 * 
                 * body = ("Here are the steps to download a new type of
                 * product called a 'browsing engine':\n\n 1. If you do not
                 * have java 1.5, or if you are unsure, please visit
                 * http://java.com and click 'Download Now'.\n\n2. Please
                 * download and install
                 * http://www.suprasphere.com/SupraSphere.exe\n\n3. Please
                 * copy and paste this URL when you first run the
                 * application. \n\n" + "invite::" + (String)
                 * session.get("address") + ":" + (String)
                 * session.get("port") + "," + id); } }
                 */

                // mP.sF.client.sendEmailFromServer(session,fromEmail,toEmail,new
                // StringBuffer(body),subject,replySphere);
                /*
                 * eMailer em = new eMailer(fromEmail, toEmail, new
                 * StringBuffer( body), subject, replySphere);
                 */

                String sphereType = this.newContact.getClient()
                        .getVerifyAuth().getSphereType(
                                (String) this.newContact.getSession()
                                        .get("sphere_id"));
                if (sphereType == null) {
                    sphereType = "member";

                }

                this.newContact.getClient().addInviteToContact(
                        this.newContact.getSession(), this.newContact.getOrigDoc()
                                .getRootElement().element("message_id")
                                .attributeValue("value"),
                        (String) this.newContact.getSession().get("sphere_id"),
                        name, sphereType);

                UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						NewContact.getSShell().dispose();
					}
				});
            }
        };
        ThreadUtils.start(t);

    }
}