package br.com.caelum.vraptor.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Multimap;

/**
 * Class that represents an error list.
 * 
 * @author Otávio Scherer Garcia
 */
public class ErrorList extends ForwardingList<Message> {
	
	private final List<Message> delegate;

	public ErrorList(List<Message> delegate) {
		this.delegate = delegate;
	}
	
	public Map<String, Collection<String>> asMap() {
		Multimap<String, String> out = ArrayListMultimap.create();
		for(Message message: delegate) {
			out.put(message.getCategory(), message.getMessage());
		}
		return out.asMap();
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}
}
