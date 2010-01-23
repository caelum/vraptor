package br.com.caelum.vraptor.restfulie.relation;

import java.lang.reflect.Method;

/**
 * A relation based on a resource URI.
 * 
 * @author guilherme silveira
 * @author pedro matiello
 * @since 1.1.0
 */
public class UriBasedRelation implements Relation {
	
	private final String name, uri;
	
	public UriBasedRelation(String name, String uri) {
		super();
		this.name = name;
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public boolean matches(Method method) {
		return false;
	}

}
