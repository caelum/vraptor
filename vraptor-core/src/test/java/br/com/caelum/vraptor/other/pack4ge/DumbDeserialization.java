package br.com.caelum.vraptor.other.pack4ge;

import java.io.InputStream;

import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Test Deserialization's comparator
 * @author Nykolas Lima
 *
 */
public class DumbDeserialization implements Deserializer {

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		return null;
	}

}
