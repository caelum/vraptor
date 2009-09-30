
package br.com.caelum.vraptor.vraptor2.util;

import java.util.Enumeration;

/**
 * An enumeration which goes through two enumerations.
 * @author guilherme silveira
 *
 */
public class FallbackEnumeration implements Enumeration<String> {

	private final Enumeration<String> first;
	private final Enumeration<String> second;

	public FallbackEnumeration(Enumeration<String> first, Enumeration<String> second) {
		this.first = first;
		this.second = second;
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
