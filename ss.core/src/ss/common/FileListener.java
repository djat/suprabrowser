package ss.common;

import java.io.File;



/**
 * Interface for listening to disk file changes.
 * @see FileMonitor
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public interface FileListener
{
  /**
   * Called when one of the monitored files are created, deleted
   * or modified.
   * 
   * @param file  File which has been changed.
   */
  void fileChanged (File file);
 
  
}
