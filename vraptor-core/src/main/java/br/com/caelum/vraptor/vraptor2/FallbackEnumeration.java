package br.com.caelum.vraptor.vraptor2;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * An enumeration which goes through two enumerations.
 * @author guilherme silveira
 *
 */
public class FallbackEnumeration implements Enumeration<String> {

	private final ResourceBundle main;
	private final ResourceBundle fallback;
	private Enumeration<String> first;
	private Enumeration<String> second;

	public FallbackEnumeration(ResourceBundle main, ResourceBundle fallback) {
		this.main = main;
		this.fallback = fallback;
		this.first = main.getKeys();
		this.second = fallback.getKeys();
	}

	public boolean hasMoreElements() {
		return first.hasMoreElements() || second.hasMoreElements();
	}

	public String nextElement() {
		if(first.hasMoreElements()) {
			return first.nextElement();
		}
		return second.nextElement();
	}

}
