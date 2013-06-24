package ss.client.ui.models.autocomplete;

public interface ResultListener<DataSourceType> {

    /**
         * invoked when user hit Enter in text field
         * 
         * @param selection
         *                text.getText() at invoked moment
         */
    public void processResult(String selection, DataSourceType realData);

    /**
         * invoked when user hit Enter in list.
         * 
         * @param listSelection
         *                selected item in list
         */
    public void processListSelection(String listSelection, DataSourceType realData);

    public void processEmptyResult();
}
