/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

/**
 * <P>
 * Wraps a MimeMessage adding routing information (from SMTP) and some simple
 * API enhancements.
 * </P>
 * <P>
 * From James version > 2.2.0a8 "mail attributes" have been added. Backward and
 * forward compatibility is supported: messages stored in file repositories
 * <I>without</I> attributes by James version <= 2.2.0a8 will be processed by
 * later versions as having an empty attributes hashmap; messages stored in file
 * repositories <I>with</I> attributes by James version > 2.2.0a8 will be
 * processed by previous versions, ignoring the attributes.
 * </P>
 * 
 * @version CVS $Revision: 1.17.4.6 $ $Date: 2004/03/15 03:54:15 $
 */
public class MailImpl implements Mail {
	/**
	 * We hardcode the serialVersionUID so that from James 1.2 on, MailImpl will
	 * be deserializable (so your mail doesn't get lost)
	 */
	public static final long serialVersionUID = -4289663364703986260L;

	/**
	 * The error message, if any, associated with this mail.
	 */
	private String errorMessage;

	/**
	 * The state of this mail, which determines how it is processed.
	 */
	private String state;

	/**
	 * The MimeMessage that holds the mail data.
	 */
	private MimeMessage message;

	/**
	 * The sender of this mail.
	 */
	private MailAddress sender;

	/**
	 * The collection of recipients to whom this mail was sent.
	 */
	private Collection recipients;

	/**
	 * The identifier for this mail message
	 */
	private String name;

	/**
	 * The remote host from which this mail was sent.
	 */
	private String remoteHost = "localhost";

	/**
	 * The remote address from which this mail was sent.
	 */
	private String remoteAddr = "127.0.0.1";

	/**
	 * The last time this message was updated.
	 */
	private Date lastUpdated = new Date();

	/**
	 * Attributes added to this MailImpl instance
	 */
	private HashMap attributes;

	/**
	 * A constructor that creates a new, uninitialized MailImpl
	 */
	public MailImpl() {
		setState(Mail.DEFAULT);
		this.attributes = new HashMap();
	}

	/**
	 * A constructor that creates a MailImpl with the specified name, sender,
	 * and recipients.
	 * 
	 * @param name
	 *            the name of the MailImpl
	 * @param sender
	 *            the sender for this MailImpl
	 * @param recipients
	 *            the collection of recipients of this MailImpl
	 */
	@SuppressWarnings("unchecked")
	public MailImpl(String name, MailAddress sender, Collection recipients) {
		this();
		this.name = name;
		this.sender = sender;
		this.recipients = null;

		// Copy the recipient list
		if (recipients != null) {
			Iterator theIterator = recipients.iterator();
			this.recipients = new ArrayList();
			while (theIterator.hasNext()) {
				this.recipients.add(theIterator.next());
			}
		}
	}

	/**
	 * A constructor that creates a MailImpl with the specified name, sender,
	 * recipients, and message data.
	 * 
	 * @param name
	 *            the name of the MailImpl
	 * @param sender
	 *            the sender for this MailImpl
	 * @param recipients
	 *            the collection of recipients of this MailImpl
	 * @param messageIn
	 *            a stream containing the message source
	 */
	public MailImpl(String name, MailAddress sender, Collection recipients,
			InputStream messageIn) throws MessagingException {
		this(name, sender, recipients);
		MimeMessageSource source = new MimeMessageInputStreamSource(name,
				messageIn);
		MimeMessageWrapper wrapper = new MimeMessageWrapper(source);
		this.setMessage(wrapper);
	}

	/**
	 * A constructor that creates a MailImpl with the specified name, sender,
	 * recipients, and MimeMessage.
	 * 
	 * @param name
	 *            the name of the MailImpl
	 * @param sender
	 *            the sender for this MailImpl
	 * @param recipients
	 *            the collection of recipients of this MailImpl
	 * @param message
	 *            the MimeMessage associated with this MailImpl
	 */
	public MailImpl(String name, MailAddress sender, Collection recipients,
			MimeMessage message) {
		this(name, sender, recipients);
		this.setMessage(message);
	}

