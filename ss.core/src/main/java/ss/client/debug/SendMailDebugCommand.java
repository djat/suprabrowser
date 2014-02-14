package ss.client.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import ss.smtp.*;
import ss.smtp.reciever.EmailProcessor;

public class SendMailDebugCommand extends AbstractDebugCommand {

    private String recipient;
    
    private String subject;
    
    private String sender;
    
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendMailDebugCommand.class);
	/**
	 * @param mainCommandName
	 */
	public SendMailDebugCommand() {
		super( "send-mail", "Send test mail to specified address" );
	}

	
	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		try {
			logger.info("prepare mail");
			final ArrayList<String> recivers = new ArrayList<String>( );
			final MailAddress reciver = new MailAddress( getRecipientAddress() );
			recivers.add( reciver.toString() );
			HashMap<String, MailAddress> state = new HashMap<String, MailAddress>();
			final MailAddress sender = new MailAddress( getSenderAddress() );
			state.put( EmailProcessor.SENDER, sender);
			Session session = Session.getInstance( new Properties(), new Authenticator() {
			        protected PasswordAuthentication getPasswordAuthentication() {
			          return new PasswordAuthentication("smtpUsername", "smtpPassword" );
			        }
			      });
			MimeMessage msg = new MimeMessage(session);
			msg.setText( getMessageText() );
			msg.setRecipients(RecipientType.TO, reciver.toString() );
			msg.setSender(sender.toInternetAddress() );
            msg.setSubject( getSubject() );
            
            msg.setHeader("In-Reply-To", "aaa7559610131315392480-4105783495654015194-8649225833233884801@.on.suprasphere");
            msg.setHeader("Message-ID", "4565465465465465464");
			Mail mail = new MailImpl( "SupraSphere", sender, recivers, msg );
			logger.info("process email");
			new EmailProcessor( state ).processEmail( mail );
            super.getCommandOutput().appendln( "Mail sended... " )
            .append( "To ").appendln( getRecipientAddress() )
            .append( "From ").appendln( getSenderAddress() )
            .append( "Subject ").appendln( getSubject() );
		}
		catch (Throwable ex) {
			throw new DebugCommanRunntimeException( ex );
		}		
	}


    private String getSubject() {
        return this.subject;
    }


    /**
     * @return
     */
    private String getMessageText() {
        return "Text for " + getSubject() + " and urlka: no for now tuta";
    }


    /**
     * @return
     */
    private String getSenderAddress() {
        return this.sender;
    }


    /**
     * @return
     */
    private String getRecipientAddress() {
        return this.recipient;
    }


    /* (non-Javadoc)
     * @see ss.client.debug.AbstractDebugCommand#processCommandLine(ss.client.debug.ParsedDebugCommandLine)
     */
    @Override
    protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
        super.processCommandLine(parsedDebugCommandLine);
        this.recipient = parsedDebugCommandLine.getArg0( "zobo@somedomen" );
        this.sender = parsedDebugCommandLine.getArg1( "default-sender@address" );
        this.subject = parsedDebugCommandLine.getArg2( "default subject" );
    }
	
}
