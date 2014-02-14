package ss.suprabrowser.exception;

/**
 * User: krishnm
 * Date: Jul 28, 2006
 * Time: 9:10:55 PM
  */

@SuppressWarnings("serial")
public class HTMLDOMHighlighterException extends Exception
{
    private String errMsg = null;

    public HTMLDOMHighlighterException()
    {
        super();
        this.errMsg = "UNKNOWN";
    }

    public HTMLDOMHighlighterException(String errMsg)
    {
        super();
        this.errMsg = errMsg;
    }   

    public String getMessage()
    {
        return this.errMsg;
    }
}

