// $Id$
package ss.common;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * ProtocolUtil contains boilerplate static methods to create the legacy
 * protocol Maps and Lists
 * 
 * @author sbitteker
 * @version $revision$
 * @version $Id$
 */
public class ProtocolUtil {

  /**
   * COMMAND constant of the SupraSphere Communication protocol
   */
  public static final String COMMAND = "protocol";
    
  /**
   * SESSION constant of the SupraSphere Communication protocol
   */
  public static final String SESSION = "session";
  
  /**
   * RESULTS constant of the SupraSphere Communication protocol
   */
  public static final String RESULTS = "results";

  /**
   * EXCEPTION constant of the SupraSphere Communication protocol
   */
  public static final String EXCEPTION = "exception";
  
  /**
   * CRITERIA constant of the SupraSphere Communication protocol
   */
  public static final String CRITERIA = "criteria";
  
  /**
   * OPEN_BACKGROUND constant of the SupraSphere Communication protocol
   */
  public static final String OPEN_BACKGROUND = "openBackground";
  
  /**
   * SPHERE_TYPE constant of the SupraSphere Communication protocol
   */
  public static final String SPHERE_TYPE = "sphere_type";

  /**
   * SPHERE_DEFINITION constant of the SupraSphere Communication protocol
   */
  public static final String SPHERE_DEFINITION = "sphere_definition";
  
  /**
   * Create the legacy Hashtable for type compatability.  Should move to
   * first class command (protocol) objects instead of passing structs
   * around to take advantage of polymorphism

   * @param commandString the command being sent
   * @param session the current session of the user
   * @param results the results to be sent to the receiver
   * @param exception excption that occured during processing 
   * @return a Hashtable to support legacy code
   */
  @SuppressWarnings("unchecked")
public static Hashtable createCommand(String protocolString,
    Map session, List results, Exception exception) {
    
    // Create the legacy Hashtable for type compatability.  Should move to
    // first class command (protocol) objects instead of passing structs
    // around to take advantage of polymorphism
    
    Hashtable command = new Hashtable();
    
    // we should probably enforce that the following 3 parameters are
    // not null here
    command.put(COMMAND, protocolString);
    command.put(SESSION, session);
    command.put(RESULTS, results);
    if(exception != null)
      command.put(EXCEPTION, exception);
      
    return command;
  }
  
  
  /**
   * Create the legacy Hashtable for type compatability.  Should move to
   * first class command (protocol) objects instead of passing structs
   * around to take advantage of polymorphism

   * @param commandString the command being sent
   * @param session the current session of the user
   * @param results the results to be sent to the receiver
   * @return a Hashtable to support legacy code
   */
  public static Hashtable createCommand(String commandString,
    Map session, List results) {

    return createCommand(commandString, session, results, null);
  }
}
