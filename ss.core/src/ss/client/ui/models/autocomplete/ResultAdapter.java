package ss.client.ui.models.autocomplete;

public abstract class ResultAdapter<DataSourceType> implements
	ResultListener<DataSourceType> {

	public void processListSelection(String listSelection,
	    DataSourceType realData) {

    }
    
    /// TODO: ask denis: what is it?
	/**
	 * @see ss.client.ui.models.autocomplete.ResultListener.processResult();
	 */
    public void processResult(String selection, DataSourceType realData) {
    	processListSelection( selection, realData);
    }

    /// TODO: ask denis: what is it?
    public void processEmptyResult() {
    }

}
