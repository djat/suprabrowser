package ss.server.networking.util;

import java.util.AbstractCollection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.SC;

public class FilteredHandlers extends AbstractCollection<DialogsMainPeer> {

	private Filter filter;

	private Iterable<DialogsMainPeer> handlers;

	public FilteredHandlers(Filter f, Iterable<DialogsMainPeer> handlers) {
		this.filter = f;
		this.handlers = handlers;
	}

	@Override
	public Iterator<DialogsMainPeer> iterator() {
		return new FilteredIterator(this.filter, this.handlers.iterator());
	}

	@Override
	public int size() {
		int i = 0;
		for ( DialogsMainPeer h : this) {
			i++;
		}
		return i;
	}

	public static FilteredHandlers getAllSphereHandlersFromSession(
			Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		return getAllSphereHandlers(supraSphere);
	}

	public static FilteredHandlers getAllSphereHandlers(String supraSphere) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SUPRA_SPHERE, supraSphere));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getAllNonSphereUserHandlersFromSession(
			Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String username = (String) session.get(SC.USERNAME);
		FilteredHandlers filteredHandlers = getAllNonSphereUserHandlers(
				supraSphere, username);
		return filteredHandlers;
	}

	public static FilteredHandlers getAllNonSphereUserHandlers(
			String supraSphere, String username) {
		Filter filter = new Filter(UnaryOperation.NOT);
		filter.add(new Expression(HandlerKey.SUPRA_SPHERE, supraSphere));
		filter.add(new Expression(HandlerKey.USERNAME, username));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getAllNonUserHandlersFromSession(
			Hashtable session) {
		String username = (String) session.get(SC.USERNAME);
		return getAllNonUserHandlers(username);
	}

	public static FilteredHandlers getAllNonUserHandlers(String username) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.USERNAME, username,
				UnaryOperation.NOT));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getSphereUserHandlersFromSession(
			Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String username = (String) session.get(SC.USERNAME);
		FilteredHandlers filteredHandlers = getSphereUserHandlers(supraSphere,
				username);
		return filteredHandlers;
	}

	public static FilteredHandlers getSphereUserHandlers(String supraSphere,
			String username) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SUPRA_SPHERE, supraSphere));
		filter.add(new Expression(HandlerKey.USERNAME, username));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getUserAllHandlers(String username) {
		Filter f = new Filter();
		f.add(new Expression(HandlerKey.USERNAME, username));
		FilteredHandlers fh = new FilteredHandlers(f, DialogsMainPeerManager.INSTANCE.getHandlers() );
		return fh;
	}

	public static FilteredHandlers getUserAllHandlersFromSession(
			Hashtable session) {
		String username = (String) session.get(SC.USERNAME);
		return getUserAllHandlers(username);
	}

	public static FilteredHandlers getExactHandlers(String supraSphere,
			String username, String session) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SUPRA_SPHERE, supraSphere));
		filter.add(new Expression(HandlerKey.USERNAME, username));
		filter.add(new Expression(HandlerKey.SESSION, session));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getExactHandlersFromSession(Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String username = (String) session.get(SC.USERNAME);
		String sessionName = (String) session.get(SC.SESSION);
		return getExactHandlers(supraSphere, username, sessionName);
	}

	public static FilteredHandlers getExactNotHandlers(String supraSphere,
			String username, String session) {
		Filter filter = new Filter(UnaryOperation.NOT);
		filter.add(new Expression(HandlerKey.SUPRA_SPHERE, supraSphere));
		filter.add(new Expression(HandlerKey.USERNAME, username));
		filter.add(new Expression(HandlerKey.SESSION, session));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		return filteredHandlers;
	}

	public static FilteredHandlers getExactNotHandlersForHandler(
			DialogsMainPeer handler) {
		return getExactNotHandlers(handler.get(HandlerKey.SUPRA_SPHERE), handler
				.get(HandlerKey.USERNAME), handler.get(HandlerKey.SESSION));
	}

}

class FilteredIterator implements Iterator<DialogsMainPeer> {
	private Filter f;

	private Iterator<DialogsMainPeer> iterator;

	private DialogsMainPeer next = null;

	private boolean finded = false;

	private boolean needFind = true;

	public FilteredIterator(Filter f, Iterator<DialogsMainPeer> iterator) {
		this.f = f;
		this.iterator = iterator;
	}

	public boolean hasNext() {
		find();
		return this.finded;
	}

	public DialogsMainPeer next() {
		find();
		this.needFind = true;
		if (this.finded) {
			return this.next;
		} else {
			throw new NoSuchElementException();
		}
	}

	public void remove() {
	}

	private void find() {
		if (this.needFind) {
			this.finded = false;
			while ((!this.finded) && (this.iterator.hasNext())) {
				DialogsMainPeer h = this.iterator.next();
				if (this.f.filter(h)) {
					this.finded = true;
					this.next = h;
				}
			}
			this.needFind = false;
		}
	}
}
