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
	private Map<String, Collection<String>> byCategory;

	public ErrorList(List<Message> delegate) {
		this.delegate = delegate;
	}

	public Map<String, Collection<String>> asMap() {
		if (byCategory == null) {
			Multimap<String, String> out = ArrayListMultimap.create();
			for (Message message : delegate) {
				out.put(message.getCategory(), message.getMessage());
			}
			byCategory = out.asMap();
		}
		return byCategory;
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}
}