package ss.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchCriteria implements Serializable {

  private static final long serialVersionUID = 9021938612028800107L;
  private String searchKeywords;
  private String author;
  private Date beginDateRange;
  private Date endDateRange;
  private ArrayList<ContentType> contentTypes = new ArrayList<ContentType>();
  private int pageNumber = 0;
  private int resultsPerPage = 25;
  private static final int SYSTEM_PAGESIZE_MAX = 500;
  
  /**
   * Default SearchCriteria will search with no search keywords
   * with no date range limit and all ContentTypes. The default page
   * size of 25.
   */
  public SearchCriteria() {
  }
  
  public String getSearchString() {
    return this.searchKeywords;
  }

  public void setSearchString(String searchString) {
    this.searchKeywords = searchString;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getBeginDateRange() {
    return this.beginDateRange;
  }

  public void setBeginDateRange(Date beginDateRange) {
    this.beginDateRange = beginDateRange;
  }

  /**
   * returns the list of ContentTypes for this SearchCriteria
   * @return
   */
  public List getContentType() {
    return this.contentTypes;
  }

  /**
   * Add the contentType to the search criteria
   * @param contentType to be added
   */
  public void addContentType(ContentType contentType) {
    this.contentTypes.add(contentType);
  }

  /**
   * Remove the following content type. If it doesn't exist there is 
   * no side effect
   * @param contentType the contentType to remove
   */
  public void removeContentType(ContentType contentType) {
    this.contentTypes.remove(contentType);
  }

  public Date getEndDateRange() {
    return this.endDateRange;
  }

  public void setEndDateRange(Date endDateRange) {
    this.endDateRange = endDateRange;
  }

  public String getSearchKeywords() {
    return this.searchKeywords;
  }

  public void setSearchKeywords(String searchKeywords) {
    this.searchKeywords = searchKeywords;
  }  
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("SearchCriteria: keywords='")
      .append(this.searchKeywords)
      .append("' beginDate='")
      .append(this.beginDateRange)
      .append("' endDate='")
      .append("' contentTypes='")
      .append(this.contentTypes)
      .append("' author='")
      .append(this.author)
      .append("'");
        
    return buffer.toString();
  }
  
  /**
   * setPage should be called when there is a specific page desired
   * on the next query. This first page is 1.
   * @param pageNumber
   */
  public void setPage(int page) {
    // the internal pageNumber is relative to the mysql databases zero
    // relative paging, so subtract one when setting the page number.

    this.pageNumber = page - 1;
  }
  
  /**
   * getPage will return the current page defined in this SearchCriteria
   * After the first results come back the page will be 2.
   * @return the page that will be returned in the next query
   */
  public int getPage( ) {
    // the internal pageNumber is relative to the mysql databases zero
    // relative paging, so add one when returning the page number.
    
    return this.pageNumber + 1; 
  }

  /**
   * Return the current number of results per page. The default is
   * 25.
   * @return the current number of results per page
   */
  public int getResultsPerPage() {
    return this.resultsPerPage;
  }

  /**
   * Set the number of results per page. The default is 25. The system max
   * is 500. Any resultsPerPage given that is above 500 will quietly set the
   * max to 500. 
   * @param resultsPerPage desired number of results per page
   */
  public void setResultsPerPage(int resultsPerPage) {
    if(resultsPerPage > SYSTEM_PAGESIZE_MAX)
      this.resultsPerPage = SYSTEM_PAGESIZE_MAX;
    else
      this.resultsPerPage = resultsPerPage;
  }  
}
