package br.com.caelum.vraptor.http.route;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Order Routes by priority
 * @author Lucas Cavalcanti
 *
 */
public class PriorityRoutesList implements Collection<Route> {

	private final SortedMap<Integer,List<Route>> map;

	private List<Route> cache;

	public PriorityRoutesList() {
		map = new TreeMap<Integer, List<Route>>();
	}

	private List<Route> getFullList() {
		if (cache == null) {
			cache = new LinkedList<Route>();
			for (Entry<Integer, List<Route>> entry : map.entrySet()) {
				cache.addAll(entry.getValue());
			}
		}
		return cache;
	}
	private List<Route> getListFor(Route e) {
		if (!map.containsKey(e.getPriority())) {
			map.put(e.getPriority(), new LinkedList<Route>());
		}
		List<Route> list = map.get(e.getPriority());
		return list;
	}

	public boolean add(Route e) {
		cache = null;
		return getListFor(e).add(e);
	}


	public boolean addAll(Collection<? extends Route> c) {
		for (Route route : c) {
			add(route);
		}
		return true;
	}

	public void clear() {
		cache = null;
		map.clear();
	}

	public boolean contains(Object o) {
		return getFullList().contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return getFullList().containsAll(c);
	}

	public boolean isEmpty() {
		return getFullList().isEmpty();
	}

	public Iterator<Route> iterator() {
		return getFullList().iterator();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return getFullList().size();
	}

	public Object[] toArray() {
		return getFullList().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return getFullList().toArray(a);
	}

}
