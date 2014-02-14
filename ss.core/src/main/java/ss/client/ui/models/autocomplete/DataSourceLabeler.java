package ss.client.ui.models.autocomplete;

/**
 * This interface responsible for corresponding DataSource as Strings. It may be
 * emplemented as <code>return datasource.toString()</code>, or as
 * <code>return datasource;//if DataSource==String</code> or more
 * coplicated(especially when retrieving values from XML).
 * 
 * @author dankosedin
 * @param <DataSourceType>
 */
public interface DataSourceLabeler<DataSourceType> {

    public abstract String getDataLabel(DataSourceType data);

}