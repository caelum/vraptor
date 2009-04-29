package br.com.caelum.vraptor.http;


public class Rules {
	
	public UriBasedRule when(String uri) {
		return new UriBasedRule(uri);
	}
	
}
