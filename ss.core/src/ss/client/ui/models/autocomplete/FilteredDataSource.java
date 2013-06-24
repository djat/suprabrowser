package ss.client.ui.models.autocomplete;

import java.util.Vector;

public interface FilteredDataSource<DataSourceType> {
    
    public abstract Vector<DataSourceType> getData(String filter);

}
