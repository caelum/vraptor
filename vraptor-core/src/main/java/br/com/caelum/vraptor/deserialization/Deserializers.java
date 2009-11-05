package br.com.caelum.vraptor.deserialization;

import java.io.InputStream;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface Deserializers {

	Object[] deserialize(InputStream inputStream, String contentType, ResourceMethod method);

}