	/**
	 * A constructor which will attempt to obtain sender and recipients from the
	 * headers of the MimeMessage supplied.
	 * 
	 * @param message -
	 *            a MimeMessage from which to construct a Mail
	 */
	@SuppressWarnings("unchecked")
	public MailImpl(MimeMessage message) throws MessagingException {
		this();
		MailAddress sender = getReturnPath(message);
		Collection recipients = null;
		Address[] addresses = message
				.getRecipients(MimeMessage.RecipientType.TO);
		if (addresses != null) {
			recipients = new ArrayList();
			for (int i = 0; i < addresses.length; i++) {
				try {
					recipients.add(new MailAddress(new InternetAddress(
							addresses[i].toString(), false)));
				} catch (ParseException pe) {
					// RFC 2822 section 3.4 allows To: fields without <>
					// Let's give this one more try with <>.
					try {
						recipients.add(new MailAddress("<"
								+ new InternetAddress(addresses[i].toString())
										.toString() + ">"));
					} catch (ParseException _) {
						throw new MessagingException(
								"Could not parse address: "
										+ addresses[i].toString()
										+ " from "
										+ message.getHeader(RFC2822Headers.TO,
												", "), pe);
					}
				}
			}
		}
		this.name = message.toString();
		this.sender = sender;
		this.recipients = recipients;
		this.setMessage(message);
	}

	/**
	 * Gets the MailAddress corresponding to the existing "Return-Path" of
	 * <I>message</I>. If missing or empty returns <CODE>null</CODE>,
	 */
	private MailAddress getReturnPath(MimeMessage message)
			throws MessagingException {
		MailAddress mailAddress = null;
		String[] returnPathHeaders = message
				.getHeader(RFC2822Headers.RETURN_PATH);
		String returnPathHeader = null;
		if (returnPathHeaders != null) {
			returnPathHeader = returnPathHeaders[0];
			if (returnPathHeader != null) {
				returnPathHeader = returnPathHeader.trim();
				if (!returnPathHeader.equals("<>")) {
					try {
						mailAddress = new MailAddress(new InternetAddress(
								returnPathHeader, false));
					} catch (ParseException pe) {
						throw new MessagingException(
								"Could not parse address: "
										+ returnPathHeader
										+ " from "
										+ message.getHeader(
												RFC2822Headers.RETURN_PATH,
												", "), pe);
					}
				}
			}
		}
		return mailAddress;
	}

	/**
	 * Duplicate the MailImpl.
	 * 
	 * @return a MailImpl that is a duplicate of this one
	 */
	public Mail duplicate() {
		return duplicate(this.name);
	}

	/**
	 * Duplicate the MailImpl, replacing the mail name with the one passed in as
	 * an argument.
	 * 
	 * @param newName
	 *            the name for the duplicated mail
	 * 
	 * @return a MailImpl that is a duplicate of this one with a different name
	 */
	public Mail duplicate(String newName) {
		try {
			MailImpl newMail = new MailImpl(newName, this.sender,
					this.recipients, getMessage());
			newMail.setRemoteHost(this.remoteHost);
			newMail.setRemoteAddr(this.remoteAddr);
			newMail.setLastUpdated(this.lastUpdated);
			newMail.setAttributesRaw((HashMap) this.attributes.clone());
			return newMail;
		} catch (MessagingException me) {
			// Ignored. Return null in the case of an error.
		}
		return (Mail) null;
	}

	/**
	 * Get the error message associated with this MailImpl.
	 * 
	 * @return the error message associated with this MailImpl
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Get the MimeMessage associated with this MailImpl.
	 * 
	 * @return the MimeMessage associated with this MailImpl
	 */
	public MimeMessage getMessage() throws MessagingException {
		return this.message;
	}

	/**
	 * Set the name of this MailImpl.
	 * 
	 * @param name
	 *            the name of this MailImpl
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of this MailImpl.
	 * 
	 * @return the name of this MailImpl
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the recipients of this MailImpl.
	 * 
	 * @return the recipients of this MailImpl
	 */
	public Collection getRecipients() {
		return this.recipients;
	}

	/**
	 * Get the sender of this MailImpl.
	 * 
	 * @return the sender of this MailImpl
	 */
	public MailAddress getSender() {
		return this.sender;
	}

	/**
	 * Get the state of this MailImpl.
	 * 
	 * @return the state of this MailImpl
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Get the remote host associated with this MailImpl.
	 * 
	 * @return the remote host associated with this MailImpl
	 */
	public String getRemoteHost() {
		return this.remoteHost;
	}

	/**
	 * Get the remote address associated with this MailImpl.
	 * 
	 * @return the remote address associated with this MailImpl
	 */
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	/**
	 * Get the last updated time for this MailImpl.
	 * 
	 * @return the last updated time for this MailImpl
	 */
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	/**
	 * <p>
	 * Return the size of the message including its headers.
	 * MimeMessage.getSize() method only returns the size of the message body.
	 * </p>
	 * 
	 * <p>
	 * Note: this size is not guaranteed to be accurate - see Sun's
	 * documentation of MimeMessage.getSize().
	 * </p>
	 * 
	 * @return approximate size of full message including headers.
	 * 
	 * @throws MessagingException
	 *             if a problem occurs while computing the message size
	 */
	public long getMessageSize() throws MessagingException {
		// If we have a MimeMessageWrapper, then we can ask it for just the
		// message size and skip calculating it
		if (this.message instanceof MimeMessageWrapper) {
			MimeMessageWrapper wrapper = (MimeMessageWrapper) this.message;
			return wrapper.getMessageSize();
		}
		// SK: Should probably eventually store this as a locally
		// maintained value (so we don't have to load and reparse
		// messages each time).
		long size = this.message.getSize();
		Enumeration e = this.message.getAllHeaderLines();
		while (e.hasMoreElements()) {
			size += ((String) e.nextElement()).length();
		}
		return size;
	}

