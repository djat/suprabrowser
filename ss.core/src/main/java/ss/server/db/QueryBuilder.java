package ss.server.db;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ss.common.SearchCriteria;
import ss.common.exception.SystemException;

/**
 * QueryBuilder will help build queries.
 */
public class QueryBuilder {

  public static final String SPHERE_ID = "sphere_id";
  public static final String REC_ID = "recid";
  public static final String XMLDATA = "xmldata";
  public static final String TYPE = "type";
  public static final String MOMENT = "moment";
  public static final String THREAD_TYPE = "thread_type";
  public static final String CREATE_TS = "create_ts";
  public static final String THREAD_ID = "thread_id";
  public static final String MESSAGE_ID = "message_id";
  public static final String IS_RESPONSE = "isResponse";
  public static final String USED = "used";
  public static final String MODIFIED = "modified";
  public static final String TOTAL_ACCRUED = "total_accrued";
  
  /**
   * Creates the defailt QueryBuilder
   */
  public QueryBuilder() {
    
  }
  
  /**
   * Returns a string to query the supraspheres table given the criteria
   * and the sphereIds to search.
   * @param criteria
   * @param sphereIds
   * @return the query string (select xmldata from suprasphere where...)
   * @throws SystemException if there are no sphereIds to search
   */
  @SuppressWarnings("unchecked")
public String buildSupraQuery(SearchCriteria criteria,
      Collection sphereIds) throws SystemException {
    
    boolean previousCriteria = false;
    StringBuffer queryBuffer = new StringBuffer();
    Date beginDate = criteria.getBeginDateRange();
    Date endDate = criteria.getEndDateRange(); 

    queryBuffer.append("select xmldata, recid from supraspheres where ");

    // add begin and end dates if requested
    if(beginDate != null || endDate != null) {
      if(beginDate != null && endDate != null) {
        queryBuffer.append("((modified > ")
        .append(beginDate)
        .append(" and modified < ")
        .append(endDate)
        .append(") or (create_ts > ")
        .append(beginDate)
        .append(" and create_ts < ")
        .append(endDate)
        .append("))");
      }
      else if(beginDate != null) {
        queryBuffer.append("(modified > ")
        .append(beginDate)
        .append(" or create_ts > ")
        .append(beginDate)
        .append(")");
      }
      else if(endDate != null) {
        queryBuffer.append("(modified < ")
        .append(endDate)
        .append(" or create_ts < ")
        .append(endDate)
        .append(")");
      }
      previousCriteria = true;
    }
    
    List contentTypes = criteria.getContentType();

    // add content types to where clause
    if(!contentTypes.isEmpty()) {
      previousCriteria = addAndIfNeeded(queryBuffer, previousCriteria);
      
      queryBuffer.append(buildInClause(TYPE, contentTypes));
    }
    
    if(!sphereIds.isEmpty()) {
      // it should always have at least one sphereId
      previousCriteria = addAndIfNeeded(queryBuffer, previousCriteria);
        
      queryBuffer.append(buildInClause(SPHERE_ID, sphereIds));      
    }
    else
      throw new SystemException("no sphereIds to do a supra search on!");
    
    int resultsPerPage = criteria.getResultsPerPage();
    queryBuffer.append(" order by modified limit ")
      .append((criteria.getPage() - 1) * resultsPerPage)
      .append(", ")
      .append(resultsPerPage);
        
    return queryBuffer.toString();
  }

  /**
   * private helper method to add an ' and ' if there were previous criteria
   * in the where clause
   * @param buffer
   * @param previousCriteria
   * @return the new state of previousCriteria. It will always return true
   * it is assumed that it will be called after a criteria has been added.
   * It is shorthand to be able to update the previousCriteria boolean in
   * the caller, so an extra assignment is not needed.
   */
  private boolean addAndIfNeeded(StringBuffer buffer, boolean previousCriteria) {
    if(previousCriteria == true)
      buffer.append(" and ");
    else
      previousCriteria = true;

    return previousCriteria;
  }
  
  /**
   * Given a collection of Strings x, y, and z build a sql IN clause
   * that reads "columnName in ('x', 'y', 'z')"
   * If an emtpy collection is given an empty string will be returned, 
   * thus having no side effects on the query
   * 
   * @param columnName the table column name that is being queried
   * @param elements in the IN clause 
   * @return an IN clause given the input data
   */
  private String buildInClause(String columnName, Collection<String> elements) {
    StringBuffer buffer = new StringBuffer();
    
    if(!elements.isEmpty()) {
      buffer.append(columnName)
        .append(" in (");
      
      boolean addComma = false;
      for (Iterator elementIterator = elements.iterator(); elementIterator.hasNext();) {
        String inElement = (String) elementIterator.next();

        // add a comma after the 'in' element
        if(addComma)
          buffer.append(",");
        else
          addComma = true;

        buffer.append("'")
          .append(inElement)
          .append("'");
      }
      
      buffer.append(")");
    }
      
    return buffer.toString();
  }
}
