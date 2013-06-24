package ss.client.ui.models.autocomplete;


/**
 * This is interface of data cource model that supports listeners with
 * autocomplite values. Use <code>setFilter</code> for notifying model of
 * changing its filter.
 * 
 * @author dankosedin
 * 
 * @param <T>
 */
public interface DataModel<T> {

    void setDataSourceLabeler(DataSourceLabeler<T> dataSourceLabeler);

    void setFilter(String filter);

    void addDataListener(DataListener listener);

    void removeDataListener(DataListener listener);

    ProposalCollection<T> getProposals();
    
    void dropCashedData();

}