	/**
	 * Set the error message associated with this MailImpl.
	 * 
	 * @param msg
	 *            the new error message associated with this MailImpl
	 */
	public void setErrorMessage(String msg) {
		this.errorMessage = msg;
	}

	/**
	 * Set the MimeMessage associated with this MailImpl.
	 * 
	 * @param message
	 *            the new MimeMessage associated with this MailImpl
	 */
	public void setMessage(MimeMessage message) {
		this.message = message;
	}

	/**
	 * Set the recipients for this MailImpl.
	 * 
	 * @param recipients
	 *            the recipients for this MailImpl
	 */
	public void setRecipients(Collection recipients) {
		this.recipients = recipients;
	}

	/**
	 * Set the sender of this MailImpl.
	 * 
	 * @param sender
	 *            the sender of this MailImpl
	 */
	public void setSender(MailAddress sender) {
		this.sender = sender;
	}

	/**
	 * Set the state of this MailImpl.
	 * 
	 * @param state
	 *            the state of this MailImpl
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Set the remote address associated with this MailImpl.
	 * 
	 * @param remoteHost
	 *            the new remote host associated with this MailImpl
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * Set the remote address associated with this MailImpl.
	 * 
	 * @param remoteAddr
	 *            the new remote address associated with this MailImpl
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	/**
	 * Set the date this mail was last updated.
	 * 
	 * @param lastUpdated
	 *            the date the mail was last updated
	 */
	public void setLastUpdated(Date lastUpdated) {
		// Make a defensive copy to ensure that the date
		// doesn't get changed external to the class
		if (lastUpdated != null) {
			lastUpdated = new Date(lastUpdated.getTime());
		}
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Writes the message out to an OutputStream.
	 * 
	 * @param out
	 *            the OutputStream to which to write the content
	 * 
	 * @throws MessagingException
	 *             if the MimeMessage is not set for this MailImpl
	 * @throws IOException
	 *             if an error occurs while reading or writing from the stream
	 */
	public void writeMessageTo(OutputStream out) throws IOException,
			MessagingException {
		if (this.message != null) {
			this.message.writeTo(out);
		} else {
			throw new MessagingException("No message set for this MailImpl.");
		}
	}

	/**
	 * Generates a bounce mail that is a bounce of the original message.
	 * 
	 * @param bounceText
	 *            the text to be prepended to the message to describe the bounce
	 *            condition
	 * 
	 * @return the bounce mail
	 * 
	 * @throws MessagingException
	 *             if the bounce mail could not be created
	 */
	@SuppressWarnings("unchecked")
	public Mail bounce(String bounceText) throws MessagingException {
		// This sends a message to the james component that is a bounce of the
		// sent message
		MimeMessage original = getMessage();
		MimeMessage reply = (MimeMessage) original.reply(false);
		reply.setSubject("Re: " + original.getSubject());
		Collection recipients = new HashSet();
		recipients.add(getSender());
		InternetAddress addr[] = { new InternetAddress(getSender().toString()) };
		reply.setRecipients(Message.RecipientType.TO, addr);
		reply.setFrom(new InternetAddress(getRecipients().iterator().next()
				.toString()));
		reply.setText(bounceText);
		reply.setHeader(RFC2822Headers.MESSAGE_ID, "replyTo-" + getName());
		return new MailImpl("replyTo-" + getName(), new MailAddress(
				getRecipients().iterator().next().toString()), recipients,
				reply);
	}

	/**
	 * Writes the content of the message, up to a total number of lines, out to
	 * an OutputStream.
	 * 
	 * @param out
	 *            the OutputStream to which to write the content
	 * @param lines
	 *            the number of lines to write to the stream
	 * 
	 * @throws MessagingException
	 *             if the MimeMessage is not set for this MailImpl
	 * @throws IOException
	 *             if an error occurs while reading or writing from the stream
	 */
	public void writeContentTo(OutputStream out, int lines) throws IOException,
			MessagingException {
		String line;
		BufferedReader br;
		if (this.message != null) {
			br = new BufferedReader(new InputStreamReader(this.message
					.getInputStream()));
			while (lines-- > 0) {
				if ((line = br.readLine()) == null) {
					break;
				}
				line += "\r\n";
				out.write(line.getBytes());
			}
		} else {
			throw new MessagingException("No message set for this MailImpl.");
		}
	}

	// Serializable Methods
	// TODO: These need some work. Currently very tightly coupled to
	// the internal representation.
	/**
	 * Read the MailImpl from an <code>ObjectInputStream</code>.
	 * 
	 * @param in
	 *            the ObjectInputStream from which the object is read
	 * 
	 * @throws IOException
	 *             if an error occurs while reading from the stream
	 * @throws ClassNotFoundException ?
	 * @throws ClassCastException
	 *             if the serialized objects are not of the appropriate type
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		try {
			Object obj = in.readObject();
			if (obj == null) {
				this.sender = null;
			} else if (obj instanceof String) {
				this.sender = new MailAddress((String) obj);
			} else if (obj instanceof MailAddress) {
				this.sender = (MailAddress) obj;
			}
		} catch (ParseException pe) {
			throw new IOException("Error parsing sender address: "
					+ pe.getMessage());
		}
		this.recipients = (Collection) in.readObject();
		this.state = (String) in.readObject();
		this.errorMessage = (String) in.readObject();
		this.name = (String) in.readObject();
		this.remoteHost = (String) in.readObject();
		this.remoteAddr = (String) in.readObject();
		setLastUpdated((Date) in.readObject());
		// the following is under try/catch to be backwards compatible
		// with messages created with James version <= 2.2.0a8
		try {
			this.attributes = (HashMap) in.readObject();
		} catch (OptionalDataException ode) {
			if (ode.eof) {
				this.attributes = new HashMap();
			} else {
				throw ode;
			}
		}
	}

	/**
	 * Write the MailImpl to an <code>ObjectOutputStream</code>.
	 * 
	 * @param in
	 *            the ObjectOutputStream to which the object is written
	 * 
	 * @throws IOException
	 *             if an error occurs while writing to the stream
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		this.lastUpdated = new Date();
		out.writeObject(this.sender);
		out.writeObject(this.recipients);
		out.writeObject(this.state);
		out.writeObject(this.errorMessage);
		out.writeObject(this.name);
		out.writeObject(this.remoteHost);
		out.writeObject(this.remoteAddr);
		out.writeObject(this.lastUpdated);
		out.writeObject(this.attributes);
	}

	/**
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
	public void dispose() {
		/*
		 * try { MimeMessage wrapper = getMessage(); if (wrapper instanceof
		 * Disposable) { ((Disposable)wrapper).dispose(); } } catch
		 * (MessagingException me) { // Ignored }
		 */
	}

