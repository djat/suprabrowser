package ss.client.ui.messagedeliver;

public abstract class AbstractDeliveringElementProcessor<E extends AbstractDeliveringElement> {

	public abstract Class<E> getDeliveringElementClass();
	
	abstract void process( E element );
	
}
