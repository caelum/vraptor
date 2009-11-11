package br.com.caelum.vraptor.serialization;



public interface XmlSerializer {

	<T> XmlSerializer from(T object);

	XmlSerializer exclude(String... names);

	void serialize();

	XmlSerializer include(String... names);

}