	/**
	 * This method is necessary, when Mail repositories needs to deal explicitly
	 * with storing Mail attributes as a Serializable Note: This method is not
	 * exposed in the Mail interface, it is for internal use by James only.
	 * 
	 * @return Serializable of the entire attributes collection
	 * @since 2.2.0
	 */
	public HashMap getAttributesRaw() {
		return this.attributes;
	}

	/**
	 * This method is necessary, when Mail repositories needs to deal explicitly
	 * with retriving Mail attributes as a Serializable Note: This method is not
	 * exposed in the Mail interface, it is for internal use by James only.
	 * 
	 * @return Serializable of the entire attributes collection
	 * @since 2.2.0
	 */
	public void setAttributesRaw(HashMap attr) {
		this.attributes = (attr == null) ? new HashMap() : attr;
	}

	/**
	 * @see org.apache.mailet.Mail#getAttribute(String)
	 * @since 2.2.0
	 */
	public Serializable getAttribute(String key) {
		return (Serializable) this.attributes.get(key);
	}

	/**
	 * @see org.apache.mailet.Mail#setAttribute(String,Serializable)
	 * @since 2.2.0
	 */
	@SuppressWarnings("unchecked")
	public Serializable setAttribute(String key, Serializable object) {
		return (Serializable) this.attributes.put(key, object);
	}

	/**
	 * @see org.apache.mailet.Mail#removeAttribute(String)
	 * @since 2.2.0
	 */
	public Serializable removeAttribute(String key) {
		return (Serializable) this.attributes.remove(key);
	}

	/**
	 * @see org.apache.mailet.Mail#removeAllAttributes()
	 * @since 2.2.0
	 */
	public void removeAllAttributes() {
		this.attributes.clear();
	}

	/**
	 * @see org.apache.mailet.Mail#getAttributeNames()
	 * @since 2.2.0
	 */
	public Iterator getAttributeNames() {
		return this.attributes.keySet().iterator();
	}

	/**
	 * @see org.apache.mailet.Mail#hasAttributes()
	 * @since 2.2.0
	 */
	public boolean hasAttributes() {
		return !this.attributes.isEmpty();
	}
}