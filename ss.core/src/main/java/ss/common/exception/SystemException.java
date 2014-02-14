// $Id$
package ss.common.exception;

/**
 * SystemException
 * @author sbitteker
 * @version $revision$
 */
public class SystemException extends Exception {
  
  /**
   * serialVersionUID for SystemException
   */
  private static final long serialVersionUID = 9132700522158209118L;

  /**
   * Constructs a new exception with null as its detail message.
   */
  public SystemException() {
    super();
  }
  
  /**
   * Constructs a new exception with the specified detail message.
   * @param name
   */
  public SystemException(String name) {
    super(name);    
  }
  
  /**
   * Constructs a new exception with the specified detail message and cause.
   * @param message
   * @param cause
   */
  public SystemException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructs a new exception with the specified cause and a detail
   * message of (cause==null ? null : cause.toString()) (which typically
   * contains the class and detail message of cause).
   * @param cause
   */
  public SystemException(Throwable cause) {
    super(cause);
  }
}
