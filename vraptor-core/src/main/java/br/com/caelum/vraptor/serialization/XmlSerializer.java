package br.com.caelum.vraptor.serialization;

import java.util.Collection;

public interface XmlSerializer {

	<T> XmlSerializer from(T object);

	XmlSerializer exclude(String... names);

	void serialize();

	XmlSerializer include(String fieldName);

	XmlSerializer addMethod(String methodName);

	XmlSerializer from(String prefix, Collection collection);

	XmlSerializer namespace(String uri, String prefix);

}