package br.com.caelum.vraptor.restfulie.serialization;

/**
 * Represent a link to Resources in a JSON Restful serialization.
 * 
 * @author ac de souza
 */
class Link {
	public String rel;
	public String href;

	public Link(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}
	public String getRel(){ return rel; }
	public String getHref(){ return href; }
}