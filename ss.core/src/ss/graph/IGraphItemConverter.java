package ss.graph;

public interface IGraphItemConverter<T> {

	ItemIdentity calcId( T data );
	
	IGraphItem createItem( T data );
	
}
