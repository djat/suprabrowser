/**
 * SupraSphere Inc. Copyright 2006
 */
package ss.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ContentType 
 */
public enum ContentType implements Serializable {
  
  IM, NOTE, BOOKMARK, CONTACT, RSS, FILE, SPHERE, TAG;
  
  static final Map<String, ContentType> contentTypeMap = 
    new HashMap<String, ContentType>();
  
  static {
    for(ContentType c : ContentType.values())
      contentTypeMap.put(c.toString(), c);
  }
} 
