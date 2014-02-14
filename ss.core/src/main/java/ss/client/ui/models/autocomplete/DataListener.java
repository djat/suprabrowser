package ss.client.ui.models.autocomplete;

/**
 * By this interface <code>DataModel</code> notify Listeners about loading and arriving new data.  
 * @author dankosedin
 *
 */
public interface DataListener {
    
    public abstract void newData();    
    
    public abstract void loadingData();

}
